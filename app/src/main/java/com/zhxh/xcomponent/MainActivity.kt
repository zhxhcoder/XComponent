package com.zhxh.xcomponent

import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.zhxh.xcomponent.widget.PagerTabDialog
import com.zhxh.xcomponentlib.AlwaysShowToast
import com.zhxh.xcomponentlib.SlideSwitch
import com.zhxh.xcomponentlib.arcibrary.CArcProgress
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

                    val resultStrMap =
                            """["11111111","22222222","33333333"]"""
                    val typeMap =
                            object : TypeToken<Object>() {}.type
                    val resp = Gson().fromJson<Object>(
                            resultStrMap,
                            typeMap
                    )

                    Log.d("xxxxxxxxx", resp.toString())
                    Log.d("xxxxxxxxx", resultStrMap)

                    val dialog = PagerTabDialog(this, null)
                    dialog.show(resp as List<String>)
                }

        ctvKeyValueText.text = "应收本金    890元"

        ctvKeyValueText.setOnClickListener {
            val toast = AlwaysShowToast(this)
            toast.setView(tiltText)
        }


        raImageView.setImageResource(R.mipmap.ic_default_banner)
        raImageView.setCorners(20, 20)
        myProgress.setOnClickListener {
            myProgress.runProgress(60, 50)
        }
        myProgress.setOnCenterDraw { canvas, rectF, x, y, _, progress ->
            val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)

            textPaint.color = Color.parseColor("#999999")
            textPaint.textSize = 24f
            val plusStr = "+3"
            canvas.drawText(plusStr, x - textPaint.measureText(plusStr) / 2, rectF.top + 120, textPaint)

            textPaint.flags = Paint.FAKE_BOLD_TEXT_FLAG
            textPaint.color = Color.parseColor("#333333")
            textPaint.textSize = 66f
            val progressStr = "$progress%"
            canvas.drawText(progressStr, x - textPaint.measureText(progressStr) / 2, rectF.top + 200, textPaint)

            textPaint.color = Color.parseColor("#333333")
            textPaint.textSize = 46f
            val priceStr = "-良好-"
            canvas.drawText(priceStr, x - textPaint.measureText(priceStr) / 2, rectF.top + 320, textPaint)


            textPaint.flags = Paint.ANTI_ALIAS_FLAG
            textPaint.color = Color.parseColor("#999999")
            textPaint.textSize = 36f
            val percentStr = "-打败了66.8%的在投用户-"
            canvas.drawText(percentStr, x - textPaint.measureText(percentStr) / 2, rectF.bottom - 10, textPaint)

        }
        myProgress.runProgress(85, 50)
    }
}
