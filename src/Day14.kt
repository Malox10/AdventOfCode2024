fun main() {
//    val width = 101
//    val height = 103

//    val width = 11
//    val height = 7
    data class Guard(var position: Point, val move: Point) {
        fun move(width: Int, height: Int) {
            val newRow = ((position.first + move.first) + height) % height
            val newCol = ((position.second + move.second) + width) % width

            position = newRow to newCol
        }
    }
    fun parse(input: List<String>): List<Guard> {
        return input.map { line ->
            val (p, v) = line.trim().split(" ")
            val (pX, pY) = p.substring(2..p.lastIndex).split(",").map { it.trim().toInt() }
            val (vX, vY) = v.substring(2..v.lastIndex).split(",").map { it.trim().toInt() }

            Guard(pY to pX, vY to vX)
        }
    }

//    fun generateGrid() = List(103) { MutableList(101) { emptyList<Guard>().toMutableList() } }

    fun printArray(guards: List<Guard>, width: Int, height: Int) {
        val array = Array(height) { Array(width) { 0 } }
        guards.forEach { guard -> array[guard.position.first][guard.position.second]++ }
        array.forEach { row -> row.joinToString("") { if (it > 0) "█" else " " }.println() }
    }
    fun part1(input: List<String>, width: Int = 101, height: Int = 103): Int {
        val guards = parse(input)
        repeat(100) {
            guards.forEach { it.move(width, height) }
        }

        val list = MutableList(4) { 0 }
        guards.forEach { guard ->
            val row = height / 2
            val col = width / 2
            if(guard.position.first < row) {
                if(guard.position.second < col) list[0]++
                if(guard.position.second > col) list[1]++
            }

            if(guard.position.first > row) {
                if(guard.position.second < col) list[2]++
                if(guard.position.second > col) list[3]++
            }
        }

        printArray(guards, width, height)
        return list.reduce(Int::times)
    }

    fun checkKernel(guards: List<Guard>, width: Int, height: Int): Boolean {
        val array = Array(height) { Array(width) { 0 } }
        guards.forEach { guard -> array[guard.position.first][guard.position.second]++ }

        for(row in array.indices) {
            loop@ for(col in array.first().indices) {
                for(i in 1..5) {
                    for(j in 1..5) {
                        if((array.getOrNull(row + i)?.getOrNull(col + j) ?: 0) == 0) continue@loop
                    }
                }
                return true
            }
        }

        return false
    }
    fun part2(input: List<String>, width: Int = 101, height: Int = 103): Int {
        val guards = parse(input)
        var counter = 0
        while(true) {
            counter++
            guards.forEach { it.move(width, height) }
            if(checkKernel(guards, width, height)) {
                println("index: $counter")
                printArray(guards, width, height)
            }
        }
    }



    val testInput = readInput("Day14Test")
    checkDebug(part1(testInput, 11, 7), 12)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day14")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}