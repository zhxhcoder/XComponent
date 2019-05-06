package com.zhxh.xcomponent;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.zhxh.xcomponentlib.ShadowDrawable;
import com.zhxh.xcomponentlib.xmenu.CDropDownFloatMenu;

import java.util.Arrays;
import java.util.List;

public class ScrollPopActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_pop);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show());


        List<String> strings = Arrays.asList("首页全部", "平安银行-已开通", "百信银行", "众邦银行", "全部银行", "平安银行-已开通", "百信银行", "众邦银行", "全部", "平安银行-已开通", "测试数据", "众邦银行");
        TextView tvPopOut = findViewById(R.id.tvPopOut);

        final int[] selectIndex = {2};

        tvPopOut.setOnClickListener(v -> {

            CDropDownFloatMenu downMenu = new CDropDownFloatMenu(ScrollPopActivity.this, strings, selectIndex[0], new CDropDownFloatMenu.ItemSelect() {
                @Override
                public void onSelect(int index) {
                    selectIndex[0] = index;
                    Log.d("zhxhzhxh", "选中 " + strings.get(index));
                    Toast.makeText(ScrollPopActivity.this, "选中" + strings.get(index), Toast.LENGTH_LONG).show();
                }

                @Override
                public void onDismiss() {
                    Log.d("zhxhzhxh", "消失");
                    Toast.makeText(ScrollPopActivity.this, "消失", Toast.LENGTH_LONG).show();
                }
            });
            downMenu.showAsDropDown(tvPopOut);

        });


        TextView tvShapeDrawable = findViewById(R.id.tvShapeDrawable);
        ShadowDrawable.setShadowDrawable(tvShapeDrawable, new int[]{
                Color.parseColor("#536DFE"), Color.parseColor("#7C4DFF")}, dpToPx(8),
            Color.parseColor("#997C4DFF"), dpToPx(6), dpToPx(3), dpToPx(3));
    }

    private int dpToPx(int dp) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * dp + 0.5f);
    }
}
