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

    data class Block(val start: Int, val length: Int)
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
                val isBlank = array[endPointer] == -1
                if(isBlank)  endPointer--
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

        fun findLastBlock(): Block {
            moveEndPointer()
            val startNumber = array[endPointer]
            var length = 0
            do {
                length++
                endPointer--
                val nextNumber = array[endPointer]
                val isSameDigit = nextNumber == startNumber
            } while(isSameDigit)
            return Block(endPointer + 1, length)
        }

        fun findSpace(block: Block): Block? {
            var pointer = startPointer
            var spaceLength = 0
            do {
                val number = array[pointer]
                if(number == -1) {
                    spaceLength++
                    if(spaceLength >= block.length) {
//                        startPointer = pointer + 1 add optimization for startPointer to move to closest gap
                        return Block(pointer - (spaceLength - 1), spaceLength)
                        //27248-1-1
                        //01234 5 6
                        //p = 6, sl = 2
                    }
                } else {
                    spaceLength = 0
                }
                pointer++
            } while (pointer < block.start)

            return null
        }

        fun moveIntoSpace(block: Block, space: Block) {
            if(block.length < space.length) throw Error("expected ${block.length} space but got ${space.length}")
            val number = array[block.start]
            for(i in 0 until block.length) {
                array[block.start + i] = -1
            }

            for (i in 0 until space.length) {
                array[space.start + i] = number
            }
        }

        fun moveBlock() {
            val movingBlock = findLastBlock()
            val space = findSpace(movingBlock) ?: return
            moveIntoSpace(movingBlock, space)
        }

        fun format2() {
            do {
                moveBlock()
            } while(startPointer < endPointer)
        }

        fun getScore(): Long {
            var score = 0L
            array.forEachIndexed { index, value ->
                if(value == -1) return@forEachIndexed
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

    fun part2(input: List<String>): Long {
        val array = parse(input)
        val formatter = DiskFormatter(array)
        formatter.format2()
        return formatter.getScore()
    }

    val testInput = readInput("Day09Test")
    checkDebug(part1(testInput), 1928)
    checkDebug(part2(testInput), 2858)

    val input = readInput("Day09")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}
