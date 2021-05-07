package com.scanlibrary

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.scanlibrary.PolygonViewA
import java.util.*

/**
 * Created by jhansi on 28/03/15.
 */
class PolygonViewA : FrameLayout {
    private lateinit var contextss: Context
    private var paint: Paint? = null
    private var pointer1: ImageView? = null
    private var pointer2: ImageView? = null
    private var pointer3: ImageView? = null
    private var pointer4: ImageView? = null
    private var midPointer13: ImageView? = null
    private var midPointer12: ImageView? = null
    private var midPointer34: ImageView? = null
    private var midPointer24: ImageView? = null
    private var polygonView: PolygonViewA? = null
    private val TAG = PolygonViewA::class.java.simpleName


    private var tl: PointF = PointF()
    private var tr: PointF = PointF()
    private var br: PointF = PointF()
    private var bl: PointF = PointF()
    private val path: Path = Path()

    private val rectPaint = Paint()
    private val circlePaint = Paint()

    constructor(context: Context) : super(context) {
        this.contextss = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.contextss = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.contextss = context
        init()
    }

    private fun init() {
        Log.e(TAG, "init")
        polygonView = this
        pointer1 = getImageView(0, 0)
        pointer2 = getImageView(width, 0)
        pointer3 = getImageView(0, height)
        pointer4 = getImageView(width, height)
        midPointer13 = getImageView(0, height / 2)
        //        midPointer13.setOnTouchListener(new MidPointTouchListenerImpl(pointer1, pointer3));
        midPointer12 = getImageView(0, width / 2)
        //        midPointer12.setOnTouchListener(new MidPointTouchListenerImpl(pointer1, pointer2));
        midPointer34 = getImageView(0, height / 2)
        //        midPointer34.setOnTouchListener(new MidPointTouchListenerImpl(pointer3, pointer4));
        midPointer24 = getImageView(0, height / 2)
        //        midPointer24.setOnTouchListener(new MidPointTouchListenerImpl(pointer2, pointer4));
        addView(pointer1)
        addView(pointer2)
        addView(midPointer13)
        addView(midPointer12)
        addView(midPointer34)
        addView(midPointer24)
        addView(pointer3)
        addView(pointer4)
        initPaint()

        /*--------------------------------------------------------------------------------------------------------------------------------------*/

        Log.e(TAG,"called")
        rectPaint.color = Color.BLUE
        rectPaint.isAntiAlias = true
        rectPaint.isDither = true
        rectPaint.strokeWidth = 6F
        rectPaint.style = Paint.Style.STROKE
        rectPaint.strokeJoin = Paint.Join.ROUND    // set the join to round you want
        rectPaint.strokeCap = Paint.Cap.ROUND      // set the paint cap to round too
        rectPaint.pathEffect = CornerPathEffect(10f)

        circlePaint.color = Color.LTGRAY
        circlePaint.isDither = true
        circlePaint.isAntiAlias = true
        circlePaint.strokeWidth = 4F
        circlePaint.style = Paint.Style.STROKE









    }

    override fun attachViewToParent(child: View, index: Int, params: ViewGroup.LayoutParams) {
        Log.e(TAG, "attachViewToParent")
        super.attachViewToParent(child, index, params)
    }

    private fun initPaint() {
        paint = Paint()
        paint!!.color = resources.getColor(R.color.blue)
        paint!!.strokeWidth = 2f
        paint!!.isAntiAlias = true
    }
//
//    var points: Map<Int, PointF>
//        get() {
//            Log.e(TAG, "getPoints")
//            val points: MutableList<PointF> = ArrayList()
//            points.add(PointF(pointer1!!.x, pointer1!!.y))
//            points.add(PointF(pointer2!!.x, pointer2!!.y))
//            points.add(PointF(pointer3!!.x, pointer3!!.y))
//            points.add(PointF(pointer4!!.x, pointer4!!.y))
//            return getOrderedPoints(points)
//        }

    fun getPoints(): Map<Int?, PointF?>? {
        Log.e(TAG, "getPoints")
        val points: MutableList<PointF> = ArrayList()
        points.add(PointF(pointer1!!.x, pointer1!!.y))
        points.add(PointF(pointer2!!.x, pointer2!!.y))
        points.add(PointF(pointer3!!.x, pointer3!!.y))
        points.add(PointF(pointer4!!.x, pointer4!!.y))
        return getOrderedPoints(points)
    }


