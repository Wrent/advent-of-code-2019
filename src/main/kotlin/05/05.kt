import java.lang.RuntimeException

fun main() {
    val data = INPUT5.split(",").map { it.toInt() }.toMutableList()

    var index = 0
    while (true) {
        val instr = parseInstr(index, data) { input }
        index = instr.apply(data, index)
    }
}

data class Param(val value: Int, val mode: ParameterMode)

enum class ParameterMode {
    POSITION,
    IMMEDIATE
}

var input = 5
var output = 0

sealed class Instr(val params: List<Param>) {
    abstract fun apply(data: MutableList<Int>, index: Int): Int

    fun pval(param: Param, data: MutableList<Int>): Int {
        return when (param.mode) {
            ParameterMode.POSITION -> data[param.value]
            ParameterMode.IMMEDIATE -> param.value
        }
    }
}

class Add(params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        data[params[2].value] = pval(params[0], data) + pval(params[1], data)
        return index + 4
    }
}

class Multiply(params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        data[params[2].value] = pval(params[0], data) * pval(params[1], data)
        return index + 4
    }
}

class Halt : Instr(listOf()) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        throw RuntimeException("Halt")
    }
}

class Input(val input: Int, params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        data[params[0].value] = input
        return index + 2
    }
}

class Output(params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        output = pval(params[0], data)
        println(output)
        return index + 2
    }
}

class JumpIfTrue(params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        if (pval(params[0], data) != 0) {
            return pval(params[1], data)
        } else {
            return index + 3
        }
    }
}

class JumpIfFalse(params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        if (pval(params[0], data) == 0) {
            return pval(params[1], data)
        } else {
            return index + 3
        }
    }
}

class LessThan(params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        if (pval(params[0], data) < pval(params[1], data)) {
            data[params[2].value] = 1
        } else {
            data[params[2].value] = 0
        }
        return index + 4
    }
}

class Equals(params: List<Param>) : Instr(params) {
    override fun apply(data: MutableList<Int>, index: Int): Int {
        if (pval(params[0], data) == pval(params[1], data)) {
            data[params[2].value] = 1
        } else {
            data[params[2].value] = 0
        }
        return index + 4
    }
}

fun parseInstr(index: Int, data: MutableList<Int>, inputSupplier: () -> Int): Instr {
    val prefixed = prefix(data[index])
    val opcode = prefixed.substring(prefixed.length - 2).toInt()
    val modes = prefixed.substring(0, 3).reversed().map { parseMode(it) }

    return when (opcode) {
        1 -> Add(
            listOf(
                Param(data[index + 1], modes[0]),
                Param(data[index + 2], modes[1]),
                Param(data[index + 3], modes[2])
            )
        )
        2 -> Multiply(
            listOf(
                Param(data[index + 1], modes[0]),
                Param(data[index + 2], modes[1]),
                Param(data[index + 3], modes[2])
            )
        )
        3 -> Input(
            inputSupplier(), listOf(
                Param(data[index + 1], modes[0])
            )
        )
        4 -> Output(
            listOf(
                Param(data[index + 1], modes[0])
            )
        )
        5 -> JumpIfTrue(
            listOf(
                Param(data[index + 1], modes[0]),
                Param(data[index + 2], modes[1])
            )
        )
        6 -> JumpIfFalse(
            listOf(
                Param(data[index + 1], modes[0]),
                Param(data[index + 2], modes[1])
            )
        )
        7 -> LessThan(
            listOf(
                Param(data[index + 1], modes[0]),
                Param(data[index + 2], modes[1]),
                Param(data[index + 3], modes[2])
            )
        )
        8 -> Equals(
            listOf(
                Param(data[index + 1], modes[0]),
                Param(data[index + 2], modes[1]),
                Param(data[index + 3], modes[2])
            )
        )
        99 -> Halt()
        else -> throw RuntimeException("Invalid instruction")
    }
}

fun parseMode(it: Char): ParameterMode {
    return when (it) {
        '0' -> ParameterMode.POSITION
        '1' -> ParameterMode.IMMEDIATE
        else -> throw RuntimeException("invalid mode")
    }
}

