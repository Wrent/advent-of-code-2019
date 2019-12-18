fun main() {
    val underground = mutableMapOf<Coord, Underground>()
    INPUT18.split("\n")
        .forEachIndexed { i, row ->
            row.split("").filter { it != "" }.forEachIndexed { j, cell ->
                val coord = Coord(i, j)
                underground[coord] = Underground(coord, undergroundBlock(cell[0]), Int.MAX_VALUE, listOf())
            }
        }
    underground.values.forEach {
        val validNeighbours = mutableListOf<Underground>()
        processNeighbour(it.coord.north(), underground, validNeighbours)
        processNeighbour(it.coord.south(), underground, validNeighbours)
        processNeighbour(it.coord.east(), underground, validNeighbours)
        processNeighbour(it.coord.west(), underground, validNeighbours)
        it.validNeighbors = validNeighbours
    }

    val keyPairs = mutableMapOf<Pair<Coord, Coord>, Pair<Int, MutableSet<Char>>>()

    val keys = underground.values
            .filter { it.block is Key || it.block is Entrance }

    keys.forEach{first ->
        keys.forEach{second ->
            if (first !== second) {
                if (keyPairs[Pair(second.coord, first.coord)] == null) {
                    keyPairs[Pair(first.coord, second.coord)] = getDistanceWithRequiredKeys(first, second)
                }
            }
        }
    }

    val entrance = underground.values.first { it.block is Entrance }

    val pickedKeys = mutableSetOf<Char>('@')
        val results = mutableListOf<Int>()
////    val closedDoors = mutableSetOf<Char>()
    println("first result")

    start(entrance, pickedKeys, keyPairs, underground, results, 0)



//    val current = entrance
//
//    process(current, pickedKeys, underground, 0, results)
//
    println(results.min())

//    while (true) {
//        val keys = findAvailableKeys(current, pickedKeys, mutableSetOf<Coord>(), 0)
//        keys.forEach {
//            // move to this key
//            current = underground[it.first]!!
//            // pick this key
//            val pickedKeysCopy = HashSet(pickedKeys)
//            pickedKeysCopy.add(current.block.char)
//            // find available keys for this key and do the same
//            findAvailableKeys(current, pickedKeysCopy, mutableSetOf(), it.second)
//        }
//    }

//    enter(underground, entrance, pickedKeys, closedDoors, 0)
//    probe(underground, entrance.coord, 0, mutableSetOf())

}

fun start(current: Underground, pickedKeys: MutableSet<Char>, keyPairs: MutableMap<Pair<Coord, Coord>, Pair<Int, MutableSet<Char>>>, underground: MutableMap<Coord, Underground>, results: MutableList<Int>, steps: Int) {
    val availableKeys = keyPairs
            .filter { it.key.first == current.coord || it.key.second == current.coord }
            .filter { pickedKeys.containsAll(it.value.second) }

    availableKeys.forEach lambda@{
        // move to this key
        val next = if (it.key.first == current.coord) {
            underground[it.key.second]!!
        } else {
            underground[it.key.first]!!
        }
        if (pickedKeys.contains(next.block.char)) {
            return@lambda
        }
        val distance = keyPairs[it.key]!!.first
        val min = results.min() ?: Int.MAX_VALUE
        println("${steps + distance} $next $pickedKeys $min")
        if (steps + distance >= min) {
            return@lambda
        }
        // pick this key
        val pickedKeysCopy = HashSet(pickedKeys)
        pickedKeysCopy.add(next.block.char)
        //check results
        if (!underground.hasKeys(pickedKeysCopy)) {
//            println("adding ${it.second}")
            results.add(steps + distance)
            return@lambda
        }
        // find available keys for this key and do the same
        start(next, pickedKeysCopy, keyPairs, underground, results, steps + distance)
    }
}

val cache = mutableMapOf<Coord, MutableMap<Set<Char>, List<Pair<Coord, Int>>>>()

