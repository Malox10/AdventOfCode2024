fun main() {

    fun List<String>.toBittingCode(): List<Int> {
        val grid = this.map { string -> string.map { it } }
        val transposed = grid.transpose()
        val bitting = transposed.map { line -> line.count { it == '#' } - 1 }
        return bitting
    }

    fun parse(input: List<String>): Tuple<List<List<Int>>> {
        val blocks = input.joinToString("\n").split("\n\n").map { it.lines() }
        val keys = blocks.filter { block -> block.first().all { it == '.' }}
        val locks = blocks.filter { block -> block.first().all { it == '#' }}

        val keyBittings = keys.map { it.reversed().toBittingCode() }
        val lockBittings = locks.map { it.toBittingCode() }
        return keyBittings to lockBittings
    }

    fun List<Int>.isOverlappingLock(other: List<Int>): Boolean {
        return this.mapIndexed { index, value -> index to value}.any { (index, value) ->
            (value + other[index]) > 5
        }
    }

    fun part1(input: List<String>): Int {
        val (keyBittings, lockBittings) = parse(input)
        val notOverlapping = keyBittings.sumOf { keyBitting ->
            lockBittings.count { !keyBitting.isOverlappingLock(it) }
        }
        return notOverlapping
    }

    val testInput = readInput("Day25Test")
    checkDebug(part1(testInput), 3)

    val input = readInput("Day25")
    "part1: ${part1(input)}".println()
}