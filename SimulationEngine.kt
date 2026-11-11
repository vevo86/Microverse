package it.microverse.app

import kotlin.math.*
import kotlin.random.Random

/**
 * Simplified emergent ecosystem model. It is not a molecular or consciousness simulation.
 * Simulation state is deterministic for a given initial seed and action sequence.
 */
class SimulationEngine(seed: Long = System.currentTimeMillis()) {
    private val random = Random(seed)
    val organisms = mutableListOf<Organism>()
    var tick: Long = 0
        private set
    var nutrients = 0.65f
    var oxygen = 0.8f
    var temperature = 0.5f
    var diseasePressure = 0.02f
    var worldSeed: Long = seed
    private var nextId = 1L
    var lastEvent: String = "Un nuovo ecosistema prende forma"

    init { repeat(34) { spawn() } }

    private fun genome() = Genome(
        random.nextFloat().coerceAtLeast(.1f), random.nextFloat().coerceAtLeast(.1f),
        random.nextFloat().coerceAtLeast(.1f), random.nextFloat(),
        random.nextFloat(), random.nextFloat()
    )

    fun spawn(x: Float = random.nextFloat(), y: Float = random.nextFloat(), parent: Organism? = null) {
        if (organisms.size >= 500) return
        val g = parent?.genome?.mutate() ?: genome()
        organisms += Organism(nextId++, x.coerceIn(0f, 1f), y.coerceIn(0f, 1f),
            .55f + random.nextFloat() * .4f, 0, 1f, g,
            generation = (parent?.generation ?: -1) + 1,
            species = parent?.species ?: random.nextInt(1, 5))
    }

    fun step(steps: Int = 1) {
        repeat(steps.coerceIn(1, 500)) {
            tick++
            nutrients = (nutrients + random.nextFloat() * .012f - .006f).coerceIn(.05f, 1f)
            oxygen = (oxygen + random.nextFloat() * .004f - .002f).coerceIn(.1f, 1f)
            temperature = (temperature + random.nextFloat() * .002f - .001f).coerceIn(.05f, .95f)
            if (tick % 900L == 0L) {
                diseasePressure = random.nextFloat() * .22f
                lastEvent = listOf("Fioritura di nutrienti", "Ondata di freddo",
                    "Nuova pressione epidemica", "Carenza di ossigeno", "Espansione di una colonia").random(random)
            }
            val snapshot = organisms.toList()
            for (o in snapshot) {
                o.age++
                val localRisk = diseasePressure * (1f - o.genome.resilience)
                o.health = (o.health - localRisk * .002f).coerceIn(0f, 1f)
                val hungry = o.energy < .55f
                val threatened = snapshot.any { p ->
                    p.id != o.id && distance(o, p) < .025f && p.genome.aggression > o.genome.resilience + .25f
                }
                // Action selection: needs + inherited traits + remembered resource location + bounded randomness.
                val explore = random.nextFloat() < (0.08f + o.genome.learning * .12f)
                if (hungry && nutrients > .18f) {
                    if (o.memoryTicks > 0 && random.nextFloat() < o.genome.learning) {
                        moveToward(o, o.memoryX, o.memoryY)
                        o.memoryTicks--
                    } else {
                        o.x = (o.x + random.nextFloat() * .06f - .03f).coerceIn(0f, 1f)
                        o.y = (o.y + random.nextFloat() * .06f - .03f).coerceIn(0f, 1f)
                        if (random.nextFloat() < .12f) { o.memoryX = o.x; o.memoryY = o.y; o.memoryTicks = 80 }
                    }
                    o.energy += nutrients * .008f * o.genome.metabolism
                } else if (threatened) {
                    o.x = (o.x + (random.nextFloat() - .5f) * .12f).coerceIn(0f, 1f)
                    o.y = (o.y + (random.nextFloat() - .5f) * .12f).coerceIn(0f, 1f)
                } else if (explore || random.nextFloat() < .15f) {
                    o.x = (o.x + (random.nextFloat() - .5f) * .035f * o.genome.speed).coerceIn(0f, 1f)
                    o.y = (o.y + (random.nextFloat() - .5f) * .035f * o.genome.speed).coerceIn(0f, 1f)
                }
                o.energy -= .0015f + o.genome.metabolism * .0015f
                if (o.age > 1800 || o.energy <= 0f || o.health <= .02f) organisms.remove(o)
                else if (o.energy > 1.15f && random.nextFloat() < .0018f) {
                    o.energy *= .55f
                    spawn(o.x, o.y, o)
                }
            }
            // Population-level competition and resource pressure.
            if (organisms.size > 1) nutrients = (nutrients - organisms.size * .000002f).coerceAtLeast(.02f)
            if (organisms.isEmpty()) {
                lastEvent = "Estinzione locale: l'ecosistema attende una nuova colonizzazione"
                repeat(8) { spawn() }
            }
        }
    }

    private fun moveToward(o: Organism, x: Float, y: Float) {
        o.x += (x - o.x) * .08f * o.genome.speed
        o.y += (y - o.y) * .08f * o.genome.speed
    }
    private fun distance(a: Organism, b: Organism) = hypot(a.x - b.x, a.y - b.y)

    fun addNutrients() { nutrients = (nutrients + .2f).coerceAtMost(1f); lastEvent = "Il creatore ha aggiunto nutrienti" }
    fun changeClimate() { temperature = random.nextFloat(); lastEvent = "Il creatore ha modificato il clima" }
    fun introduceLife() { repeat(5) { spawn() }; lastEvent = "Il creatore ha introdotto nuove forme di vita" }
}