    fun setPoints(pointFMap: Map<Int?, PointF?>,tempBitmap:Bitmap) {
        Log.e(TAG, "setPoints: " + pointFMap.size)
        if (pointFMap.size == 4) {
            setPointsCoordinates(pointFMap,tempBitmap)
        }
    }


//        set(pointFMap,bitmap) {
//            Log.e(TAG, "setPoints: " + pointFMap.size)
//            if (pointFMap.size == 4) {
//                setPointsCoordinates(pointFMap,bitmap)
//            }
//        }

    fun getOrderedPoints(points: List<PointF>): Map<Int?, PointF?>? {
        Log.e(TAG, "getOrderedPoints")
        val centerPoint = PointF()
        val size = points.size
        for (pointF in points) {
            centerPoint.x += pointF.x / size
            centerPoint.y += pointF.y / size
        }
        val orderedPoints: MutableMap<Int?, PointF?> = HashMap()
        for (pointF in points) {
            var index = -1
            if (pointF.x < centerPoint.x && pointF.y < centerPoint.y) {
                index = 0
            } else if (pointF.x > centerPoint.x && pointF.y < centerPoint.y) {
                index = 1
            } else if (pointF.x < centerPoint.x && pointF.y > centerPoint.y) {
                index = 2
            } else if (pointF.x > centerPoint.x && pointF.y > centerPoint.y) {
                index = 3
            }
            orderedPoints[index] = pointF
        }
        return orderedPoints
    }

    private fun setPointsCoordinates(corners: Map<Int?, PointF?>,bitmap: Bitmap) {
        Log.e(TAG, "setPointsCoordinates")
//        pointer1!!.x = pointFMap[0]!!.x
//        pointer1!!.y = pointFMap[0]!!.y
//        pointer2!!.x = pointFMap[1]!!.x
//        pointer2!!.y = pointFMap[1]!!.y
//        pointer3!!.x = pointFMap[2]!!.x
//        pointer3!!.y = pointFMap[2]!!.y
//        pointer4!!.x = pointFMap[3]!!.x
//        pointer4!!.y = pointFMap[3]!!.y




        tl = corners[0] ?: SourceManager.defaultTl
        tr = corners[1] ?: SourceManager.defaultTr
        br = corners[3] ?: SourceManager.defaultBl
        bl = corners[2] ?: SourceManager.defaultBr

        path.reset()
        path.moveTo(tl.x.toFloat(), tl.y.toFloat())
        path.lineTo(tr.x.toFloat(), tr.y.toFloat())
        path.lineTo(br.x.toFloat(), br.y.toFloat())
        path.lineTo(bl.x.toFloat(), bl.y.toFloat())
        path.close()
        invalidate()




        val displayMetrics = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
        //exclude status bar height
        bitmap.width.toDouble()
        val statusBarHeight = getStatusBarHeight(context)
        ratioX = bitmap.width.toDouble()?.div(displayMetrics.widthPixels) ?: 1.0
        ratioY = bitmap.height.toDouble()?.div(displayMetrics.heightPixels - statusBarHeight) ?: 1.0
        resize()
        movePoints()




    }

    private fun movePoints() {
        path.reset()
        path.moveTo(tl.x.toFloat(), tl.y.toFloat())
        path.lineTo(tr.x.toFloat(), tr.y.toFloat())
        path.lineTo(br.x.toFloat(), br.y.toFloat())
        path.lineTo(bl.x.toFloat(), bl.y.toFloat())
        path.close()
        invalidate()
    }

    private fun getStatusBarHeight(pContext: Context): Int {
        val resources = pContext.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }


    private var ratioX: Double = 1.0
    private var ratioY: Double = 1.0

