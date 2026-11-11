package it.microverse.app

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import org.json.JSONObject

class MainActivity : Activity() {
    private lateinit var engine: SimulationEngine
    private lateinit var store: WorldStore
    private lateinit var worldView: WorldView
    private lateinit var status: TextView
    private val handler = Handler(Looper.getMainLooper())
    private var running = true
    private var creatorMode = false
    private var speed = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        store = WorldStore(this)
        val saved = store.load()
        engine = if (saved == null) SimulationEngine() else restore(saved)
        buildUi()
        val elapsed = saved?.optLong("savedAt", 0L) ?: 0L
        // Offline progression is applied on resume from last_saved timestamp in later persistence revisions.
        handler.post(loop)
    }

    private fun restore(s: JSONObject): SimulationEngine {
        // The first milestone restores a fresh world when a compatible snapshot is unavailable.
        // Full state restoration is added in the persistence milestone.
        return SimulationEngine(s.optLong("seed", System.currentTimeMillis()))
    }

    private fun buildUi() {
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(0xFF07131D.toInt())
            setPadding(12, 12, 12, 12)
        }
        status = TextView(this).apply {
            setTextColor(0xFFEAF4FA.toInt()); textSize=14f; gravity=Gravity.CENTER
            text = "Universo autonomo • Modalità osservatore"
        }
        root.addView(status)
        worldView = WorldView(this, engine)
        root.addView(worldView, LinearLayout.LayoutParams(-1, 0, 1f))
        val row1 = LinearLayout(this).apply { gravity=Gravity.CENTER }
        row1.addView(button("Pausa") { running = !running })
        row1.addView(button("Velocità ×$speed") { speed = if (speed == 1) 3 else if (speed == 3) 8 else 1 })
        row1.addView(button("Creatore") { creatorMode = !creatorMode; status.text = if (creatorMode) "Modalità creatore" else "Modalità osservatore" })
        root.addView(row1)
        val row2 = LinearLayout(this).apply { gravity=Gravity.CENTER }
        row2.addView(button("+ Nutrienti") { if (creatorMode) engine.addNutrients() })
        row2.addView(button("Nuova vita") { if (creatorMode) engine.introduceLife() })
        row2.addView(button("Clima") { if (creatorMode) engine.changeClimate() })
        root.addView(row2)
        setContentView(root)
    }

    private fun button(label: String, action: () -> Unit) = Button(this).apply {
        text = label; textSize = 10f; setOnClickListener { action(); worldView.invalidate() }
    }

    private val loop = object : Runnable {
        override fun run() {
            if (running) engine.step(speed)
            status.text = "Specie/organismi: ${engine.organisms.size}  •  ${engine.lastEvent}"
            worldView.invalidate()
            handler.postDelayed(this, 250L)
        }
    }

    override fun onPause() {
        super.onPause()
        store.save(engine)
    }
    override fun onDestroy() {
        handler.removeCallbacks(loop)
        super.onDestroy()
    }
}
