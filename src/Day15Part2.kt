fun main() {
    data class Warehouse(val grid: List<MutableList<Char>>, val moveset: LoopedList<Direction>, var robot: Point)

    fun Warehouse.printOut() {
        grid.forEachIndexed { row, line ->
            line.mapIndexed { col, char ->
                if (row to col == robot) '@' else char
            }.joinToString("").println()
        }
        kotlin.io.println()
    }

    fun parse(input: List<String>): Warehouse {
        val (warehouse, moves) = input.joinToString("\n").split("\n\n").map { it.split("\n") }

        val moveList = moves.joinToString("").map { char ->
            when (char) {
                '^' -> Direction.North
                '>' -> Direction.East
                'v' -> Direction.South
                '<' -> Direction.West
                else -> throw IllegalStateException("Unknown move $char")
            }
        }
        var robot = -1 to -1
        val grid = warehouse.mapIndexed { row, line ->
            line.trim().flatMapIndexed { col, char ->
                when (char) {
                    '@' -> {
                        robot = row to col * 2
                        listOf('.', '.')
                    }

                    'O' -> listOf('[', ']')
                    else -> listOf(char, char)
                }
            }.toMutableList()
        }
        return Warehouse(grid, LoopedList(moveList), robot).apply { printOut() }
    }

    fun Warehouse.tryMoveHorizontal(position: Point, direction: Direction): Boolean {
        val nextPosition = position + direction.offset
        return when (val nextChar = grid[nextPosition]) {
            null -> throw Error("can't move out of the maze, should meet barrier")
            '#' -> false
            '.' -> true
            '[', ']' -> { //if there's a box at next position, we try to move the box recursively
                val canMoveBox = tryMoveHorizontal(nextPosition, direction)
                if (canMoveBox) {
                    grid[nextPosition] = '.'
                    grid[nextPosition + direction.offset] = nextChar
                }
                canMoveBox
            }

            else -> throw Error("unknown token: $nextChar")
        }
    }


    data class PendingChange(val canMove: Boolean, val change: () -> Unit)

    fun Warehouse.tryMoveVertical(position: Point, direction: Direction): PendingChange {
        fun Warehouse.moveRecursive(
            nextPosition: Point,
            nextChar: Char,
            otherDirection: Direction,
            otherBoxPart: Char
        ): PendingChange {
            val moveBox = tryMoveVertical(nextPosition, direction)
            val moveOtherPart = tryMoveVertical(nextPosition + otherDirection.offset, direction)
            val canMoveBoth = moveBox.canMove && moveOtherPart.canMove
            val change = lambda@{
                moveBox.change()
                moveOtherPart.change()
                if ( //check this so we don't try to move the same box twice, this could've been prevented by making the box objects and not mutating the grid itself
                    grid[nextPosition] != '.' &&
                    grid[nextPosition + direction.offset] != nextChar &&

                    grid[nextPosition + otherDirection.offset] != '.' &&
                    grid[nextPosition + otherDirection.offset + direction.offset] != otherBoxPart
                ) {
                    grid[nextPosition] = '.'
                    grid[nextPosition + direction.offset] = nextChar

                    grid[nextPosition + otherDirection.offset] = '.'
                    grid[nextPosition + otherDirection.offset + direction.offset] = otherBoxPart
                }
            }
            return PendingChange(canMoveBoth, change)
        }

        val nextPosition = position + direction.offset
        return when (val nextChar = grid[nextPosition]) {
            null -> throw Error("can't move out of the maze, should meet barrier")
            '#' -> PendingChange(false) {}
            '.' -> PendingChange(true) {}
            '[' -> moveRecursive(nextPosition, nextChar, Direction.East, ']')
            ']' -> moveRecursive(nextPosition, nextChar, Direction.West, '[')
            else -> throw Error("unknown token: $nextChar")
        }
    }


    fun Warehouse.move() {
        val move = moveset.next()
//        move.println()
        if (move == Direction.East || move == Direction.West) {
            if (tryMoveHorizontal(robot, move)) robot += move.offset
        } else {
            val pendingChange = tryMoveVertical(robot, move)
            if (pendingChange.canMove) {
                pendingChange.change()
                robot += move.offset
            }
        }
    }

    fun Warehouse.score(): Int {
//        printOut()
        return this.grid.flatMapIndexed { row, line ->
            line.mapIndexed { col, char ->
                if (char == '[') (row * 100) + col else 0
            }
        }.sum()
    }

    fun Warehouse.solve(): Int {
//        val initialCounts = grid.flatten().counts()
        do {
//            printOut()
            move()
//            val afterCounts = grid.flatten().counts()
//            val matches = initialCounts.all { (key, value) ->
//                afterCounts[key] == value
//            }
//            if (!matches) {
//                printOut()
//                throw Error("count of elements changed")
//            }
        } while (moveset.pointer != 0)
        return score()
    }

    fun part2(input: List<String>): Int {
        val warehouse = parse(input)
        val score = warehouse.solve()
        warehouse.printOut()
        return score
    }

    val testInput3 = readInput("Day15Test3")
    part2(testInput3)

    val testInput = readInput("Day15Test")
    checkDebug(part2(testInput), 9021)

    val input = readInput("Day15")
    "part2: ${part2(input)}".println()
}