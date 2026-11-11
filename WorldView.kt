package it.microverse.app

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.MotionEvent
import android.view.View
import kotlin.math.max
import kotlin.math.min

class WorldView(context: Context, private val engine: SimulationEngine) : View(context) {
    private val p = Paint(Paint.ANTI_ALIAS_FLAG)
    private var zoom = 1f
    private var offsetX = 0f
    private var offsetY = 0f
    private var downX = 0f
    private var downY = 0f

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(0xFF07131D.toInt())
        val w = width.toFloat(); val h = height.toFloat()
        p.color = 0xFF102B38.toInt()
        canvas.drawCircle(w*.5f, h*.5f, min(w,h)*.43f*zoom, p)
        for (o in engine.organisms) {
            val x = w*.08f + o.x*w*.84f
            val y = h*.08f + o.y*h*.84f
            p.color = when {
                o.health < .35f -> 0xFFFF8A65.toInt()
                o.genome.aggression > .7f -> 0xFFFFCA67.toInt()
                o.genome.sociability > .7f -> 0xFF64D8C5.toInt()
                else -> 0xFF86B9FF.toInt()
            }
            val r = (3.5f + o.genome.resilience*5f) * zoom
            canvas.drawCircle(w*.5f + (x-w*.5f)*zoom + offsetX,
                h*.5f + (y-h*.5f)*zoom + offsetY, r, p)
        }
        p.color = 0xFFD8E7F0.toInt(); p.textSize = 30f
        canvas.drawText("MICROVERSE", 28f, 42f, p)
        p.textSize = 22f
        canvas.drawText("Organismi: ${engine.organisms.size}  •  Tempo: ${engine.tick}", 28f, 72f, p)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> { downX=event.x; downY=event.y; return true }
            MotionEvent.ACTION_MOVE -> {
                offsetX += event.x-downX; offsetY += event.y-downY
                downX=event.x; downY=event.y; invalidate(); return true
            }
            MotionEvent.ACTION_UP -> { performClick(); return true }
        }
        return true
    }
    override fun performClick(): Boolean { super.performClick(); return true }
}
