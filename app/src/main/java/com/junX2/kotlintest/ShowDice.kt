package com.junX2.kotlintest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.content.Context
import android.widget.LinearLayout
import android.widget.TextView

import android.annotation.TargetApi
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat.getSystemService

import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.*
import android.view.animation.*
import android.widget.Scroller
import android.os.Vibrator




class ShowDice : Service() {

    var centerX: Float = 0.toFloat()
    var centerY: Float = 0.toFloat()
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        var windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        var displayMetrics=DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        var linearLayout = LinearLayout(this)
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayout.setBackgroundColor(Color.argb(0, 0, 0, 0))
        linearLayout.layoutParams=layoutParams
        var custom3DView=Custom3DView(this)
        var textView1=TextView(this)
        textView1.text=intent?.getStringExtra("txt")
        textView1.setBackgroundColor(Color.argb(80, 255, 128, 128))
        textView1.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
        textView1.gravity=Gravity.CENTER


        var textView2=TextView(this)
        textView2.text=intent?.getStringExtra("txt1")
        textView2.setBackgroundColor(Color.argb(80, 128, 255, 128))
        textView2.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
        textView2.gravity=Gravity.CENTER
        var textView3=TextView(this)
        textView3.text=intent?.getStringExtra("txt2")
        textView3.setBackgroundColor(Color.argb(80, 128, 128, 255))
        textView3.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
        textView3.gravity=Gravity.CENTER
        var textView4=TextView(this)
        textView4.text=intent?.getStringExtra("txt3")
        textView4.setBackgroundColor(Color.argb(80, 128, 128, 128))
        textView4.textAlignment=TextView.TEXT_ALIGNMENT_CENTER
        textView4.gravity=Gravity.CENTER
        custom3DView.addView(textView1)
        custom3DView.addView(textView2)
        custom3DView.addView(textView3)
        custom3DView.addView(textView4)

        linearLayout.addView(custom3DView)
        val params = WindowManager.LayoutParams(
            displayMetrics.widthPixels/2,
            80,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.x = 0
        params.y = 0
        params.gravity = Gravity.TOP
        windowManager.addView(linearLayout, params)
        return super.onStartCommand(intent, flags, startId)
    }
}

