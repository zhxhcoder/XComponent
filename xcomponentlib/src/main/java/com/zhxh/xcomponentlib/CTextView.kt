package com.zhxh.xcomponentlib

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextUtils
import android.text.format.DateUtils
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import java.text.ParseException
import java.text.ParsePosition
import java.text.SimpleDateFormat

/**
 * Created by zhxh on 2019/4/18
 * 继X系列开源库之后，开始C(新公司前缀)系列开源库开发
 * 倾斜字体 实现银行精选列表中，开抢时间的倾斜展示
 * 开始支持 下面带边框的效果
 * 特殊字体支持 CRegText 正则匹配 187% %颜色或字体大小改变
 */
class CTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    android.support.v7.widget.AppCompatTextView(context, attrs, defStyleAttr) {
    private var gradientDrawable: GradientDrawable? = null

    //关键属性设置
    private var xsolidColor = Color.TRANSPARENT
    private var strokeColor = Color.TRANSPARENT
    private var pressedColor = Color.TRANSPARENT
    private var pressedTextColor = Color.TRANSPARENT
    private var clickTextColor = Color.TRANSPARENT
    private var angleCorner = 0
    private var strokeWidth = 0

    private var specialTextReg: String? = null
    private var specialTextSize = 0
    private var specialTextColor = Color.TRANSPARENT


    private var defaultTextColor: Int = 0

    private var drawableWidth: Int = 0
    private var position: DrawablePosition? = null

    internal var bounds: Rect? = null
    private var drawablePadding = 0

    internal var isTouchPass = true

    private var degrees: Int = 0

    internal enum class DrawablePosition {
        NONE,
        LEFT_AND_RIGHT,
        LEFT,
        RIGHT
    }

    init {

        init(context, attrs)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init(context: Context, attrs: AttributeSet?) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CTextView)
        pressedColor = a.getColor(R.styleable.CTextView_CPressedColor, pressedColor)
        pressedTextColor = a.getColor(R.styleable.CTextView_CPressedTextColor, pressedTextColor)
        clickTextColor = a.getColor(R.styleable.CTextView_CClickTextColor, clickTextColor)
        xsolidColor = a.getColor(R.styleable.CTextView_CSolidColor, solidColor)
        strokeColor = a.getColor(R.styleable.CTextView_CStrokeColor, strokeColor)
        angleCorner = a.getDimensionPixelSize(R.styleable.CTextView_CAngleCorner, angleCorner)
        strokeWidth = a.getDimensionPixelSize(R.styleable.CTextView_CStrokeWidth, strokeWidth)
        drawablePadding = a.getDimensionPixelSize(R.styleable.CTextView_CDrawablePadding, drawablePadding)
        degrees = a.getInteger(R.styleable.CTextView_CRotateDegree, 0)
        specialTextReg = a.getString(R.styleable.CTextView_CSpecialTextReg)
        specialTextSize = a.getDimensionPixelSize(R.styleable.CTextView_CSpecialTextSize, specialTextSize)
        specialTextColor = a.getColor(R.styleable.CTextView_CSpecialTextColor, specialTextColor)

        defaultTextColor = this.currentTextColor

        if (pressedTextColor == Color.TRANSPARENT) {
            pressedTextColor = defaultTextColor
        }
        if (clickTextColor == Color.TRANSPARENT) {
            clickTextColor = defaultTextColor
        }

        if (pressedColor == Color.TRANSPARENT) {
            if (solidColor != Color.TRANSPARENT) {
                pressedColor = solidColor
            }
        }

        if (null == bounds) {
            bounds = Rect()
        }
        if (null == gradientDrawable) {
            gradientDrawable = GradientDrawable()
        }

        if (drawablePadding != 0) {
            gravity = Gravity.CENTER
        }

        setDrawablePadding(drawablePadding)
        setBtnDrawable()

        //设置按钮点击之后的颜色更换
        setOnTouchListener { arg0, event ->
            setBackgroundDrawable(gradientDrawable)
            setColor(event.action)
        }

