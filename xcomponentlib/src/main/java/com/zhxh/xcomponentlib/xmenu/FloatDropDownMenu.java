package com.zhxh.xcomponentlib.xmenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.zhxh.xcomponentlib.FlowLayout;
import com.zhxh.xcomponentlib.R;

/**
 * Created by zhxh on 2019/4/19
 * 下拉选择框
 */
public class FloatDropDownMenu {

    private Context context;

    public PopupWindow popupwindow;

    private int height = 0;
    private int width = 0;

    private String selectStr;

    private String[] stringArray;

    int selectColor;

    ItemClickTextView itemClick;

    public FloatDropDownMenu(Context context, String[] stringArray, String selectStr, ItemClickTextView itemClick) {
        this.context = context;
        this.stringArray = stringArray;
        this.selectStr = selectStr;
        this.itemClick = itemClick;

        if (stringArray != null) {
            createAutoDialog();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void createAutoDialog() {
        selectColor = Color.RED;
        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflate.inflate(R.layout.pop_common_drop_down, null);

        popupView.setOnTouchListener((v, event) -> {
            dismiss();
            return false;
        });

        final LinearLayout pop_layout;
        final FlowLayout item_container;

        pop_layout = popupView.findViewById(R.id.pop_layout);
        item_container = popupView.findViewById(R.id.item_container);

        for (int i = 0; i < stringArray.length; i++) {

            TextView textView = new TextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
            textView.setText(stringArray[i]);

            if (selectStr.equalsIgnoreCase(stringArray[i])) {
                textView.setTextColor(selectColor);
            } else {
                textView.setTextColor(Color.WHITE);
            }
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(200, 100);
            textView.setLayoutParams(textViewParams);
            textView.setPadding(0, 20, 0, 20);

            item_container.addView(textView);

            textView.setOnClickListener(new FloatDropDownMenu.ClickTextView(i, item_container));
        }
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int widthPixels = dm.widthPixels;

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        pop_layout.measure(w, h);

        height = pop_layout.getMeasuredHeight();

        popupwindow = new PopupWindow(popupView, widthPixels, ViewGroup.LayoutParams.MATCH_PARENT);
        popupwindow.setTouchable(true); // 设置PopupWindow可触摸
        popupwindow.setOutsideTouchable(true);
        popupwindow.setFocusable(true);
        popupwindow.setBackgroundDrawable(new BitmapDrawable());

        popupwindow.setOnDismissListener(() -> {
            //Todo
        });
    }


    public void dismiss() {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
        }
    }

    //默认调用方法
    public void showAsDropDown(View anchorView) {
        if (Build.VERSION.SDK_INT >= 24) {
            Rect visibleFrame = new Rect();
            anchorView.getGlobalVisibleRect(visibleFrame);
            int height = anchorView.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            popupwindow.setHeight(height);
            popupwindow.showAsDropDown(anchorView, 0, 0);
        } else {
            popupwindow.showAsDropDown(anchorView, 0, 0);
        }
    }

    public void show(View anchorView) {
        int[] location = new int[2];

        anchorView.getLocationOnScreen(location);
        //为popWindow添加动画效果
        popupwindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupwindow.showAtLocation(anchorView, Gravity.TOP, location[0] - width / 3, location[1] + height);
    }


    public void show(View anchorView, int x, int y) {

        int[] location = new int[2];

        anchorView.getLocationInWindow(location);
        //为popWindow添加动画效果
        popupwindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupwindow.showAtLocation(anchorView, Gravity.TOP, x, y);
    }

    public void show(View anchorView, int y) {

        int[] location = new int[2];

        anchorView.getLocationInWindow(location);
        //为popWindow添加动画效果
        popupwindow.setAnimationStyle(android.R.style.Animation_Dialog);
        popupwindow.showAtLocation(anchorView, Gravity.TOP, location[0] - width / 2, y);
    }

    /**
     * 定义状态改变接口
     */
    public interface ItemClickTextView {
        void onItemClick(int index);
    }

    class ClickTextView implements View.OnClickListener {
        int position;
        FlowLayout linearLayout;

        public ClickTextView(int position, FlowLayout linearLayout) {
            this.position = position;
            this.linearLayout = linearLayout;
        }

        @Override
        public void onClick(View v) {

            int childCount = linearLayout.getChildCount();

            for (int i = 0; i < childCount; i++) {
                View view = linearLayout.getChildAt(i);
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(Color.WHITE);
                }
            }
            TextView textView = (TextView) v;
            textView.setTextColor(selectColor);
            itemClick.onItemClick(position);
            dismiss();
        }
    }
}
