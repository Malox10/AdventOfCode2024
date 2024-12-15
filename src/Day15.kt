fun main() {
    data class Warehouse(val grid: List<MutableList<Char>>, val moveset: LoopedList<Direction>, var robot: Point)
    fun parse(input: List<String>): Warehouse {
        val (warehouse, moves) = input.joinToString("\n").split("\n\n").map { it.split("\n") }

        val moveList = moves.joinToString("").map { char ->
            when(char) {
                '^' -> Direction.North
                '>' -> Direction.East
                'v' -> Direction.South
                '<' -> Direction.West
                else -> throw IllegalStateException("Unknown move $char")
            }
        }
        var robot = -1 to -1
        val grid = warehouse.mapIndexed { row, line ->
            line.trim().mapIndexed { col, char ->
                if (char == '@') {
                    robot = row to col
                    '.'
                } else {
                    char
                }
            }.toMutableList()
        }
        return Warehouse(grid, LoopedList(moveList), robot)
    }

    fun Warehouse.tryMove(position: Point, direction: Direction): Boolean {
        val nextPosition = position + direction.offset
        return when(val nextChar = grid[nextPosition]) {
            null -> throw Error("can't move out of the maze, should meet barrier")
            '#' -> false
            '.' -> true
            'O' -> { //if there's a box at next position, we try to move the box recursively
                val canMoveBox = tryMove(nextPosition, direction)
                if(canMoveBox) {
                    grid[nextPosition] = '.'
                    grid[nextPosition + direction.offset] = 'O'
                }
                canMoveBox
            }
            else -> throw Error("unknown token: $nextChar")
        }
    }

    fun Warehouse.move() {
        val move = moveset.next()
        if(tryMove(robot, move)) robot += move.offset
    }

    fun Warehouse.score(): Int {
        return this.grid.flatMapIndexed { row, line ->
            line.mapIndexed { col, char ->
                if(char == 'O') (row * 100) + col else 0
            }
        }.sum()
    }

    fun Warehouse.solve(): Int {
        do {
            move()
        } while(moveset.pointer != 0)
        return score()
    }

    fun part1(input: List<String>): Int {
        val warehouse = parse(input)
        return warehouse.solve()
    }

    val testInput = readInput("Day15Test")
    checkDebug(part1(testInput), 10092)

    val testInput2 = readInput("Day15Test2")
    checkDebug(part1(testInput2), 2028)

    val input = readInput("Day15")
    "part1: ${part1(input)}".println()
}