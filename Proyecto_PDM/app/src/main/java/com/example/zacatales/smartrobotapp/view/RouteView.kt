package com.example.zacatales.smartrobotapp.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import com.example.zacatales.smartrobotapp.model.BotonPresionado


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

    private var scaleFactor = 1f
    private val gestureDetector: GestureDetector
    private val scaleGestureDetector: ScaleGestureDetector


    private var routeList: List<BotonPresionado> = emptyList()


    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var posX = 0f
    private var posY = 0f

    init {
        scaleGestureDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                scaleFactor *= detector.scaleFactor
                scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 5.0f))
                invalidate()
                return true
            }
        })

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {
                scaleFactor = 1f
                invalidate()
                return true
            }
        })
    }

    fun setRouteList(routeList: List<BotonPresionado>) {
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

        routeList.forEach {  buttonPress ->
            when ( buttonPress.accion) {
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

        // Establecer dimensiones mínimas
        val minWidth = 2500 // Establecer a tu valor mínimo preferido
        val minHeight = 1000 // Establecer a tu valor mínimo preferido

        val width = MeasureSpec.makeMeasureSpec(Math.max(calculatedWidth.toInt(), minWidth), MeasureSpec.EXACTLY)
        val height = MeasureSpec.makeMeasureSpec(Math.max(calculatedHeight.toInt(), minHeight), MeasureSpec.EXACTLY)

        setMeasuredDimension(width, height)
    }
    fun clearRoute() {
        routeList = emptyList()
        invalidate() // Fuerza a la vista a redibujarse, esto limpiará el lienzo
        requestLayout() // Esto se asegura de que onMeasure sea llamado de nuevo para ajustar el tamaño de la vista.
    }
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()

        canvas.translate(posX, posY)
        canvas.scale(scaleFactor, scaleFactor, width / 2f, height / 2f)


        val width = measuredWidth.toFloat()
        val height = measuredHeight.toFloat()

        val centerX = width / 2
        val centerY = height / 2

        canvas.translate(centerX, centerY)


        var currentX = 0f
        var currentY = 0f

        // Dibuja un círculo verde en el punto de inicio
        val startCirclePaint = Paint().apply {
            color = Color.GREEN
            style = Paint.Style.FILL
        }
        canvas.drawCircle(currentX, currentY, 20f, startCirclePaint)


        routeList.forEach { route ->
            val pressDurationFactor = route.duracion.toFloat() / 10
            when (route.accion) {
                "F" -> {
                    // Dibujar flecha hacia arriba
                    canvas.drawLine(currentX, currentY, currentX, currentY - pressDurationFactor, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX, currentY - pressDurationFactor)
                    arrowPath.lineTo(currentX - 10f, currentY - (pressDurationFactor - 20f))
                    arrowPath.lineTo(currentX + 10f, currentY - (pressDurationFactor - 20f))
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentY -= pressDurationFactor
                }
                "B" -> {
                    // Dibujar flecha hacia abajo
                    canvas.drawLine(currentX, currentY, currentX, currentY + pressDurationFactor, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX, currentY + pressDurationFactor)
                    arrowPath.lineTo(currentX - 10f, currentY + (pressDurationFactor - 20f))
                    arrowPath.lineTo(currentX + 10f, currentY + (pressDurationFactor - 20f))
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentY += pressDurationFactor
                }
                "L" -> {
                    // Dibujar flecha hacia la izquierda
                    canvas.drawLine(currentX, currentY, currentX - pressDurationFactor, currentY, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX - pressDurationFactor, currentY)
                    arrowPath.lineTo(currentX - (pressDurationFactor - 20f), currentY - 10f)
                    arrowPath.lineTo(currentX - (pressDurationFactor - 20f), currentY + 10f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentX -= pressDurationFactor
                }
                "R" -> {
                    // Dibujar flecha hacia la derecha
                    canvas.drawLine(currentX, currentY, currentX + pressDurationFactor, currentY, pathPaint)
                    arrowPath.reset()
                    arrowPath.moveTo(currentX + pressDurationFactor, currentY)
                    arrowPath.lineTo(currentX + (pressDurationFactor - 20f), currentY - 10f)
                    arrowPath.lineTo(currentX + (pressDurationFactor - 20f), currentY + 10f)
                    arrowPath.close()
                    canvas.drawPath(arrowPath, arrowPaint)

                    currentX += pressDurationFactor
                }
            }
        }

        canvas.restore()
    }
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    // Guardar la posición inicial al tocar la pantalla
                    lastTouchX = event.x
                    lastTouchY = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    // Calcula la distancia recorrida
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY

                    // Actualizar la posición
                    posX += dx
                    posY += dy

                    // Guarda la posición actual como la última
                    lastTouchX = event.x
                    lastTouchY = event.y

                    // Redraw the view
                    invalidate()
                }
            }

            gestureDetector.onTouchEvent(event)
            scaleGestureDetector.onTouchEvent(event)
        }
        return true
    }

}

