fun main() {
    fun parse(input: List<String>): IntArray {
        val digits = input.first().map { it.digitToInt() }
        val array = IntArray(digits.sum()) { -1 }

        var pointer = 0
        digits.chunked(2).mapIndexed { index, pair ->
            val blockLength = pair[0]
            val space = pair.getOrNull(1) ?: 0

            for(i in 0 until blockLength) {
                array[pointer] = index
                pointer++
            }
            pointer += space
        }
        return array
    }

    class DiskFormatter(val array: IntArray) {
        private var startPointer = 0
        private var endPointer = array.size - 1

        init {
            moveStartPointer()
        }

        private fun moveStartPointer() {
            do {
                startPointer++
                val isBlank = array[startPointer] == -1
            } while(!isBlank)
        }

        private fun moveEndPointer() {
            do {
                endPointer--
                val isBlank = array[endPointer] == -1
            } while(isBlank)
        }

        private fun moveDigit() {
            val digitToMove = array[endPointer]
            array[endPointer] = -1
            moveEndPointer()

            array[startPointer] = digitToMove
            moveStartPointer()
        }

        fun format() {
            do {
                moveDigit()
                val isFormatted = startPointer >= endPointer
            } while(!isFormatted)
            array.println()
        }

        fun getScore(): Long {
            var score = 0L
            array.forEachIndexed { index, value ->
                if(value == -1) return score
                score += value * index
            }
            return score
        }
    }

    fun part1(input: List<String>): Long {
        val array = parse(input)
        val formatter = DiskFormatter(array)
        formatter.format()
        return formatter.getScore()
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day09Test")
    checkDebug(part1(testInput), 1928)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day09")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

