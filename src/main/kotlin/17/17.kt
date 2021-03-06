import java.math.BigInteger

fun main() {
    val space = mutableMapOf<Coord, ViewPoint>()
    var outputCnt = 0

    val data = mutableMapOf<BigInteger, BigInteger>()
    INPUT17.split(",").map { it.toBigInteger() }.forEachIndexed { index, i -> data[index.toBigInteger()] = i }
    data[0.toBigInteger()] = 2.toBigInteger()

    var index = BigInteger.ZERO
    relativeBase = BigInteger.ZERO
    try {
        while (true) {
            val instr = parseInstr(index, data, { getInput(space) }, {
                handleOutput(it, space, outputCnt++)
            })
            index = instr.apply(data, index)
        }
    } catch (ex: HaltException) {
        println("first result")
        val intersections = space.filter { it.value == ViewPoint.SCAFFOLD }
            .map { it.key }
            .filter { it.isIntersection(space) }

//        println(intersections.map { it.x * it.y }.sum())
    }
}

val inputs = "A,B,A,B,C,B,C,A,C,C\nR,12,L,10,L,10\nL,6,L,12,R,12,L,4\nL,12,R,12,L,6\nn\n".toCharArray().toList().iterator()

fun getInput(space: MutableMap<Coord, ViewPoint>) : BigInteger {
    space.print()
    return inputs.next().toInt().toBigInteger()
}

private fun MutableMap<Coord, ViewPoint>.print() {
    val maxX = this.keys.maxBy { it.x }!!.x
    val maxY = this.keys.maxBy { it.y }!!.y

    for (j in 0..maxY) {
        for (i in 0..maxX) {
            print(this[Coord(i, j)]?.toChar())
        }
        println()
    }
}

private fun Coord.isIntersection(space: MutableMap<Coord, ViewPoint>): Boolean {
    return space[this.north()] == ViewPoint.SCAFFOLD
            && space[this.south()] == ViewPoint.SCAFFOLD
            && space[this.east()] == ViewPoint.SCAFFOLD
            && space[this.west()] == ViewPoint.SCAFFOLD
}

var currentCoord = Coord(0, 0)

fun handleOutput(output: BigInteger, space: MutableMap<Coord, ViewPoint>, outputCnt: Int) {
    if (output == 10.toBigInteger()) {
        currentCoord = Coord(0, currentCoord.y + 1)
    } else {
        try {
            space[currentCoord] = viewPoint(output)
        } catch (ex: java.lang.RuntimeException) {

        }
        currentCoord = Coord(currentCoord.x + 1, currentCoord.y)
    }
}
// R,12,L,10,L,10, A
// L,6,L,12,R,12,L,4 B
// R,12,L,10,L,10, A
// L,6,L,12,R,12,L,4 B
// L,12,R,12,L,6, C
// L,6,L,12,R,12,L,4 B
// L,12,R,12,L,6 C
// R,12,L,10,L,10, A
// L,12,R,12,L,6 C
// L,12,R,12,L,6 C
enum class ViewPoint(val value: BigInteger) {
    SPACE(46.toBigInteger()),
    SCAFFOLD(35.toBigInteger()),
    NORTH(94.toBigInteger()),
    SOUTH(118.toBigInteger()),
    WEST(60.toBigInteger()),
    EAST(62.toBigInteger());

    fun toChar(): Char {
        return value.toInt().toChar()
    }
}

fun viewPoint(value: BigInteger): ViewPoint {
    return ViewPoint.values().firstOrNull() { it.value == value } ?: throw RuntimeException("value not found: $value")
}

