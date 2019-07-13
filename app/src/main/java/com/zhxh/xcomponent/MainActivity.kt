package com.zhxh.xcomponent

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast

import com.zhxh.xcomponentlib.AlwaysShowToast
import com.zhxh.xcomponentlib.SlideSwitch
import kotlinx.android.synthetic.main.activity_main.*

/**
 * Created by zhxh on 2018/6/3
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tv_countdown_time.setTimes(100)
        tv_countdown_time.setOnFinish {
            Toast.makeText(this@MainActivity, "倒计时结束", Toast.LENGTH_LONG).show()
            finish()
        }

        tv_countdown_time.setOnClickListener { v -> startActivity(Intent(this@MainActivity, GalleryActivity::class.java)) }

        head_home_layout.setOnStartActivity { startActivity(Intent(this@MainActivity, TabHomeActivity::class.java)) }
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        head_home_recyclerview.layoutManager = layoutManager

        xEditText.setDrawableClickListener { target -> Toast.makeText(this@MainActivity, "点击的是右面的眼睛", Toast.LENGTH_LONG).show() }

        expansionFrame.setOnExpansionUpdateListener { expansionFraction -> expand_arrow.rotation = expansionFraction * 180 }

        slideSwitch.setSlideListener(object : SlideSwitch.SlideListener {
            override fun open() {
                startActivity(Intent(this@MainActivity, TabHomeActivity::class.java))
                expansionFrame.toggle()
            }

            override fun close() {
                expansionFrame.toggle()
            }
        })

        tiltText.setOnClickListener { v ->
            Toast.makeText(this, "gogogo", Toast.LENGTH_LONG).show()
            startActivity(Intent(this@MainActivity, ScrollPopActivity::class.java))
        }
        ctvClickText.setSpecialText("我是谁我要点击点击什么吗", "点击点击", Color.RED, 0) {
            Toast.makeText(this, "我点击了：$it", Toast.LENGTH_LONG).show()
        }
        ctvClickText
            .withText("我是谁我要点击点击什么吗")
            .withRegex("点击点击")
            .withColor(Color.RED)
            .withSize(20)
            .withBack {
                Toast.makeText(this, "我点击了：$it", Toast.LENGTH_LONG).show()
            }

        ctvKeyValueText.text = "应收本金    890元"

        ctvKeyValueText.setOnClickListener {
            val toast = AlwaysShowToast(this)
            toast.setView(tiltText)
        }


        raImageView.setImageResource(R.mipmap.ic_default_banner)
        raImageView.setCorners(20, 20)
    }

}