    private fun resize() {
        tl.x = tl.x.div(ratioX).toFloat()
        tl.y = tl.y.div(ratioY).toFloat()
        tr.x = tr.x.div(ratioX).toFloat()
        tr.y = tr.y.div(ratioY).toFloat()
        br.x = br.x.div(ratioX).toFloat()
        br.y = br.y.div(ratioY).toFloat()
        bl.x = bl.x.div(ratioX).toFloat()
        bl.y = bl.y.div(ratioY).toFloat()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
//        Log.e(TAG, "dispatchDraw")
//        canvas.drawLine(pointer1!!.x + pointer1!!.width / 2, pointer1!!.y + pointer1!!.height / 2, pointer3!!.x + pointer3!!.width / 2, pointer3!!.y + pointer3!!.height / 2, paint)
//        canvas.drawLine(pointer1!!.x + pointer1!!.width / 2, pointer1!!.y + pointer1!!.height / 2, pointer2!!.x + pointer2!!.width / 2, pointer2!!.y + pointer2!!.height / 2, paint)
//        canvas.drawLine(pointer2!!.x + pointer2!!.width / 2, pointer2!!.y + pointer2!!.height / 2, pointer4!!.x + pointer4!!.width / 2, pointer4!!.y + pointer4!!.height / 2, paint)
//        canvas.drawLine(pointer3!!.x + pointer3!!.width / 2, pointer3!!.y + pointer3!!.height / 2, pointer4!!.x + pointer4!!.width / 2, pointer4!!.y + pointer4!!.height / 2, paint)
//        midPointer13!!.x = pointer3!!.x - (pointer3!!.x - pointer1!!.x) / 2
//        midPointer13!!.y = pointer3!!.y - (pointer3!!.y - pointer1!!.y) / 2
//        midPointer24!!.x = pointer4!!.x - (pointer4!!.x - pointer2!!.x) / 2
//        midPointer24!!.y = pointer4!!.y - (pointer4!!.y - pointer2!!.y) / 2
//        midPointer34!!.x = pointer4!!.x - (pointer4!!.x - pointer3!!.x) / 2
//        midPointer34!!.y = pointer4!!.y - (pointer4!!.y - pointer3!!.y) / 2
//        midPointer12!!.x = pointer2!!.x - (pointer2!!.x - pointer1!!.x) / 2
//        midPointer12!!.y = pointer2!!.y - (pointer2!!.y - pointer1!!.y) / 2


        canvas?.drawPath(path, rectPaint)
            canvas?.drawCircle(tl.x.toFloat(), tl.y.toFloat(), 20F, circlePaint)
            canvas?.drawCircle(tr.x.toFloat(), tr.y.toFloat(), 20F, circlePaint)
            canvas?.drawCircle(bl.x.toFloat(), bl.y.toFloat(), 20F, circlePaint)
            canvas?.drawCircle(br.x.toFloat(), br.y.toFloat(), 20F, circlePaint)



    }

    private fun getImageView(x: Int, y: Int): ImageView {
        Log.e(TAG, "getImageView x =$x y = $y")
        val imageView = ImageView(contextss)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

//        LayoutParams layoutParams = new LayoutParams(200,200);
        imageView.layoutParams = layoutParams
        //        imageView.setPadding(60,60,60,60);
        imageView.setImageResource(R.drawable.circle_wala)
        imageView.x = x.toFloat()
        imageView.y = y.toFloat()
        //        imageView.setOnTouchListener(new TouchListenerImpl());
        return imageView

    }

