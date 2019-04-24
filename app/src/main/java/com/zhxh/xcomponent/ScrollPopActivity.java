package com.zhxh.xcomponent;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhxh.xcomponentlib.xmenu.FloatDownMenu;

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


        String[] strings = {"aa", "bb", "cc"};
        TextView tvPopOut = findViewById(R.id.tvPopOut);

        tvPopOut.setOnClickListener(v -> {

            Toast.makeText(ScrollPopActivity.this, "弹出弹框", Toast.LENGTH_LONG).show();

            FloatDownMenu downMenu = new FloatDownMenu(ScrollPopActivity.this, tvPopOut, strings, 1, new FloatDownMenu.ItemClickTextView() {

                @Override
                public void onItemClick(int index) {
                    Toast.makeText(ScrollPopActivity.this, "弹出弹框--" + strings[index], Toast.LENGTH_LONG).show();
                }
            });

            downMenu.show();
        });
    }
}
