import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    val name = "Day02"
    //128 too low

    fun parse(input: List<String>): List<Level<Int>> {
        return input.map { line ->
            line.split(" ")
                .filter { it.isNotEmpty() }
                .map { it.trim().toInt() }
        }
    }

    fun Level<Int>.isSafe() = this.windowed(2).map { (a, b) ->
        val diff = a - b
        if (diff.absoluteValue > 3 || diff == 0) return false
        diff.sign
    }.toSet().size == 1

    fun part1(input: List<String>): Int {
        val levels = parse(input)
        return levels.count{ it.isSafe() }
    }

    fun part2(input: List<String>): Int {
        val levels = parse(input)
        return levels.map { level ->
            val newLevels = List(level.size) { i -> level.filterIndexed { index, _ -> i != index } }
            newLevels.map { it.isSafe() }.any { it }
        }.count { it }
    }

    val testInput = readInput("${name}Test")
    part1(testInput).println()
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput(name)
    part1(input).println()
    part2(input).println()
}

typealias Level<T> = List<T>
