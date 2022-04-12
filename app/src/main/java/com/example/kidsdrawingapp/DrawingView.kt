package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class DrawingView(context: Context, attrs: AttributeSet ) : View(context, attrs){
    //Variable of CustomPath inner class
    private var mDrawPath: CustomPath? = null
    //An instance of the Bitmap
    private var mCanvasBitmap: Bitmap? = null
    //The Paint class holds the style and color information about how to draw
    private var mDrawPaint: Paint? = null
    //Instance of Canvas paint view
    private var mCanvasPaint: Paint? = null
    //Variable for stroke/brush size to draw on the canvas
    private var mBrushSize: Float = 0.toFloat()
    //Variable to hold a color of the stroke
    private var color = Color.BLACK
    /**
     * A variable for canvas, will be initialized later and used
     *
     * The Canvas class holds the 'draw' calls. To draw something, you need 4 basic components.
     * the draw calls(writing into the bitmap), a drawing primitive(e.g. Rect, Path, text, Bitmap),
     * and a paint(to describe the colors and styles for the drawing)
     */
    private var canvas: Canvas? = null
    private val mPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    /**
     *This method initializes the attributes of the ViewForDrawing class
     */

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint!!.color = color
        mDrawPaint!!.style = Paint.Style.STROKE
        mDrawPaint!!.strokeJoin = Paint.Join.ROUND
        mDrawPaint!!.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
        //mBrushSize = 20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        canvas = Canvas(mCanvasBitmap!!)
    }

    /**
     * This method is called when a stroke is drawn on the canvas as a part of painting
    */
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        mCanvasBitmap?.let {
            canvas.drawBitmap(it, 0f,   0f, mCanvasPaint)
        }
        //canvas.drawBitmap(mCanvasBitmap!!, 0f,0f, mCanvasPaint)

        for (path in mPaths){
            //How thick the pen should be
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path, mDrawPaint!!)
        }

        if (!mDrawPath!!.isEmpty) {
            //How thick the pen should be
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!, mDrawPaint!!)
        }
    }

    /**
     * This method as an event listener when a touch event is detected on the device
     */

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                //How thick the path is
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.moveTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (touchX != null) {
                    if (touchY != null) {
                        mDrawPath!!.lineTo(touchX, touchY)
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                mPaths.add(mDrawPath!!)
                mDrawPath = CustomPath(color, mBrushSize)
            }
            else -> return false
        }
        invalidate()

        return true
    }

    fun setSizeForBrush(newSize: Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, resources.displayMetrics)

        mDrawPaint!!.strokeWidth = mBrushSize
    }


    internal inner class CustomPath(var color: Int, var brushThickness: Float): Path()  {

    }
}