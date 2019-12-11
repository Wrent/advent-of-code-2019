package `11`

import Coord
import HaltException
import parseInstr
import relativeBase
import java.math.BigInteger

fun main() {
    val map = mutableMapOf<Coord, Color>()

    map.put(Coord(0, 0), Color.WHITE) // part two

    var robotPosition = Coord(0, 0)
    var direction = Direction.UP
    var outputCnt = 0

    val data = mutableMapOf<BigInteger, BigInteger>()
    INPUT11.split(",").map { it.toBigInteger() }.forEachIndexed { index, i -> data[index.toBigInteger()] = i }

    var index = BigInteger.ZERO
    relativeBase = BigInteger.ZERO
    try {

        while (true) {
            val prevDirection = direction
            val instr = parseInstr(index, data, { provideInput(map, robotPosition) }, {
                direction = handleOutput(it, map, robotPosition, direction, outputCnt++)
            })
            index = instr.apply(data, index)
            if (prevDirection != direction) {
                robotPosition = direction.move(robotPosition)
                println(robotPosition)
            }
        }
    } catch (ex: HaltException) {
        println("first result")
        println(map.size)

        val minX = map.map { it.key.x }.min()!!
        val maxX = map.map { it.key.x }.max()!!
        val minY = map.map { it.key.y }.min()!!
        val maxY = map.map { it.key.y }.max()!!

        for (i in minY..maxY) {
            for (j in minX..maxX) {
                print(map.g(Coord(j, i)).char)
            }
            println()
        }
    }
}


fun handleOutput(output: BigInteger, map: MutableMap<Coord, Color>, robotPosition: Coord, direction: Direction, outputCnt: Int): Direction {
    if (outputCnt % 2 == 0) {
        // painting
        if (output == BigInteger.ZERO) {
            map.put(robotPosition, Color.BLACK)
        } else if (output == BigInteger.ONE) {
            map.put(robotPosition, Color.WHITE)
        }
    } else {
        // rotating
        if (output == BigInteger.ZERO) {
            return direction.left()
        } else if (output == BigInteger.ONE) {
            return direction.right()
        }
    }
    return direction
}

fun provideInput(map: MutableMap<Coord, Color>, robotPosition: Coord): BigInteger {
    return map.g(robotPosition).value
}

fun MutableMap<Coord, Color>.g(coord: Coord): Color {
    return this.get(coord) ?: Color.BLACK
}

enum class Direction {
    UP {
        override fun left(): Direction = LEFT
        override fun right(): Direction = RIGHT
        override fun move(coord: Coord): Coord = Coord(coord.x, coord.y - 1)
    },
    RIGHT {
        override fun left(): Direction = UP
        override fun right(): Direction = DOWN
        override fun move(coord: Coord): Coord = Coord(coord.x + 1, coord.y)
    },
    DOWN {
        override fun left(): Direction = RIGHT
        override fun right(): Direction = LEFT
        override fun move(coord: Coord): Coord = Coord(coord.x, coord.y + 1)
    },
    LEFT {
        override fun left(): Direction = DOWN
        override fun right(): Direction = UP
        override fun move(coord: Coord): Coord = Coord(coord.x - 1, coord.y)
    };

    abstract fun right(): Direction
    abstract fun left(): Direction
    abstract fun move(coord: Coord): Coord
}

enum class Color(val value: BigInteger, val char: Char) {
    BLACK(0.toBigInteger(), ' '),
    WHITE(1.toBigInteger(), '#')
}

const val INPUT11 =
    """3,8,1005,8,328,1106,0,11,0,0,0,104,1,104,0,3,8,1002,8,-1,10,1001,10,1,10,4,10,1008,8,0,10,4,10,1001,8,0,29,1,104,7,10,3,8,1002,8,-1,10,101,1,10,10,4,10,1008,8,0,10,4,10,1001,8,0,55,1,2,7,10,1006,0,23,3,8,102,-1,8,10,1001,10,1,10,4,10,1008,8,0,10,4,10,1001,8,0,84,1006,0,40,1,1103,14,10,1,1006,16,10,3,8,102,-1,8,10,101,1,10,10,4,10,108,1,8,10,4,10,1002,8,1,116,1006,0,53,1,1104,16,10,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,1,10,4,10,102,1,8,146,2,1104,9,10,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,1,10,4,10,1001,8,0,172,1006,0,65,1,1005,8,10,1,1002,16,10,3,8,102,-1,8,10,1001,10,1,10,4,10,108,0,8,10,4,10,102,1,8,204,2,1104,9,10,1006,0,30,3,8,102,-1,8,10,101,1,10,10,4,10,108,0,8,10,4,10,102,1,8,233,2,1109,6,10,1006,0,17,1,2,6,10,3,8,102,-1,8,10,101,1,10,10,4,10,108,1,8,10,4,10,102,1,8,266,1,106,7,10,2,109,2,10,2,9,8,10,3,8,102,-1,8,10,101,1,10,10,4,10,1008,8,1,10,4,10,1001,8,0,301,1,109,9,10,1006,0,14,101,1,9,9,1007,9,1083,10,1005,10,15,99,109,650,104,0,104,1,21102,1,837548789788,1,21101,0,345,0,1106,0,449,21101,0,846801511180,1,21101,0,356,0,1106,0,449,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,3,10,104,0,104,1,3,10,104,0,104,0,3,10,104,0,104,1,21101,235244981271,0,1,21101,403,0,0,1105,1,449,21102,1,206182744295,1,21101,0,414,0,1105,1,449,3,10,104,0,104,0,3,10,104,0,104,0,21102,837896937832,1,1,21101,0,437,0,1106,0,449,21101,867965862668,0,1,21102,448,1,0,1106,0,449,99,109,2,22102,1,-1,1,21101,40,0,2,21102,1,480,3,21101,0,470,0,1106,0,513,109,-2,2106,0,0,0,1,0,0,1,109,2,3,10,204,-1,1001,475,476,491,4,0,1001,475,1,475,108,4,475,10,1006,10,507,1101,0,0,475,109,-2,2106,0,0,0,109,4,1201,-1,0,512,1207,-3,0,10,1006,10,530,21102,1,0,-3,22102,1,-3,1,21201,-2,0,2,21102,1,1,3,21102,549,1,0,1106,0,554,109,-4,2105,1,0,109,5,1207,-3,1,10,1006,10,577,2207,-4,-2,10,1006,10,577,21202,-4,1,-4,1106,0,645,21202,-4,1,1,21201,-3,-1,2,21202,-2,2,3,21101,596,0,0,1106,0,554,21201,1,0,-4,21102,1,1,-1,2207,-4,-2,10,1006,10,615,21101,0,0,-1,22202,-2,-1,-2,2107,0,-3,10,1006,10,637,22102,1,-1,1,21101,637,0,0,105,1,512,21202,-2,-1,-2,22201,-4,-2,-4,109,-5,2106,0,0"""