fun process(current: Underground, pickedKeys: MutableSet<Char>, underground: MutableMap<Coord, Underground>, steps: Int, results: MutableList<Int>) {
    val keys = cache[current.coord]?.get(pickedKeys.toSortedSet())?.apply { println("cache hit") } ?: run {
        val tmp = findAvailableKeys(current, pickedKeys, mutableSetOf(), 0)
        cache.putIfAbsent(current.coord, mutableMapOf())
        cache[currentCoord]?.put(pickedKeys.toSortedSet(), tmp)
        tmp
    }
//    println(keys)
//    println(current)
    val minA = results.min() ?: Int.MAX_VALUE
    if (steps >= minA) {
        return
    }
    keys.forEach {
        // move to this key
        val next = underground[it.first]!!
        val min = results.min() ?: Int.MAX_VALUE
        println("${steps + it.second} $next $pickedKeys $min")
        if (pickedKeys.contains(next.block.char) || steps + it.second >= min) {
            return
        }
        // pick this key
        val pickedKeysCopy = HashSet(pickedKeys)
        pickedKeysCopy.add(next.block.char)
        //check results
        if (!underground.hasKeys(pickedKeysCopy)) {
//            println("adding ${it.second}")
            results.add(steps + it.second)
            return
        }
//        cache.putIfAbsent(next.coord, mutableMapOf())
//        cache[next.coord]!![pickedKeysCopy] = it.second
        // find available keys for this key and do the same
        process(next, pickedKeysCopy, underground, steps + it.second, results)
    }
//    println("exiting")
}

fun getDistanceWithRequiredKeys(first: Underground, second: Underground): Pair<Int, MutableSet<Char>> {
    val requiredKeys = mutableSetOf<Char>()
    val visited = mutableSetOf<Coord>()

    return find(first, second, requiredKeys, visited, 0)
            .minBy { it.first }!!
}

fun find(current: Underground, second: Underground, requiredKeys: MutableSet<Char>, visited: MutableSet<Coord>, steps: Int) : List<Pair<Int, MutableSet<Char>>> {
    if (current == second) {
        return listOf(Pair(steps, requiredKeys))
    }

    val newKeys = HashSet(requiredKeys)
    if (current.block is Door) {
        newKeys.add(current.block.key())
    }
    visited.add(current.coord)
    return current.validNeighbors
            .filter { !visited.contains(it.coord) }
            .flatMap { find(it, second, newKeys, visited, steps + 1) }
}


fun findAvailableKeys(current: Underground, pickedKeys: MutableSet<Char>, visited: MutableSet<Coord>, steps: Int): List<Pair<Coord, Int>> {
    val resultList = mutableListOf<Pair<Coord, Int>>()
    if (current.block is Key && !pickedKeys.contains(current.block.char)) {
        resultList.add(Pair(current.coord, steps))
    }
    visited.add(current.coord)

    val list = current.validNeighbors
        .filter { !visited.contains(it.coord) }
        .filter { canBeOpened(it, pickedKeys) }
        .flatMap { findAvailableKeys(it, pickedKeys, HashSet(visited), steps + 1) } + resultList
    return list.shuffled()
}

fun canBeOpened(it: Underground, pickedKeys: MutableSet<Char>): Boolean {
    return when (it.block) {
        is Door -> return pickedKeys.contains(it.block.key())
        else -> true
    }
}

//fun enter(underground: MutableMap<Coord, Underground>, block: Underground, pickedKeys: MutableSet<Char>, closedDoors: MutableSet<Char>, steps: Int) {
//    if (!underground.hasKeys()) {
//        throw RuntimeException("${steps - 1}")
//    }
//    block.validNeighbors
//        .filter { it.block is Door && !pickedKeys.contains(it.block.key()) }
//        .forEach { closedDoors.add(it.block.char) }
//    block.steps = steps
//    block.validNeighbors.forEach {
//        if (it.steps < steps && closedDoors.isEmpty()) {
//            return
//        }
//        if (it.block is Door) {
//            if (!pickedKeys.contains(it.block.key())) {
//                return
//            } else {
//                underground[it.coord] = Underground(it.coord, Empty(), block.steps, it.validNeighbors)
//                pickedKeys.remove(it.block.key())
//            }
//        }
//        if (it.block is Key) {
//            pickedKeys.add(block.block.char)
//            underground[it.coord] = Underground(it.coord, Empty(), block.steps, it.validNeighbors)
//        }
//        enter(underground, it, HashSet(pickedKeys), HashSet(closedDoors), steps + 1)
//    }
//}

fun processNeighbour(coord: Coord, underground: MutableMap<Coord, Underground>, validNeighbors: MutableList<Underground>) {
    val block = underground[coord] ?: return
    when (block.block) {
        is Wall -> return
    }
    validNeighbors.add(block)
}

