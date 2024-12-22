fun main() {
    fun parse(input: List<String>) = input.map { it.toLong() }

    fun mix(a: Long, b: Long) = a xor b
    fun prune(a: Long) = a % 16777216
    fun Long.step1(): Long {
        val a = this * 64
        val b = mix(this, a)
        return prune(b)
    }

    fun Long.step2(): Long {
        val a = this / 32
        val b = mix(this, a)
        return prune(b)
    }

    fun Long.step3(): Long {
        val a = this * 2048
        val b = mix(this, a)
        return prune(b)
    }

    fun Long.nextSecret() = this.step1().step2().step3()

    fun part1(input: List<String>): Long {
        val numbers = parse(input)
        return numbers.sumOf { initial ->
            var number = initial
            repeat(2000) {
                number = number.nextSecret()
            }
            number
        }
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day22Test")
    checkDebug(part1(testInput), 37327623)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day22")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}