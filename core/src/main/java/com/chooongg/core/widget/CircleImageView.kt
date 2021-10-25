package com.chooongg.core.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView
import com.chooongg.core.R
import kotlin.math.min
import kotlin.math.pow

class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private val SCALE_TYPE = ScaleType.CENTER_CROP
        private val BITMAP_CONFIG = Bitmap.Config.ARGB_8888
        private const val COLOR_DRAWABLE_DIMENSION = 2
        private const val DEFAULT_BORDER_WIDTH = 0
        private const val DEFAULT_BORDER_COLOR: Int = Color.BLACK
        private const val DEFAULT_CIRCLE_BACKGROUND_COLOR: Int = Color.TRANSPARENT
        private const val DEFAULT_IMAGE_ALPHA = 255
        private const val DEFAULT_BORDER_OVERLAY = false
    }

    private val drawableRect = RectF()
    private val borderRect = RectF()

    private val shaderMatrix: Matrix = Matrix()
    private val bitmapPaint = Paint().apply {
        isAntiAlias = true
        isDither = true
        isFilterBitmap = true
        alpha = imageAlpha
        colorFilter = colorFilter
    }
    private val borderPaint = Paint().apply {
        style = Paint.Style.STROKE
        isAntiAlias = true
        color = borderColor
        strokeWidth = borderWidth.toFloat()
    }
    private val circleBackgroundPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = circleBackgroundColor
    }

    private var borderColor = DEFAULT_BORDER_COLOR
    private var borderWidth = DEFAULT_BORDER_WIDTH
    private var circleBackgroundColor = DEFAULT_CIRCLE_BACKGROUND_COLOR
    private var imageAlpha = DEFAULT_IMAGE_ALPHA

    private var bitmap: Bitmap? = null
    private var bitmapCanvas: Canvas? = null

    private var drawableRadius = 0f
    private var borderRadius = 0f

    private var colorFilter: ColorFilter? = null

    private var initialized = false
    private var rebuildShader = false
    private var drawableDirty = false

    private var borderOverlay = false
    private val disableCircularTransformation = false

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.CircleImageView, defStyleAttr, 0)
        borderWidth = a.getDimensionPixelSize(
            R.styleable.CircleImageView_civ_border_width,
            DEFAULT_BORDER_WIDTH
        )
        borderColor =
            a.getColor(R.styleable.CircleImageView_civ_border_color, DEFAULT_BORDER_COLOR)
        borderOverlay =
            a.getBoolean(R.styleable.CircleImageView_civ_border_overlay, DEFAULT_BORDER_OVERLAY)
        circleBackgroundColor = a.getColor(
            R.styleable.CircleImageView_civ_circle_background_color,
            DEFAULT_CIRCLE_BACKGROUND_COLOR
        )
        a.recycle()
        initialized = true
        super.setScaleType(SCALE_TYPE)

        outlineProvider = OutlineProvider()
    }

    override fun setScaleType(scaleType: ScaleType?) {
        if (scaleType != SCALE_TYPE)
            throw IllegalArgumentException("ScaleType %s not supported.".format(scaleType))
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        if (adjustViewBounds) throw IllegalArgumentException("adjustViewBounds not supported.")
    }

    @SuppressLint("CanvasSize")
    override fun onDraw(canvas: Canvas) {
        if (disableCircularTransformation) {
            super.onDraw(canvas)
            return
        }
        if (circleBackgroundColor != Color.TRANSPARENT) {
            canvas.drawCircle(
                drawableRect.centerX(),
                drawableRect.centerY(),
                drawableRadius,
                circleBackgroundPaint
            )
        }
        if (bitmap != null) {
            if (drawableDirty && bitmapCanvas != null) {
                drawableDirty = false
                drawable.setBounds(0, 0, bitmapCanvas!!.width, bitmapCanvas!!.height)
                drawable.draw(bitmapCanvas!!)
            }
            if (rebuildShader) {
                rebuildShader = false
                bitmapPaint.shader =
                    BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP).apply {
                        setLocalMatrix(shaderMatrix)
                    }
            }
            canvas.drawCircle(
                drawableRect.centerX(),
                drawableRect.centerY(),
                drawableRadius,
                bitmapPaint
            )
        }
        if (borderWidth > 0) {
            canvas.drawCircle(
                borderRect.centerX(),
                borderRect.centerY(),
                borderRadius,
                borderPaint
            )
        }
    }

    override fun invalidateDrawable(dr: Drawable) {
        drawableDirty = true
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateDimensions()
        invalidate()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        updateDimensions()
        invalidate()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        updateDimensions()
        invalidate()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
        invalidate()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
        invalidate()
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
        invalidate()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
        invalidate()
    }

    override fun setImageAlpha(alpha: Int) {
        val alphaTemp = alpha and 0xFF
        if (alphaTemp == imageAlpha) return
        imageAlpha = alphaTemp
        if (initialized) {
            bitmapPaint.alpha = alphaTemp
            invalidate()
        }
    }

    override fun getImageAlpha() = imageAlpha

    override fun setColorFilter(cf: ColorFilter?) {
        if (cf == colorFilter) return
        colorFilter = cf
        if (initialized) {
            bitmapPaint.colorFilter = colorFilter
            invalidate()
        }
    }

    override fun getColorFilter() = colorFilter

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(
                    COLOR_DRAWABLE_DIMENSION,
                    COLOR_DRAWABLE_DIMENSION,
                    BITMAP_CONFIG
                )
            } else {
                Bitmap.createBitmap(
                    drawable.intrinsicWidth,
                    drawable.intrinsicHeight,
                    BITMAP_CONFIG
                )
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        bitmap = getBitmapFromDrawable(drawable)
        bitmapCanvas = if (bitmap != null && bitmap!!.isMutable) {
            Canvas(bitmap!!)
        } else {
            null
        }
        if (!initialized) {
            return
        }
        if (bitmap != null) {
            updateShaderMatrix()
        } else {
            bitmapPaint.shader = null
        }
    }

    private fun updateDimensions() {
        borderRect.set(calculateBounds())
        borderRadius = min(
            (borderRect.height() - borderWidth) / 2.0f,
            (borderRect.width() - borderWidth) / 2.0f
        )
        drawableRect.set(borderRect)
        if (!borderOverlay && borderWidth > 0) {
            drawableRect.inset(borderWidth - 1.0f, borderWidth - 1.0f)
        }
        drawableRadius = min(drawableRect.height() / 2.0f, drawableRect.width() / 2.0f)
        updateShaderMatrix()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val sideLength = min(availableWidth, availableHeight)
        val left = paddingLeft + (availableWidth - sideLength) / 2f
        val top = paddingTop + (availableHeight - sideLength) / 2f
        return RectF(left, top, left + sideLength, top + sideLength)
    }

    private fun updateShaderMatrix() {
        if (bitmap == null) return
        val scale: Float
        var dx = 0f
        var dy = 0f
        shaderMatrix.set(null)
        val bitmapHeight = bitmap!!.height
        val bitmapWidth = bitmap!!.width
        if (bitmapWidth * drawableRect.height() > drawableRect.width() * bitmapHeight) {
            scale = drawableRect.height() / bitmapHeight.toFloat()
            dx = (drawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = drawableRect.width() / bitmapWidth.toFloat()
            dy = (drawableRect.height() - bitmapHeight * scale) * 0.5f
        }
        shaderMatrix.setScale(scale, scale)
        shaderMatrix.postTranslate(
            (dx + 0.5f).toInt() + drawableRect.left,
            (dy + 0.5f).toInt() + drawableRect.top
        )
        rebuildShader = true
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (disableCircularTransformation) {
            super.onTouchEvent(event)
        } else inTouchableArea(event.x, event.y) && super.onTouchEvent(event)
    }

    private fun inTouchableArea(x: Float, y: Float): Boolean {
        return if (borderRect.isEmpty) {
            true
        } else (x - borderRect.centerX()).toDouble()
            .pow(2.0) + (y - borderRect.centerY()).toDouble()
            .pow(2.0) <= borderRadius.toDouble().pow(2.0)
    }

    private inner class OutlineProvider : ViewOutlineProvider() {
        override fun getOutline(view: View?, outline: Outline) {
            if (disableCircularTransformation) {
                BACKGROUND.getOutline(view, outline)
            } else {
                val bounds = Rect()
                borderRect.roundOut(bounds)
                outline.setRoundRect(bounds, bounds.width() / 2.0f)
            }
        }
    }
}