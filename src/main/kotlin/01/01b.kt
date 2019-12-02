
fun main(args: Array<String>) {
    val res = INPUT
    	.split("\n")
        .map { it.toInt() }
    	.map {
            var current = getFuel(it)
            var res = current;
            while(true) {
                val next = getFuel(current)
                if (next > 0) {
                    res += next
                    current = next;
                } else {
                    break
                }
            }
            res
        }
        .sum()
    println(res)
}

fun getFuel(mass: Int): Int {
    return mass
    .let { it / 3}
    .let { kotlin.math.floor(it.toDouble()) }
    .let { it - 2}.toInt()
}
