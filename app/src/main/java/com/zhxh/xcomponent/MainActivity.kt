package com.zhxh.xcomponent

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast

import com.zhxh.xcomponent.xmenu.MainMenuActivity
import com.zhxh.xcomponentlib.CTextView
import com.zhxh.xcomponentlib.ExpansionFrame
import com.zhxh.xcomponentlib.SlideSwitch
import com.zhxh.xcomponentlib.XEditText
import com.zhxh.xcomponentlib.xstickyhorizon.XStickyNavContainer
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

        val layout = findViewById<XStickyNavContainer>(R.id.head_home_layout)

        layout.setOnStartActivity { startActivity(Intent(this@MainActivity, TabHomeActivity::class.java)) }
        val mHeadRecyclerView = findViewById<RecyclerView>(R.id.head_home_recyclerview)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        mHeadRecyclerView.layoutManager = layoutManager

        val xEditText = findViewById<XEditText>(R.id.xEditText)
        xEditText.setDrawableClickListener { target -> Toast.makeText(this@MainActivity, "点击的是右面的眼睛", Toast.LENGTH_LONG).show() }

        val expansionFrame = findViewById<ExpansionFrame>(R.id.expansionFrame)
        expansionFrame.setOnExpansionUpdateListener { expansionFraction -> expand_arrow.rotation = expansionFraction * 180 }

        val slideSwitch = findViewById<SlideSwitch>(R.id.slideSwitch)
        slideSwitch.setSlideListener(object : SlideSwitch.SlideListener {
            override fun open() {
                expansionFrame.toggle()
            }

            override fun close() {
                expansionFrame.toggle()
            }
        })

        val tiltText = findViewById<CTextView>(R.id.tiltText)

        tiltText.setOnClickListener { v ->
            startActivity(Intent(this@MainActivity, MainMenuActivity::class.java))
            startActivity(Intent(this@MainActivity, MainPopActivity::class.java))
            startActivity(Intent(this@MainActivity, ScrollPopActivity::class.java))
        }
    }

}
