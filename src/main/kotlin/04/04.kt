fun main() {
    val from = 359282
    val to = 820401

    println("first result")
    println((from..to).filter { isValid(it.toString()) }.count())

    println("second result")
    println((from..to)
        .filter { isValidWithNewRule(it.toString()) }
        .filter { isValid(it.toString()) }
        .count()
    )
}

fun isValid(password: String): Boolean {
    return hasDouble(password) && isIncreasing(password)
}

fun hasDouble(password: String): Boolean {
    return password.split("").filter { it != "" }.groupBy { it }.keys.size != password.length
}

fun getCharsThatAppearMoreThatTwice(password: String): List<String> {
    return password.split("").filter { it != "" }.groupBy { it }.entries.filter { it.value.size > 2 }.map { (k, _) -> k }
}

fun isIncreasing(password: String): Boolean {
    var number = 0
    for (ch in password) {
        val next = ch.toInt()
        if (next < number) {
            return false
        }
        number = next
    }
    return true
}

fun isValidWithNewRule(password: String): Boolean {
    var pass = password
    val moreThanDoubles = getCharsThatAppearMoreThatTwice(password)
    moreThanDoubles.forEach { pass = pass.replace(it, "") }
    return isValid(pass)
}
