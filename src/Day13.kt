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

    fun solveLinearSystem(a: LongPoint, b: LongPoint, result: LongPoint): LongPoint? {
        val aLCM = lcm(a.first, a.second)
        val firstMultiplier = aLCM / a.first
        val secondMultiplier = aLCM / a.second

        val newB = (b.second * secondMultiplier) - (b.first * firstMultiplier)
        val newPrize = (result.second * secondMultiplier) - (result.first * firstMultiplier)
        if(newB == 0L) return null //co-linear

        val finalB = newPrize.toDouble() / newB.toDouble()
        if(finalB % 1.0 != 0.0) return null //non-integer solution

        val finalBLong = finalB.toLong()
        val rightSide = result.first - (b.first * finalBLong)

        val finalA = rightSide.toDouble() / a.first.toDouble()
        if(finalA % 1.0 != 0.0) return null //non-integer solution

        if(finalA <= 0 || finalBLong <= 0) return null

        return finalA.toLong() to finalBLong
    }

    fun part2(input: List<String>): Long {
        val games = parse(input, true)
        return games.map { game ->
            with(game) {
                val solution = if(a.first < a.second) {
                    solveLinearSystem(a, b, prize) ?: return@map 0
                } else {
                    solveLinearSystem(a.swap(), b.swap(), prize.swap()) ?: return@map 0
                }

                val score = (solution.first * 3) + solution.second
                score
            }
        }.sum()
    }



    val testInput = readInput("Day13Test")
    checkDebug(part1(testInput), 480)
//    checkDebug(part2(testInput), 480)

    val input = readInput("Day13")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}