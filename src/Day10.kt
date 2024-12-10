fun main() {
    fun parse(input: List<String>): List<List<Int>> = input.map { it.map { char -> char.digitToInt() } }

    val offsets = listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)
    fun findNeighbours(start: Point, height: Int, grid: List<List<Int>>)
        = offsets.map { offset -> start + offset }.filter { grid[it] == height + 1 }


    fun findPaths(start: Point, grid: List<List<Int>>): Set<Point> {
        val height = grid[start] ?: throw Error("starting point $start not in grid")
        if(height == 9) return setOf(start)
        val neighbours = findNeighbours(start, height, grid)

        return neighbours.flatMap { neighbour ->
            findPaths(neighbour, grid)
        }.toSet()
    }



    fun part1(input: List<String>): Int {


        val maze = parse(input)
        val starts = maze.flatMapIndexed { rowIndex, row ->
            row.mapIndexedNotNull { colIndex, value ->
                if(value == 0) rowIndex to colIndex else null
            }
        }

        return starts.sumOf { findPaths(it, maze).count() }
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day10Test")
    checkDebug(part1(testInput), 36)
    checkDebug(part2(testInput), 81)

    val input = readInput("Day10")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}