    //    private class MidPointTouchListenerImpl implements OnTouchListener {
    //
    //        PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
    //        PointF StartPT = new PointF(); // Record Start Position of 'img'
    //
    //        private ImageView mainPointer1;
    //        private ImageView mainPointer2;
    //
    //        public MidPointTouchListenerImpl(ImageView mainPointer1, ImageView mainPointer2) {
    //            this.mainPointer1 = mainPointer1;
    //            this.mainPointer2 = mainPointer2;
    //        }
    //
    //        @Override
    //        public boolean onTouch(View v, MotionEvent event) {
    //            int eid = event.getAction();
    //            Log.e(TAG,"onTouch MidPointTouchListenerImpl");
    //            switch (eid) {
    //                case MotionEvent.ACTION_MOVE:
    //                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
    //
    //                    if (Math.abs(mainPointer1.getX() - mainPointer2.getX()) > Math.abs(mainPointer1.getY() - mainPointer2.getY())) {
    //                        if (((mainPointer2.getY() + mv.y + v.getHeight() < polygonView.getHeight()) && (mainPointer2.getY() + mv.y > 0))) {
    //                            v.setX((int) (StartPT.y + mv.y));
    //                            StartPT = new PointF(v.getX(), v.getY());
    //                            mainPointer2.setY((int) (mainPointer2.getY() + mv.y));
    //                        }
    //                        if (((mainPointer1.getY() + mv.y + v.getHeight() < polygonView.getHeight()) && (mainPointer1.getY() + mv.y > 0))) {
    //                            v.setX((int) (StartPT.y + mv.y));
    //                            StartPT = new PointF(v.getX(), v.getY());
    //                            mainPointer1.setY((int) (mainPointer1.getY() + mv.y));
    //                        }
    //                    } else {
    //                        if ((mainPointer2.getX() + mv.x + v.getWidth() < polygonView.getWidth()) && (mainPointer2.getX() + mv.x > 0)) {
    //                            v.setX((int) (StartPT.x + mv.x));
    //                            StartPT = new PointF(v.getX(), v.getY());
    //                            mainPointer2.setX((int) (mainPointer2.getX() + mv.x));
    //                        }
    //                        if ((mainPointer1.getX() + mv.x + v.getWidth() < polygonView.getWidth()) && (mainPointer1.getX() + mv.x > 0)) {
    //                            v.setX((int) (StartPT.x + mv.x));
    //                            StartPT = new PointF(v.getX(), v.getY());
    //                            mainPointer1.setX((int) (mainPointer1.getX() + mv.x));
    //                        }
    //                    }
    //                    Log.e(TAG,"action_move");
    //
    //                    break;
    //                case MotionEvent.ACTION_DOWN:
    //                    DownPT.x = event.getX();
    //                    DownPT.y = event.getY();
    //                    StartPT = new PointF(v.getX(), v.getY());
    //                    Log.e(TAG,"ACTION_DOWN");
    //
    //                    break;
    //                case MotionEvent.ACTION_UP:
    //                    int color = 0;
    //                    if (isValidShape(getPoints())) {
    //                        color = getResources().getColor(R.color.blue);
    //                    } else {
    //                        color = getResources().getColor(R.color.orange);
    //                    }
    //                    paint.setColor(color);
    //                    Log.e(TAG,"ACTION_UP");
    //
    //
    //                    break;
    //                default:
    //                    break;
    //            }
    //            polygonView.invalidate();
    //            return true;
    //        }
    //    }
    var DownPT = PointF() // Record Mouse Position When Pressed Down
    var StartPT = PointF() // Record Start Position of 'img'
    override fun onTouchEvent(event: MotionEvent): Boolean {
//          Log.e(TAG,"onTouchEvent");
//        return super.onTouchEvent(event);
        val eid = event.action
        Log.e(TAG, "onTouch TouchListenerImpl$eid")
        when (eid) {
            MotionEvent.ACTION_MOVE -> {
                Log.e(TAG, ": " + event.x + " , " + event.y)
                val mv = PointF(event.x - DownPT.x, event.y - DownPT.y)
            }
        }
        return super.onTouchEvent(event)
    }

    fun isValidShape(pointFMap: Map<Int?, PointF?>): Boolean {
        return pointFMap.size == 4
    } //    private class TouchListenerImpl implements OnTouchListener {
    //
    //        PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
    //        PointF StartPT = new PointF(); // Record Start Position of 'img'
    //
    //        @Override
    //        public boolean onTouch(View v, MotionEvent event) {
    //            int eid = event.getAction();
    //            Log.e(TAG,"onTouch TouchListenerImpl"+eid);
    //
    //            switch (eid) {
    //                //2
    //                case MotionEvent.ACTION_MOVE:
    //                    Log.e(TAG,": "+event.getX()+" , "+event.getY());
    //                    PointF mv = new PointF(event.getX() - DownPT.x, event.getY() - DownPT.y);
    //                    if (((StartPT.x + mv.x + v.getWidth()) < polygonView.getWidth() && (StartPT.y + mv.y + v.getHeight() < polygonView.getHeight())) && ((StartPT.x + mv.x) > 0 && StartPT.y + mv.y > 0)) {
    //                        v.setX((int) (StartPT.x + mv.x));
    //                        v.setY((int) (StartPT.y + mv.y));
    //                        StartPT = new PointF(v.getX(), v.getY());
    //                    }
    //                    break;
    //                    //0
    //                case MotionEvent.ACTION_DOWN:
    //                    DownPT.x = event.getX();
    //                    DownPT.y = event.getY();
    //                    StartPT = new PointF(v.getX(), v.getY());
    //                    break;
    //                case MotionEvent.ACTION_UP:
    //                    int color = 0;
    //                    if (isValidShape(getPoints())) {
    //                        color = getResources().getColor(R.color.blue);
    //                    } else {
    //                        color = getResources().getColor(R.color.orange);
    //                    }
    //                    paint.setColor(color);
    //                    break;
    //                default:
    //                    break;
    //            }
    //            polygonView.invalidate();
    //            return true;
    //        }
    //
    //    }
}