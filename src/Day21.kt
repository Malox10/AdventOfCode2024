import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    fun parse(input: List<String>): List<List<Char>> {
        return input.map { line -> line.map { it } }
    }

    val keyPad = listOf(
        listOf('7','8','9'),
        listOf('4','5','6'),
        listOf('1','2','3'),
        listOf(null,'0','A')
    )

    val keyIndices = keyPad.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char -> char to (rowIndex to colIndex) }
    }.toMap()

    val arrowKeys = listOf(
        listOf(null, '^', 'A'),
        listOf('<', 'v', '>'),
    )

    val arrowIndices = arrowKeys.flatMapIndexed { rowIndex, row ->
        row.mapIndexed { colIndex, char -> char to (rowIndex to colIndex) }
    }.toMap()


    fun isNull(point: Point, isKeyPad: Boolean) = (if(isKeyPad) keyPad[point] else arrowKeys[point]) == null

    //calculates the shortest path from A to B
    fun shortestPaths(a: Point, b: Point, isKeyPad: Boolean): List<List<Direction>> {
        if(a == b) error("same point paths should never be calculated")
        val delta = b - a
        if(delta.first == 0 || delta.second == 0) {
            val offset = delta.first.sign to delta.second.sign
            val direction = Direction.pointToDirection[offset] ?: error("delta: $delta should be convertible")
            val times = if(delta.first != 0) delta.first.absoluteValue else delta.second.absoluteValue
            return listOf(List(times) { direction } )
        }
        val rowOffset = delta.first.sign to 0
        val rowDirection = Direction.pointToDirection[rowOffset] ?: error("delta: $delta should be convertible")
        val rowTimes = delta.first.absoluteValue
        val rowMoves = List(rowTimes) { rowDirection }

        val colOffset = 0 to delta.second.sign
        val colDirection = Direction.pointToDirection[colOffset] ?: error("delta: $delta should be convertible")
        val colTimes = delta.second.absoluteValue
        val colMoves = List(colTimes) { colDirection }

        val rowFirst = rowMoves + colMoves
        val colFirst = colMoves + rowMoves

        val rowFirstPoints = rowFirst.runningFold(a) { acc, direction -> acc + direction.offset }
        if(rowFirstPoints.any { isNull(it, isKeyPad) }) return listOf(colFirst)

        val colFirstPoints = colFirst.runningFold(a) { acc, direction -> acc + direction.offset }
        if(colFirstPoints.any { isNull(it, isKeyPad) }) return listOf(rowFirst)

        return listOf(rowFirst, colFirst)
    }

    val keys = keyPad.flatten()
    val keyPadPaths = keys
        .flatMap { key -> keys.mapNotNull { (key ?: return@mapNotNull null) to (it ?: return@mapNotNull null) } }
        .filter { it.first != it.second }
        .associate { (a, b) ->
            val aPoint = keyIndices[a]!!
            val bPoint = keyIndices[b]!!
            (a to b) to shortestPaths(aPoint, bPoint, true)
        }

    val arrows = arrowKeys.flatten()
    val arrowPadPaths = arrows
        .flatMap { key -> arrows.mapNotNull { (key ?: return@mapNotNull null) to (it ?: return@mapNotNull null) } }
        .filter { it.first != it.second }
        .associate { (a, b) ->
            "$a, $b".println()
            val aPoint = arrowIndices[a]!!
            val bPoint = arrowIndices[b]!!
            (a to b) to shortestPaths(aPoint, bPoint, false)
        }

    fun solvePad(password: List<Char>, isKeyPad: Boolean): List<KeyPadPath> {
        var pathPrefixes = mutableListOf<KeyPadPath>(emptyList())
        password.windowed(2).map { (a, b) ->
            val newPathPrefixes = mutableListOf<List<Char>>()
            pathPrefixes.forEach { pathPrefix ->
                val additionalPaths = if(a == b) {
                    emptyList()
                } else {
                    if(isKeyPad) keyPadPaths[a to b]!! else arrowPadPaths[a to b]!!
                }
                additionalPaths.forEach { additionalPath ->
                    newPathPrefixes.add(pathPrefix + additionalPath.map { it.arrow } + listOf('A'))
                }
                if(additionalPaths.isEmpty()) newPathPrefixes.add(pathPrefix + listOf('A'))
            }
            pathPrefixes = newPathPrefixes
        }

        return pathPrefixes
    }

    fun solve(password: List<Char>): List<KeyPadPath> {
        val expandedKeyPadPaths = solvePad(listOf('A') + password, true)
        val arrowKeyPad1Paths = expandedKeyPadPaths.flatMap { path -> solvePad(listOf('A') + path, false) }
        val arrowKeyPad2Paths = arrowKeyPad1Paths.flatMap { path -> solvePad(listOf('A') + path, false) }
        return arrowKeyPad2Paths
    }

    fun getScore(solution: List<Char>, password: List<Char>): Int {
        return solution.size * password.dropLast(1).joinToString("").toInt()
    }
    fun part1(input: List<String>): Int {
        val passwords = parse(input)
        val scores = passwords.map { password ->
            val solutions = solve(password)
            val bestSolution = solutions.minBy { it.size }
            getScore(bestSolution, password)
        }
        return scores.sum()
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day21Test")
    checkDebug(part1(testInput), 126384)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day21")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

typealias KeyPadPath = List<Char>