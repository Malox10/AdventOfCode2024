fun main() {
    val name = "Day03"
    fun parse(input: List<String>) {

    }

    fun part1(input: List<String>): Int {
        val regex = Regex("""mul\((\d{1,3}),(\d{1,3})\)""")

        return input
            .flatMap { regex.findAll(it) }
            .sumOf {
                it.groupValues.drop(1).map { group ->
                    group.toInt()
                }.let { (a, b) -> a * b }
            }
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("${name}Test")
    check(part1(testInput) == 161)
//    check(part2(testInput) == 1)

    val input = readInput(name)
    part1(input).println()
    part2(input).println()
}
