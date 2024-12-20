import java.util.*

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
        if(start == 0 to 0 || end == 0 to 0) throw Error("start or end not initialized")
        return Maze(grid, start, end)
    }

    data class Node(val position: Point, val cheatTime: Int, val time: Int, val score: Int)
    val cheatStarts = mutableListOf<Node>()
    fun Node.getScore(end: Point) = (end - position).let { it.first + it.second } + time
    fun Node.getNextNodes(maze: Maze): List<Node>? {
        return Direction.entries.mapNotNull { newDirection ->
            if(time == cheatTime) {
                Direction.entries
                    .asSequence()
                    .map { it.offset * 2 }
                    .map { position + it  }
                    .map { it to maze.grid[it] }
                    .filter { it.second != Tile.Wall }
                    .filter { it.first.isInside(maze.grid) }
                    .toList()
                    .forEach { cheatStarts.add(Node(it.first, -1, time + 2, 0)) }
                return null
            }

            val nextPosition = position + newDirection.offset
            if(!nextPosition.isInside(maze.grid)) return@mapNotNull null
            if(maze.grid[nextPosition] == Tile.Wall) return@mapNotNull null

            val newNode = Node(nextPosition, cheatTime, time + 1, score)
            val newScore = newNode.getScore(maze.end)
            newNode.copy(score = newScore)
        }
    }

    fun Maze.aStar(startNode: Node): Int? {
        val priorityQueue = PriorityQueue<Node>(compareBy { it.score })
        priorityQueue.add(startNode)
        val visitedNodes = mutableMapOf<Point, Int>()

        while(priorityQueue.isNotEmpty()) {
            val node = priorityQueue.remove()!!
            if(grid[node.position] == Tile.End) return node.time

            val key = node.position
            val value = visitedNodes[key]
            if(value != null && value < node.time) continue

            val nextNodes = node.getNextNodes(this) ?: return null
            priorityQueue.addAll(nextNodes)
            visitedNodes[key] = node.time
        }

        throw Error("No Path found")
    }

    fun part1(input: List<String>): Int {
        val maze = parse(input)
        val baseTime = maze.aStar(Node(maze.start, -1,0, 0))!!
        cheatStarts.clear()
        (0..baseTime).forEach { cheatTime ->
            maze.aStar(Node(maze.start, cheatTime,0, 0))
        }

        cheatStarts.println()
        val cheatTimes = cheatStarts.map { cheatStart ->
            val savedTime = baseTime - maze.aStar(cheatStart)!!
            if(savedTime < 0) 0 else savedTime
        }.counts()
        return cheatTimes.map { (key, value) -> if(key >= 100) value else 0 }.sum()
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day20Test")
//    checkDebug(part1(testInput), 1)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day20")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}