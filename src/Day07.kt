fun main() {
    data class Equation(val target: Long, val operands: List<Long>) {
        fun dropFirst() = this.copy(operands = operands.drop(1))
    }

    fun parse(input: List<String>): List<Equation> {
        return input.map { line ->
            val (result, operands) = line.split(":").map { it.trim() }
            val parsedOperands = operands.split(" ").map { it.trim().toLong() }

            Equation(result.toLong(), parsedOperands)
        }
    }

    val operatorsPart1 = listOf(
        { a: Long, b: Long -> a * b },
        { a: Long, b: Long -> a + b },
    )

    fun Equation.findCombinations(accumulator: Long, operators: List<(Long, Long) -> Long>): Boolean {
        if (this.operands.isEmpty()) return this.target == accumulator
        return operators.any { operation ->
            val result = operation(accumulator, this.operands.first())
            this.dropFirst().findCombinations(result, operators)
        }
    }

    fun Equation.hasCombination(operators: List<(Long, Long) -> Long> = operatorsPart1) =
        this.dropFirst().findCombinations(this.operands.first(), operators)

    fun part1(input: List<String>): Long {
        val equations = parse(input)
        return equations.filter { it.hasCombination() }.sumOf { it.target }
    }

    fun part2(input: List<String>): Long {
        val equations = parse(input)
        val allOperators = operatorsPart1.plus { a: Long, b: Long -> "$a$b".toLong() }

        return equations.filter { it.hasCombination(allOperators) }.sumOf { it.target }
    }

    val testInput = readInput("Day07Test")
    checkDebug(part1(testInput), 3749)
    checkDebug(part2(testInput), 11387)

    val input = readInput("Day07")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}