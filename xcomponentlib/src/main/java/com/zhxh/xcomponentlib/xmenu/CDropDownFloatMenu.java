package com.zhxh.xcomponentlib.xmenu;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.zhxh.xcomponentlib.CTextView;
import com.zhxh.xcomponentlib.FlowLayout;
import com.zhxh.xcomponentlib.R;

import java.util.List;

/**
 * Created by zhxh on 2019/4/19
 * 下拉选择框
 */
public class CDropDownFloatMenu {

    private Activity context;
    public PopupWindow popupwindow;

    private int height = 0;

    private int selectIndex;
    private List<String> stringList;
    ItemSelect itemSelect;

    private int selectTextColor;
    private int selectSolidColor;
    private int defaultSolidColor;
    private int defaultTextColor;


    public CDropDownFloatMenu(Activity activity, List<String> stringList, int selectIndex, ItemSelect itemSelect) {
        this.context = activity;
        this.stringList = stringList;
        this.selectIndex = selectIndex;
        this.itemSelect = itemSelect;

        if (stringList != null) {
            initPopLayout();
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPopLayout() {
        selectTextColor = Color.parseColor("#E54749");
        selectSolidColor = Color.parseColor("#FFE8E8");

        defaultTextColor = Color.parseColor("#48484F");
        defaultSolidColor = Color.parseColor("#F0F0F0");

        LayoutInflater inflate = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = inflate.inflate(R.layout.pop_common_drop_down, null);

        popupView.setOnTouchListener((v, event) -> {
            dismiss();
            return false;
        });

        final FlowLayout item_container;
        item_container = popupView.findViewById(R.id.item_container);

        final LinearLayout pop_layout;
        pop_layout = popupView.findViewById(R.id.pop_layout);

        createFloatItem(item_container, true);

        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        int widthPixels = dm.widthPixels;

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        pop_layout.measure(w, h);

        height = pop_layout.getMeasuredHeight();

        popupwindow = new PopupWindow(popupView, widthPixels, height);
        popupwindow.setTouchable(true); // 设置PopupWindow可触摸
        popupwindow.setOutsideTouchable(true);
        popupwindow.setFocusable(true);

        //WindowManager.LayoutParams lp =context.getWindow().getAttributes();
        //lp.alpha = 0.3f;
        //context.getWindow().setAttributes(lp);

        popupwindow.setOnDismissListener(() -> {
            //Todo
            itemSelect.onDismiss();
        });
    }

    private void createFloatItem(FlowLayout item_container, boolean isInit) {
        item_container.removeAllViews();
        for (int i = 0; i < stringList.size(); i++) {
            CTextView textView = new CTextView(context);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            textView.setText(stringList.get(i));

            if (selectIndex == i) {
                textView.initBtnAttr(
                    selectSolidColor
                    , selectTextColor
                    , selectSolidColor
                    , selectTextColor
                    , dip2px(2)
                    , dip2px(1));
                textView.setTextColor(selectTextColor);
            } else {
                textView.initStrokeAttr(defaultSolidColor, defaultSolidColor, dip2px(2));
                textView.setTextColor(defaultTextColor);
            }

            textView.setTypeface(Typeface.DEFAULT_BOLD);

            //兼容低版本可能出现的paddingLeft和paddingRight不起作用
            textView.setWidth((int) textView.getPaint().measureText(stringList.get(i)) + dip2px(20));
            textView.setPadding(0, dip2px(5), 0, dip2px(5));
            item_container.addView(textView);

            if (isInit) {//只有首次才有必要设置clickListener
                int finalI = i;
                textView.setOnClickListener(v -> {
                    selectIndex = finalI;
                    createFloatItem(item_container, false);
                    itemSelect.onSelect(finalI);
                    //item_container.postDelayed而不用v.postDelayed而不用 生命周期不同
                    item_container.post(() -> dismiss());
                });
            }
        }
    }

    public void dismiss() {
        if (popupwindow != null && popupwindow.isShowing()) {
            popupwindow.dismiss();
        }
    }

    //默认调用方法
    public void showAsDropDown(View anchorView) {
        Rect visibleFrame = new Rect();
        anchorView.getGlobalVisibleRect(visibleFrame);
        int height;
        if (checkHasNavigationBar(context)) {
            height = anchorView.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
        } else {
            height = anchorView.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom + getNavigationBarHeight();
        }
        popupwindow.setHeight(height);
        popupwindow.showAsDropDown(anchorView, 0, 0);
    }

    public int dip2px(float dpValue) {
        if (context == null) {
            return 0;
        }
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 定义状态改变接口
     */
    public interface ItemSelect {
        void onSelect(int index);

        void onDismiss();
    }

    //获取底部navigationBar高度
    private int getNavigationBarHeight() {
        Resources resources = context.getResources();
        int height = 0;
        try {
            int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            height = resources.getDimensionPixelSize(resourceId);
            return height;
        } catch (Resources.NotFoundException e) {
            return height;
        }
    }

    public static boolean checkHasNavigationBar(Activity activity) {
        WindowManager windowManager = activity.getWindowManager();
        Display d = windowManager.getDefaultDisplay();

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            d.getRealMetrics(realDisplayMetrics);
        }

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
    }

}