public class Custom3DView(context: Context?,attrs: AttributeSet?,defStyleAttr: Int)
    : ViewGroup(context,attrs,defStyleAttr),SensorEventListener {
    private var mCamera: Camera? = null
    private var mMatrix: Matrix? = null
    private var mStartScreen: Int = 1
    private var mDownY: Float = 0f
    private val standerSpeed: Int = 2000
    private var mCurScreen: Int = 1
    private var mHeight: Int = 0
    private var mTracker: VelocityTracker? = null
    private var mScroller: Scroller? = null
    private var angle: Float = 90f

    private val STATE_PRE: Int = 0
    private val STATE_NEXT: Int = 1
    private val STATE_NORMAL: Int = 2
    private var STATE: Int = -1
    private var resistance: Float = 1.0f


    var lastUpdate: Long = 0
    var last_x: Float = 0.toFloat()
    var last_y: Float = 0.toFloat()
    var last_z: Float = 0.toFloat()
    val SHAKE_THRESHOLD = 600
    var senSensorManager: SensorManager? = null
    var senAccelerometer: Sensor? = null
    var bb = 0
    var vibrator: Vibrator? = null

    init {
        senSensorManager = getSystemService(context as Context, SensorManager::class.java)
        senAccelerometer = senSensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager?.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        vibrator = getSystemService(context, Vibrator::class.java)

    }

    override fun onSensorChanged(event: SensorEvent?) {
        val mySensor = event?.sensor
        if (mySensor?.getType() == Sensor.TYPE_ACCELEROMETER) {
            val x = event?.values[0]
            val y = event?.values[1]
            val z = event?.values[2]
            val curTime = System.currentTimeMillis()
            if (curTime - lastUpdate > 100) {
                val diffTime = curTime - lastUpdate
                lastUpdate = curTime
                val speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
                if (speed > SHAKE_THRESHOLD) {
                    bb++
                    if (bb % 3 === 0)
                        randomRoll()
                }
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    init {
        _init()
        randomRoll()

    }

    constructor(context: Context?) : this(context, null) {}
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0) {}

    private fun _init() {
        mCamera = Camera()
        mMatrix = Matrix()
        if (mScroller == null) {
            mScroller = Scroller(getContext(), LinearInterpolator())
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun randomRoll() {
        Thread {
            var count = (Math.random() * 10).toInt()+5;
            while (count-- != 0) {
                handler?.post(runnable)
                vibrator?.vibrate(VibrationEffect.createOneShot(100,100));
                Thread.sleep(100);
            }
        }.start()
    }

    override fun generateDefaultLayoutParams(): LayoutParams {
        return MarginLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
    }

    override fun generateLayoutParams(p: LayoutParams): LayoutParams {
        return MarginLayoutParams(p)
    }

    override fun generateLayoutParams(attrs: AttributeSet?): LayoutParams {
        return MarginLayoutParams(getContext(), attrs)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        var measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
        var params: MarginLayoutParams = this.getLayoutParams() as MarginLayoutParams;
        var childWidthMeasureSpec =
            MeasureSpec.makeMeasureSpec(measureWidth - (params.leftMargin + params.rightMargin), MeasureSpec.EXACTLY);
        var childHeightMeasureSpec =
            MeasureSpec.makeMeasureSpec(measureHeight - (params.topMargin + params.bottomMargin), MeasureSpec.EXACTLY);
        measureChildren(childWidthMeasureSpec, childHeightMeasureSpec);
        mHeight = getMeasuredHeight();
        scrollTo(0, mStartScreen * mHeight);
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var params = this.getLayoutParams() as MarginLayoutParams
        var childTop = 0

        for (i in 0..getChildCount()) {
            var child = getChildAt(i)
            if (child != null && child.getVisibility() != GONE) {
                if (i == 0) {
                    childTop += params.topMargin
                }

                child.layout(
                    params.leftMargin,
                    childTop,
                    child.measuredWidth + params.leftMargin,
                    childTop + child.measuredHeight
                )
                childTop += child.measuredHeight
            }
        }
    }

    override fun dispatchDraw(canvas: Canvas?) {
        for (i in 0..getChildCount()) {
            drawScreen(canvas as Canvas, i, drawingTime)
        }
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {

        return true
    }

    override fun performClick(): Boolean {
        return super.performClick()
    }

    var runnable: Runnable = Runnable {

        toNextPager()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (mTracker == null) {
            mTracker = VelocityTracker.obtain()
        }
        mTracker?.addMovement(event)
        var y = event?.y
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (!(mScroller?.isFinished as Boolean)) {
                    mScroller?.finalY = mScroller?.currY as Int
                    mScroller?.abortAnimation()
                    scrollTo(0, scrollY)
                } else {
                    performClick()
                }
                mDownY = y as Float
                randomRoll()
            }
//            MotionEvent.ACTION_MOVE->{
//                var disY=mDownY.toInt()-(y as Float).toInt()
//                mDownY=y as Float
//                if (mScroller?.isFinished as Boolean){
//                    recycleMove(disY)
//                }
//            }
//            MotionEvent.ACTION_UP->{
//                mTracker?.computeCurrentVelocity(1000)
//                var velocity=mTracker?.yVelocity
//                if (velocity != null) {
//                    if (velocity>standerSpeed|| ((scrollY + mHeight / 2) / mHeight < mStartScreen)){
//                        STATE =  STATE_PRE
//                    }else if (velocity<-standerSpeed || ((scrollY + mHeight / 2) / mHeight > mStartScreen)){
//                        STATE =  STATE_NEXT
//                    }else{
//                        STATE =  STATE_NORMAL
//                    }
//                    changeByState()
//                    if (mTracker != null)
//                    {
//                        mTracker?.recycle()
//                        mTracker = null
//                    }
//                }
//            }

        }
        return true
    }

    private fun changeByState() {
        when (STATE) {
            STATE_NORMAL -> toNormalPager()
            STATE_PRE -> toPrePager()
            STATE_NEXT -> toNextPager()
        }
        invalidate();
    }

    private fun toNormalPager() {
        var startY = 0
        var delta = 0
        var duration = 0
        STATE = STATE_NORMAL;
        startY = getScrollY();
        delta = mHeight * mStartScreen - getScrollY();
        duration = (Math.abs(delta)) * 4;
        mScroller?.startScroll(0, startY, 0, delta, duration);
    }

    private fun toPrePager() {
        var startY = 0
        var delta = 0
        var duration = 0
        STATE = STATE_PRE; //增加新的页面
        setPre(); //mScroller开始的坐标
        startY = getScrollY() + mHeight;
        setScrollY(startY); //mScroller移动的距离
        delta = -(startY - mStartScreen * mHeight);
        duration = (Math.abs(delta)) * 2;
        mScroller?.startScroll(0, startY, 0, delta, duration)
    }

    private fun toNextPager() {
        var startY = 0
        var delta = 0
        var duration = 0
        STATE = STATE_NEXT;
        setNext();
        startY = getScrollY() - mHeight;
        setScrollY(startY);
        delta = mHeight * mStartScreen - startY;
        duration = (Math.abs(delta)) * 2;
        mScroller?.startScroll(0, startY, 0, delta, 100);
    }

    private fun setNext() {
        mCurScreen = (mCurScreen + 1) % getChildCount();
        var childCount = getChildCount();
        var view = getChildAt(0);
        removeViewAt(0);
        addView(view, childCount - 1);
    }

    private fun setPre() {
        mCurScreen = ((mCurScreen - 1) + getChildCount()) % getChildCount();
        var childCount = getChildCount();
        var view = getChildAt(childCount - 1);
        removeViewAt(childCount - 1);
        addView(view, 0);
    }

    private fun recycleMove(_delta: Int) {
        var delta = _delta % mHeight;
        delta = (delta / resistance).toInt()
        if (Math.abs(delta) > mHeight / 4) return
        if (getScrollY() <= 0 && mCurScreen <= 0 && delta <= 0) {
            return; }
        if (mHeight * mCurScreen <= getScrollY() && mCurScreen == getChildCount() - 1 && delta >= 0) {
            return; }
        scrollBy(0, delta); if (getScrollY() < 8 && mCurScreen != 0) {
            setPre(); scrollBy(0, mHeight); } else if (getScrollY() > (getChildCount() - 1) * mHeight - 8) {
            setNext(); scrollBy(0, -mHeight); }
    }

    override fun computeScroll() {
        if (mScroller?.computeScrollOffset() as Boolean) {
            scrollTo(mScroller?.getCurrX() as Int, mScroller?.getCurrY() as Int);
            postInvalidate(); }
    }

    private fun drawScreen(canvas: Canvas, screen: Int, drawingTime: Long) {
        // 得到当前子View的高度
        val height = getHeight();
        val scrollHeight = screen * height;
        val scrollY = this.getScrollY(); // 偏移量不足的时
        if (scrollHeight > scrollY + height || scrollHeight + height < scrollY) {
            return; }
        val child = getChildAt(screen);
        val faceIndex = screen;
        val currentDegree = getScrollY() * (angle / getMeasuredHeight());
        val faceDegree = currentDegree - faceIndex * angle;
        if (faceDegree > 90 || faceDegree < -90) {
            return; }
        val centerY = if (scrollHeight < scrollY) {
            scrollHeight + height
        } else scrollHeight;
        val centerX = getWidth() / 2;
        val camera = mCamera;
        val matrix = mMatrix; canvas.save();
        camera?.save();
        camera?.rotateX(faceDegree);
        camera?.getMatrix(matrix);
        camera?.restore();
        matrix?.preTranslate(-centerX + 0f, 0f - centerY);
        matrix?.postTranslate(centerX + 0f, centerY + 0f);
        canvas.concat(matrix);
        drawChild(canvas, child, drawingTime);
        canvas.restore();
    }

}

class Rotate3DAnimation (fromDegree: Float, toDegree: Float, depthZ: Float, mCenterX: Float, mCenterY: Float, reverse: Boolean) : Animation(){
    private var fromDegree: Float = 0.toFloat()
    private var toDegree: Float = 0.toFloat()
    private var mCamera: Camera? = null
    //动画执行中心
    private var mCenterX: Float = 0.toFloat()
    private var mCenterY: Float = 0.toFloat()
    //z轴上的深度
    private var mDepthZ: Float = 0.toFloat()
    //由远及近   还是  由近及远
    private var mReverse: Boolean = false

    init
    {
        this.fromDegree = fromDegree
        this.toDegree = toDegree
        this.mDepthZ = depthZ
        this.mCenterX = mCenterX
        this.mCenterY = mCenterY
        this.mReverse = reverse
        println("centerX ：$mCenterX")
        println("centerY ：$mCenterY")
    }

    override fun initialize(
        width: Int, height: Int, parentWidth: Int,
        parentHeight: Int
    ) {
        super.initialize(width, height, parentWidth, parentHeight)
        mCamera = Camera()
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        super.applyTransformation(interpolatedTime, t)

        val degrees = fromDegree + (fromDegree - toDegree) * interpolatedTime
        mCamera?.save()

        if (mReverse) {
            //由近到远的效果
            mCamera?.translate(0f, 0f, mDepthZ * interpolatedTime)
        } else {
            //由远及近的效果
            mCamera?.translate(0f, 0f, mDepthZ * (1 - interpolatedTime))
        }
        val matrix = t.matrix
        mCamera?.rotateY(degrees)
        mCamera?.getMatrix(matrix)
        mCamera?.restore()

        //总体效果是   以mCenterX,mCenterY为中心执行
        //执行前平移
        matrix.preTranslate(-mCenterX, -mCenterY)
        //执行后平移
        matrix.postTranslate(mCenterX, mCenterY)
    }
}
