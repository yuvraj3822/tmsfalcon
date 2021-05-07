package com.scanlibrary

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import com.scanlibrary.PolygonViewB
import java.util.*

/**
 * Created by jhansi on 28/03/15.
 */
class PolygonViewB : FrameLayout {
    protected var contexts: Context
    private var paint: Paint? = null
    private var pointer1: ImageView? = null
    private var pointer2: ImageView? = null
    private var pointer3: ImageView? = null
    private var pointer4: ImageView? = null
    private var midPointer13: ImageView? = null
    private var midPointer12: ImageView? = null
    private var midPointer34: ImageView? = null
    private var midPointer24: ImageView? = null
    private var polygonView: PolygonViewB? = null
    private val TAG = PolygonViewB::class.java.simpleName

    constructor(context: Context) : super(context) {
        this.contexts = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        this.contexts = context
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.contexts = context
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
        midPointer13!!.setOnTouchListener(MidPointTouchListenerImpl(pointer1!!, pointer3!!));

        midPointer12 = getImageView(0, width / 2)
        midPointer12!!.setOnTouchListener(MidPointTouchListenerImpl(pointer1!!, pointer2!!))
        //
        midPointer34 = getImageView(0, height / 2)
        midPointer34!!.setOnTouchListener(MidPointTouchListenerImpl(pointer3!!, pointer4!!))

        //        midPointer34.setOnTouchListener(new MidPointTouchListenerImpl(pointer3, pointer4));
        midPointer24 = getImageView(0, height / 2)
        midPointer24!!.setOnTouchListener(MidPointTouchListenerImpl(pointer2!!, pointer4!!))

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

    var points: Map<Int, PointF>
        get() {
            Log.e(TAG, "getPoints")
            val points: MutableList<PointF> = ArrayList()
            points.add(PointF(pointer1!!.x, pointer1!!.y))
            points.add(PointF(pointer2!!.x, pointer2!!.y))
            points.add(PointF(pointer3!!.x, pointer3!!.y))
            points.add(PointF(pointer4!!.x, pointer4!!.y))
            return getOrderedPoints(points)
        }
        set(pointFMap) {
            Log.e(TAG, "setPoints: " + pointFMap.size)
            if (pointFMap.size == 4) {
                setPointsCoordinates(pointFMap)
            }
        }

    fun getOrderedPoints(points: List<PointF>): Map<Int, PointF> {
        Log.e(TAG, "getOrderedPoints")
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

    private fun setPointsCoordinates(pointFMap: Map<Int, PointF>) {
        Log.e(TAG, "setPointsCoordinates")
        pointer1!!.x = pointFMap[0]!!.x
        pointer1!!.y = pointFMap[0]!!.y
        pointer2!!.x = pointFMap[1]!!.x
        pointer2!!.y = pointFMap[1]!!.y
        pointer3!!.x = pointFMap[2]!!.x
        pointer3!!.y = pointFMap[2]!!.y
        pointer4!!.x = pointFMap[3]!!.x
        pointer4!!.y = pointFMap[3]!!.y
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        Log.e(TAG, "dispatchDraw")
        canvas.drawLine(pointer1!!.x + pointer1!!.width / 2, pointer1!!.y + pointer1!!.height / 2, pointer3!!.x + pointer3!!.width / 2, pointer3!!.y + pointer3!!.height / 2, paint!!)
        canvas.drawLine(pointer1!!.x + pointer1!!.width / 2, pointer1!!.y + pointer1!!.height / 2, pointer2!!.x + pointer2!!.width / 2, pointer2!!.y + pointer2!!.height / 2, paint!!)
        canvas.drawLine(pointer2!!.x + pointer2!!.width / 2, pointer2!!.y + pointer2!!.height / 2, pointer4!!.x + pointer4!!.width / 2, pointer4!!.y + pointer4!!.height / 2, paint!!)
        canvas.drawLine(pointer3!!.x + pointer3!!.width / 2, pointer3!!.y + pointer3!!.height / 2, pointer4!!.x + pointer4!!.width / 2, pointer4!!.y + pointer4!!.height / 2, paint!!)

        midPointer13!!.x = pointer3!!.x - (pointer3!!.x - pointer1!!.x) / 2
        midPointer13!!.y = pointer3!!.y - (pointer3!!.y - pointer1!!.y) / 2
        midPointer24!!.x = pointer4!!.x - (pointer4!!.x - pointer2!!.x) / 2
        midPointer24!!.y = pointer4!!.y - (pointer4!!.y - pointer2!!.y) / 2
        midPointer34!!.x = pointer4!!.x - (pointer4!!.x - pointer3!!.x) / 2
        midPointer34!!.y = pointer4!!.y - (pointer4!!.y - pointer3!!.y) / 2
        midPointer12!!.x = pointer2!!.x - (pointer2!!.x - pointer1!!.x) / 2
        midPointer12!!.y = pointer2!!.y - (pointer2!!.y - pointer1!!.y) / 2


//        midPointer13.setX(pointer3.getX() - ((pointer3.getX() - pointer1.getX()) / 2));


//        midPointer13.setY(pointer3.getY() - ((pointer3.getY() - pointer1.getY()) / 2));
//        midPointer24.setX(pointer4.getX() - ((pointer4.getX() - pointer2.getX()) / 2));
//        midPointer24.setY(point er4.getY() - ((pointer4.getY() - pointer2.getY()) / 2));
//        midPointer34.setX(pointer4.getX() - ((pointer4.getX() - pointer3.getX()) / 2));
//        midPointer34.setY(pointer4.getY() - ((pointer4.getY() - pointer3.getY()) / 2));
//        midPointer12.setX(pointer2.getX() - ((pointer2.getX() - pointer1.getX()) / 2));
//        midPointer12.setY(pointer2.getY() - ((pointer2.getY() - pointer1.getY()) / 2));

    }

    private fun getImageView(x: Int, y: Int): ImageView {
        Log.e(TAG, "getImageView x =$x y = $y")
        val imageView = ImageView(contexts)
        val layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

//        LayoutParams layoutParams = new LayoutParams(200,200);
        imageView.layoutParams = layoutParams
        //        imageView.setPadding(60,60,60,60);
        imageView.setImageResource(R.drawable.circle_wala)
        imageView.x = x.toFloat()
        imageView.y = y.toFloat()
//                imageView.setOnTouchListener( TouchListenerImpl());
        return imageView
    }

    private inner class MidPointTouchListenerImpl(private val mainPointer1: ImageView, private val mainPointer2: ImageView) : OnTouchListener {
        var DownPT = PointF() // Record Mouse Position When Pressed Down
        var StartPT = PointF() // Record Start Position of 'img'
        override fun onTouch(v: View, event: MotionEvent): Boolean {
            val eid = event.action
            Log.e(TAG, "onTouch MidPointTouchListenerImpl")
            when (eid) {
                MotionEvent.ACTION_MOVE -> {
                    val mv = PointF(event.x - DownPT.x, event.y - DownPT.y)
                    if (Math.abs(mainPointer1.x - mainPointer2.x) > Math.abs(mainPointer1.y - mainPointer2.y)) {
                        if (mainPointer2.y + mv.y + v.height < polygonView!!.height && mainPointer2.y + mv.y > 0) {
                            v.setX((StartPT.y + mv.y) )
                            StartPT = PointF(v.x, v.y)
                            mainPointer2.setY((mainPointer2.y + mv.y) )
                        }
                        if (mainPointer1.y + mv.y + v.height < polygonView!!.height && mainPointer1.y + mv.y > 0) {
                            v.setX((StartPT.y + mv.y) )
                            StartPT = PointF(v.x, v.y)
                            mainPointer1.setY((mainPointer1.y + mv.y) )
                        }
                    } else {
                        if (mainPointer2.x + mv.x + v.width < polygonView!!.width && mainPointer2.x + mv.x > 0) {
                            v.setX((StartPT.x + mv.x) )
                            StartPT = PointF(v.x, v.y)
                            mainPointer2.setX((mainPointer2.x + mv.x) )
                        }
                        if (mainPointer1.x + mv.x + v.width < polygonView!!.width && mainPointer1.x + mv.x > 0) {
                            v.setX((StartPT.x + mv.x) )
                            StartPT = PointF(v.x, v.y)
                            mainPointer1.setX((mainPointer1.x + mv.x) )
                        }
                    }
                    Log.e(TAG, "action_move")
                }
                MotionEvent.ACTION_DOWN -> {
                    DownPT.x = event.x
                    DownPT.y = event.y
                    StartPT = PointF(v.x, v.y)
                    Log.e(TAG, "ACTION_DOWN")
                }
                MotionEvent.ACTION_UP -> {
                    var color = 0
                    color = if (isValidShape(points)) {
                        resources.getColor(R.color.blue)
                    } else {
                        resources.getColor(R.color.orange)
                    }
                    paint!!.color = color
                    Log.e(TAG, "ACTION_UP")
                }
                else -> {
                }
            }
            polygonView!!.invalidate()
            return true
        }

    }

    private var latestDownX = 0.0f
    private var latestDownY = 0.0f
    override fun onTouchEvent(event: MotionEvent): Boolean {

        Log.e(TAG, "touch")
        val e = event.action
        when (e) {
            MotionEvent.ACTION_DOWN -> {
                Log.e("motion", "ACTION_DOWN")
                latestDownX = event.x
                latestDownY = event.y
                calculatePoint2Move(event.x, event.y)
            }
            MotionEvent.ACTION_MOVE ->{
                Log.e("motion", "ACTION_MOVE")

                var dynamicY= (event.y - latestDownY) + pointerToMove!!.y + pointerToMove!!.height
                var dynamicX = (event.x - latestDownX) + pointerToMove!!.x + pointerToMove!!.width

                if((event.x - latestDownX) + pointerToMove!!.x>0
                        && (event.y - latestDownY) + pointerToMove!!.y>0
                        && dynamicY < polygonView!!.height
                        && dynamicX < polygonView!!.width){
                    // perforn action
                    pointerToMove!!.x = (event.x - latestDownX) + pointerToMove!!.x
                    pointerToMove!!.y = (event.y - latestDownY) + pointerToMove!!.y
                    movePoints(event.x,event.y)
                    latestDownY = event.y
                    latestDownX = event.x


                }


            }
            MotionEvent.ACTION_UP -> Log.e("motion", "ACTION_UP")
        }
        polygonView!!.invalidate()

        return true
    }

    private var pointerToMove:ImageView? = null


    private fun movePoints(moveX: Float, moveY: Float){
//        pointerToMove?.x = moveX
//        pointerToMove?.y = moveY
//
        Log.e("movePointerX",": "+pointerToMove?.x)
        Log.e("movePointerY",": "+pointerToMove?.y)


        Log.e("currentPointerX",": "+moveX)
        Log.e("currentPointerY",": "+moveY)

    }


    private fun calculatePoint2Move(downX: Float, downY: Float) {
//        val points = listOf(tl, tr, br, bl)
//        point2Move = points.minBy { Math.abs((it.x - downX).times(it.y - downY)) } ?: tl
        val listPoint = ArrayList<ImageView?>()
        listPoint.add(pointer1)
        listPoint.add(pointer2)
        listPoint.add(pointer3)
        listPoint.add(pointer4)


        pointerToMove =  listPoint.minBy {
            Math.abs((it!!.x - downX).times(it.y - downY))
        }
//        listPoint.
    }




    fun isValidShape(pointFMap: Map<Int, PointF>): Boolean {
        return pointFMap.size == 4
    }

//    private inner class TouchListenerImpl : OnTouchListener {
//        var DownPT = PointF() // Record Mouse Position When Pressed Down
//        var StartPT = PointF() // Record Start Position of 'img'
//        override fun onTouch(v: View, event: MotionEvent): Boolean {
//            val eid = event.action
//            Log.e(TAG, "onTouch TouchListenerImpl$eid")
//            when (eid) {
//                MotionEvent.ACTION_MOVE -> {
//                    Log.e(TAG, ": " + event.x + " , " + event.y)
//                    val mv = PointF(event.x - DownPT.x, event.y - DownPT.y)
//                    if (StartPT.x + mv.x + v.width < polygonView!!.width && StartPT.y + mv.y + v.height < polygonView!!.height && StartPT.x + mv.x > 0 && StartPT.y + mv.y > 0) {
//                        v.setX((StartPT.x + mv.x) )
//                        v.setY((StartPT.y + mv.y) )
//                        StartPT = PointF(v.x, v.y)
//                    }
//                }
//                MotionEvent.ACTION_DOWN -> {
//                    DownPT.x = event.x
//                    DownPT.y = event.y
//                    StartPT = PointF(v.x, v.y)
//                }
//                MotionEvent.ACTION_UP -> {
//                    var color = 0
//                    color = if (isValidShape(points)) {
//                        resources.getColor(R.color.blue)
//                    } else {
//                        resources.getColor(R.color.orange)
//                    }
//                    paint!!.color = color
//                }
//                else -> {
//                }
//            }
//            polygonView!!.invalidate()
//            return true
//        }
//    }
}