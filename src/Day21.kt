import kotlin.math.absoluteValue
import kotlin.math.sign

fun main() {
    fun parse(input: List<String>): List<List<Char>> {
        return input.map { line -> line.map { it } }
    }

    val keyPad = listOf(
        listOf('7', '8', '9'),
        listOf('4', '5', '6'),
        listOf('1', '2', '3'),
        listOf(null, '0', 'A')
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


    fun isNull(point: Point, isKeyPad: Boolean) = (if (isKeyPad) keyPad[point] else arrowKeys[point]) == null

    //calculates the shortest path from A to B
    fun shortestPaths(a: Point, b: Point, isKeyPad: Boolean): List<List<Direction>> {
        if (a == b) error("same point paths should never be calculated")
        val delta = b - a
        if (delta.first == 0 || delta.second == 0) {
            val offset = delta.first.sign to delta.second.sign
            val direction = Direction.pointToDirection[offset] ?: error("delta: $delta should be convertible")
            val times = if (delta.first != 0) delta.first.absoluteValue else delta.second.absoluteValue
            return listOf(List(times) { direction })
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
        if (rowFirstPoints.any { isNull(it, isKeyPad) }) return listOf(colFirst)

        val colFirstPoints = colFirst.runningFold(a) { acc, direction -> acc + direction.offset }
        if (colFirstPoints.any { isNull(it, isKeyPad) }) return listOf(rowFirst)

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
            val aPoint = arrowIndices[a]!!
            val bPoint = arrowIndices[b]!!
            val pair = a to b
            pair to when(pair) {
//                '^' to '>' -> listOf(listOf(Direction.South, Direction.East))
//                'A' to 'v' -> listOf(listOf(Direction.West, Direction.South))
//                '>' to '^' -> listOf(listOf(Direction.West, Direction.North))
//                'v' to 'A' -> listOf(listOf(Direction.East, Direction.North)) //not 100% sure about this optimization
                else -> shortestPaths(aPoint, bPoint, false)
            }

        }

    fun solvePad(password: List<Char>, isKeyPad: Boolean, ruleset: Ruleset? = null): List<KeyPadPath> {
        var pathPrefixes = mutableListOf<KeyPadPath>(emptyList())
        password.windowed(2).mapIndexed { index, (a, b) ->
            val newPathPrefixes = mutableListOf<List<Char>>()
            pathPrefixes.forEach { pathPrefix ->
                val additionalPaths = if (a == b) {
                    emptyList()
                } else {
//                    ruleset?.get(a to b) ?: if (isKeyPad) keyPadPaths[a to b]!! else arrowPadPaths[a to b]!!
                    if(ruleset != null) {
                        ruleset[a to b]!!
                    }
                    else if (isKeyPad) keyPadPaths[a to b]!! else arrowPadPaths[a to b]!!
                }
                additionalPaths.forEach { additionalPath ->
                    newPathPrefixes.add(pathPrefix + additionalPath.map { it.arrow } + listOf('A'))
                }
                if (additionalPaths.isEmpty()) newPathPrefixes.add(pathPrefix + listOf('A'))
            }
            pathPrefixes = newPathPrefixes
        }

        return pathPrefixes
    }

    fun solve(password: List<Char>, depth: Int = 2): List<KeyPadPath> {
        val expandedKeyPadPaths = solvePad(listOf('A') + password, true)
        var paths = expandedKeyPadPaths
        repeat(depth) { depth ->
            paths = paths.flatMapIndexed { index, path ->
                solvePad(listOf('A') + path, false)
            }
        }
        return paths
    }

    fun getScore(solution: List<Char>, password: List<Char>) =
        solution.size * password.dropLast(1).joinToString("").toInt()

    fun getScore(solution: Long, password: List<Char>) = solution * password.dropLast(1).joinToString("").toLong()
    fun part1(input: List<String>): Int {
        val passwords = parse(input)
        val scores = passwords.map { password ->
            val solutions = solve(password)
            val bestSolution = solutions.minBy { it.size }
            getScore(bestSolution, password)
        }
        return scores.sum()
    }

//    fun solveRecursive() {
//        val test1 = arrowPadPaths.map { (key, value) ->
//            val x = solvePad(listOf('A') + value.first().map { it.arrow }, false)
//            x
//        }
//
//
//        val a1 = solvePad(listOf('A', '<', 'v', 'A'), false)
//        val a2 = solvePad(listOf('A') + a1.first(), false)
//
//        val b1 = solvePad(listOf('A', 'v', '<', 'A'), false)
//        val b2 = solvePad(listOf('A') + b1.first(), false)
//        //[<, v, A, <, A, >, >, ^, A, v, A, <, ^, A, >, A] <<<AAAAAA vv >>> ^^
//        //[v, <, <, A, >, A, ^, >, A, <, A, v, >, A, ^, A] <<<AAAAAA vv >>> ^^
//        //<vA a -b
//        //v<<A b
//        val a = listOf('<', 'v', 'A', '<', 'A', '>', '>', '^', 'A', 'v', 'A', '<', '^', 'A', '>', 'A')
//        val b = listOf('v', '<', '<', 'A', '>', 'A', '^', '>', 'A', '<', 'A', 'v', '>', 'A', '^', 'A')
//        val aS = a.joinToString("").split("A").map { listOf('A') + it.toCharArray().toList() + listOf('A') }
//        val bS = b.joinToString("").split("A").map { listOf('A') + it.toCharArray().toList() + listOf('A') }
//        val aL = aS.map { solvePad(it, false) }
//        val bL = bS.map { solvePad(it, false) }
//        val x = solvePad(listOf('A', '<', 'A'), false)
//        val y = x.flatMap { path -> solvePad(listOf('A') + path, false) }
//        val z = y.flatMap { path -> solvePad(listOf('A') + path, false) }
//        val z2 = z.flatMap { path -> solvePad(listOf('A') + path, false) }
//        val q = z2.map { it.size }.counts()
//        q.println()
//        val allPaths = keyPadPaths.values.flatten().map { list ->
//            val chars = listOf('A') + list.map { it.arrow } + listOf('A')
//            val paths = solvePad(chars, false)
//            val double = paths.map { solvePad(listOf('A') + it, false) }.flatten()
//            val triple = double.map { it to solvePad(listOf('A') + it, false) }
//            triple
//        }.filter { it.size != 1 }
////            .filter { list -> list.any { it.size != 1 } }
//        allPaths.println()
////        val (a,b) = x.first().joinToString("").split('A')
////        val aS = solvePad(listOf('A') + a.toCharArray().toList() + listOf('A'), false)
////        val bS = solvePad(listOf('A') + b.toCharArray().toList() + listOf('A'), false)
////        aS.println()
//    }

    fun KeyPadPath.toCount(): Fragments {
        val parts = this.joinToString("").split("A").dropLast(1) //handle multiple As back to back
        return parts.countsLong().toList().toSet()
    }

    val fragmentCache = mutableMapOf<String, Set<Fragments>>()
    fun calculateFragment(fragment: String, count: Long, ruleset: Ruleset): Set<Fragments> {
        val value = fragmentCache[fragment] ?: run {
            val newPaths = solvePad(listOf('A') + fragment.toList() + listOf('A'), false, ruleset)
            val counts: Set<Fragments> = newPaths.map { it.toCount() }.toSet()
            fragmentCache[fragment] = counts
            counts
        }

        return value.map { fragments -> fragments.map { (key, amount) -> key to amount * count }.toSet() }.toSet()
    }

    operator fun Fragments.plus(other: Fragments): Fragments {
        val map = this.toMap().toMutableMap()
        other.forEach { (key, value) -> map.addToCountLong(key, value) }
        return map.toList().toSet()
    }

    fun expandPossibility(fragments: Fragments, ruleset: Ruleset): Set<Fragments> {
        val expandedFragments = fragments.map { calculateFragment(it.first, it.second, ruleset) }
        val allPossibilities = expandedFragments.reduce { a, b ->
            a.flatMap { fragments ->
                b.map {
                    val result = fragments + it
                    result
                }
            }.toSet()
        }
        return allPossibilities
    }

    fun Fragments.score() = sumOf { (it.first.length + 1) * it.second }
    fun KeyPadPath.solveRecursiveActual(depth: Int, ruleSet: Ruleset): Set<Fragments> {
        if (depth == 1) {
            val list = solvePad(listOf('A') + this, false, ruleSet)
            val count = list.map { it.toCount() }
            return count.toSet()
        }

        val possibilities = solveRecursiveActual(depth - 1, ruleSet)
        val allPossibilities = possibilities.map { expandPossibility(it, ruleSet) }
        val set = allPossibilities.flatten().toSet()
        val scored = set.map { fragments ->
            //one score for each button press + the additional A press to input that button, consecutive A presses have 0 length so it's just the A press
            fragments to fragments.score()
        }
        val minimum = scored.minBy { it.second }.second
        val filtered = scored.filter { (_, score) -> score == minimum }
        val filteredSet = filtered.map { it.first }.toSet()
        return filteredSet
    }

    fun Map.Entry<Pair<Char, Char>, List<List<Direction>>>.split(): List<Pair<Pair<Char, Char>, List<List<Direction>>>> {
        return this.value.map { value -> this.key to listOf(value) }
    }
    fun allRulesets(): List<List<Pair<Pair<Char, Char>, List<List<Direction>>>>> {
        val branches = arrowPadPaths.entries.filter { it.value.size > 1 }
        val starts = branches.first().split()
        val possibilities = branches.drop(1).fold(starts.map { listOf(it) }) { acc, next ->
            acc.flatMap { outer ->
                next.split().map { inner ->
                    outer + inner
                }
            }
        }

        val singles = arrowPadPaths.entries.filter { it.value.size == 1 }.map { (key, value) -> key to value }
        val all = possibilities.map { possibility -> singles + possibility }
        return all
    }

    //1360749272122 too low
    //76005733319250 too low
    //190256807834012 too high
    fun part2(input: List<String>): Long {
        val rulesets = allRulesets().map { it.toMap() }
        //        val x = listOf('<','A','^','A','>','^','^','A','v','v','v','A').solveRecursiveActual(2)
//        listOf('<', '^', 'A', 'A', 'A').solveRecursiveActual(4)
        val passwords = parse(input)
        val scores = passwords.map { password ->
            val start = solve(password, 0)
            val rulesetScores = rulesets.map { ruleset ->
                fragmentCache.clear()
                val results = start.map { it.solveRecursiveActual(25, ruleset) }
                val score = results
                    .minOfOrNull { it.first().score() }
                    .let { getScore(it!!.toLong(), password) }
                score.println()
                score
            }
            rulesetScores.min()
        }

        return scores.sum()
    }


//    val testInput = readInput("Day21Test")
//    checkDebug(part1(testInput), 126384)

    val input = readInput("Day21")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

typealias KeyPadPath = List<Char>
typealias Fragments = Set<Pair<String, Long>>
typealias Ruleset = Map<Pair<Char, Char>, List<List<Direction>>>