//fun probe(underground: MutableMap<Coord, Underground>, coord: Coord, steps: Int, pickedKeys: MutableSet<Char>, unopenedDoors: MutableSet<Char>, seenCoords: MutableSet<Coord>) {
//    val block = underground[coord]!!
//
//    if (seenCoords.contains(coord) && unopenedDoors.isEmpty()) {
//        return
//    }
//
//    seenCoords.add(coord)
//    when (block.block) {
//        is Wall -> return
//        is Empty -> {
//        }
//        is Entrance -> {
//        }
//        is Key -> {
//            pickedKeys.add(block.block.char)
//            underground[coord] = Underground(coord, Empty(), block.steps)
//        }
//        is Door -> {
//            if (pickedKeys.contains(block.block.key())) {
//                underground[coord] = Underground(coord, Empty(), block.steps)
//                pickedKeys.remove(block.block.key())
//            } else {
//
//                return
//            }
//        }
//    }
//    if (!underground.hasKeys()) {
//        println(steps)
//        return
//    }
//
//    block.steps = steps
//    probe(underground, coord.north(), steps + 1, HashSet(pickedKeys))
//    probe(underground, coord.south(), steps + 1, HashSet(pickedKeys))
//    probe(underground, coord.west(), steps + 1, HashSet(pickedKeys))
//    probe(underground, coord.east(), steps + 1, HashSet(pickedKeys))
//}


private fun MutableMap<Coord, Underground>.hasKeys(): Boolean {
    return this.values.filter { it.block is Key }.count() > 0
}

private fun MutableMap<Coord, Underground>.hasKeys(pickedKeys: MutableSet<Char>): Boolean {
    return !pickedKeys.containsAll(this.values.filter { it.block is Key }.map { it.block.char }.toSet())
}

data class Underground(val coord: Coord, val block: UndergroundBlock, var steps: Int, var validNeighbors: List<Underground>) {
    override fun toString(): String {
        return "Underground(coord=$coord, block=$block)"
    }
}

fun undergroundBlock(char: Char): UndergroundBlock {
    return when (char) {
        '#' -> Wall()
        '.' -> Empty()
        '@' -> Entrance()
        else -> {
            if (char.toUpperCase() == char) {
                Door(char)
            } else {
                Key(char)
            }
        }
    }
}

sealed class UndergroundBlock(val char: Char)


class Wall : UndergroundBlock('#')
class Empty : UndergroundBlock('.')
class Entrance : UndergroundBlock('@')
class Door(char: Char) : UndergroundBlock(char) {
    fun key(): Char = this.char.toLowerCase()
}

class Key(char: Char) : UndergroundBlock(char) {
    fun door(): Char = this.char.toUpperCase()
    override fun toString(): String {
        return "Key($char)"
    }


}

