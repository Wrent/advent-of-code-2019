import java.lang.RuntimeException
import java.math.BigInteger

const val INPUT22size = 10007

fun main() {
    val cards = IntArray(INPUT22size)
    for (i in 0 until INPUT22size) {
        cards[i] = i
    }
    val operations = INPUT22.split("\n")
            .map { parseShuffleOp(it) }
    var result = cards
    operations.forEachIndexed { index, i ->
        result = i.perform(result)
//        println("$index ${result.toList()}")
    }
    println("first result")
    result.toList().forEachIndexed { index, i -> if (i == 2019) println(index) }

    println("second result")
//    val numberOfCards = 10007.toBigInteger()
    val numberOfCards = "119315717514047".toBigInteger()
    val shuffleCount = "101741582076661".toBigInteger()
//    val shuffleCount = "1".toBigInteger()

    val reversedOps = operations.reversed()

    var pos = "2020".toBigInteger()
    reversedOps.forEachIndexed { index, i ->
        pos = i.getOriginalPosOfPos(pos, numberOfCards)
//        println("$index $pos")
    }
    println(pos)
    val posChange = numberOfCards.minus(pos).plus("2020".toBigInteger())
    val re = "2020".toBigInteger().minus(posChange.times(shuffleCount)).mod(numberOfCards)
    println(re)

//    pos = "2020".toBigInteger()
////    var prev = pos
//    for (i in 0 until shuffleCount.longValueExact()) {
//        reversedOps.forEachIndexed { index, i ->
//            pos = i.getOriginalPosOfPos(pos, numberOfCards)
////        println("$index $pos")
//        }
////        println("$pos ${prev - pos}")
////        prev = pos
//        println(i)
//    }
//    println(pos)
}

fun parseShuffleOp(line: String): ShuffleOp {
    if (line.trim() == "deal into new stack") {
        return DealIntoNew()
    } else if (line.contains("increment")) {
        val n = line.replace("deal with increment ", "").toInt()
        return DealWithIncrement(n)
    } else {
        val n = line.replace("cut ", "").toInt()
        return CutCards(n)
    }
}

interface ShuffleOp {
    fun perform(input: IntArray): IntArray
    fun getOriginalPosOfPos(pos: BigInteger, numberOfCards: BigInteger): BigInteger
}

class DealIntoNew : ShuffleOp {
    override fun perform(input: IntArray): IntArray {
        val new = input.clone()
        new.reverse()
        return new
    }

    override fun getOriginalPosOfPos(pos: BigInteger, numberOfCards: BigInteger): BigInteger {
        return numberOfCards.minus(BigInteger.ONE).minus(pos)
    }
}

class CutCards(val n: Int) : ShuffleOp {
    override fun perform(input: IntArray): IntArray {
        if (n > 0) {
            return (input.takeLast(input.size - n) + input.take(n)).toIntArray()
        } else {
            return (input.takeLast(n * -1) + input.take(input.size + n)).toIntArray()
        }
    }

    override fun getOriginalPosOfPos(pos: BigInteger, numberOfCards: BigInteger): BigInteger {
        return (pos + n.toBigInteger()).mod(numberOfCards)
    }
}

class DealWithIncrement(val n: Int) : ShuffleOp {
    override fun perform(input: IntArray): IntArray {
        val new = IntArray(INPUT22size)
        val iterator = input.iterator()
        var pos = 0
        while (iterator.hasNext()) {
            new[pos] = iterator.nextInt()
            pos = (pos + n) % input.size
        }
        return new
    }

    override fun getOriginalPosOfPos(pos: BigInteger, numberOfCards: BigInteger): BigInteger {
        var origPos = pos
        while (origPos.mod(n.toBigInteger()) != BigInteger.ZERO) {
            origPos += numberOfCards
        }
        return origPos.divide(n.toBigInteger()).mod(numberOfCards)
//        val iterator = (0 until numberOfCards.longValueExact()).iterator()
//        var pos = BigInteger.ZERO
//        var i = 0
//        while (iterator.hasNext()) {
//            if (pos == input) {
//                return i.toBigInteger()
//            }
//            pos = (pos + n.toBigInteger()).mod(numberOfCards)
//            i++
//        }
//        throw RuntimeException()
    }
}

const val TEST221 = """deal with increment 7
deal into new stack
deal into new stack"""

const val TEST222 = """cut 6
deal with increment 7
deal into new stack"""

const val TEST223 = """deal with increment 7
deal with increment 9
cut -2"""

const val INPUT22 = """deal with increment 18
cut -3893
deal with increment 15
cut -3085
deal with increment 43
cut -2092
deal into new stack
cut 7372
deal with increment 66
deal into new stack
cut -5126
deal with increment 60
cut 2307
deal with increment 5
cut 971
deal with increment 74
cut -3236
deal with increment 29
cut -6691
deal with increment 64
cut -8296
deal with increment 49
cut -1717
deal with increment 55
deal into new stack
cut 2992
deal with increment 65
cut 2166
deal with increment 72
cut 4752
deal with increment 35
cut 8476
deal with increment 50
cut -6138
deal with increment 73
cut -91
deal with increment 73
cut 2012
deal with increment 4
cut 3963
deal into new stack
cut 1186
deal with increment 25
cut 8476
deal with increment 36
cut -6069
deal with increment 18
deal into new stack
deal with increment 56
cut -6009
deal with increment 33
cut 1273
deal with increment 10
cut 6912
deal with increment 62
deal into new stack
deal with increment 48
cut -9706
deal with increment 53
cut 6162
deal with increment 38
cut 6576
deal with increment 10
cut 9123
deal with increment 4
cut 1355
deal with increment 34
cut -3784
deal with increment 59
deal into new stack
cut -9109
deal with increment 3
cut 4903
deal with increment 73
cut 8575
deal with increment 34
deal into new stack
cut -5046
deal with increment 75
deal into new stack
deal with increment 42
cut 4671
deal with increment 57
deal into new stack
deal with increment 14
cut 5464
deal with increment 37
cut 6782
deal with increment 29
cut 4233
deal with increment 37
cut -5577
deal with increment 50
cut -3111
deal with increment 56
deal into new stack
deal with increment 75
cut 1205
deal with increment 2
cut -7531"""