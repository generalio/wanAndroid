package com.generlas.winterexam.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * description ： TODO:绘制轮播图的圆点
 * date : 2025/2/4 15:41
 */

class CarouselDot @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr){

    private var numbers = 3
    private var currentDot = 0
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val radius = 12f
    private val space = 20f

    init {
        paint.style = Paint.Style.FILL
    }

    //绘制圆点
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val width = width
        val height = height
        val trueWidth = 2 * radius * numbers + space * (numbers - 1)
        val dotX = width - trueWidth
        val dotY = height / 2f

        for(i in 0 until numbers) {
            if(i == currentDot) {
                paint.color = 0xFF0099FF.toInt()
            } else {
                paint.color = 0xF0F0F0F0.toInt()
            }
            canvas.drawCircle(dotX + (i - 1) * radius * 2 + radius + (i - 1) * space,dotY,radius,paint)
        }
    }

    //初始化圆点
    fun initDots(numbers: Int, currentDot: Int) {
        this.numbers = numbers
        this.currentDot = currentDot
        invalidate()
        requestLayout()
    }

    //改变圆点选中状态
    fun changeDots(currentDot: Int) {
        this.currentDot = currentDot
        invalidate()
    }
}