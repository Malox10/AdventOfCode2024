fun main() {
    val name = "Day03"

    val baseRegex = """mul\((\d{1,3}),(\d{1,3})\)"""
    fun List<String>.findScore() = this.map { it.toInt() }.let { (a, b) -> a * b }
    fun List<String>.findTotalScore(regex: Regex) =
        this.flatMap { regex.findAll(it) }.sumOf { it.groupValues.drop(1).findScore() }

    fun part1(input: List<String>): Int {
        val regex = Regex(baseRegex)
        return input.findTotalScore(regex)
    }

    fun part2(input: List<String>): Int {
        val regex = Regex("""don't\(\)|do\(\)|$baseRegex""")
        var enabled = true

        return regex.findAll(input.joinToString()).sumOf{ match ->
            when (match.value) {
                "don't()" -> enabled = false
                "do()" -> enabled = true
                else -> if (enabled) return@sumOf match.groupValues.drop(1).findScore()
            }
            0
        }
    }

    val testInput = readInput("${name}Test")
    check(part1(testInput) == 161)
    check(part2(listOf("xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))")) == 48)

    val input = readInput(name)
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}
