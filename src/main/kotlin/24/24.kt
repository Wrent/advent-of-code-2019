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