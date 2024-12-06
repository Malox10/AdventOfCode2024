fun main() {
    fun part1(input: List<String>): Int {
        val guard = Guard(input.map { line -> line.map { it } })
        guard.moveAll()
        return guard.visitedPositions.size
    }

    fun part2(input: List<String>): Int {
        val guard = Guard(input.map { line -> line.map { it } })
        guard.moveAll()
        return guard.visitedPositions.map {  block ->
            val newGrid = input.mapIndexed { row, line ->
                line.mapIndexed inner@ { col, char->
                    if(char == '^') return@inner char
                    if(block.first == row && block.second == col) '#' else char
                }
            }

            val newGuard = Guard(newGrid)
            newGuard.moveAllPart2()
        }.count { it }
    }



    val testInput = readInput("Day06Test")
    checkDebug(part1(testInput), 41)
    checkDebug(part2(testInput), 6)

    val input = readInput("Day06")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

class Guard(private val grid: List<List<Char>>) {
    private val directions = LoopedList(listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)) // starts always looking north?
    private var currentDirection: Pair<Int, Int> = directions.next()
    private lateinit var currentPosition: Pair<Int, Int>
    val visitedPositions = mutableSetOf<Pair<Int, Int>>()
    private val visitedStates = mutableSetOf<Pair<Pair<Int, Int>, Pair<Int, Int>>>()

    init {
        outer@ for (row in grid.indices) {
            for (col in grid[0].indices) {
                if(grid[row][col] == '^') {
                    currentPosition = row to col
                    break@outer
                }
            }
        }
    }

    private fun turn() {
        currentDirection = directions.next()
    }

    private fun move(): Boolean {
        val newPosition = currentPosition + currentDirection
        val tile = grid[newPosition]
        when (tile) {
            null -> return false
            '#' -> turn()
            else -> currentPosition = newPosition
        }
        return true
    }

    fun moveAll() {
        do {
            visitedPositions.add(currentPosition)
            val canMove = move()
        } while(canMove)
    }

    //returns true if it loops
    fun moveAllPart2(): Boolean {
        do {
            val newState = currentPosition to currentDirection
            if(visitedStates.contains(newState)) return true
            visitedStates.add(newState)
            val canMove = move()
        } while(canMove)

        return false
    }
}
