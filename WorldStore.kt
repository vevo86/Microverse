package it.microverse.app

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.json.JSONArray
import org.json.JSONObject

class WorldStore(context: Context) : SQLiteOpenHelper(context, "microverse.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE world (id INTEGER PRIMARY KEY, state TEXT NOT NULL, saved_at INTEGER NOT NULL)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun save(engine: SimulationEngine) {
        val arr = JSONArray()
        engine.organisms.forEach { o ->
            val g = o.genome
            arr.put(JSONObject().put("id", o.id).put("x", o.x).put("y", o.y)
                .put("energy", o.energy).put("age", o.age).put("health", o.health)
                .put("speed", g.speed).put("resilience", g.resilience).put("metabolism", g.metabolism)
                .put("aggression", g.aggression).put("sociability", g.sociability).put("learning", g.learning)
                .put("memoryX", o.memoryX).put("memoryY", o.memoryY).put("memoryTicks", o.memoryTicks)
                .put("generation", o.generation).put("species", o.species))
        }
        val state = JSONObject().put("seed", engine.worldSeed).put("tick", engine.tick)
            .put("nutrients", engine.nutrients).put("oxygen", engine.oxygen)
            .put("temperature", engine.temperature).put("disease", engine.diseasePressure)
            .put("event", engine.lastEvent).put("organisms", arr).toString()
        val values = android.content.ContentValues().apply {
            put("id", 1); put("state", state); put("saved_at", System.currentTimeMillis())
        }
        writableDatabase.insertWithOnConflict("world", null, values, SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun load(): JSONObject? {
        readableDatabase.rawQuery("SELECT state FROM world WHERE id=1", null).use { c ->
            return if (c.moveToFirst()) JSONObject(c.getString(0)) else null
        }
    }
}
