import java.util.*


//too high 3445362
//         1009299
fun main() {
    data class Maze(val grid: List<List<Tile>>, val start: Point, val end: Point)

    fun parse(input: List<String>): Maze {
        var end = 0 to 0
        var start = 0 to 0
        val grid = input.mapIndexed { row, line ->
            line.mapIndexed { col, char ->
                when (char) {
                    '.' -> Tile.Floor
                    '#' -> Tile.Wall
                    'E' -> {
                        end = row to col
                        Tile.End
                    }

                    'S' -> {
                        start = row to col
                        Tile.Floor
                    }

                    else -> throw Error("unknown char: $char")
                }
            }
        }
        if (start == 0 to 0 || end == 0 to 0) throw Error("start or end not initialized")
        return Maze(grid, start, end)
    }

    data class Node(val position: Point, val cheatTime: Int, val time: Int, val score: Int)

    val cheatStarts = mutableListOf<Node>()
    fun Node.getScore(end: Point) = (end - position).let { it.first + it.second } + time
    fun Node.getNextNodes(maze: Maze): List<Node>? {
        return Direction.entries.mapNotNull { newDirection ->
            if (time == cheatTime) {
                val cheatPositions = mutableMapOf(position to time)
                repeat(20) {
                    val newCheats = mutableMapOf<Point, Int>()
                    cheatPositions.forEach { cheatPosition ->
                        Direction.entries
                            .asSequence()
                            .map { it.offset }
                            .map { cheatPosition.key + it }
//                            .map { it to maze.grid[it] }
//                            .filter { it.second != Tile.Wall }
                            .filter { it.isInside(maze.grid) }
                            .forEach { newPoint ->
                                if (!newCheats.containsKey(newPoint)) newCheats[newPoint] = cheatPosition.value + 1
                            }
                    }
                    newCheats.forEach { newCheat ->
                        if (!cheatPositions.containsKey(newCheat.key)) cheatPositions[newCheat.key] = newCheat.value
                    }
                }
                cheatPositions
                    .filter { it.key != position }
                    .map { it to maze.grid[it.key] }
                    .filter { it.second != Tile.Wall }
                    .forEach { (entry, _) ->
                        cheatStarts.add(Node(entry.key, -1, entry.value, 0))
                    }
                return null
            }

            val nextPosition = position + newDirection.offset
            if (!nextPosition.isInside(maze.grid)) return@mapNotNull null
            if (maze.grid[nextPosition] == Tile.Wall) return@mapNotNull null

            val newNode = Node(nextPosition, cheatTime, time + 1, score)
            val newScore = newNode.getScore(maze.end)
            newNode.copy(score = newScore)
        }
    }

    fun Maze.aStar(startNode: Node): Int? {
        val priorityQueue = PriorityQueue<Node>(compareBy { it.score })
        priorityQueue.add(startNode)
        val visitedNodes = mutableMapOf<Point, Int>()

        while (priorityQueue.isNotEmpty()) {
            val node = priorityQueue.remove()!!
            if (grid[node.position] == Tile.End) return node.time

            val key = node.position
            val value = visitedNodes[key]
            if (value != null && value < node.time) continue

            val nextNodes = node.getNextNodes(this) ?: return null
            priorityQueue.addAll(nextNodes)
            visitedNodes[key] = node.time
        }

        throw Error("No Path found")
    }

    val cache = mutableMapOf<Point, Int>()
    fun part2(input: List<String>, cutOff: Int = 100)
//    : List<Pair<Int, Int>> {
    :Int {
        val maze = parse(input)
        val baseTime = maze.aStar(Node(maze.start, -1, 0, 0))!!
        cheatStarts.clear()
        cache.clear()
        (0..baseTime).forEach { cheatTime ->
            cheatTime.println()
            maze.aStar(Node(maze.start, cheatTime, 0, 0))
        }

        val cheatTimes = cheatStarts.map { cheatStart ->
            val value = cache[cheatStart.position]
            if(value != null) {
                return@map baseTime - (value + cheatStart.time)
            }
            val shortcutTime= maze.aStar(cheatStart)!!
            cache[cheatStart.position] = shortcutTime - cheatStart.time

            val savedTime = baseTime - shortcutTime
            savedTime
        }.map {  savedTime -> if (savedTime < 0) 0 else savedTime }.counts()

        val filteredCheatTimes = cheatTimes.filter { (key, _) -> key >= cutOff }
//        return filteredCheatTimes.toList().map { it.swap() }.sortedBy { it.second }
        return filteredCheatTimes.map { it.value }.sum()
    }


    val testInput = readInput("Day20Test")
//    checkDebug(part2(testInput, 50), 44)
//    checkDebug(part2(testInput, 50)
//        , listOf(
//            32 to 50,
//            31 to 52,
//            29 to 54,
//            39 to 56,
//            25 to 58,
//            23 to 60,
//            20 to 62,
//            19 to 64,
//            12 to 66,
//            14 to 68,
//            12 to 70,
//            22 to 72,
//            4 to 74,
//            3 to 76,
//        ))

    val input = readInput("Day20")
    "part2: ${part2(input)}".println()
}