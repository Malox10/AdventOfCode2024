import java.util.*

fun main() {
    fun parse(input: List<String>) = input.map { line -> line.map { it } }

    val offsets = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1) //, 1 to 1, -1 to -1, 1 to -1, -1 to 1
    fun getNeighbours(start: Point) = offsets.map { offset -> start + offset }

    fun expandArea(point: Point, grid: List<List<Char>>): Pair<Set<Point>, Int> {
        val closedInsideArea = mutableSetOf<Point>()
//        val edgePoints = mutableSetOf<Point>()
        var edges = 0
        val edgeList = mutableListOf<Point>()

        val queue = ArrayDeque<Point>()
        queue.add(point)
        val char = grid[point]!!

        while (queue.isNotEmpty()) {
            val currentPoint = queue.removeFirst()
            getNeighbours(currentPoint).filter{
                !queue.contains(it) && !closedInsideArea.contains(it) // && !edgePoints.contains(it)
            }.forEach { neighbour ->
//                val neighbourChar = grid[neighbour] ?: return@forEach {}.let { edgePoints.add(neighbour) }
                                val neighbourChar = grid[neighbour] ?: return@forEach {}.let { edges++; edgeList.add(neighbour) }
                if(neighbourChar == char) queue.add(neighbour) else { edges++; edgeList.add(neighbour) }
            }
            closedInsideArea.add(currentPoint)
        }

//        val filteredEdges = edgePoints.filter { edge ->
//            getNeighbours(edge).count { closedInsideArea.contains(it) } == 1
//        }.toSet()
        return closedInsideArea to edges
    }

    fun part1(input: List<String>): Int {
        val grid = parse(input)
        val exploredPoints = mutableSetOf<Point>()

        var totalCost = 0
        for(row in grid.indices) {
            inner@ for(col in grid.first().indices) {
                val point = row to col
                if(exploredPoints.contains(point)) continue@inner
                val (inside, edge) = expandArea(row to col, grid)
                exploredPoints.addAll(inside)
                println("${grid[point]} area: ${inside.size} perimeter: ${edge}")
                totalCost += inside.size * edge
            }
        }
        return totalCost
    }

    val testInput = readInput("Day12Test")
    checkDebug(part1(testInput), 140)

    val testInput2 = readInput("Day12Test-2")
    checkDebug(part1(testInput2), 772)

    val testInput3 = readInput("Day12Test-3")
    checkDebug(part1(testInput3), 1930)

    val input = readInput("Day12")
    "part1: ${part1(input)}".println()
}