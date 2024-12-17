import OpCode.*
import kotlin.math.pow

fun main() {
    data class CPU(var aRegister: Long, private var bRegister: Long, private var cRegister: Long, val program: List<Int>) {
        private var programCounter = 0
        private val output = mutableListOf<Int>()

        private fun comboOperand(operand: Int): Long {
            return when (operand) {
                0, 1, 2, 3 -> operand.toLong()
                4 -> aRegister
                5 -> bRegister
                6 -> cRegister
                else -> throw IllegalArgumentException("Unknown operand $operand")
            }
        }

        private fun execute(): Boolean {
            val opCodeNumber = program.getOrNull(programCounter) ?: return false
            val opCode = OpCode.map[opCodeNumber]!!
            val operand = program.getOrNull(programCounter + 1) ?: return false
            when (opCode) {
                ADV -> { aRegister /= 2.0.pow(comboOperand(operand).toDouble()).toInt() }
                BXL -> { bRegister = bRegister xor operand.toLong() }
                BST -> { bRegister = comboOperand(operand) % 8 }
                JNZ -> { if(aRegister != 0L) programCounter = operand else programCounter += 2 }
                BXC -> { bRegister = bRegister xor cRegister }
                OUT -> { output.add((comboOperand(operand) % 8).toInt()) }
                BDV -> { bRegister = aRegister / 2.0.pow(comboOperand(operand).toDouble()).toInt() }
                CDV -> { cRegister = aRegister / 2.0.pow(comboOperand(operand).toDouble()).toInt() }
            }

            if(opCode != JNZ) programCounter += 2
            return true
        }

        fun runProgram(): String {
            do {
                val canContinue = execute()
            } while(canContinue)

            return output.joinToString(",")
        }
    }

    fun parse(input: List<String>): CPU {
        val (aS, bS, cS, _, programS) = input
        val (a, b, c) = listOf(aS, bS, cS).map { it.split(": ")[1].trim().toLong() }

        val program = programS.split(": ")[1].trim().split(",").map { it.trim().toInt() }
        return CPU(a, b, c, program)
    }

    fun part1(input: List<String>): String {
        val cpu = parse(input)
        return cpu.runProgram()
    }

    fun Int.toBinaryPadded(pad: Int) = Integer.toBinaryString(this).padStart(pad, '0')
//    fun CPU.findPrefix(program: List<Int>, start: Int = 0, minimumDigitLength: Int = 1): List<String> {
//        if(program.isEmpty()) throw Error("Can't match empty program")
//        val target = program.joinToString(",")
//
//        var nextStart = 0
//        do {
//            val allPrefixed = if(program.size == 1) listOf()  else findPrefix(program.drop(1))
//            nextStart = if(addedSuffix == 0) 1 else addedSuffix + 1
//            var counter = start
//            val digitLength = log2(start.toDouble()).toInt()
//            val ceiling = 2.0.pow(digitLength).toLong()
//            while(counter < ceiling) {
//                val binaryString = counter.toBinaryPadded(digitLength)
//                val combined = prefix + binaryString
//                val aValue = combined.toLong(2)
//
//                val cpu = this.copy(aRegister = aValue)
//                val output = cpu.runProgram()
//                if(output == target) {
//                    return (prefix + counter.toBinaryPadded(digitLength) to counter)
//                }
//                counter++
//            }
//        } while(true)
//
//        throw Error("no solution, stopped at prefix: $nextStart")
//    }

    val digitLength = 3
    fun CPU.findPrefixBottomUp(prefix: String = "", depth: Int = 1): String? {
        val target = program.takeLast(depth).joinToString(",")

        val ceiling = 2.0.pow(digitLength).toLong()
        var counter = 0
        val solutions = mutableListOf<String>()
        while(counter < ceiling) {
            val binaryString = counter.toBinaryPadded(digitLength)
            val combined = prefix + binaryString
            val aValue = combined.toLong(2)

            val cpu = this.copy(aRegister = aValue)
            val output = cpu.runProgram()
            if(output == target) {
                val solution = prefix + counter.toBinaryPadded(3)
                solutions += solution
                if(program.size == depth) return solution
            }
            counter++
        }

        solutions.mapNotNull { newPrefix ->
            val solution = findPrefixBottomUp(newPrefix, depth + 1) ?: return@mapNotNull null
            return solution
        }
        return null
    }

    fun part2(input: List<String>): Long {
        val baseCPU = parse(input)
        val solution = baseCPU.findPrefixBottomUp()
        return solution!!.toLong(2)
    }


    val testInput = readInput("Day17Test")
    checkDebug(part1(testInput), "4,6,3,5,6,3,5,2,1,0")
//    checkDebug(part2(testInput), 117440)

    val input = readInput("Day17")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

enum class OpCode(val opCodeValue: Int) {
    ADV(0),
    BXL(1),
    BST(2),
    JNZ(3),
    BXC(4),
    OUT(5),
    BDV(6),
    CDV(7);

    companion object {
        val map = entries.associateBy(OpCode::opCodeValue)
    }
}