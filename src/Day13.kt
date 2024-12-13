fun main() {

    data class Game(val prize: LongPoint, val a: LongPoint, val b: LongPoint)
    fun parse(input: List<String>, isPartTwo: Boolean = false): List<Game> {
        return input.joinToString("\n").split("\n\n").map { block ->
            val regex = Regex("\\d+")
            val (a, b, prize) = block.split("\n").map { line ->
                val values = regex.findAll(line).flatMap { it.groupValues }.map { it.toLong() }
                val (x, y) = values.toList()
                x to y
            }
            
            val error = 10000000000000L to 10000000000000L
            Game(if(isPartTwo) prize + error else prize, a, b)
        }
    }

    fun part1(input: List<String>): Int {
        val games = parse(input)
        val score = games.sumOf { game ->
            var lowestTokenCost = 0
            for(a in 1..100) {
                for(b in 1..100) {
                    if(game.prize == game.a * a + game.b * b) {
                        val cost = a * 3 + b
                        if(lowestTokenCost == 0 || cost < lowestTokenCost) lowestTokenCost = cost
                    }
                }
            }
            lowestTokenCost
        }
        return score
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day13Test")
    checkDebug(part1(testInput), 480)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day13")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}