const val INPUT17 =
    """1,330,331,332,109,3888,1101,1182,0,15,1101,1469,0,24,1001,0,0,570,1006,570,36,102,1,571,0,1001,570,-1,570,1001,24,1,24,1106,0,18,1008,571,0,571,1001,15,1,15,1008,15,1469,570,1006,570,14,21101,58,0,0,1106,0,786,1006,332,62,99,21102,1,333,1,21101,0,73,0,1106,0,579,1102,0,1,572,1101,0,0,573,3,574,101,1,573,573,1007,574,65,570,1005,570,151,107,67,574,570,1005,570,151,1001,574,-64,574,1002,574,-1,574,1001,572,1,572,1007,572,11,570,1006,570,165,101,1182,572,127,1001,574,0,0,3,574,101,1,573,573,1008,574,10,570,1005,570,189,1008,574,44,570,1006,570,158,1105,1,81,21102,1,340,1,1105,1,177,21102,477,1,1,1106,0,177,21101,0,514,1,21101,0,176,0,1106,0,579,99,21102,184,1,0,1106,0,579,4,574,104,10,99,1007,573,22,570,1006,570,165,102,1,572,1182,21102,375,1,1,21101,0,211,0,1105,1,579,21101,1182,11,1,21102,1,222,0,1106,0,979,21102,1,388,1,21102,233,1,0,1105,1,579,21101,1182,22,1,21101,244,0,0,1105,1,979,21101,401,0,1,21102,1,255,0,1106,0,579,21101,1182,33,1,21102,1,266,0,1106,0,979,21101,0,414,1,21102,1,277,0,1105,1,579,3,575,1008,575,89,570,1008,575,121,575,1,575,570,575,3,574,1008,574,10,570,1006,570,291,104,10,21102,1182,1,1,21102,1,313,0,1106,0,622,1005,575,327,1102,1,1,575,21102,1,327,0,1106,0,786,4,438,99,0,1,1,6,77,97,105,110,58,10,33,10,69,120,112,101,99,116,101,100,32,102,117,110,99,116,105,111,110,32,110,97,109,101,32,98,117,116,32,103,111,116,58,32,0,12,70,117,110,99,116,105,111,110,32,65,58,10,12,70,117,110,99,116,105,111,110,32,66,58,10,12,70,117,110,99,116,105,111,110,32,67,58,10,23,67,111,110,116,105,110,117,111,117,115,32,118,105,100,101,111,32,102,101,101,100,63,10,0,37,10,69,120,112,101,99,116,101,100,32,82,44,32,76,44,32,111,114,32,100,105,115,116,97,110,99,101,32,98,117,116,32,103,111,116,58,32,36,10,69,120,112,101,99,116,101,100,32,99,111,109,109,97,32,111,114,32,110,101,119,108,105,110,101,32,98,117,116,32,103,111,116,58,32,43,10,68,101,102,105,110,105,116,105,111,110,115,32,109,97,121,32,98,101,32,97,116,32,109,111,115,116,32,50,48,32,99,104,97,114,97,99,116,101,114,115,33,10,94,62,118,60,0,1,0,-1,-1,0,1,0,0,0,0,0,0,1,12,10,0,109,4,1201,-3,0,587,20102,1,0,-1,22101,1,-3,-3,21101,0,0,-2,2208,-2,-1,570,1005,570,617,2201,-3,-2,609,4,0,21201,-2,1,-2,1105,1,597,109,-4,2105,1,0,109,5,1201,-4,0,630,20101,0,0,-2,22101,1,-4,-4,21102,1,0,-3,2208,-3,-2,570,1005,570,781,2201,-4,-3,653,20101,0,0,-1,1208,-1,-4,570,1005,570,709,1208,-1,-5,570,1005,570,734,1207,-1,0,570,1005,570,759,1206,-1,774,1001,578,562,684,1,0,576,576,1001,578,566,692,1,0,577,577,21102,702,1,0,1105,1,786,21201,-1,-1,-1,1105,1,676,1001,578,1,578,1008,578,4,570,1006,570,724,1001,578,-4,578,21102,731,1,0,1106,0,786,1105,1,774,1001,578,-1,578,1008,578,-1,570,1006,570,749,1001,578,4,578,21102,1,756,0,1105,1,786,1106,0,774,21202,-1,-11,1,22101,1182,1,1,21101,774,0,0,1105,1,622,21201,-3,1,-3,1106,0,640,109,-5,2106,0,0,109,7,1005,575,802,21001,576,0,-6,20102,1,577,-5,1106,0,814,21101,0,0,-1,21101,0,0,-5,21102,1,0,-6,20208,-6,576,-2,208,-5,577,570,22002,570,-2,-2,21202,-5,41,-3,22201,-6,-3,-3,22101,1469,-3,-3,2102,1,-3,843,1005,0,863,21202,-2,42,-4,22101,46,-4,-4,1206,-2,924,21102,1,1,-1,1106,0,924,1205,-2,873,21101,35,0,-4,1105,1,924,2101,0,-3,878,1008,0,1,570,1006,570,916,1001,374,1,374,2102,1,-3,895,1102,1,2,0,1202,-3,1,902,1001,438,0,438,2202,-6,-5,570,1,570,374,570,1,570,438,438,1001,578,558,922,20101,0,0,-4,1006,575,959,204,-4,22101,1,-6,-6,1208,-6,41,570,1006,570,814,104,10,22101,1,-5,-5,1208,-5,59,570,1006,570,810,104,10,1206,-1,974,99,1206,-1,974,1101,1,0,575,21101,973,0,0,1106,0,786,99,109,-7,2105,1,0,109,6,21101,0,0,-4,21102,0,1,-3,203,-2,22101,1,-3,-3,21208,-2,82,-1,1205,-1,1030,21208,-2,76,-1,1205,-1,1037,21207,-2,48,-1,1205,-1,1124,22107,57,-2,-1,1205,-1,1124,21201,-2,-48,-2,1106,0,1041,21101,-4,0,-2,1105,1,1041,21101,0,-5,-2,21201,-4,1,-4,21207,-4,11,-1,1206,-1,1138,2201,-5,-4,1059,1202,-2,1,0,203,-2,22101,1,-3,-3,21207,-2,48,-1,1205,-1,1107,22107,57,-2,-1,1205,-1,1107,21201,-2,-48,-2,2201,-5,-4,1090,20102,10,0,-1,22201,-2,-1,-2,2201,-5,-4,1103,1201,-2,0,0,1105,1,1060,21208,-2,10,-1,1205,-1,1162,21208,-2,44,-1,1206,-1,1131,1106,0,989,21101,0,439,1,1106,0,1150,21101,477,0,1,1106,0,1150,21102,1,514,1,21101,0,1149,0,1105,1,579,99,21102,1,1157,0,1105,1,579,204,-2,104,10,99,21207,-3,22,-1,1206,-1,1138,2101,0,-5,1176,2102,1,-4,0,109,-6,2105,1,0,14,11,30,1,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,13,38,1,1,1,38,1,1,1,38,1,1,1,26,13,1,1,40,1,40,1,40,1,40,1,40,1,40,1,40,1,40,5,40,1,40,1,3,7,30,1,3,1,5,1,30,1,3,1,5,1,30,1,3,1,5,1,30,1,3,1,5,1,30,1,3,1,5,1,30,1,3,1,5,1,30,1,3,1,5,1,4,7,19,1,3,1,5,1,10,1,19,1,3,1,5,1,10,1,13,5,1,11,10,1,13,1,3,1,5,1,16,1,11,13,16,1,11,1,1,1,3,1,22,1,11,1,1,1,3,1,22,1,11,1,1,1,3,1,22,1,11,13,16,1,13,1,3,1,5,1,16,1,13,1,3,1,5,1,16,1,13,1,3,1,5,1,16,13,1,1,3,1,5,1,28,1,1,1,3,1,5,1,18,13,3,13,12,1,9,1,11,1,5,1,12,1,9,1,11,1,5,1,12,1,9,1,11,1,5,1,10,13,11,1,5,1,10,1,1,1,21,1,5,14,21,8,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,1,9,1,30,11,30"""