import java.lang.IllegalStateException
import java.math.BigInteger
import java.util.*

fun main() {
    var permutations = getPermutations(listOf(0L, 1L, 2L, 3L, 4L).map { it.toBigInteger() }.toTypedArray())
    var max = BigInteger.ZERO

    permutations.forEach {
        val res = evaluateAmplifier(it[0], evaluateAmplifier(it[1], evaluateAmplifier(it[2], evaluateAmplifier(it[3], evaluateAmplifier(it[4], BigInteger.ZERO)))))
        if (res > max) max = res
    }
    println(max)

    println("second part")
    permutations = getPermutations(listOf(5L, 6L, 7L, 8L, 9L).map { it.toBigInteger() }.toTypedArray())
    max = BigInteger.ZERO

    permutations.forEach {
        val res = evaluatePermutation(it, INPUT7)
        if (res > max) max = res
    }
    println(max)

}

fun evaluatePermutation(it: Array<BigInteger>, input: String): BigInteger {
    val amplifierE = Amplifier(getData(input), BigInteger.ZERO, null)
    val amplifierD = Amplifier(getData(input), BigInteger.ZERO, amplifierE)
    val amplifierC = Amplifier(getData(input), BigInteger.ZERO, amplifierD)
    val amplifierB = Amplifier(getData(input), BigInteger.ZERO, amplifierC)
    val amplifierA = Amplifier(getData(input), BigInteger.ZERO, amplifierB)

    amplifierE.next = amplifierA

    amplifierA.queue.add(it[0])
    amplifierA.queue.add(BigInteger.ZERO)
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
        } catch (ex: HaltException) {
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

class Amplifier(val data: MutableMap<BigInteger, BigInteger>, var index: BigInteger, var next: Amplifier?) {
    val queue: Queue<BigInteger> = LinkedList()
    var lastOutput = BigInteger.ZERO
    var isStopped = false
}

fun getPermutations(input: Array<BigInteger>): List<Array<BigInteger>> {
    val indexes = LongArray(input.size)
    for (i in 0 until input.size) {
        indexes[i] = 0
    }
    var current = input.clone()
    val result = mutableListOf<Array<BigInteger>>()

    result.add(current)

    var i = 0
    while (i < input.size) {
        if (indexes[i] < i) {
            swap(current, if (i % 2 == 0) 0 else indexes[i].toInt(), i)
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


fun swap(input: Array<BigInteger>, a: Int, b: Int) {
    val tmp: BigInteger = input[a]
    input[a] = input[b]
    input[b] = tmp
}

fun evaluateAmplifier(a: BigInteger, b: BigInteger): BigInteger {
    val data = getData(INPUT7)
    var index = 0L.toBigInteger()
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

fun getData(input: String): MutableMap<BigInteger, BigInteger> {
    val data = mutableMapOf<BigInteger, BigInteger>()
    input.split(",").map { it.toBigInteger() }.toMutableList().forEachIndexed { index, i -> data[index.toBigInteger()] = i }
    return data
}

const val INPUT7 =
    "3,8,1001,8,10,8,105,1,0,0,21,46,67,76,97,118,199,280,361,442,99999,3,9,1002,9,3,9,101,4,9,9,102,3,9,9,1001,9,3,9,1002,9,2,9,4,9,99,3,9,102,2,9,9,101,5,9,9,1002,9,2,9,101,2,9,9,4,9,99,3,9,101,4,9,9,4,9,99,3,9,1001,9,4,9,102,2,9,9,1001,9,4,9,1002,9,5,9,4,9,99,3,9,102,3,9,9,1001,9,2,9,1002,9,3,9,1001,9,3,9,4,9,99,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,99,3,9,102,2,9,9,4,9,3,9,101,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,102,2,9,9,4,9,3,9,101,1,9,9,4,9,3,9,101,2,9,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,1,9,4,9,3,9,101,1,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1001,9,1,9,4,9,3,9,1002,9,2,9,4,9,3,9,1001,9,1,9,4,9,99,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1002,9,2,9,4,9,3,9,101,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,101,1,9,9,4,9,3,9,102,2,9,9,4,9,3,9,102,2,9,9,4,9,3,9,1001,9,2,9,4,9,3,9,1002,9,2,9,4,9,99"