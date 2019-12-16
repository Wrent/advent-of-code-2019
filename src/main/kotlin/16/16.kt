import java.util.stream.IntStream

fun main() {
    val pattern = listOf(0, 1, 0, -1)
    var input = INPUT16.split("").filter { it != "" }.map { it.toInt() }

    for (i in 0 until 100) {
        input = getNext(input, pattern)
    }
    println("first result")
    println(input.subList(0, 8).joinToString(separator = ""))

    patterns.clear()
    println("second result")
    var input2 = INPUT16.repeat(10000).split("").filter { it != "" }.map { it.toInt() }.toIntArray()

    for (i in 0 until 100) {
        input2 = getNext2(input2)
    }
    val offset = INPUT16.substring(0, 7).toInt()
    println(input2.toList().subList(offset, offset + 8).joinToString(separator = ""))
}

fun getNext2(input2: IntArray): IntArray {
    val next = IntArray(input2.size)
    next[input2.size - 1] = input2[input2.size - 1]
    for (i in 1 until input2.size) {
        next[input2.size - 1 - i] = (input2[input2.size - 1 - i] + next[input2.size - i]) % 10
    }
    return next
}

fun getNext(input: List<Int>, pattern: List<Int>): List<Int> {
    val next = mutableListOf<Int>()
    for (i in input.indices) {
        next.add(countIndex(i, input, pattern))
    }
    return next
}

fun countIndex(index: Int, input: List<Int>, pattern: List<Int>): Int {
//    var milis = System.currentTimeMillis()
    val wholePattern = getPattern(pattern, index, input.size)
//    println("pattern calc {${System.currentTimeMillis() - milis}")
//    milis = System.currentTimeMillis()
    val result = IntStream.range(0, input.size)
        .parallel()
        .map { input[it] * wholePattern[it] }
        .sum()
        .let { Math.abs(it) }
        .let { it % 10 }
//    println("result calc {${System.currentTimeMillis() - milis}")
//    if (index % 10000 == 0) println("index $index")
    return result
}

val patterns = mutableMapOf<Int, List<Int>>()

fun getPattern(pattern: List<Int>, index: Int, length: Int): List<Int> {
    if (patterns[index] != null) return patterns[index]!!
    val repeat = index + 1
    val result = IntArray(length + 2)

    var i = 0
    while (true) {
        for (j in 0 until repeat) {
            result[i*repeat + j] = pattern[i % pattern.size]
            if (i*repeat + j == length + 1) {
                val wholePattern = result.toList().subList(1, result.size)
                patterns[index] = wholePattern
                return wholePattern
            }
        }
        i++
    }
}

const val TEST161 = "12345678"
const val TEST162 = "80871224585914546619083218645595"
const val TEST163 = "19617804207202209144916044189917"
const val TEST164 = "69317163492948606335995924319873"
const val INPUT16 =
    """59773590431003134109950482159532121838468306525505797662142691007448458436452137403459145576019785048254045936039878799638020917071079423147956648674703093863380284510436919245876322537671069460175238260758289779677758607156502756182541996384745654215348868695112673842866530637231316836104267038919188053623233285108493296024499405360652846822183647135517387211423427763892624558122564570237850906637522848547869679849388371816829143878671984148501319022974535527907573180852415741458991594556636064737179148159474282696777168978591036582175134257547127308402793359981996717609700381320355038224906967574434985293948149977643171410237960413164669930"""