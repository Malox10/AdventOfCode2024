fun main() {
    fun parse(input: List<String>) = input.first().split(" ").map { it.trim().toLong() }


    fun List<Long>.applyRules() = this.flatMap { number ->
        if (number == 0L) return@flatMap listOf(1L)
        val string = "$number"
        if (string.length % 2 == 0) return@flatMap listOf(
            string.substring(0, string.length / 2),
            string.substring(string.length / 2)
        ).map { it.toLong() }
        return@flatMap listOf(number * 2024)
    }

    fun part1(input: List<String>): Int {
        val stones = parse(input)
        var newStones = stones
        repeat(25) {
            newStones = newStones.applyRules()
        }
        return newStones.size
    }


    data class Key(val number: Long, val depth: Int)
    val cache = mutableMapOf<Key, Long>()

    fun applyRulesRecursive(currentNumber: Long, count: Long, depth: Int): Long {
        val key = Key(currentNumber, depth)
        val cachedCount = cache[key]
        if (cachedCount != null) return cachedCount

        if (depth == 0) return count

        val value = if (currentNumber == 0L) {
            applyRulesRecursive(1, count, depth - 1)
        } else {
            val string = "$currentNumber"
            if (string.length % 2 == 0) {
                listOf(
                    string.substring(0, string.length / 2),
                    string.substring(string.length / 2)
                ).sumOf { applyRulesRecursive(it.toLong(), count, depth - 1) }
            } else {
                applyRulesRecursive(currentNumber * 2024, count, depth - 1)
            }
        }

        cache[key] = value
        return value
    }

    fun part2(input: List<String>, depth: Int = 75): Long {
        val stones = parse(input)
        return stones.sumOf { stone -> applyRulesRecursive(stone, 1L, depth) }
    }

    val testInput = readInput("Day11Test")
    checkDebug(part1(testInput), 55312)
    checkDebug(part2(testInput, 25), 55312)

    val input = readInput("Day11")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}