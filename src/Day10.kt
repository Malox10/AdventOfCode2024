fun main() {
    fun parse(input: List<String>): List<List<Int>> = input.map { it.map { char -> char.digitToInt() } }

    val offsets = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)
    fun List<List<Int>>.findNeighbours(start: Point, height: Int)
        = offsets.map { offset -> start + offset }.filter { this[it] == height + 1 }

    fun List<List<Int>>.findStartingPoints(): List<Point> {
        return this.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, value ->
                if(value == 0) rowIndex to colIndex else null
            }
        }
    }

    fun List<List<Int>>.findPaths(start: Point): Set<Point> {
        val height = this[start] ?: throw Error("starting point $start not in grid")
        if(height == 9) return setOf(start)
        val neighbours = findNeighbours(start, height)

        return neighbours.flatMap { neighbour ->
            findPaths(neighbour)
        }.toSet()
    }

    fun part1(input: List<String>): Int {
        val grid = parse(input)
        val starts = grid.findStartingPoints()
        return starts.sumOf { grid.findPaths(it).count() }
    }

    fun List<List<Int>>.findPathsPart2(start: Point, path: List<Point> = emptyList()): List<Path> {
        val height = this[start] ?: throw Error("starting point $start not in grid")
        if(height == 9) return listOf(path)
        val neighbours = findNeighbours(start, height)
        return neighbours.flatMap { neighbour: Point ->
            findPathsPart2(neighbour, path + neighbour)
        }
    }

    fun part2(input: List<String>): Int {
        val grid = parse(input)
        val starts = grid.findStartingPoints()
        return starts.sumOf { grid.findPathsPart2(it).count() }
    }

    val testInput = readInput("Day10Test")
    checkDebug(part1(testInput), 36)
    checkDebug(part2(testInput), 81)

    val input = readInput("Day10")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

typealias Path = List<Point>