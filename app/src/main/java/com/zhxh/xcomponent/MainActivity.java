package com.zhxh.xcomponent;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.zhxh.xcomponent.xmenu.MainMenuActivity;
import com.zhxh.xcomponentlib.CTextView;
import com.zhxh.xcomponentlib.ExpansionFrame;
import com.zhxh.xcomponentlib.SlideSwitch;
import com.zhxh.xcomponentlib.TimeTextView;
import com.zhxh.xcomponentlib.XEditText;
import com.zhxh.xcomponentlib.xstickyhorizon.XStickyNavContainer;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

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

        tv_countdown_time.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, GalleryActivity.class)));


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
        expansionFrame.setOnExpansionUpdateListener(expansionFraction -> expand_arrow.setRotation(expansionFraction * 180));


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


        CTextView tiltText = findViewById(R.id.tiltText);

        tiltText.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
            startActivity(new Intent(MainActivity.this, MainPopActivity.class));
            startActivity(new Intent(MainActivity.this, ScrollPopActivity.class));
        });

        showDataTime();
    }


    private void showDataTime() {
        System.out.println("xxxxx " + getFormatDate("2019-05-16"));
        System.out.println("xxxxx " + "xx1xx");
        System.out.println("xxxxx " + getFormatDate("2019-05-16 12:59:59"));
        System.out.println("xxxxx " + "xx2xx");
        System.out.println("xxxxx " + getFormatDate("2019-05-16 13:59:59"));
        System.out.println("xxxxx " + "xx3xx");
        System.out.println("xxxxx " + getFormatDate("2019/05/15 23:59:50"));
        System.out.println("xxxxx " + "xx4xx");
        System.out.println("xxxxx " + getFormatDate("2019-05-17 23:59:59"));

        System.out.println("xxxxx " + "xx5xx");

        System.out.println("xxxxx " + strToTimeStamp("2019-05-17 23:59:59", 0));
        System.out.println("xxxxx " + strToTimeStamp("2019-05-17 23:59:59", 0));
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

    /**
     * yyyy-MM-dd HH:mm:ss
     * yyyy/MM/dd HH:mm:ss
     *
     * @param s
     * @param offset
     * @return 判断是否为
     */
    public static Long strToTimeStamp(String s, int offset) {
        //数据长度验证
        if (TextUtils.isEmpty(s) || s.length() < 10) {
            throw new RuntimeException("不合法日期");
        }
        //修正数据
        if (!isValidDate(s)) {
            throw new RuntimeException("不合法日期");
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        Date date = simpleDateFormat.parse(s, pos);
        return date.getTime() + offset * 24 * 60 * 60 * 1000;
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     * yyyy/MM/dd HH:mm:ss
     *
     * @return 判断是否为
     */
    public static boolean isValidDate(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        String s = str.replace("/", "-");

        boolean isValid = true;
        // 指定日期格式为四位年-两位月份-两位日期，注意yyyy-MM-dd区分大小写；
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            //设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2019/02/29会被接受，并转换成比如2019/03/01
            format.setLenient(false);
            format.parse(s);
        } catch (ParseException e) {
            //如果throw java.text.ParseException或者NullPointerException，就说明格式不对
            isValid = false;
        }
        return isValid;
    }

    public static String getFormatDate(String str) {
        if (TextUtils.isEmpty(str) || str.length() < 10) {
            return "";
        }
        String s = str.replace("/", "-");
        if (DateUtils.isToday(strToTimeStamp(s, -1))) {
            return "明天";
        } else if (DateUtils.isToday(strToTimeStamp(s, 0))) {
            return "今天";
        } else if (DateUtils.isToday(strToTimeStamp(s, 1))) {
            return "昨天";
        } else {
            return s.substring(0, 10);
        }
    }
}
