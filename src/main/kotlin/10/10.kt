package `10`

import Coord
import java.lang.Math.abs
import java.lang.Math.atan2


fun main() {
    val asteroids = parseAsteroids(INPUT10)
    val visibleAsteroids = mutableMapOf<Coord, Set<Coord>>()

    asteroids.forEach {
        val visible = getVisible(it, asteroids)
        visibleAsteroids.put(it, visible)
    }

    println("first result")
    visibleAsteroids.maxBy { it.value.size }.let { println("${it?.key} ${it?.value?.size}") }

    println("second result")
    val base = visibleAsteroids.maxBy { it.value.size }?.key!!
    val asteroidsFromBase = asteroids
        .filter { it != base }
        .map { AsteroidInfo(it, getDegree(base, it), getDist(base, it)) }
        .groupBy { it.degree }
        .mapValues { it.value.toMutableList() }
        .toMutableMap()

    println(removeByLaser(asteroidsFromBase, 200).let { it.x * 100 + it.y })
}

fun removeByLaser(asteroidsFromBase: MutableMap<Double, MutableList<AsteroidInfo>>, cnt: Int): Coord {
    val degrees = asteroidsFromBase.keys.sorted()
    var i = 0
    var j = 0
    while (i <= cnt) {
        var degree: Double? = null
        var closest: AsteroidInfo? = null
        do {
            degree = dgr(degrees, j)
            closest = asteroidsFromBase[degree]?.minBy { it.distance }
            j++
        } while (closest == null)
        println("Vaporizing $i asteroid: $closest")
        asteroidsFromBase[degree]?.removeIf { it == closest }
        i++
        if (i == cnt) {
            return closest.coord
        }
    }
    throw RuntimeException()
}

fun dgr(degrees: List<Double>, i: Int): Double {
    return degrees[i % degrees.size]
}

fun getVisible(asteroid: Coord, asteroids: MutableSet<Coord>): MutableSet<Coord> {
    val asteroidsWithVisibility = mutableMapOf<Coord, Boolean>()

    asteroids.forEach { asteroidsWithVisibility.put(it, true) }
    asteroidsWithVisibility[asteroid] = false

    asteroids.forEach { checked ->
        if (asteroid != checked) {
            asteroids.forEach { other ->
                if (asteroid != other) {
                    if (checked != other) {
                        if (areOnSameLineOnSameSide(asteroid, checked, other)) {
                            if (isCloser(asteroid, checked, other)) {
                                asteroidsWithVisibility[other] = false
                            } else {
                                asteroidsWithVisibility[checked] = false
                            }
                        }
                    }
                }
            }
        }
    }
    return asteroidsWithVisibility.filter { it.value == true }.keys.toMutableSet()
}

fun isCloser(a: Coord, b: Coord, c: Coord): Boolean {
    val distA = getDist(a, b)
    val distB = getDist(a, c)
    return distA <= distB
}

fun getDist(a: Coord, b: Coord): Int {
    return (b.x - a.x) * (b.x - a.x) + (b.y - a.y) * (b.y - a.y)
}

fun areOnSameLineOnSameSide(a: Coord, b: Coord, c: Coord): Boolean {
    val sameLine = (c.y - b.y) * (b.x - a.x) == (b.y - a.y) * (c.x - b.x)
    return sameLine && getDist(b, c) < getDist(a, b) + getDist(a, c)
}

private fun getDegree(a: Coord, b: Coord): Double {
    val dx = a.x - b.x
    val dy = -(a.y - b.y)

    var inRads = atan2(dy.toDouble(), dx.toDouble())

    inRads = if (inRads < 0) abs(inRads) else 2 * Math.PI - inRads

    val result = Math.toDegrees(inRads) - 90.0
    return if (result < 0) 360.0 + result else result
}

private fun parseAsteroids(input: String): MutableSet<Coord> {
    val asteroids = mutableSetOf<Coord>()
    input.split("\n").forEachIndexed { rowIndex, line ->
        line.forEachIndexed { colIndex, char ->
            if (char == '#') {
                asteroids.add(Coord(colIndex, rowIndex))
            }
        }
    }
    return asteroids
}

data class AsteroidInfo(val coord: Coord, val degree: Double, val distance: Int)

const val INPUT10 = """##.##..#.####...#.#.####
##.###..##.#######..##..
..######.###.#.##.######
.#######.####.##.#.###.#
..#...##.#.....#####..##
#..###.#...#..###.#..#..
###..#.##.####.#..##..##
.##.##....###.#..#....#.
########..#####..#######
##..#..##.#..##.#.#.#..#
##.#.##.######.#####....
###.##...#.##...#.######
###...##.####..##..#####
##.#...#.#.....######.##
.#...####..####.##...##.
#.#########..###..#.####
#.##..###.#.######.#####
##..##.##...####.#...##.
###...###.##.####.#.##..
####.#.....###..#.####.#
##.####..##.#.##..##.#.#
#####..#...####..##..#.#
.##.##.##...###.##...###
..###.########.#.###..#."""
const val TEST101 = """.#..#
.....
#####
....#
...##"""
const val TEST102 = """......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####"""
const val TEST103 = """#.#...#.#.
.###....#.
.#....#...
##.#.#.#.#
....#.#.#.
.##..###.#
..#...##..
..##....##
......#...
.####.###."""
const val TEST104 = """.#..#..###
####.###.#
....###.#.
..###.##.#
##.##.#.#.
....###..#
..#.#..#.#
#..#.#.###
.##...##.#
.....#.#.."""
const val TEST105 = """.#..##.###...#######
##.############..##.
.#.######.########.#
.###.#######.####.#.
#####.##.#.##.###.##
..#####..#.#########
####################
#.####....###.#.#.##
##.#################
#####.##.###..####..
..######..##.#######
####.##.####...##..#
.#####..#.######.###
##...#.##########...
#.##########.#######
.####.#.###.###.#.##
....##.##.###..#####
.#.#.###########.###
#.#.#.#####.####.###
###.##.####.##.#..##"""

const val TEST_VAPOR = """.#....#####...#..
##...##.#####..##
##...#...#.#####.
..#.....#...###..
..#.#.....#....##"""