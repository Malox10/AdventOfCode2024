import OpCode.*
import kotlin.math.pow
fun main() {
    class CPU(a: Int, b: Int, c: Int, val program: List<Int>) {
        private var aRegister = a
        private var bRegister = b
        private var cRegister = c
        private var programCounter = 0
        private val output = mutableListOf<Int>()

        private fun comboOperand(operand: Int): Int {
            return when (operand) {
                0, 1, 2, 3 -> operand
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
                ADV -> { aRegister /= 2.0.pow(comboOperand(operand)).toInt() }
                BXL -> { bRegister = bRegister xor operand }
                BST -> { bRegister = comboOperand(operand) % 8 }
                JNZ -> { if(aRegister != 0) programCounter = operand else programCounter += 2 }
                BXC -> { bRegister = bRegister xor cRegister }
                OUT -> { output.add(comboOperand(operand) % 8) }
                BDV -> { bRegister = aRegister / 2.0.pow(comboOperand(operand)).toInt() }
                CDV -> { cRegister = aRegister / 2.0.pow(comboOperand(operand)).toInt() }
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
        val (a, b, c) = listOf(aS, bS, cS).map { it.split(": ")[1].trim().toInt() }

        val program = programS.split(": ")[1].trim().split(",").map { it.trim().toInt() }
        return CPU(a, b, c, program)
    }

    fun part1(input: List<String>): String {
        val cpu = parse(input)
        return cpu.runProgram()
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }


    val testInput = readInput("Day17Test")
    checkDebug(part1(testInput), "4,6,3,5,6,3,5,2,1,0")
//    checkDebug(part2(testInput), 1)

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