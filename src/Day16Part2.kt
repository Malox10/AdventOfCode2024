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


    data class Node(val position: Point, val direction: Direction, val score: Int, val visitedTiles: List<Point>)
    fun Node.getNextNodes(maze: Maze): List<Node> {
        val newDirections = direction.neighbours() + direction
        return newDirections.mapNotNull { newDirection ->
            var newScore = score + 1
            if(newDirection != direction) newScore += 1000

            val nextPosition = position + newDirection.offset
            if(maze.grid[nextPosition] == Tile.Wall) return@mapNotNull null

            Node(nextPosition, newDirection, newScore, visitedTiles + nextPosition)
        }
    }

    fun Maze.aStar(): Int {
         val priorityQueue = PriorityQueue<Node>(compareBy { it.score })
         priorityQueue.add(Node(start, Direction.East, 0, listOf(start)))
        val visitedNodes = mutableMapOf<Pair<Point, Direction>, Int>()

        var endScore = 0
        val optimalTiles = mutableSetOf<Point>()

         while(priorityQueue.isNotEmpty()) {
             val node = priorityQueue.remove()!!
             if(endScore > 0 && node.score > endScore) return optimalTiles.size

             if(grid[node.position] == Tile.End) {
                 if(endScore == 0) endScore = node.score
                 optimalTiles.addAll(node.visitedTiles)
             }

             val key = node.position to node.direction
             val value = visitedNodes[key]
             if(value != null && value < node.score) continue

             val nextNodes = node.getNextNodes(this)
             priorityQueue.addAll(nextNodes)
             visitedNodes[key] = node.score
         }

        throw Error("No Path found")
    }

    fun part2(input: List<String>): Int {
        val maze = parse(input)
        return maze.aStar()
    }

    val testInput = readInput("Day16Test")
    checkDebug(part2(testInput), 45)

    val testInput2 = readInput("Day16Test2")
    checkDebug(part2(testInput2), 64)

    val input = readInput("Day16")
    "part2: ${part2(input)}".println()
}
