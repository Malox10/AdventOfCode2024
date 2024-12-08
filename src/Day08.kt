fun main() {
    data class AntennaMap(val grid: List<List<Char>>, val antennas: Map<Char, List<Pair<Int, Int>>>)
    fun parse(input: List<String>): AntennaMap {
        val grid = input.map { line -> line.map { it } }
        val antennas = mutableMapOf<Char, MutableList<Pair<Int, Int>>>()

         grid.forEachIndexed { row, line ->
            line.forEachIndexed inner@ { col, char ->
                if(char == '.') return@inner
                val entry = antennas[char]
                if (entry != null) {
                    entry.add(row to col)
                } else {
                    antennas[char] = mutableListOf(row to col)
                }
            }
        }

        return AntennaMap(grid, antennas)
    }

    fun<T> Pair<Int, Int>.isInside(grid: List<List<T>>): Boolean {
        if(this.first < 0 || this.first >= grid.size) return false
        if(this.second < 0 || this.second >= grid.first().size) return false
        return true
    }
    fun List<Pair<Int, Int>>.antinodes(
        antinodeProducer: (origin: Pair<Int, Int>, otherAntenna: Pair<Int, Int>) -> List<Pair<Int, Int>>
    ): List<Pair<Int, Int>> {
        val antinodes = this.flatMap { origin ->
            val otherAntennas = this.filter { it != origin }
            otherAntennas.flatMap { otherAntenna ->
                antinodeProducer(origin, otherAntenna)
            }
        }

        return antinodes
    }

    fun part1(input: List<String>): Int {
        val antennaMap = parse(input)
        val antinodes = antennaMap.antennas.flatMap { (_, value) ->
            value.antinodes { origin, otherAntenna ->
                val delta = otherAntenna - origin
                val antinode = otherAntenna + delta
                listOf(antinode)
            }.filter { it.isInside(antennaMap.grid) }
        }.toSet()
        return antinodes.size
    }

    fun part2(input: List<String>): Int {
        val antennaMap = parse(input)
        val antinodes = antennaMap.antennas.flatMap { (_, value) ->
            value.antinodes { origin, otherAntenna ->
                val delta = otherAntenna - origin
                val antinodes = mutableListOf(origin)
                var frequency = 1
                do {
                    val antinode = otherAntenna + (delta * frequency)
                    val isInside = antinode.isInside(antennaMap.grid)
                    if(isInside) antinodes.add(antinode)
                    frequency++
                } while (isInside)

                antinodes
            }.filter { it.isInside(antennaMap.grid) }
        }.toSet()
        return antinodes.size
    }

    val testInput = readInput("Day08Test")
    checkDebug(part1(testInput), 14)
    checkDebug(part2(testInput), 34)

    val input = readInput("Day08")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}