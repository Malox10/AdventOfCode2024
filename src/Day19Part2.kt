import kotlin.math.pow

fun main() {
    //too high 6444504781413110
    //too low  894884712569357
    //too low  645205898520291
    //too low  644450478141311
    fun parseSimple(input: List<String>): Pair<List<String>, List<String>> {
        val (towelsS, patternsS) = input.joinToString("\n").split("\n\n")
        val towels = towelsS.split(",").map { it.trim() }
        val patterns = patternsS.lines().map { it.trim() }

        return patterns to towels
    }

    fun parse(input: List<String>): Map<String, Map<Int, List<Int>>> {
        val (towelsS, patternsS) = input.joinToString("\n").split("\n\n")
        val towels = towelsS.split(",").map { it.trim() }
        val patterns = patternsS.lines().map { it.trim() }

        val patternMap = patterns.associateWith { pattern ->
            val map = mutableMapOf<Int, MutableList<Int>>()
            towels.forEach { towel ->
                val regex = Regex(towel)
                val length = towel.length
                val matches = regex.findAll(pattern).toList()
                matches.map { it.range.first }.forEach { towelStart ->
                    if (map.containsKey(towelStart)) {
                        map[towelStart]!!.add(length)
                    } else {
                        map[towelStart] = mutableListOf(length)
                    }
                }
            }

            map.values.forEach { it.sort() }
            map
        }

        return patternMap
    }

    val cache = mutableMapOf<String, Long>()
    fun Int.solveMap(towelMatches: Map<Int, List<Int>>, pattern: String, depth: Int = 0): Long {
        val value = cache[pattern]
        if(value != null) return value

        if(depth == this) return 1L
        if(depth > this) throw Error("Depth $depth exceeds maximum of $this")
        val matchIndices = towelMatches[depth] ?: return 0L
        val totalCount = matchIndices.sumOf { matchIndex ->
            val newString = pattern.substring(matchIndex)
            solveMap(towelMatches, newString,depth + matchIndex)
        }
        cache[pattern] = totalCount
        return totalCount
    }

    //    fun part2(input: List<String>): Long {
//        val patternMap = parse(input)
//        val combinations = patternMap.map { (pattern, towelMatches) ->
//            cache.clear()
//            pattern to pattern.length.solveMap(towelMatches, pattern)
//        }
////        combinations.forEach { if(it.second == 0L) println(it.first) }
//        return combinations.sumOf { it.second }
//    }

    fun List<String>.solve(pattern: String): Long {
        val value = cache[pattern]
        if(value != null) return value

        if(pattern == "") return 1L

        val matchingTowels = filter { pattern.startsWith(it) }
        val totalCount = matchingTowels.sumOf { towel ->
            val remainingPattern = pattern.removePrefix(towel)
            solve(remainingPattern)
        }

        cache[pattern] = totalCount
        return totalCount
    }

    fun part2(input: List<String>): Long {
        val (patterns, towels) = parseSimple(input)
        val combinations = patterns.map { pattern ->
            cache.clear()
            pattern to towels.solve(pattern)
        }
        return combinations.sumOf { it.second}
    }

    val testInput = readInput("Day19Test")
    checkDebug(part2(testInput), 16)

    val repeat = 10
    val custom = ("r, b, rb, rrb\n\n" + "rrb".repeat(repeat)).lines()
    checkDebug(part2(custom), 3.0.pow(repeat).toLong())


    cache.clear()

    val input = readInput("Day19")
    "part2: ${part2(input)}".println()
}
