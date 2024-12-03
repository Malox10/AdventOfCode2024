import kotlin.math.absoluteValue

fun main() {
    val name = "Day01"
    fun parse(input: List<String>): Pair<List<Int>, List<Int>> {
        val pairs = input.map { line ->
            line.split(" ")
                .filter { it.isNotEmpty() }
                .map { it.trim().toInt() }
                .let { (a, b) -> a to b }
        }

        return pairs.map { it.first } to pairs.map { it.second }
    }

    fun part1(input: List<String>): Int {
        val (left, right) = parse(input)

        return left.sorted()
            .zip(right.sorted())
            .sumOf { (a, b) -> (a - b).absoluteValue }
    }

    fun part2(input: List<String>): Int {
        val (left, right) = parse(input)
            .map { list -> list.toSet().associateWith { number -> list.count { it == number } } }

        return left.entries.sumOf { (key, count) -> key * count * (right[key] ?: 0) }
    }

    val testInput = readInput("${name}Test")
    check(part1(testInput) == 11)
    check(part2(testInput) == 31)

    val input = readInput(name)
    part1(input).println()
    part2(input).println()
}
