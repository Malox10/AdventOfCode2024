import OpCode.*
//import java.util.*
import kotlin.collections.ArrayDeque
import kotlin.math.pow

fun main() {
    data class CPU(private var aRegister: Long, private var bRegister: Long, private var cRegister: Long, val program: List<Int>, var target: ArrayDeque<Int>? = null) {
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
                OUT -> {
                    val newOutput = comboOperand(operand) % 8
                    if(target != null) {
                        val targetValue = target!!.removeFirstOrNull()?.toLong()
                        if(newOutput != targetValue) return false
                    }
                    output.add(newOutput.toInt())
                }
                BDV -> { bRegister = aRegister / 2.0.pow(comboOperand(operand).toDouble()).toInt() }
                CDV -> { cRegister = aRegister / 2.0.pow(comboOperand(operand).toDouble()).toInt() }
            }

            if(opCode != JNZ) programCounter += 2
            return true
        }

        fun checkTarget(): Boolean {
            output.forEachIndexed { index, value -> if(value != target!!.getOrNull(index)) return false }
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

    fun part2(input: List<String>): Long {
        val baseCPU = parse(input)
        baseCPU.target = ArrayDeque(baseCPU.program)
        val baseProgram = baseCPU.program.joinToString(",")
        var tentativeAValue = 0L
        while(true) {
            if(tentativeAValue % 10_000_000L == 0L) tentativeAValue.println()
            val cpu = baseCPU.copy(aRegister = tentativeAValue)
            val output = cpu.runProgram()
            if(output == baseProgram) return tentativeAValue
            tentativeAValue++
        }
    }


    val testInput = readInput("Day17Test")
    checkDebug(part1(testInput), "4,6,3,5,6,3,5,2,1,0")

    val testInput2 = readInput("Day17Test2")
    checkDebug(part2(testInput2), 117440)

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