const val TEST181 = """#########
#b.A.@.a#
#########"""
const val TEST182 = """########################
#f.D.E.e.C.b.A.@.a.B.c.#
######################.#
#d.....................#
########################"""
const val TEST183 = """########################
#...............b.C.D.f#
#.######################
#.....@.a.B.c.d.A.e.F.g#
########################"""
const val TEST184 = """#################
#i.G..c...e..H.p#
########.########
#j.A..b...f..D.o#
########@########
#k.E..a...g..B.n#
########.########
#l.F..d...h..C.m#
#################"""
const val TEST185 = """########################
#@..............ac.GI.b#
###d#e#f################
###A#B#C################
###g#h#i################
########################"""
const val INPUT18 = """#################################################################################
#.#...#.........#i........U.........#...#.........#.......#.............#.....E.#
#.#.#.#.###.###Q#########.#########I#X#####.#####.#####.#.#.#######.###.#.#####.#
#...#..u..#...#.#.........#l#...D.#.#...#...#...#.......#.#.#.......#.#.#.#...#.#
#############.#.#.#########.#.###.#.###.#.###.#.#.#######.###.#######.#.#.#B#.#.#
#...A.......#.#...#.#.....#...#...#...#.#.#.#.#...#...#......p#.......#.#.#.#.#.#
#.#########.#.#####.#.#.#S#.###.#####.#.#.#.#.#####.#.#########.###.###.###.#.#.#
#.#.........#...#...#.#.#.#...#.....#.#.#.#...#.#...#.......#.....#.#...#...#...#
#.#########.#.#.###.#.#.#####.###.#.#.#.#.#.###.#.#########.#####.#.#.###.#####.#
#.#.......#.#q#...#...#.#.....#.#.#.#...#.#.....#.#.....#.#.K.#...#.......#...#.#
#.#.#####.#.#.###W#####.#.#####.#.#.###.#.#######.###.#.#.###.#.###########.#.#.#
#...#...#.#y#...#.....#.#...L...#.#d#...#.....#...#...#.....#.#.#...#...#...#...#
#####.#.#.#.#########.#.#######.#.###.#######.#.###.#########.###.#.#.#.#.#######
#.....#...#.#.........#.N.....#n#...#x#.#.....#.#.....#.......#...#.#.#.#f#..o..#
#.#########.#.#########.###.###.###.#.#.#.###.#.#.###.#.#######.###.#.###.#.###.#
#...#.#.....#...#.....#.#.#...#.#.#.....#..t#.#.#...#.#...#...#...#.#.#...#...#.#
###O#.#.#####.#.#.#.###.#.###.#.#.#####.###.###.#####.###.#.#.#.###.#.#.#####.#.#
#...#...#.....#.#.#.#..s#...#.....#..v#.#...#...........#.#.#...#...#.......#.#.#
#.###.#########.#.#.#.###.#.#####.#.#.###.###.###########.#.#####.#########.###.#
#.#..g..........#.#.......#.#...#.#.#...#.#...#.#.......#.......#.#...#.....#...#
#.#.#############.#########.#.#.###.###.#.#.###.#.#####.#.#######.#.#.#.#####.#.#
#.#.#...........#.#.#.#....z#.#.....#...#.#.....#...#.#.#.#.#...#.#.#.#.#.....#.#
#.###.#########.#.#.#.#######.#######.###.#####.###.#.#.#.#.###.#.#.#.#.#####.#.#
#.....#...#.....#...#.........#.....#...#.....#...#...#.#.....#.#...#.#.#.....#.#
#.#####.#.#.#######.###########.#######.#.#######.###.#.###.###.#####.#.#.#####.#
#.....#.#.#...#.Z.#.#....j..#.........#.#...#.......#.#.#...#...#.....#.#.#...#.#
#######.#.###.#.###.#.#####.#.#####.###.#.#.#.#######.#.#####.###.#####.#.###.#.#
#.......#.#...#.....#.#...#.#...#.#.....#.#.#.#.#.....#...#...#...#.....#...#.#.#
#.#######.#.#.#####.#.###.#.###.#.#######.#.#.#.#.#######.#.###.#.###.#####.#.#.#
#.#.#.....#.#.#...#.#...#.#...#...#.#...#.#...#.#.....#.#...#...#...#.......#...#
#.#.#.#####.#.#.#.#####.#.###.###.#.#.#.#.#####.#####.#.#####.#####.#########.###
#.#...#.....#.#.#.#...#...#.#.#...#...#.#.#...#.....#...#.#...#...#.....#...#.#.#
#.###.###.#.###.#.#.#.###.#.#.#.#######.#.#.#.#####.###.#.#.###.#.#####.###.#.#.#
#...#...#.#.#...#.#.#...#.#.#.#.#.......#...#.........#.#...#...#.#...#...#.#...#
#.#.###.#.#.#.###.#.###.#.#.#.#.###.###.###########.###.#.###.###.###.###.#.###.#
#.#...#.#.#.#...#...#...#.#.#.#.....#...#...#.....#.#...#.....#.....#.#...#..h#.#
#.###.#.#.#####.#####.#.#.#.#.#######.###.#.#####.#.#.#############.#.#.###.#.#.#
#...#.#.F.#...#.....#.#.#.#.#.#...#...#.#.#.......#.#.#...#...#...#...#.#.#.#...#
###.#.#####.#.#####.#.###.#.#.#.#.#.###.#.#########.#.#.#.#.#.#.#.#####.#.#.#####
#...#.......#.......#.......#...#..................r#...#...#...#.......#.......#
#######################################.@.#######################################
#...........#.............#.#.....#...........#...........#...#.....#.....#.....#
#########.#.#.###########.#.#.#.###.#.#.#.#.###.#####.#####.#.#####.#.###.#.#.###
#...#.....#.#...#.......#.#.#.#.....#.#.#.#.....#.....#.....#.#...#....w#...#...#
#.#.#.#####.###.#####.#.#.#.#.#####.#.###.#######.###.#.#####.#.#.#####.#######.#
#.#.H.#...#...#.#...#.#.#.#...#...#.#...#.#.....#.#...#.....#...#...#...#.....#.#
#.#.###.#.#####.#.#.#.###.#.###.#.###.#.#.###.#.#.#####.#######.###.#####.###.#.#
#.#.#...#.......#.#...#...#.#...#...#.#.#...#.#.#.......#.....#.#...#.....#.#a#.#
#.#.#.###########.#####.###R#.#####.###.###.###.#########.###.#.#.###.#####.#.#.#
#.#.#.#.....#.....#...#.#...#.#...#...#.#.#...#.....#.....#...#.#.#.......#...#.#
#.#.#.#.###.#.#.###.#.#.#####.#.#####.#.#.###.#.#.#.#.#####.#####.#.#####.#.###.#
#.#.#.#...#...#.....#.#...#...#.....#...#.#...#.#.#.#.#...........#.#.....#...#.#
#.#.#.###.###########.###.#.###.###.###.#.#.###.#.###.#############.#.#######.#Y#
#.#.#.#.#.#.........#...#.#...#.#.#...#.#.#...#.#.#...#...#.#.....#.#.....#...#.#
#.#.#.#.#.#########.#.###.###.#.#.#.#.#.#.###.###.#.###.#.#.#.#.###.#####.#.###.#
#.#.#.#...#.........#.........#...#.#.#.#...#.....#.....#.#...#.........#.#.#...#
#.###.#.###.#.#######.###########.#.###.#.#######.#######.#.#############.#.#.#.#
#...#.#.#...#.#.....#.#.....#...J.#.....#...#...#...#.....#.#...#...#.....#...#.#
###.#.#.###.#.#.###.###.###.#.###########.#.#.#.###.#.#####.#.#.###.#.#########.#
#...#.#.....#.#...#.....#...#.#...#.....#.#.#.#.....#.#.......#.....#.....#.....#
#.###.###.#######.#######.###.#.###.#.#####.#.#######.###.#########.#####.#.#####
#...#.#.#.#.......#.#.....#.#.#.....#...#.V.#.....#.#...#...#.#...#...#...#.....#
###.#.#.#.#.#######.#######.#.#########.#.#######.#.###.###.#.#.#####.#.#######.#
#...#.#...#.#.......#.......#.......#...#.......#.#...#...#.#.#...#...#.#...#...#
#.###.#####.#.#####.#.#.###.#######.#.#.#.#.###.#.#.#.###.###.###.#.###.#.#G#.###
#...#.....#...#...#.#.#.#.#.#.....#...#.#.#.#...#.#.#b..#...#...#.#.#c..#.#.....#
#.#.#####.#####.#.#.#.#.#.#.###.#######.#.#.#####.#####.###.###.#.#.#.###########
#.#.C...#.......#...#.#.#.......#...#...#.#.#...#.#.....#.#.#...#...#...#......m#
#.#####.#############.#.###.#####.#.#.###.#.#.#T#.#.###.#.#.#.#####.###.#.#####.#
#...#.#.......#.....#.#...#.#.....#...#.#.#.#.#...#.#...#.#.#.....#...#.....#...#
###.#.#####.###.#.#.#.###.#.#.#########.#.#.#.#####.#.###.#.#####.###.#######.#.#
#.#...#...#.#...#.#.#.#...#.#.....#.....#.#...#.....#.#...#.....#.#...#.#...#.#.#
#.###.#.###M#.###.###.#.#########.###.#.#.#####.#.###.#.#.#.#####.###.#.#.#.#.#.#
#.....#...#...#...#...#...#.......#...#.#.#...#.#...#.#.#.#.....#...#...#.#...#.#
#.#######.#####.#.#.#####.#.#######.###.#.#.#.#####.#.#.#######.###.#####.#####.#
#.#.........#...#.#.#.#...#.#.......#...#k#.#.......#.#.....#...#...#.....#.....#
#.#.#######.#.###.#.#.#.###.###.#####.###.#.#########.#####.#.###.###.#####.#####
#...#...#...#...#.#...#...#...#...#.#.#.#...#.#.....#.#e....#.....#...#...#.#...#
#####.#.#.#####.#####.###P###.###.#.#.#.#####.#.#.###.#.###.#######.###.###.#.#.#
#.....#.......#.........#.........#.....#.......#.......#...........#.........#.#
#################################################################################"""