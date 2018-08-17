package com.zhxh.xcomponent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhxh.xcomponentlib.ExpansionFrame;
import com.zhxh.xcomponentlib.SlideSwitch;
import com.zhxh.xcomponentlib.TimeTextView;
import com.zhxh.xcomponentlib.XEditText;
import com.zhxh.xcomponentlib.xstickyhorizon.XStickyNavContainer;


/**
 * Created by zhxh on 2018/6/3
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeTextView tv_countdown_time;
        tv_countdown_time = findViewById(R.id.tv_countdown_time);
        tv_countdown_time.setTimes(100);
        tv_countdown_time.setOnFinish(() -> {
            Toast.makeText(MainActivity.this, "倒计时结束", Toast.LENGTH_LONG).show();
            finish();
        });

        tv_countdown_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, GalleryActivity.class));
            }
        });


        XStickyNavContainer layout = findViewById(R.id.head_home_layout);

        layout.setOnStartActivity(() -> {
            startActivity(new Intent(MainActivity.this, TabHomeActivity.class));

        });
        RecyclerView mHeadRecyclerView = findViewById(R.id.head_home_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mHeadRecyclerView.setLayoutManager(layoutManager);
        HomeAdapters mHomeAdapter = new HomeAdapters();
        mHeadRecyclerView.setAdapter(mHomeAdapter);

        XEditText xEditText = findViewById(R.id.xEditText);
        xEditText.setDrawableClickListener(target -> Toast.makeText(MainActivity.this, "点击的是右面的眼睛", Toast.LENGTH_LONG).show());


        ImageView expand_arrow = findViewById(R.id.expand_arrow);

        ExpansionFrame expansionFrame = findViewById(R.id.expansionFrame);
        expansionFrame.setOnExpansionUpdateListener(new ExpansionFrame.OnExpansionUpdateListener() {
            @Override
            public void onExpansionUpdate(float expansionFraction) {
                expand_arrow.setRotation(expansionFraction * 180);
            }
        });


        SlideSwitch slideSwitch = findViewById(R.id.slideSwitch);
        slideSwitch.setSlideListener(new SlideSwitch.SlideListener() {
            @Override
            public void open() {
                expansionFrame.toggle();
            }

            @Override
            public void close() {
                expansionFrame.toggle();
            }
        });


    }


    public class HomeAdapters extends RecyclerView.Adapter<HomeAdapters.ViewHolder> {

        @Override
        public HomeAdapters.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_adapter_homes, parent, false);
            return new HomeAdapters.ViewHolder(mView);
        }

        @Override
        public void onBindViewHolder(HomeAdapters.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 6;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            ViewHolder(View view) {
                super(view);
            }
        }
    }

}
