import java.util.*

fun main() {
    fun parse(input: List<String>) = input.map { line -> line.map { it } }

    data class Range(val start: Int, val end: Int)
    fun Set<Range>.simplify(): Set<Range> {
        if(this.size <= 1) return this
        val ranges = this.sortedBy { it.start }.toMutableList()
        val output = mutableListOf<Range>()
        while(ranges.size >= 2) {
            val first = ranges[0]
            val second = ranges[1]
            val delta = second.start - first.end

            ranges.remove(first)
            if(delta == 1) {
                ranges.remove(second)
                ranges.addFirst(Range(first.start, second.end))
            } else {
                output.add(first)
            }
        }

        output.addAll(ranges)
        return output.toSet()
    }

    data class Edge(val start: Point, val end: Point, val direction: Direction) {
        fun toRange(): Range {
            return if(direction.offset.first != 0) Range(start.second, end.second)
                else Range(start.first, end.first)
        }
    }
    fun List<Edge>.simplify(): List<Range> {
        val edgeMap = mutableMapOf<Pair<Direction, Int>, MutableList<Edge>>()
        this.forEach { edge ->
            val alignedDirection = if (edge.direction.offset.first == 0) edge.start.second else edge.start.first
            val key = (edge.direction to alignedDirection)
            if(edgeMap.containsKey(key)) {
                edgeMap[key]!!.add(edge)
            } else {
                edgeMap[key] = mutableListOf(edge)
            }
        }
        val rangeMap = edgeMap.map { (key, value) -> key to value.map { it.toRange() } }.toMap()
        val simplified = rangeMap.values.flatMap { it.toSet().simplify() }
        return simplified
    }

    fun expandArea(point: Point, grid: List<List<Char>>): Pair<Set<Point>, Int> {
        val closedInsideArea = mutableSetOf<Point>()
        val edges = mutableListOf<Edge>()

        val queue = ArrayDeque<Point>()
        queue.add(point)
        val char = grid[point]!!

        while (queue.isNotEmpty()) {
            val currentPoint = queue.removeFirst()
            Direction.entries.map { direction ->
                direction to currentPoint + direction.offset
            }.filter { (_, newPoint) ->
                !queue.contains(newPoint) && !closedInsideArea.contains(newPoint)
            }.forEach { (direction, neighbour) ->
                val edge = Edge(neighbour, neighbour, direction)
                val neighbourChar = grid[neighbour] ?: return@forEach run { edges.add(edge) }
                if (neighbourChar == char) queue.add(neighbour) else edges.add(edge)
            }

            closedInsideArea.add(currentPoint)
        }

        return closedInsideArea to edges.simplify().size
    }

    fun part2(input: List<String>): Int {
        val grid = parse(input)
        val exploredPoints = mutableSetOf<Point>()

        var totalCost = 0
        for (row in grid.indices) {
            inner@ for (col in grid.first().indices) {
                val point = row to col
                if (exploredPoints.contains(point)) continue@inner
                val (inside, edges) = expandArea(row to col, grid)
                exploredPoints.addAll(inside)
//                println("${grid[point]} area: ${inside.size} perimeter: $corners")
                totalCost += inside.size * edges
            }
        }
        return totalCost
    }

    val testInput = readInput("Day12Test")
    checkDebug(part2(testInput), 80)

    val testInput2 = readInput("Day12Test-2")
    checkDebug(part2(testInput2), 436)

    val testInput3 = readInput("Day12Test-4")
    checkDebug(part2(testInput3), 236)

    val testInput4 = readInput("Day12Test-5")
    checkDebug(part2(testInput4), 368)

    val input = readInput("Day12")
    "part2: ${part2(input)}".println()
}