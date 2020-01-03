fun main() {
    val map = mutableMapOf<Coord, Eris>()

    val history = mutableSetOf<String>()
    INPUT24.split("\n")
        .forEachIndexed { i, row ->
            row.split("").filter { it != "" }.forEachIndexed { j, cell ->
                val coord = Coord(j, i)
                map[coord] = eris(cell[0])
            }
        }

    var nextMap = map
    while (true) {
        val biodiversity = biodiversity(nextMap)
        if (history.contains(biodiversity)) {
            println("first result")
            println(biodiversity.toInt(2))
            break;
        }
        history.add(biodiversity)
        nextMap = nextMap.mapValues { getNext(nextMap, it.key) }.toMutableMap()
    }

    val recursiveMap = mutableMapOf<Int, MutableMap<Coord, Eris>>()
    recursiveMap[0] = mutableMapOf()

    INPUT24.split("\n")
        .forEachIndexed { i, row ->
            row.split("").filter { it != "" }.forEachIndexed { j, cell ->
                val coord = Coord(j, i)
                recursiveMap[0]!![coord] = eris(cell[0])
            }
        }

    var nextRecursiveMap = recursiveMap
    initLevels(nextRecursiveMap)
    for (i in 0 until 200) {
        val newMap = mutableMapOf<Int, MutableMap<Coord, Eris>>()
        nextRecursiveMap.forEach {
            newMap.putIfAbsent(it.key, mutableMapOf())
            it.value.forEach { inner ->
                val next = getNext(nextRecursiveMap, inner.key, it.key)
                newMap[it.key]!![inner.key] = next
            }
        }
        initLevels(newMap)
        nextRecursiveMap = newMap
    }
    println("second result")
    println(nextRecursiveMap.values.flatMap { it.values }.filter { it == Eris.BUG }.count())
}

fun initLevels(map: MutableMap<Int, MutableMap<Coord, Eris>>) {
    initLevel(map, map.keys.min()!! - 1)
    initLevel(map, map.keys.max()!! + 1)
}

private fun initLevel(map: MutableMap<Int, MutableMap<Coord, Eris>>, level: Int) {
    map[level] = mutableMapOf()
    for (i in 0..4) {
        for (j in 0..4) {
            map[level]!![Coord(i, j)] = Eris.EMPTY
        }
    }
}

fun getNext(map: MutableMap<Int, MutableMap<Coord, Eris>>, coord: Coord, level: Int): Eris {
    if (coord == Coord(2, 2)) {
        return Eris.EMPTY
    }
//    map.putIfAbsent(level - 1, mutableMapOf())
    val count = (listOf(
        map[level]!![coord.north()] ?: map[level - 1]?.get(Coord(2, 1)) ?: Eris.EMPTY,
        map[level]!![coord.south()] ?: map[level - 1]?.get(Coord(2, 3)) ?: Eris.EMPTY,
        map[level]!![coord.east()] ?: map[level - 1]?.get(Coord(3, 2)) ?: Eris.EMPTY,
        map[level]!![coord.west()] ?: map[level - 1]?.get(Coord(1, 2)) ?: Eris.EMPTY
    ) + addSubgrid(map, coord, level))
        .filter { it == Eris.BUG }
        .count()
    when (map[level]!![coord]) {
        Eris.BUG ->
            return if (count == 1) {
                Eris.BUG
            } else {
                Eris.EMPTY
            }
        Eris.EMPTY -> {
            return if (count == 1 || count == 2) {
                Eris.BUG
            } else {
                Eris.EMPTY
            }
        }
    }
    throw RuntimeException()
}

fun addSubgrid(map: MutableMap<Int, MutableMap<Coord, Eris>>, coord: Coord, level: Int): List<Eris> {
//    map.putIfAbsent(level + 1, mutableMapOf())
    return when (coord) {
        Coord(2, 1) ->
            listOf(
                map[level + 1]?.get(Coord(0,0)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(1,0)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(2,0)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(3,0)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(4,0)) ?: Eris.EMPTY
            )
        Coord(2, 3) ->
            listOf(
                map[level + 1]?.get(Coord(0,4)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(1,4)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(2,4)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(3,4)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(4,4)) ?: Eris.EMPTY
            )
        Coord(1, 2) ->
            listOf(
                map[level + 1]?.get(Coord(0,0)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(0,1)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(0,2)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(0,3)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(0,4)) ?: Eris.EMPTY
            )
        Coord(3, 2) ->
            listOf(
                map[level + 1]?.get(Coord(4,0)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(4,1)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(4,2)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(4,3)) ?: Eris.EMPTY,
                map[level + 1]?.get(Coord(4,4)) ?: Eris.EMPTY
            )
        else ->
            listOf()
    }
}

fun biodiversity(map: Map<Coord, Eris>): String {
    val maxX = map.keys.maxBy { it.x }!!.x
    val maxY = map.keys.maxBy { it.y }!!.y
    val minX = map.keys.minBy { it.x }!!.x
    val minY = map.keys.minBy { it.y }!!.y

    var res = ""
    for (i in minY..maxY) {
        for (j in minX..maxX) {
            val current = map[Coord(j, i)]
            if (current == Eris.BUG) {
                res = "1" + res
            } else {
                res = "0" + res
            }
        }
    }
    return res
}

fun getNext(map: Map<Coord, Eris>, coord: Coord): Eris {
    val count = listOf(
        map[coord.north()],
        map[coord.south()],
        map[coord.east()],
        map[coord.west()]
    )
        .filterNotNull()
        .filter { it == Eris.BUG }
        .count()
    when (map[coord]) {
        Eris.BUG ->
            return if (count == 1) {
                Eris.BUG
            } else {
                Eris.EMPTY
            }
        Eris.EMPTY -> {
            return if (count == 1 || count == 2) {
                Eris.BUG
            } else {
                Eris.EMPTY
            }
        }
    }
    throw RuntimeException()
}

enum class Eris(val char: Char) {
    BUG('#'),
    EMPTY('.')
}

fun eris(char: Char): Eris {
    return Eris.values().find { it.char == char }!!
}

const val INPUT24 = """#####
.#.#.
.#..#
....#
..###"""

const val TEST241 = """....#
#..#.
#..##
..#..
#...."""