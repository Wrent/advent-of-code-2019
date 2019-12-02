import java.lang.RuntimeException

fun main() {
    try {
        compute(12, 2)
    } catch (e: ResultException) {
        println("first result")
        println(e.result)
    }

    println("second result")
    for (noun in 0..99) {
        for (verb in 0..99) {
            try {
                compute(noun, verb)
            } catch (e: ResultException) {
                if (e.result == 19690720) {
                    println("noun: $noun, verb: $verb")
                    println("result: ${100*noun + verb}")
                    return
                }
            }
        }
    }
}

private fun compute(noun: Int, verb: Int) {
    val input = parse()
    input.set(1, noun)
    input.set(2, verb)

    var index = 0
    while (true) {
        val step = processPosition(index, input)
        index += step + 1
    }
}

fun processPosition(index: Int, data: MutableList<Int>) : Int {
    val instruction = instruction(data.get(index))
    when (instruction) {
        Instruction.ADD -> {
            val value = data.get(data.get(index + 1)) + data.get(data.get(index + 2))
            data.set(data.get(index + 3), value)
        }
        Instruction.MULTIPLY -> {
            val value = data.get(data.get(index + 1)) * data.get(data.get(index + 2))
            data.set(data.get(index + 3), value)
        }
        Instruction.HALT -> throw ResultException(data.get(0))
    }
    return instruction.length
}

class ResultException(val result: Int) : Throwable()

fun parse(): MutableList<Int> {
    return INPUT2.split(",").map { it.toInt() }.toMutableList()
}

fun instruction(value: Int) : Instruction {
    return Instruction.values().find { it.value == value } ?: throw RuntimeException("invalid instruction")
}

enum class Instruction(val value: Int, val length: Int) {
    ADD(1, 3),
    MULTIPLY(2, 3),
    HALT(99, 0);
}

const val INPUT2 =
    "1,0,0,3,1,1,2,3,1,3,4,3,1,5,0,3,2,10,1,19,1,5,19,23,1,23,5,27,2,27,10,31,1,5,31,35,2,35,6,39,1,6,39,43,2,13,43,47,2,9,47,51,1,6,51,55,1,55,9,59,2,6,59,63,1,5,63,67,2,67,13,71,1,9,71,75,1,75,9,79,2,79,10,83,1,6,83,87,1,5,87,91,1,6,91,95,1,95,13,99,1,10,99,103,2,6,103,107,1,107,5,111,1,111,13,115,1,115,13,119,1,13,119,123,2,123,13,127,1,127,6,131,1,131,9,135,1,5,135,139,2,139,6,143,2,6,143,147,1,5,147,151,1,151,2,155,1,9,155,0,99,2,14,0,0"