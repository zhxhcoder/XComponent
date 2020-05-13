package com.zhxh.xcomponentlib

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.widget.ProgressBar

/**
 * Created by zhxh on 2020/4/14
 */
class CArcProgress @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : ProgressBar(context, attrs, defStyleAttr) {
    private val DEFAULT_LINEHEIGHT = dp2px(12)
    private val DEFAULT_mTickWidth = dp2px(2)
    private val DEFAULT_mRadius = dp2px(72)
    private val DEFAULT_mUnmProgressColor = -0x151516
    private val DEFAULT_mProgressColor = Color.YELLOW
    private val DEFAULT_OFFSETDEGREE = 60f
    private val DEFAULT_DENSITY = 4
    private val MIN_DENSITY = 2
    private val MAX_DENSITY = 8
    private var mStyleProgress = STYLE_ARC
    private val mBgShow: Boolean
    private val mRadius: Float
    private val mArcbgColor: Int
    private val mBoardWidth: Int
    private var mDegree = DEFAULT_OFFSETDEGREE
    private var mArcRectF: RectF? = null
    private val mLinePaint: Paint
    private val mArcPaint: Paint
    private val mTextPaint: Paint
    private val mUnmProgressColor: Int
    private val mProgressColor: Int
    private val mTickWidth: Int
    private var mTickDensity: Float
    private var mCenterBitmap: Bitmap? = null
    private var mCenterCanvas: Canvas? = null
    private var mOnCenter: OnCenterDraw? = null

    private var animTim = 0L
    fun setOnCenterDraw(mOnCenter: OnCenterDraw?) {
        this.mOnCenter = mOnCenter
    }

    /**
     * progress 表示进度 0-100
     * plus增加了多少 例如 +3
     * rate 表示分数 例如 85分
     * defeat 例如 65.5%
     * itemDuration 速度 默认25ms
     */
    private fun runProgress(progress: Int, plus: String?, rate: String?, defeat: String, itemDuration: Long) {
        initDataDraw(plus, rate, defeat)
        startRun(progress, itemDuration)
    }

    /**
     * progress 表示进度 0-100
     * plus增加了多少 例如 +3
     * rate 表示分数 例如 85分
     * defeat 例如 65.5%
     */
    fun runProgress(progress: Int, plus: String?, rate: String?, defeat: String) {
        runProgress(progress, plus, rate, defeat, 25)
    }

    /**
     * 改函数适用当没有plus情况
     */
    fun runProgress(progress: Int, rate: String?, defeat: String) {
        runProgress(progress, "", rate, defeat)
    }

