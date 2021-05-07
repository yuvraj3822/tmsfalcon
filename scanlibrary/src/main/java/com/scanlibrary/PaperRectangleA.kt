package com.scanlibrary

import android.app.Activity
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.View
import java.util.*

class PaperRectangleA : View {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributes: AttributeSet) : super(context, attributes)
    constructor(context: Context, attributes: AttributeSet, defTheme: Int) : super(context, attributes, defTheme)

    private val rectPaint = Paint()
    private val circlePaint = Paint()
    private var ratioX: Double = 1.0
    private var ratioY: Double = 1.0
    private var tl: PointF = PointF()
    private var tr: PointF = PointF()
    private var br: PointF = PointF()
    private var bl: PointF = PointF()
    private val path: Path = Path()
    private var point2Move = PointF()
    private var cropMode = false
    private var latestDownX = 0.0F
    private var latestDownY = 0.0F

    private var TAG = PaperRectangleA::class.java.name

    init {

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

//    fun onCornersDetected(corners: Corners) {
//        ratioX = corners.size.width.div(measuredWidth)
//        ratioY = corners.size.height.div(measuredHeight)
//        tl = corners.corners[0] ?: PointF()
//        tr = corners.corners[1] ?: PointF()
//        br = corners.corners[2] ?: PointF()
//        bl = corners.corners[3] ?: PointF()
//        resize()
//        path.reset()
//        path.moveTo(tl.x.toFloat(), tl.y.toFloat())
//        path.lineTo(tr.x.toFloat(), tr.y.toFloat())
//        path.lineTo(br.x.toFloat(), br.y.toFloat())
//        path.lineTo(bl.x.toFloat(), bl.y.toFloat())
//        path.close()
//        invalidate()
//    }




    fun onCornersDetecteds(corners:Map<Int, PointF?>) {
//        ratioX = corners.size.width.div(measuredWidth)
//        ratioY = corners.size.height.div(measuredHeight)

        tl = corners[0]!!
        tr = corners[1]!!
        br = corners[3]!!
        bl = corners[2]!!
        resize()
        path.reset()
        path.moveTo(tl.x.toFloat(), tl.y.toFloat())
        path.lineTo(tr.x.toFloat(), tr.y.toFloat())
        path.lineTo(br.x.toFloat(), br.y.toFloat())
        path.lineTo(bl.x.toFloat(), bl.y.toFloat())
        path.close()
        invalidate()

    }


    fun setPoints(pointFMap: Map<Int?, PointF?>,tempBitmap:Bitmap) {
        Log.e(TAG, "setPoints: " + pointFMap.size)
        if (pointFMap.size == 4) {
            setPointsCoordinates(pointFMap,tempBitmap)
        }
    }


    private fun setPointsCoordinates(corners: Map<Int?, PointF?>,bitmap: Bitmap) {
        Log.e(TAG, "setPointsCoordinates")
//        cropMode = true
//        tl = corners[0]!!
//        tr = corners[1]!!
//        br = corners[3]!!
//        bl = corners[2]!!
//        resize()
//        path.reset()
//        path.moveTo(tl.x.toFloat(), tl.y.toFloat())
//        path.lineTo(tr.x.toFloat(), tr.y.toFloat())
//        path.lineTo(br.x.toFloat(), br.y.toFloat())
//        path.lineTo(bl.x.toFloat(), bl.y.toFloat())
//        path.close()
//        invalidate()


        cropMode = true
        tl = corners[0] ?: SourceManager.defaultTl




        tr = corners[1] ?: SourceManager.defaultTr
        br = corners[3] ?: SourceManager.defaultBl
        bl = corners[2] ?: SourceManager.defaultBr





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


    fun isValidShape(pointFMap: Map<Int?, PointF?>): Boolean {
        return pointFMap.size == 4
    }
    fun onCornersNotDetected() {
        path.reset()
        invalidate()
    }


    fun getOrderedPoints(points: List<PointF>): Map<Int, PointF>? {
        Log.e(TAG,"getOrderedPoints")
        val centerPoint = PointF()
        val size = points.size
        for (pointF in points) {
            centerPoint.x += pointF.x / size
            centerPoint.y += pointF.y / size
        }
        val orderedPoints: MutableMap<Int, PointF> = HashMap()
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


    fun onCorners2Crops(corners:Map<Int, PointF?>,bitmap: Bitmap) {



        cropMode = true
        tl = corners[0] ?: SourceManager.defaultTl




        tr = corners[1] ?: SourceManager.defaultTr
        br = corners[3] ?: SourceManager.defaultBl
        bl = corners[2] ?: SourceManager.defaultBr





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

//    fun onCorners2Crop(corners: Corners?, size: Size?) {
//
//        cropMode = true
//        tl = corners?.corners?.get(0) ?: SourceManager.defaultTl
//        tr = corners?.corners?.get(1) ?: SourceManager.defaultTr
//        br = corners?.corners?.get(2) ?: SourceManager.defaultBr
//        bl = corners?.corners?.get(3) ?: SourceManager.defaultBl
//        val displayMetrics = DisplayMetrics()
//        (context as Activity).windowManager.defaultDisplay.getMetrics(displayMetrics)
//        //exclude status bar height
//        val statusBarHeight = getStatusBarHeight(context)
//        ratioX = size?.width?.div(displayMetrics.widthPixels) ?: 1.0
//        ratioY = size?.height?.div(displayMetrics.heightPixels - statusBarHeight) ?: 1.0
//        resize()
//        movePoints()
//    }

    fun getCorners2Crop(): List<PointF> {
        reverseSize()
        return listOf(tl, tr, br, bl)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        Log.e(TAG,"getOrderedPoints")
        canvas?.drawPath(path, rectPaint)
        if (cropMode) {
            canvas?.drawCircle(tl.x.toFloat(), tl.y.toFloat(), 20F, circlePaint)
            canvas?.drawCircle(tr.x.toFloat(), tr.y.toFloat(), 20F, circlePaint)
            canvas?.drawCircle(bl.x.toFloat(), bl.y.toFloat(), 20F, circlePaint)
            canvas?.drawCircle(br.x.toFloat(), br.y.toFloat(), 20F, circlePaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        Log.e(TAG,"touch")

        if (!cropMode) {
            return false
        }
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                latestDownX = event.x
                latestDownY = event.y
                calculatePoint2Move(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE -> {
                point2Move.x = (event.x - latestDownX) + point2Move.x
                point2Move.y = (event.y - latestDownY) + point2Move.y
                movePoints()
                latestDownY = event.y
                latestDownX = event.x

            }
        }
        return true
    }

    private fun calculatePoint2Move(downX: Float, downY: Float) {
        val points = listOf(tl, tr, br, bl)
        point2Move = points.minBy { Math.abs((it.x - downX).times(it.y - downY)) } ?: tl

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

    private fun reverseSize() {
        tl.x = tl.x.times(ratioX).toFloat()
        tl.y = tl.y.times(ratioY).toFloat()
        tr.x = tr.x.times(ratioX).toFloat()
        tr.y = tr.y.times(ratioY).toFloat()
        br.x = br.x.times(ratioX).toFloat()
        br.y = br.y.times(ratioY).toFloat()
        bl.x = bl.x.times(ratioX).toFloat()
        bl.y = bl.y.times(ratioY).toFloat()
    }

    private fun getNavigationBarHeight(pContext: Context): Int {
        val resources = pContext.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

    private fun getStatusBarHeight(pContext: Context): Int {
        val resources = pContext.resources
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }

}