fun prefix(value: Int): String {
    var res = value.toString()
    while (res.length < 5) {
        res = "0" + res
    }
    return res
}

const val INPUT5 =
    "3,225,1,225,6,6,1100,1,238,225,104,0,1,192,154,224,101,-161,224,224,4,224,102,8,223,223,101,5,224,224,1,223,224,223,1001,157,48,224,1001,224,-61,224,4,224,102,8,223,223,101,2,224,224,1,223,224,223,1102,15,28,225,1002,162,75,224,1001,224,-600,224,4,224,1002,223,8,223,1001,224,1,224,1,224,223,223,102,32,57,224,1001,224,-480,224,4,224,102,8,223,223,101,1,224,224,1,224,223,223,1101,6,23,225,1102,15,70,224,1001,224,-1050,224,4,224,1002,223,8,223,101,5,224,224,1,224,223,223,101,53,196,224,1001,224,-63,224,4,224,102,8,223,223,1001,224,3,224,1,224,223,223,1101,64,94,225,1102,13,23,225,1101,41,8,225,2,105,187,224,1001,224,-60,224,4,224,1002,223,8,223,101,6,224,224,1,224,223,223,1101,10,23,225,1101,16,67,225,1101,58,10,225,1101,25,34,224,1001,224,-59,224,4,224,1002,223,8,223,1001,224,3,224,1,223,224,223,4,223,99,0,0,0,677,0,0,0,0,0,0,0,0,0,0,0,1105,0,99999,1105,227,247,1105,1,99999,1005,227,99999,1005,0,256,1105,1,99999,1106,227,99999,1106,0,265,1105,1,99999,1006,0,99999,1006,227,274,1105,1,99999,1105,1,280,1105,1,99999,1,225,225,225,1101,294,0,0,105,1,0,1105,1,99999,1106,0,300,1105,1,99999,1,225,225,225,1101,314,0,0,106,0,0,1105,1,99999,1108,226,226,224,102,2,223,223,1005,224,329,101,1,223,223,107,226,226,224,1002,223,2,223,1005,224,344,1001,223,1,223,107,677,226,224,102,2,223,223,1005,224,359,101,1,223,223,7,677,226,224,102,2,223,223,1005,224,374,101,1,223,223,108,226,226,224,102,2,223,223,1006,224,389,101,1,223,223,1007,677,677,224,102,2,223,223,1005,224,404,101,1,223,223,7,226,677,224,102,2,223,223,1006,224,419,101,1,223,223,1107,226,677,224,1002,223,2,223,1005,224,434,1001,223,1,223,1108,226,677,224,102,2,223,223,1005,224,449,101,1,223,223,108,226,677,224,102,2,223,223,1005,224,464,1001,223,1,223,8,226,677,224,1002,223,2,223,1005,224,479,1001,223,1,223,1007,226,226,224,102,2,223,223,1006,224,494,101,1,223,223,1008,226,677,224,102,2,223,223,1006,224,509,101,1,223,223,1107,677,226,224,1002,223,2,223,1006,224,524,1001,223,1,223,108,677,677,224,1002,223,2,223,1005,224,539,1001,223,1,223,1107,226,226,224,1002,223,2,223,1006,224,554,1001,223,1,223,7,226,226,224,1002,223,2,223,1006,224,569,1001,223,1,223,8,677,226,224,102,2,223,223,1006,224,584,101,1,223,223,1008,677,677,224,102,2,223,223,1005,224,599,101,1,223,223,1007,226,677,224,1002,223,2,223,1006,224,614,1001,223,1,223,8,677,677,224,1002,223,2,223,1005,224,629,101,1,223,223,107,677,677,224,102,2,223,223,1005,224,644,101,1,223,223,1108,677,226,224,102,2,223,223,1005,224,659,101,1,223,223,1008,226,226,224,102,2,223,223,1006,224,674,1001,223,1,223,4,223,99,226"