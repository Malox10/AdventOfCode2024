fun main() {
    fun parse(input: List<String>): List<List<Char>> {
        return input.map { line -> line.map { it } }
    }

    val offsets: List<Offsets> = listOf(
        listOf(0 to 0, 0 to 1, 0 to 2, 0 to 3), //horizontal
        listOf(0 to 0, 1 to 0, 2 to 0, 3 to 0), //vertical
        listOf(0 to 0, 1 to 1, 2 to 2, 3 to 3), //diagonal NE
        listOf(0 to 0, 1 to -1, 2 to -2, 3 to -3) //diagonal NW
//    ).flatMap { listOf(it, it.reversed()) }
    ).flatMap { listOf(it, it.map { (a, b) -> -a to -b }) } //easier debugging to make coords negative

    fun part1(input: List<String>): Int {
        val grid = parse(input)
        var count = 0

        for (row in grid.indices) {
            for (col in grid.first().indices) {
                offsets.map { offset ->
                    val string = offset.map { (rowOffset, colOffset) ->
                        grid.getOrNull(row + rowOffset)?.getOrNull(col + colOffset) ?: ' '
                    }.joinToString("")
                    if(string == "XMAS") count++
                }
            }
        }

        return count
    }

    val interCardOffsets: List<Offsets> = listOf(
        listOf(1 to 1, -1 to -1),
        listOf(1 to -1, -1 to 1)
    )

    fun part2(input: List<String>): Int {
        val grid = parse(input)
        val aIndices = grid.flatMapIndexed { row, chars: List<Char> ->
            chars.mapIndexedNotNull { column, c ->
                if(c == 'A') row to column else null
            }
        }

        val solution = setOf('S', 'M')
        val matches = aIndices.map { (row, col) ->
            interCardOffsets.all { offset ->
                val set = offset.map { (rowOffset, colOffset) ->
                    grid.getOrNull(row + rowOffset)?.getOrNull(col + colOffset)
                }.toSet()
                set == solution
            }
        }.count { it }

        return matches
    }

    val testInput = readInput("Day04Test")
    checkDebug(part1(testInput), 18)
    checkDebug(part2(testInput), 9)

    val input = readInput("Day04")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

typealias Offsets = List<Pair<Int, Int>>