    private fun initDataDraw(plus: String?, rate: String?, defeat: String) {
        setOnCenterDraw(object : OnCenterDraw {
            override fun draw(canvas: Canvas?, rectF: RectF?, x: Float, y: Float, strokeWidth: Float, progress: Int) {
                if (canvas == null) {
                    return
                }
                if (rectF == null) {
                    return
                }

                run {
                    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    if (!plus.isNullOrEmpty()) {
                        textPaint.color = Color.parseColor("#999999")
                        textPaint.textSize = dp2px(12).toFloat()
                        canvas.drawText(plus, x - textPaint.measureText(plus) / 2, rectF.top + dp2px(50), textPaint)
                    }
                }

                val cancelRun = Runnable {
                    val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                    textPaint.textSize = dp2px(12).toFloat()

                    val paint = Paint()
                    paint.color = Color.WHITE
                    canvas.drawRect(x - textPaint.measureText(plus) / 2, rectF.top + dp2px(50), x + textPaint.measureText(plus) / 2, rectF.top + dp2px(35), paint)
                }

                handler.postDelayed(cancelRun, animTim + 250)

                val textPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                textPaint.flags = Paint.FAKE_BOLD_TEXT_FLAG
                textPaint.color = Color.parseColor("#333333")
                textPaint.textSize = dp2px(28).toFloat()
                val progressStr = "${progress}分"
                canvas.drawText(progressStr, x - textPaint.measureText(progressStr) / 2, rectF.top + dp2px(80), textPaint)

                if (!rate.isNullOrEmpty()) {
                    textPaint.flags = Paint.ANTI_ALIAS_FLAG
                    textPaint.color = Color.parseColor("#333333")
                    textPaint.textSize = dp2px(18).toFloat()
                    canvas.drawText(rate, x - textPaint.measureText(rate) / 2, rectF.top + dp2px(120), textPaint)
                }

                textPaint.flags = Paint.ANTI_ALIAS_FLAG
                textPaint.color = Color.parseColor("#999999")
                textPaint.textSize = dp2px(15).toFloat()

                val start = "- 打败了"
                val end = "的在投用户 -"
                val percentStr = "$start$defeat$end"

                val start1X = x - textPaint.measureText(percentStr) / 2
                val start2X = start1X + textPaint.measureText(start)
                val start3X = start2X + textPaint.measureText(defeat)

                canvas.drawText(start, start1X, rectF.bottom - 10, textPaint)
                canvas.drawText(end, start3X, rectF.bottom - 10, textPaint)
                textPaint.color = Color.parseColor("#FF403B")
                canvas.drawText(defeat, start2X, rectF.bottom - 10, textPaint)
            }
        })
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var widthMeasureSpec = widthMeasureSpec
        var heightMeasureSpec = heightMeasureSpec
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        if (widthMode != MeasureSpec.EXACTLY) {
            val widthSize = (mRadius * 2 + mBoardWidth * 2).toInt()
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(widthSize, MeasureSpec.EXACTLY)
        }
        if (heightMode != MeasureSpec.EXACTLY) {
            val heightSize = (mRadius * 2 + mBoardWidth * 2).toInt()
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(heightSize, MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        canvas.save()
        val rotate = progress * 1.0f / max
        val x = mArcRectF!!.right / 2 + mBoardWidth / 2
        val y = mArcRectF!!.right / 2 + mBoardWidth / 2
        if (mOnCenter != null) {
            if (mCenterCanvas == null) {
                mCenterBitmap = Bitmap.createBitmap(mRadius.toInt() * 2, mRadius.toInt() * 2, Bitmap.Config.ARGB_8888)
                mCenterCanvas = Canvas(mCenterBitmap)
            }
            mCenterCanvas!!.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
            mOnCenter!!.draw(mCenterCanvas, mArcRectF, x, y, mBoardWidth.toFloat(), progress)
            canvas.drawBitmap(mCenterBitmap, 0f, 0f, null)
        }
        val angle = mDegree / 2
        val count = ((360 - mDegree) / mTickDensity).toInt()
        val target = (rotate * count).toInt()
        if (mStyleProgress == STYLE_ARC) {
            val targetDegree = (360 - mDegree) * rotate
            //绘制未完成部分
            mArcPaint.color = mUnmProgressColor
            canvas.drawArc(mArcRectF, 90 + angle + targetDegree, 360 - mDegree - targetDegree, false, mArcPaint)
            //绘制完成部分
            mArcPaint.color = mProgressColor
            canvas.drawArc(mArcRectF, 90 + angle, targetDegree, false, mArcPaint)
            //绘制文字
            canvas.rotate(180 + angle, x, y)
            var i = 0
            while (i <= count + 1) {
                val text = i.toString()
                val textBound = Rect()
                mTextPaint.getTextBounds(text, 0, text.length, textBound)
                canvas.drawText(text, x, mBoardWidth + mBoardWidth / 2 + textBound.height() * 1.5f, mTextPaint)
                canvas.rotate(mTickDensity * 10, x, y)
                i += 10
            }
            //绘制自定义线帽 while多旋转了20度
            val pointPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            pointPaint.strokeWidth = mBoardWidth.toFloat()
            pointPaint.color = Color.WHITE
            canvas.rotate(180 + mTickDensity * target - angle - 20, x, y)
            canvas.drawCircle(x, (mBoardWidth).toFloat(), mBoardWidth.toFloat() / 3, pointPaint)

        } else { //钟表模式
            if (mBgShow) canvas.drawArc(mArcRectF, 90 + angle, 360 - mDegree, false, mArcPaint)
            canvas.rotate(180 + angle, x, y)
            for (i in 0 until count) {
                if (i < target) {
                    mLinePaint.color = mProgressColor
                } else {
                    mLinePaint.color = mUnmProgressColor
                }
                canvas.drawLine(x, mBoardWidth + mBoardWidth / 2.toFloat(), x, mBoardWidth - mBoardWidth / 2.toFloat(), mLinePaint)
                canvas.rotate(mTickDensity, x, y)
            }
        }
        canvas.restore()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mArcRectF = RectF(mBoardWidth.toFloat(),
                mBoardWidth.toFloat(),
                mRadius * 2 - mBoardWidth,
                mRadius * 2 - mBoardWidth)
    }

    /**
     * dp 2 px
     *
     * @param dpVal
     */
    private fun dp2px(dpVal: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal.toFloat(), resources.displayMetrics).toInt()
    }

    interface OnCenterDraw {
        /**
         * @param canvas
         * @param rectF       圆弧的Rect
         * @param x           圆弧的中心x
         * @param y           圆弧的中心y
         * @param strokeWidth 圆弧的边框宽度
         * @param progress    当前进度
         */
        fun draw(canvas: Canvas?, rectF: RectF?, x: Float, y: Float, strokeWidth: Float, progress: Int)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (mCenterBitmap != null) {
            mCenterBitmap!!.recycle()
            mCenterBitmap = null
        }
    }

    fun startRun(progress: Int, itemDuration: Long) {
        val va = ValueAnimator.ofInt(0, progress)
        animTim = itemDuration * progress
        va.duration = animTim
        va.addUpdateListener { valueAnimator: ValueAnimator ->
            val p = valueAnimator.animatedValue as Int
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                this@CArcProgress.setProgress(p, true)
            } else {
                this.progress = p
            }
        }
        va.start()
    }

