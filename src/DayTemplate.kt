@file:Suppress("UNUSED_PARAMETER", "UNUSED_VARIABLE")

fun main() {
    val name = "Day"
    fun parse(input: List<String>) {

    }

    fun part1(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("${name}Test")
    check(part1(testInput) == 1)
//    check(part2(testInput) == 1)

    val input = readInput(name)
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}
