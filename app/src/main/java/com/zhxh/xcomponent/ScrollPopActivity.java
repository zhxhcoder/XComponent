package com.zhxh.xcomponent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.zhxh.xcomponentlib.xmenu.CDropDownFloatMenu;

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


        String[] strings = {"首页全部", "平安银行-已开通", "百信银行", "众邦银行", "全部银行", "平安银行-已开通", "百信银行", "众邦银行", "全部", "平安银行-已开通", "测试数据", "众邦银行"};
        TextView tvPopOut = findViewById(R.id.tvPopOut);

        tvPopOut.setOnClickListener(v -> {

            CDropDownFloatMenu downMenu = new CDropDownFloatMenu(ScrollPopActivity.this, strings, "全部银行", new CDropDownFloatMenu.ItemSelect() {
                @Override
                public void onSelect(int index) {
                    Log.d("zhxhzhxh", "选中 " + strings[index]);
                    Toast.makeText(ScrollPopActivity.this, "选中" + strings[index], Toast.LENGTH_LONG).show();
                }

                @Override
                public void onDismiss() {
                    Log.d("zhxhzhxh", "消失");
                    Toast.makeText(ScrollPopActivity.this, "消失", Toast.LENGTH_LONG).show();
                }
            });
            downMenu.showAsDropDown(tvPopOut);

        });
    }
}
