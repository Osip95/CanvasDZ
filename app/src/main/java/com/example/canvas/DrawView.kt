package com.example.canvas

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import androidx.core.content.res.ResourcesCompat
import kotlin.math.abs

class DrawView @JvmOverloads constructor(  //кастомная вью, анотоция позволяет перегрузить конструктор.
    context: Context,
    attrs: AttributeSet? = null, // зануляем этот параметр, чтобы атрибуты нашей вью не менялись в зависимости от темы
    defStyleAttr: Int = 0 //  зануляем этот параметр, чтобы атрибуты нашей вью не менялись в зависимости от стиля
) : View(context, attrs, defStyleAttr) { // наследуемся от вью
    companion object {
        private const val STROKE_WIDTH = 12f
    }

    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    private var drawColor =
        ResourcesCompat.getColor(resources, COLOR.BLACK.value, null)// цвет из ресурсов

    private var path = Path() // путь - набор векторов
    private var motionTouchEventX = 0f // координаты нажатия
    private var motionTouchEventY = 0f

    private var currentX = 0f
    private var currentY = 0f
    private val touchTolerance = ViewConfiguration.get(context).scaledTouchSlop

    // Path representing
    private val drawing = Path() // the drawing
    private val curPath = Path() // what's currently being drawn

    private var onClick: () -> Unit = {}

    // Painting Settings
    private val paint = Paint().apply {//настройки линии
        color = drawColor
        isAntiAlias =
            true // Smooths out edges of what is drawn without affecting shape. сглаживание
        isDither =
            true // Dithering affects how colors with higher-precision than the device are down-sampled.
        style = Paint.Style.STROKE // default: FILL как будет выгледеть линия
        strokeJoin = Paint.Join.ROUND // default: MITER
        strokeCap = Paint.Cap.ROUND // default: BUTT кргу как у шариковой ручки
        strokeWidth = STROKE_WIDTH // default: Hairline-width (really thin) ширина
    }

    fun render(state: CanvasViewState) {
        drawColor = ResourcesCompat.getColor(resources, state.color.value, null)
        paint.color = drawColor
        paint.strokeWidth = state.size.value.toFloat()
        if (state.tools == TOOLS.DASH) {
            paint.pathEffect = DashPathEffect(
                floatArrayOf(
                    state.size.value.toFloat() * 2,
                    state.size.value.toFloat() * 2,
                    state.size.value.toFloat() * 2,
                    state.size.value.toFloat() * 2
                ), 0f
            )
        } else {
            paint.pathEffect = null
        }
    }

    fun clear() { // удаляет все что нарисовано
        extraCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
        invalidate()
    }

    fun setOnClickField(onClickField: () -> Unit) {
        onClick = onClickField
    }

    override fun onTouchEvent(event: MotionEvent): Boolean { //регистрирует ивенты, поднял, опустил, ведет палец
        motionTouchEventX = event.x
        motionTouchEventY = event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> touchStart()
            MotionEvent.ACTION_MOVE -> touchMove()
            MotionEvent.ACTION_UP -> touchUp()
        }
        return true
    }

    private fun restartCurrentXY() { // сбрасывает позицию
        currentX = motionTouchEventX
        currentY = motionTouchEventY
    }

    private fun touchStart() {  // реализация ивента пользователь нажал на экран
        onClick()
        path.reset()
        path.moveTo(motionTouchEventX, motionTouchEventY)
        restartCurrentXY()
    }

    private fun touchMove() { // реализация ивента пользователь ведет пальцем
        val dx = abs(motionTouchEventX - currentX)
        val dy = abs(motionTouchEventY - currentY)
        if (dx >= touchTolerance || dy >= touchTolerance) {
            path.quadTo(
                currentX,
                currentY,
                (motionTouchEventX + currentX) / 2,
                (motionTouchEventY + currentY) / 2
            )
            restartCurrentXY()
            extraCanvas.drawPath(path, paint)
            extraCanvas.save()
        }
        invalidate()
    }

    private fun touchUp() { // реализация ивента пользователь поднял палец
        drawing.addPath(curPath)
        curPath.reset()
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)
        if (::extraBitmap.isInitialized) extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
    }

    override fun onDraw(canvas: Canvas) { // переопределяем метод жизненного цикла вью
        super.onDraw(canvas)
        canvas.drawBitmap(extraBitmap, 0f, 0f, null) // рисуем битмап
        canvas.drawPath(drawing, paint) // рисуем путь который провел пользователь
        canvas.drawPath(curPath, paint) // рисуем то что уже было нарисовано
    }
}