import java.lang.IllegalStateException
import java.util.*

fun main() {
    var permutations = getPermutations(listOf(0, 1, 2, 3, 4).toIntArray())
    var max = 0

    permutations.forEach {
        val res = evaluateAmplifier(it[0], evaluateAmplifier(it[1], evaluateAmplifier(it[2], evaluateAmplifier(it[3], evaluateAmplifier(it[4], 0)))))
        if (res > max) max = res
    }
    println(max)

    println("second part")
    permutations = getPermutations(listOf(5, 6, 7, 8, 9).toIntArray())
    max = 0

    permutations.forEach {
        val res = evaluatePermutation(it, INPUT7)
        if (res > max) max = res
    }
    println(max)

}
 fun evaluatePermutation(it: IntArray, input: String): Int {
    val amplifierE = Amplifier(getData(input), 0, null)
    val amplifierD = Amplifier(getData(input), 0, amplifierE)
    val amplifierC = Amplifier(getData(input), 0, amplifierD)
    val amplifierB = Amplifier(getData(input), 0, amplifierC)
    val amplifierA = Amplifier(getData(input), 0, amplifierB)

    amplifierE.next = amplifierA

    amplifierA.queue.add(it[0])
    amplifierA.queue.add(0)
    amplifierB.queue.add(it[1])
    amplifierC.queue.add(it[2])
    amplifierD.queue.add(it[3])
    amplifierE.queue.add(it[4])

    var current = amplifierA

    while (true) {
        try {
            val instr = parseInstr(current.index, current.data, { current.queue.poll() }, { output ->
                current.next?.queue?.add(output)
                current.lastOutput = output
            })
            current.index = instr.apply(current.data, current.index)
        } catch (ex: IllegalStateException) {
            current = current.next!!
        } catch (ex: RuntimeException) {
            println("amplifier stopped")
            current.isStopped = true
            val tmp = current
            while (current.isStopped) {
                current = current.next!!
                if (current == tmp) {
                    break
                }
            }
            if (current.isStopped) {
                break
            }
        }
    }

    val res = amplifierE.lastOutput
    return res
}

class Amplifier(val data: MutableList<Int>, var index: Int, var next: Amplifier?) {
    val queue: Queue<Int> = LinkedList()
    var lastOutput = 0
    var isStopped = false
}

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
    val data = getData(INPUT7)
    var index = 0
    var inputCallCnt = 0
    try {
        while (true) {
            val instr = parseInstr(index, data, { if (inputCallCnt++ == 0) a else b }, {})
            index = instr.apply(data, index)
        }
    } catch (ex: RuntimeException) {
        return output
    }
}

fun getData(input: String): MutableList<Int> {
    return input.split(",").map { it.toInt() }.toMutableList()
}

const val INPUT7 = "3,8,1001,8,10,8,105,1,0,0,21,46,67,76,97,118,199,280,361,442,99999,3,9,1002,9,3,9,101,4,9,9,102,3,9,9,1001,9,3,9,1002,9,2,9,4,9,99,3,9,102,2,9,9,101,5,9,9,1002,9,2,9,101,2,9,9,4,9,99,3,9,101,4,9,9,4,9,99,3,9,1001,9,4,9,102,2,9,9,1001,9,4,9,1002,9,5,9,4,9,99,3,9,102,3,9,9,1001,9,2,9,1002,9,3,9,1001,9,3,9,4,9,99,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,99"