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
        val secrets = parse(input)
        val map = mutableMapOf<List<Int>, Int>()

        secrets.forEach { secret ->
            val secretChanges = mutableMapOf<List<Int>, Int>()
            (1..2000)
                .runningFold(secret) { acc, _ -> acc.nextSecret() }
                .windowed(5)
                .forEach { window ->
                    val deltas =
                        window.windowed(2).map { (a, b) -> "$b".last().digitToInt() - "$a".last().digitToInt() }
                    val value = window.last().toString().last().digitToInt()
                    secretChanges.putIfAbsent(deltas, value)
                }

            secretChanges.forEach { (changes, value) ->
                val readValue = map[changes]
                map[changes] = (readValue ?: 0) + value
            }
        }

        return map.maxBy { it.value }.value
    }


    val testInput = readInput("Day22Test")
    checkDebug(part1(testInput), 37327623)

    val testInput2 = readInput("Day22Test2")
    checkDebug(part2(testInput2), 23)

    val input = readInput("Day22")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}