import java.math.BigInteger

fun main() {
    val space = mutableMapOf<Coord, Int>()

    while (beamCoord.y < 50) {
        readCoord(space)
    }
    println("first result")
    println(space.values.filter { it == 1 }.count())
    space.printBeam()

    println("second result")
//    var x = 0
//    var y = 727
//    while (true) {
//        beamCoord = Coord(x++, y)
//        readCoord(space)
//        if (x > y) {
//            println(space.rowToString(y))
//            break
//        }
//    }
//    var x = 291
//    var y = 200
//    while (true) {
//        beamCoord = Coord(x, y++)
//        readCoord(space)
//        if (y > 1000) {
//            println(space.columnToString(x))
//            break
//        }
//    }
//    val firstRowThatCanFitSanta = 727
//    val xPos = 400
//
//    var x = xPos
//    var y = firstRowThatCanFitSanta
//
//    while (!space.rowCanFitSanta(y) || !space.columnCanFitSanta(x)) {
//        beamCoord = Coord(x, y)
//        readCoord(space)
//        if (x < xPos + 200) {
//            x++
//        } else {
//            x = xPos
//            y++
//        }
//    }
//    println(space.rowToString(y))
//    println(space.columnToString(x))

    val firstColumnThatCanFitSanta = 700 // 702, 1137
    val firstRowThatCanFitSanta = 1134


    for (i in firstRowThatCanFitSanta until firstRowThatCanFitSanta + 100) {
        for (j in firstColumnThatCanFitSanta until firstColumnThatCanFitSanta + 100) {
            beamCoord = Coord(j, i)
            readCoord(space)
        }
        println(space.rowToString(i))
    }
    println(firstColumnThatCanFitSanta*10000 + firstRowThatCanFitSanta)
}

private fun readCoord(space: MutableMap<Coord, Int>) {
    val data = mutableMapOf<BigInteger, BigInteger>()
    INPUT19.split(",").map { it.toBigInteger() }.forEachIndexed { index, i -> data[index.toBigInteger()] = i }

    var index = BigInteger.ZERO
    relativeBase = BigInteger.ZERO
    try {
        while (true) {
            val instr = parseInstr(index, data, { inputCoords() }, {
                handleOutput(it, space)
            })
            index = instr.apply(data, index)
        }
    } catch (ex: HaltException) {
    }
}

private fun MutableMap<Coord, Int>.rowCanFitSanta(y: Int): Boolean {
    return rowToString(y).contains("1".repeat(100))
}

private fun MutableMap<Coord, Int>.columnCanFitSanta(x: Int): Boolean {
    return columnToString(x).contains("1".repeat(100))
}

private fun MutableMap<Coord, Int>.rowToString(y: Int): String {
    return this.filter { it.key.y == y }.entries.sortedBy { it.key.x }.joinToString(separator = "") { it.value.toString() }
}

private fun MutableMap<Coord, Int>.columnToString(x: Int): String {
    return this.filter { it.key.x == x }.entries.sortedBy { it.key.y }.joinToString(separator = "") { it.value.toString() }
}

private fun MutableMap<Coord, Int>.printBeam() {
    val maxX = this.keys.maxBy { it.x }!!.x
    val maxY = this.keys.maxBy { it.y }!!.y

    for (j in 0..maxY) {
        for (i in 0..maxX) {
            print(if (this[Coord(i, j)] == 1) '#' else '.')
        }
        println()
    }
}

var beamCoord = Coord(0, 0)

var inputCnt = 0
fun inputCoords(): BigInteger {
//    println(beamCoord)
    if (inputCnt++ % 2 == 0) {
        return beamCoord.x.toBigInteger()
    } else {
        return beamCoord.y.toBigInteger()
    }
}

fun handleOutput(output: BigInteger, space: MutableMap<Coord, Int>) {
    space[beamCoord] = output.intValueExact()
    beamCoord = if (beamCoord.x == 49) {
        Coord(0, beamCoord.y + 1)
    } else {
        Coord(beamCoord.x + 1, beamCoord.y)
    }
}

const val INPUT19 =
    """109,424,203,1,21102,1,11,0,1106,0,282,21101,18,0,0,1105,1,259,2102,1,1,221,203,1,21101,0,31,0,1105,1,282,21101,0,38,0,1105,1,259,20101,0,23,2,21202,1,1,3,21101,0,1,1,21101,57,0,0,1105,1,303,1202,1,1,222,21002,221,1,3,21001,221,0,2,21102,1,259,1,21101,0,80,0,1105,1,225,21101,0,175,2,21102,1,91,0,1106,0,303,2101,0,1,223,21001,222,0,4,21102,259,1,3,21101,225,0,2,21102,1,225,1,21102,1,118,0,1105,1,225,21002,222,1,3,21101,70,0,2,21101,0,133,0,1105,1,303,21202,1,-1,1,22001,223,1,1,21102,1,148,0,1105,1,259,2102,1,1,223,21002,221,1,4,21002,222,1,3,21102,24,1,2,1001,132,-2,224,1002,224,2,224,1001,224,3,224,1002,132,-1,132,1,224,132,224,21001,224,1,1,21101,195,0,0,105,1,109,20207,1,223,2,21002,23,1,1,21101,0,-1,3,21102,1,214,0,1106,0,303,22101,1,1,1,204,1,99,0,0,0,0,109,5,2102,1,-4,249,21202,-3,1,1,22102,1,-2,2,21201,-1,0,3,21101,0,250,0,1106,0,225,21201,1,0,-4,109,-5,2105,1,0,109,3,22107,0,-2,-1,21202,-1,2,-1,21201,-1,-1,-1,22202,-1,-2,-2,109,-3,2105,1,0,109,3,21207,-2,0,-1,1206,-1,294,104,0,99,21202,-2,1,-2,109,-3,2106,0,0,109,5,22207,-3,-4,-1,1206,-1,346,22201,-4,-3,-4,21202,-3,-1,-1,22201,-4,-1,2,21202,2,-1,-1,22201,-4,-1,1,22101,0,-2,3,21101,343,0,0,1105,1,303,1105,1,415,22207,-2,-3,-1,1206,-1,387,22201,-3,-2,-3,21202,-2,-1,-1,22201,-3,-1,3,21202,3,-1,-1,22201,-3,-1,2,21201,-4,0,1,21101,0,384,0,1105,1,303,1105,1,415,21202,-4,-1,-4,22201,-4,-3,-4,22202,-3,-2,-2,22202,-2,-4,-4,22202,-3,-2,-3,21202,-4,-1,-2,22201,-3,-2,1,21201,1,0,-4,109,-5,2106,0,0"""