        //资源回收
        a.recycle()
    }

    //过滤频繁点击
    override fun performClick(): Boolean {
        return if (isQuickClick()) {
            true
        } else super.performClick()
    }

    private var lastClickTime: Long = 0
    private fun isQuickClick(): Boolean {
        val time = System.currentTimeMillis()
        if (time - lastClickTime < 500) {
            return true
        }
        lastClickTime = time
        return false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val textPaint = paint
        val text = text.toString()
        textPaint.getTextBounds(text, 0, text.length, bounds)

        val textWidth = bounds!!.width()
        val factor = if (position == DrawablePosition.LEFT_AND_RIGHT) 2 else 1
        val contentWidth = drawableWidth + drawablePadding * factor + textWidth
        val horizontalPadding = (width / 2.0 - contentWidth / 2.0).toInt()

        compoundDrawablePadding = -horizontalPadding + drawablePadding

        when (position) {
            DrawablePosition.LEFT -> setPadding(horizontalPadding, paddingTop, paddingRight, paddingBottom)

            DrawablePosition.RIGHT -> setPadding(paddingLeft, paddingTop, horizontalPadding, paddingBottom)

            DrawablePosition.LEFT_AND_RIGHT -> setPadding(
                horizontalPadding,
                paddingTop,
                horizontalPadding,
                paddingBottom
            )
            else -> setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
        }
    }


    //重新设置位置
    override fun setCompoundDrawablesWithIntrinsicBounds(
        left: Drawable?,
        top: Drawable?,
        right: Drawable?,
        bottom: Drawable?
    ) {
        super.setCompoundDrawablesWithIntrinsicBounds(left, top, right, bottom)

        if (left != null && right != null) {
            drawableWidth = left.intrinsicWidth + right.intrinsicWidth
            position = DrawablePosition.LEFT_AND_RIGHT
        } else if (left != null) {
            drawableWidth = left.intrinsicWidth
            position = DrawablePosition.LEFT
        } else if (right != null) {
            drawableWidth = right.intrinsicWidth
            position = DrawablePosition.RIGHT
        } else {
            position = DrawablePosition.NONE
        }

        requestLayout()
    }

    //还原为默认
    fun reset() {
        pressedColor = Color.TRANSPARENT
        xsolidColor = Color.TRANSPARENT
        strokeColor = Color.TRANSPARENT

        angleCorner = 0
        strokeWidth = 0
    }


    //除去Angle还原为默认
    fun resetExAngle() {
        pressedColor = Color.TRANSPARENT
        xsolidColor = Color.TRANSPARENT
        strokeColor = Color.TRANSPARENT

        strokeWidth = 0
    }

    fun setDrawablePadding(padding: Int) {
        drawablePadding = padding
        requestLayout()
    }

    private fun setBtnDrawable() {
        //设置按钮颜色
        gradientDrawable!!.setColor(xsolidColor)
        //设置按钮的边框宽度
        gradientDrawable!!.setStroke(strokeWidth, strokeColor)
        //设置按钮圆角大小
        gradientDrawable!!.cornerRadius = angleCorner.toFloat()
        setBackgroundDrawable(gradientDrawable)
    }


    //处理按钮点击事件无效
    override fun setOnClickListener(l: View.OnClickListener?) {
        super.setOnClickListener(l)
        isTouchPass = false
    }

    //处理按下去的颜色 区分solid和stroke模式
    private fun setColor(action: Int): Boolean {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                gradientDrawable!!.setColor(pressedColor)
                this.setTextColor(pressedTextColor)
            }
            MotionEvent.ACTION_UP -> {
                gradientDrawable!!.setColor(xsolidColor)
                this.setTextColor(defaultTextColor)
            }
            MotionEvent.ACTION_CANCEL -> {
                gradientDrawable!!.setColor(xsolidColor)
                this.setTextColor(defaultTextColor)
            }
        }

        return isTouchPass
    }

    override fun onDraw(canvas: Canvas) {
        val lastDegrees = degrees % 360//优化大于360度情况

        if (lastDegrees != 0) {
            canvas.rotate((-lastDegrees).toFloat(), (measuredWidth / 2).toFloat(), (measuredHeight / 2).toFloat())
        }

        specialTextReg?.let { setSpecialText(text.toString(), it, specialTextColor, specialTextSize) }

        super.onDraw(canvas)
    }

    //根据正则来 处理特殊字符串的特殊颜色或大小
    fun setSpecialText(srcStr: String, specialTextReg: String, valueColor: Int, size: Int) {
        var valueColor = valueColor

        if (TextUtils.isEmpty(srcStr) || TextUtils.isEmpty(specialTextReg)) {
            this.text = srcStr
            return
        }

        if (valueColor == 0) {
            valueColor = this.currentTextColor
        }

        val resultSpan = SpannableString(srcStr)

        val s = Regex(specialTextReg).findAll(srcStr).toList()

        if (s.isNullOrEmpty()) {
            this.text = srcStr
            return
        }

        s.forEach {
            resultSpan.setSpan(
                ForegroundColorSpan(valueColor),
                it.range.start, it.range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (size != 0) {
                resultSpan.setSpan(AbsoluteSizeSpan(size, true), it.range.start, it.range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        this.text = resultSpan
        return
    }

    //根据正则用来 处理特殊字符串的特殊颜色或大小及点击事件
    fun setSpecialText(srcStr: String, specialTextReg: String, valueColor: Int, size: Int, cb: () -> Unit) {
        var valueColor = valueColor

        if (TextUtils.isEmpty(srcStr) || TextUtils.isEmpty(specialTextReg)) {
            this.text = srcStr
            return
        }

        if (valueColor == 0) {
            valueColor = this.currentTextColor
        }

        val resultSpan = SpannableString(srcStr)

        val s = Regex(specialTextReg).findAll(srcStr).toList()

        if (s.isNullOrEmpty()) {
            this.text = srcStr
            return
        }

        s.forEach {
            resultSpan.setSpan(
                ForegroundColorSpan(valueColor),
                it.range.start, it.range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            if (size != 0) {
                resultSpan.setSpan(AbsoluteSizeSpan(size, true), it.range.start, it.range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }

            resultSpan.setSpan(object : ClickableSpan() {
                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.color = valueColor
                    ds.isUnderlineText = false
                }

                override fun onClick(widget: View) {
                    cb.invoke()
                }
            }, it.range.start, it.range.endInclusive + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        this.movementMethod = LinkMovementMethod.getInstance()
        this.text = resultSpan
        return
    }

    //根据startIndex和rangeLength来 处理特殊字符串的特殊颜色或大小
    fun setSpecialText(srcStr: String, startIndex: Int, rangeLength: Int, valueColor: Int, size: Int, cb: () -> Unit) {

        var valueColor = valueColor

        if (TextUtils.isEmpty(srcStr))
            return

        if (startIndex < 0 || rangeLength <= 0 || (startIndex + rangeLength) > srcStr.length) {
            this.text = srcStr
            return
        }

        if (valueColor == 0) {
            valueColor = this.currentTextColor
        }

        val resultSpan = SpannableString(srcStr)

        resultSpan.setSpan(
            ForegroundColorSpan(valueColor),
            startIndex, startIndex + rangeLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        if (size != 0) {
            resultSpan.setSpan(AbsoluteSizeSpan(size, true), startIndex, startIndex + rangeLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        resultSpan.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                cb.invoke()
            }
        }, startIndex, startIndex + rangeLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        this.movementMethod = LinkMovementMethod.getInstance()

        this.text = resultSpan
        return
    }


    fun setMaxLineText(srcStr: String, maxLines: Int, greater: () -> Unit, lesser: () -> Unit) {
        this.text = srcStr
        this.maxLines = maxLines

        this.post {
            if (lineCount > maxLines) {
                greater.invoke()
            } else {
                lesser.invoke()
            }
        }
    }

    override fun setTextColor(color: Int) {
        super.setTextColor(color)

        defaultTextColor = color

        if (pressedTextColor == Color.TRANSPARENT) {
            pressedTextColor = defaultTextColor
        }
        if (clickTextColor == Color.TRANSPARENT) {
            clickTextColor = defaultTextColor
        }
    }

    fun initBtnAttr(
        solidColor: Int,
        strokeColor: Int,
        pressedColor: Int,
        pressedTextColor: Int,
        angleCorner: Int,
        strokeWidth: Int
    ) {
        this.xsolidColor = solidColor
        this.strokeColor = strokeColor
        this.pressedColor = pressedColor
        this.pressedTextColor = pressedTextColor
        this.angleCorner = angleCorner
        this.strokeWidth = strokeWidth
        setBtnDrawable()
    }


    //初始化 默认
    fun initSolidAttr(textColor: Int, solidColor: Int, angleCorner: Int) {
        resetExAngle()
        initBtnAttr(solidColor, strokeColor, solidColor, textColor, angleCorner, strokeWidth)
        setTextColor(textColor)
    }


    //初始化空心的
    fun initStrokeAttr(textColor: Int, strokeColor: Int, pressedColor: Int, strokeWidth: Int, angleCorner: Int) {
        resetExAngle()
        initBtnAttr(xsolidColor, strokeColor, pressedColor, pressedTextColor, angleCorner, strokeWidth)
        setTextColor(textColor)
    }

    //stockColor等于textColor等于pressColor等于pressTextColor
    fun initStrokeAttr(strokeColor: Int, strokeWidth: Int, angleCorner: Int) {
        resetExAngle()
        initBtnAttr(xsolidColor, strokeColor, pressedColor, strokeColor, angleCorner, strokeWidth)
        setTextColor(strokeColor)
    }

    companion object {

        /**
         * yyyy-MM-dd HH:mm:ss
         * yyyy/MM/dd HH:mm:ss
         *
         * @param s
         * @param offset
         * @return 判断是否为
         */
        fun strToTimeStamp(s: String, offset: Int): Long {
            //数据长度验证
            if (TextUtils.isEmpty(s) || s.length < 10) {
                return -1
            }
            //修正数据
            if (!isValidDate(s)) {
                return -1
            }
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val pos = ParsePosition(0)
            val date = simpleDateFormat.parse(s, pos)
            return date.time + offset * 24 * 60 * 60 * 1000
        }

        /**
         * yyyy-MM-dd HH:mm:ss
         * yyyy/MM/dd HH:mm:ss
         *
         * @return 判断是否为
         */
        fun isValidDate(str: String): Boolean {
            if (TextUtils.isEmpty(str)) {
                return false
            }
            val s = str.replace("/", "-")

            var isValid = true
            // 指定日期格式为四位年-两位月份-两位日期，注意yyyy-MM-dd区分大小写；
            val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            try {
                //设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2019/02/29会被接受，并转换成比如2019/03/01
                format.isLenient = false
                format.parse(s)
            } catch (e: ParseException) {
                //如果throw java.text.ParseException或者NullPointerException，就说明格式不对
                isValid = false
            }

            return isValid
        }

        fun getFormatDate(str: String): String {
            if (TextUtils.isEmpty(str) || str.length < 10) {
                return ""
            }
            //修正数据
            if (!isValidDate(str)) {
                return ""
            }
            val s = str.replace("/", "-")
            return when {
                DateUtils.isToday(strToTimeStamp(s, -1)) -> "明天"
                DateUtils.isToday(strToTimeStamp(s, 0)) -> "今天"
                DateUtils.isToday(strToTimeStamp(s, 1)) -> "昨天"
                else -> s.substring(0, 10)
            }
        }
    }
}
