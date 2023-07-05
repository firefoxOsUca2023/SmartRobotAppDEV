package com.example.zacatales.smartrobotapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View


class RouteView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private val pathPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        style = Paint.Style.STROKE
    }
    private val arrowPaint = Paint().apply {
        color = Color.BLUE
        strokeWidth = 5f
        style = Paint.Style.FILL
    }
    private val arrowPath = Path()

    private var routeList: List<String> = emptyList()


    fun setRouteList(routeList: List<String>) {
        this.routeList = routeList
        invalidate() // Vuelve a dibujar la vista cuando cambia la lista de rutas
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var maxX = 0f
        var maxY = 0f
        var minX = 0f
        var minY = 0f

        var currentX = 0f
        var currentY = 0f

        routeList.forEach { route ->
            when (route) {
                "F" -> {
                    // Aumentar la altura
                    currentY += 100f
                    maxY = Math.max(maxY, currentY)
                }
                "B" -> {
                    // Reducir la altura
                    currentY -= 100f
                    minY = Math.min(minY, currentY)
                }
                "L" -> {
                    // Reducir la anchura
                    currentX -= 100f
                    minX = Math.min(minX, currentX)
                }
                "R" -> {
                    // Aumentar la anchura
                    currentX += 100f
                    maxX = Math.max(maxX, currentX)
                }
            }
        }

        // Tomamos el valor absoluto porque minX y minY pueden ser negativos
        val calculatedWidth = Math.max(Math.abs(maxX), Math.abs(minX)) * 2
        val calculatedHeight = Math.max(Math.abs(maxY), Math.abs(minY)) * 2

        val width = MeasureSpec.makeMeasureSpec(calculatedWidth.toInt(), MeasureSpec.EXACTLY)
        val height = MeasureSpec.makeMeasureSpec(calculatedHeight.toInt(), MeasureSpec.EXACTLY)

        setMeasuredDimension(width, height)
    }
    fun clearRoute() {
        routeList = emptyList()
        invalidate() // Fuerza a la vista a redibujarse, esto limpiará el lienzo
        requestLayout() // Esto se asegura de que onMeasure sea llamado de nuevo para ajustar el tamaño de la vista.
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()

        val centerX = width / 2
        val centerY = height / 2

        var currentX = centerX
        var currentY = centerY

        // Dibuja un círculo verde en el punto de inicio
        val startCirclePaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }
        canvas.drawCircle(centerX, centerY, 20f, startCirclePaint)

        routeList.forEach { route ->
            when (route) {
                "F" -> {
                    // Dibujar flecha hacia arriba
                    canvas.drawLine(currentX, currentY, currentX, currentY - 100f, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX, currentY - 100f)
                    arrowPath.lineTo(currentX - 10f, currentY - 80f)
                    arrowPath.lineTo(currentX + 10f, currentY - 80f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentY -= 100f
                }
                "B" -> {
                    // Dibujar flecha hacia abajo
                    canvas.drawLine(currentX, currentY, currentX, currentY + 100f, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX, currentY + 100f)
                    arrowPath.lineTo(currentX - 10f, currentY + 80f)
                    arrowPath.lineTo(currentX + 10f, currentY + 80f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentY += 100f
                }
                "L" -> {
                    // Dibujar flecha hacia la izquierda
                    canvas.drawLine(currentX, currentY, currentX - 100f, currentY, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX - 100f, currentY)
                    arrowPath.lineTo(currentX - 80f, currentY - 10f)
                    arrowPath.lineTo(currentX - 80f, currentY + 10f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentX -= 100f
                }
                "R" -> {
                    // Dibujar flecha hacia la derecha
                    canvas.drawLine(currentX, currentY, currentX + 100f, currentY, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX + 100f, currentY)
                    arrowPath.lineTo(currentX + 80f, currentY - 10f)
                    arrowPath.lineTo(currentX + 80f, currentY + 10f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentX += 100f
                }
            }
        }
    }

}
