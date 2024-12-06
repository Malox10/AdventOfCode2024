fun main() {
    fun parse(input: List<String>) {

    }

    fun part1(input: List<String>): Int {
        val guard = Guard(input.map { line -> line.map { it } })
        guard.moveAll()
        return guard.visitedPositions.size
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day06Test")
    checkDebug(part1(testInput), 41)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day06")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

class Guard(private val grid: List<List<Char>>) {
    private val directions = LoopedList(listOf(-1 to 0, 0 to 1, 1 to 0, 0 to -1)) // starts always looking north?
    private var currentDirection: Pair<Int, Int> = directions.next()
    private lateinit var currentPosition: Pair<Int, Int>
    val visitedPositions = mutableSetOf<Pair<Int, Int>>()

    init {
        outer@ for (row in grid.indices) {
            for (col in grid[0].indices) {
                if(grid[row][col] == '^') {
                    currentPosition = row to col
                    visitedPositions.add(currentPosition)
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
            else -> { // if it's . or ^
                visitedPositions.add(newPosition)
                currentPosition = newPosition
            }
        }
        return true
    }

    fun moveAll() {
        do {
            val canMove = move()
            println("$canMove at $currentPosition")
        } while(canMove)
    }
}

class LoopedList<T>(inner: List<T>) {
    private var pointer = 0
    private val list = inner

    fun next(): T {
        val element = list[pointer]
        pointer++
        if(pointer >= list.size) pointer = 0
        return element
    }
}