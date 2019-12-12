package `12`

fun main() {
    val a = Moon(Pos3D(-1, -4, 0))
    val b = Moon(Pos3D(4, 7, -1))
    val c = Moon(Pos3D(-14, -10, 9))
    val d = Moon(Pos3D(1, 2, 17))
//    val a = Moon(Pos3D(-1, 0, 2))
//    val b = Moon(Pos3D(2, -10, -7))
//    val c = Moon(Pos3D(4, -8, 8))
//    val d = Moon(Pos3D(3, 5, -1))

    val moons = listOf(a, b, c, d)

    val steps = 500000

    val seen = mutableSetOf<Set<Long>>()
    var last = 0
    for (i in 0 until steps) {
        val setOfCoords = setOf(a.pos.z, a.velocity.z, b.pos.z, b.velocity.z, c.pos.z, c.velocity.z, d.pos.z, d.velocity.z)

        if (seen.contains(setOfCoords)) {
            println("same z ${i - 1}, diff: ${i - last}")
            last = i
//            break
        }
        seen.add(setOfCoords)

        applyGravity(moons)
        applyVelocity(moons)
    }

    println("first result")
    val totalEnergy = moons.map { it.totalEnergy() }.sum()
    println(totalEnergy)

    println("second result")
    val x = 131083 // 231614 diff = 1
    val y = 85 // 193052 diff = 10
    val z = 33798 // 60424 diff = 1

}

fun applyVelocity(moons: List<Moon>) {
    for (moon in moons) {
        moon.pos = Pos3D(
            moon.pos.x + moon.velocity.x,
            moon.pos.y + moon.velocity.y,
            moon.pos.z + moon.velocity.z
        )
    }
}

fun applyGravity(moons: List<Moon>) {
    val pairs = mutableSetOf<Pair<Moon, Moon>>()

    for (first in moons) {
        for (second in moons) {
            if (!pairs.contains(Pair(first, second)) && !pairs.contains(Pair(second, first)) && first != second) {
                pairs.add(Pair(first, second))
            }
        }
    }

    for ((first, second) in pairs) {
        if (first.pos.x > second.pos.x) {
            first.velocity = first.velocity.copy(x = first.velocity.x - 1)
            second.velocity = second.velocity.copy(x = second.velocity.x + 1)
        } else if (first.pos.x < second.pos.x) {
            first.velocity = first.velocity.copy(x = first.velocity.x + 1)
            second.velocity = second.velocity.copy(x = second.velocity.x - 1)
        }
        if (first.pos.y > second.pos.y) {
            first.velocity = first.velocity.copy(y = first.velocity.y - 1)
            second.velocity = second.velocity.copy(y = second.velocity.y + 1)
        } else if (first.pos.y < second.pos.y) {
            first.velocity = first.velocity.copy(y = first.velocity.y + 1)
            second.velocity = second.velocity.copy(y = second.velocity.y - 1)
        }
        if (first.pos.z > second.pos.z) {
            first.velocity = first.velocity.copy(z = first.velocity.z - 1)
            second.velocity = second.velocity.copy(z = second.velocity.z + 1)
        } else if (first.pos.z < second.pos.z) {
            first.velocity = first.velocity.copy(z = first.velocity.z + 1)
            second.velocity = second.velocity.copy(z = second.velocity.z - 1)
        }
    }
}

data class Moon(var pos: Pos3D, var velocity: Pos3D = Pos3D(0, 0, 0)) {
    fun potentialEnergy(): Long {
        return Math.abs(pos.x) + Math.abs(pos.y) + Math.abs(pos.z)
    }

    fun kineticEnergy(): Long {
        return Math.abs(velocity.x) + Math.abs(velocity.y) + Math.abs(velocity.z)
    }

    fun totalEnergy(): Long {
        return potentialEnergy() * kineticEnergy()
    }
}

data class Pos3D(val x: Long, val y: Long, val z: Long)