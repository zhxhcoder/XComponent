package com.zhxh.xcomponent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhxh.xcomponentlib.xmenu.FloatDropDownMenu;

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


        String[] strings = {"全部", "平安银行-已开通", "百信银行", "众邦银行", "全部银行", "平安银行-已开通", "百信银行", "众邦银行", "全部", "平安银行-已开通", "百信银行", "众邦银行"};
        TextView tvPopOut = findViewById(R.id.tvPopOut);

        tvPopOut.setOnClickListener(v -> {
            Toast.makeText(ScrollPopActivity.this, "弹出弹框", Toast.LENGTH_LONG).show();
            FloatDropDownMenu downMenu = new FloatDropDownMenu(ScrollPopActivity.this, strings, "全部银行", index -> Toast.makeText(ScrollPopActivity.this, "弹出弹框--" + strings[index], Toast.LENGTH_LONG).show());
            downMenu.showAsDropDown(tvPopOut);
        });
    }
}
