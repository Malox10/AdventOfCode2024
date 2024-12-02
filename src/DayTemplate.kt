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



    // Or read a large test input from the `src/Day01_test.txt` file:
    val testInput = readInput("${name}Test")
//    check(part1(testInput) == 1)
//    check(part2(testInput) == 1)

    // Read the input from the `src/Day01.txt` file.
    val input = readInput(name)
    part1(input).println()
    part2(input).println()
}
