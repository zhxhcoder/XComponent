package com.zhxh.xcomponent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.zhxh.xcomponentlib.TimeTextView;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeTextView tv_countdown_time;
        tv_countdown_time = findViewById(R.id.tv_countdown_time);
        tv_countdown_time.setTimes(100);
        tv_countdown_time.setOnFinish(() -> {
            Toast.makeText(MainActivity.this,"倒计时结束",Toast.LENGTH_LONG).show();
            finish();
        });
    }
}