    companion object {
        const val STYLE_TICK = 1
        const val STYLE_ARC = 0
    }

    init {
        val attributes = getContext().obtainStyledAttributes(
                attrs, R.styleable.CArcProgress)
        mBoardWidth = attributes.getDimensionPixelOffset(R.styleable.CArcProgress_borderWidth, DEFAULT_LINEHEIGHT)
        mUnmProgressColor = attributes.getColor(R.styleable.CArcProgress_unprogresColor, DEFAULT_mUnmProgressColor)
        mProgressColor = attributes.getColor(R.styleable.CArcProgress_progressColor, DEFAULT_mProgressColor)
        mTickWidth = attributes.getDimensionPixelOffset(R.styleable.CArcProgress_tickWidth, DEFAULT_mTickWidth)
        mTickDensity = attributes.getFloat(R.styleable.CArcProgress_tickDensity, DEFAULT_DENSITY.toFloat())
        mRadius = attributes.getDimensionPixelOffset(R.styleable.CArcProgress_radius, DEFAULT_mRadius).toFloat()
        mArcbgColor = attributes.getColor(R.styleable.CArcProgress_arcbgColor, DEFAULT_mUnmProgressColor)
        mTickDensity = Math.max(Math.min(mTickDensity, MAX_DENSITY.toFloat()), MIN_DENSITY.toFloat())
        mBgShow = attributes.getBoolean(R.styleable.CArcProgress_bgShow, false)
        mDegree = attributes.getFloat(R.styleable.CArcProgress_degree, DEFAULT_OFFSETDEGREE)
        mStyleProgress = attributes.getInt(R.styleable.CArcProgress_progressStyle, STYLE_ARC)
        val capRound = attributes.getBoolean(R.styleable.CArcProgress_arcCapRound, false)
        mArcPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mArcPaint.color = mArcbgColor
        if (capRound) mArcPaint.strokeCap = Paint.Cap.ROUND
        mArcPaint.strokeWidth = mBoardWidth.toFloat()
        mArcPaint.style = Paint.Style.STROKE
        mLinePaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mLinePaint.strokeWidth = mTickWidth.toFloat()
        mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.textSize = dp2px(8).toFloat()
        mTextPaint.color = Color.parseColor("#999999")
    }
}