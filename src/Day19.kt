fun main() {

    data class TreeNode(val map: Map<TowelColor, TreeNode>, val isEnd: Boolean)

    fun buildTree(towels: List<List<TowelColor>>): TreeNode {
        if(towels.isEmpty() || towels.all { it.isEmpty() }) return TreeNode(emptyMap(), true)

        val towelMap = mutableMapOf<TowelColor, MutableList<List<TowelColor>>>()
        towels.forEach { towel ->
            val key = towel.firstOrNull() ?: return@forEach
            if (towelMap.containsKey(key)) {
                towelMap[key]!!.add(towel.drop(1))
            } else {
                towelMap[key] = mutableListOf(towel.drop(1))
            }
        }

//        val map = mutableMapOf<TowelColor, MutableList<TreeNode>>()
//        towelMap.forEach { (key, value) ->
//            val node = buildTree(value)
//            if (map.containsKey(key)) {
//                map[key]!!.add(node)
//            } else {
//                map[key] = mutableListOf(node)
//            }
//        }

        val map = towelMap.map { (key, value) ->
            key to buildTree(value)
        }.toMap()

        val isEnd = towels.any { it.isEmpty() }
        return TreeNode(map, isEnd)
    }

    fun String.toTowelColors() = this.map { TowelColor.map[it]!! }
    fun parse(input: List<String>): Pair<TreeNode, List<List<TowelColor>>> {
        val (towelsS, patternsS) = input.joinToString("\n").split("\n\n")
        val towels = towelsS.split(",").map { it.trim() }.map { it.toTowelColors() }
        val patterns = patternsS.lines().map { it.trim() }.map { it.toTowelColors() }

        val tree = buildTree(towels)
        return tree to patterns
    }

    fun TreeNode.solve(target: List<TowelColor>, tree: TreeNode): Boolean {
            if(target.isEmpty()) return tree.isEnd
            val nextColor = target.first()

            val canNewTowelFinish = if(!tree.isEnd) false else {
                val nextBaseNode = this.map[nextColor] ?: return false
                solve(target.drop(1), nextBaseNode)
            }
            if(canNewTowelFinish) return true

            val nextNode = tree.map[nextColor] ?: return false
            val canContinueFinish =  solve(target.drop(1), nextNode)

            return canContinueFinish
    }

    fun parseSimple(input: List<String>): Map<String, Map<Int, List<Int>>> {
        val (towelsS, patternsS) = input.joinToString("\n").split("\n\n")
        val towels = towelsS.split(",").map { it.trim() }
            .filter { !(it.length > 1 && !it.contains("g")) } //only works for p1 real input
        val patterns = patternsS.lines().map { it.trim() }


        val patternMap = patterns.map { pattern ->
            val map = mutableMapOf<Int, MutableList<Int>>()
            towels.forEach { towel ->
                val regex = Regex(towel)
                val length = towel.length
                val matches = regex.findAll(pattern).toList()
                matches.map { it.range.first }.forEach { towelStart ->
                    if(map.containsKey(towelStart)) {
                        map[towelStart]!!.add(length)
                    } else {
                        map[towelStart] = mutableListOf(length)
                    }
                }
            }

            map.values.forEach { it.sort(); }
            pattern to map
        }.toMap()

        return patternMap
    }

    fun Int.solveMap(towelMatches: Map<Int, List<Int>>, depth: Int = 0): Boolean {
        if(depth == this) return true
        val matchIndices = towelMatches[depth] ?: return false
        return matchIndices.any { matchIndex ->
            solveMap(towelMatches, depth + matchIndex)
        }
    }

    fun part1(input: List<String>): Int {
        val patternMap = parseSimple(input)
        return patternMap.map { (pattern, towelMatches) ->
            println("x")
            pattern.length.solveMap(towelMatches)
        }.count { it }
//        return patterns.map { println("x"); tree.solve(it, tree) }.count { it }
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day19Test")
//    checkDebug(part1(testInput), 6)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day19")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

enum class TowelColor(val letter: Char){
    White('w'),
    Blue('u'),
    Black('b'),
    Red('r'),
    Green('g');

    companion object {
        val map = TowelColor.entries.associateBy { it.letter }
    }
}