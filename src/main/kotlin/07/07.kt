fun main() {
    var permutations = getPermutations(listOf(0, 1, 2, 3, 4).toIntArray())
    println(permutations)
    var max = 0

    permutations.forEach {
        val res = evaluateAmplifier(it[0], evaluateAmplifier(it[1], evaluateAmplifier(it[2], evaluateAmplifier(it[3], evaluateAmplifier(it[4], 0)))))
        if (res > max) max = res
    }
    println(max)

    println("second part")
    permutations = getPermutations(listOf(5,6,7,8,9).toIntArray())
    println(permutations)
    max = 0

    permutations.forEach {
        val amplifierA = Amplifier(getData(), 0)
        val amplifierB = Amplifier(getData(), 0)
        val amplifierC = Amplifier(getData(), 0)
        val amplifierD = Amplifier(getData(), 0)
        val amplifierE = Amplifier(getData(), 0)

        val amplifiers = listOf(amplifierA, amplifierB, amplifierC, amplifierD, amplifierE).iterator()
        var current = amplifiers.next()

        try {
            while (true) {
                val instr = parseInstr(index, data) { if (inputCallCnt++ == 0) a else b }
                index = instr.apply(data, index)
            }
        } catch (ex: RuntimeException) {
            println("amplifier stopped")
        }


        if (res > max) max = res
    }
    println(max)

}

data class Amplifier(val data: MutableList<Int>, var currentIndex: Int)

fun getPermutations(input: IntArray): List<IntArray> {
    val indexes = IntArray(input.size)
    for (i in 0 until input.size) {
        indexes[i] = 0
    }
    var current = input.clone()
    val result = mutableListOf<IntArray>()

    result.add(current)

    var i = 0
    while (i < input.size) {
        if (indexes[i] < i) {
            swap(current, if (i % 2 == 0) 0 else indexes[i], i)
            result.add(current.clone())
            indexes[i]++
            i = 0
        } else {
            indexes[i] = 0
            i++
        }
    }
    return result
}


fun swap(input: IntArray, a: Int, b: Int) {
    val tmp: Int = input[a]
    input[a] = input[b]
    input[b] = tmp
}

fun evaluateAmplifier(a: Int, b: Int): Int {
    val data = getData()
    var index = 0
    var inputCallCnt = 0
    try {
        while (true) {
            val instr = parseInstr(index, data) { if (inputCallCnt++ == 0) a else b }
            index = instr.apply(data, index)
        }
    } catch (ex: RuntimeException) {
        return output
    }
}

fun getData(): MutableList<Int> {
    return INPUT7.split(",").map { it.toInt() }.toMutableList()
}

const val INPUT7 = "3,8,1001,8,10,8,105,1,0,0,21,46,67,76,97,118,199,280,361,442,99999,3,9,1002,9,3,9,101,4,9,9,102,3,9,9,1001,9,3,9,1002,9,2,9,4,9,99,3,9,102,2,9,9,101,5,9,9,1002,9,2,9,101,2,9,9,4,9,99,3,9,101,4,9,9,4,9,99,3,9,1001,9,4,9,102,2,9,9,1001,9,4,9,1002,9,5,9,4,9,99,3,9,102,3,9,9,1001,9,2,9,1002,9,3,9,1001,9,3,9,4,9,99,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,99"