fun main() {
    data class Equation(val target: Long, val operands: List<Long>)
    fun parse(input: List<String>): List<Equation> {
        return input.map { line ->
            val (result, operands) = line.split(":").map { it.trim() }
            val parsedOperands = operands.split(" ").map { it.trim().toLong() }

            Equation(result.toLong(), parsedOperands)
        }
    }

    val operators = listOf(
        { a: Long, b: Long -> a * b },
        { a: Long, b: Long -> a + b },
    )



    fun evaluateRecursive(operands: List<Long>, accumulator: Long): List<Long> {
        if(operands.isEmpty()) return listOf(accumulator)
        return operators.flatMap { operation ->
            val result = operation(accumulator, operands.first())
            evaluateRecursive(operands.drop(1), result)
        }
    }

    fun evaluate(operands: List<Long>) = evaluateRecursive(operands.drop(1), operands.first())


    fun part1(input: List<String>): Long {
        val equations = parse(input)
        return equations.sumOf { equation ->
            val possibleResults = evaluate(equation.operands)
            if(possibleResults.contains(equation.target)) equation.target else 0
        }
    }

    fun part2(input: List<String>): Long {
        val x = parse(input)
        return input.size.toLong()
    }



    val testInput = readInput("Day07Test")
    checkDebug(part1(testInput), 3749)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day07")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}