package it.microverse.app

import kotlin.math.max
import kotlin.math.min

data class Genome(
    var speed: Float,
    var resilience: Float,
    var metabolism: Float,
    var aggression: Float,
    var sociability: Float,
    var learning: Float
) {
    fun mutate(): Genome {
    fun change(v: Float) = (v + (kotlin.random.Random.Default.nextFloat() * 0.24f - 0.12f)).coerceIn(0.05f, 1f)
        return Genome(change(speed), change(resilience), change(metabolism),
            change(aggression), change(sociability), change(learning))
    }
}

data class Organism(
    val id: Long,
    var x: Float,
    var y: Float,
    var energy: Float,
    var age: Int,
    var health: Float,
    var genome: Genome,
    var memoryX: Float = -1f,
    var memoryY: Float = -1f,
    var memoryTicks: Int = 0,
    var generation: Int = 0,
    var species: Int = 1
)
