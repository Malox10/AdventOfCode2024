import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/input/$name.txt").readText().trim().lines()

/**
 * The cleaner shorthand for printing output.
 */
fun Any?.println() = println(this)
fun<T> checkDebug(actual: T, expected: T) {
    try {
        check(actual == expected)
        println("test passed with result: $actual")
    } catch (e: Throwable) {
        println("expected: $expected\nactual: $actual")
        throw e
    }
}

fun<T, R> Pair<T, T>.map(block: (T) -> R) = block(this.first) to block(this.second)
operator fun Pair<Int, Int>.plus(other: Pair<Int, Int>) = first + other.first to second + other.second
operator fun Pair<Int, Int>.minus(other: Pair<Int, Int>) = first - other.first to second - other.second
operator fun Pair<Int, Int>.times(other: Int) = first * other to second * other
operator fun<T> List<List<T>>.get(pair: Pair<Int, Int>): T? = this.getOrNull(pair.first)?.getOrNull(pair.second)
operator fun<T> List<MutableList<T>>.set(pair: Pair<Int, Int>, value: T) = this.getOrNull(pair.first)?.set(pair.second, value)
class LoopedList<T>(inner: List<T>) {
    var pointer = 0; private set
    private val list = inner

    fun next(): T {
        val element = list[pointer]
        pointer++
        if(pointer >= list.size) pointer = 0
        return element
    }
}

typealias Point = Pair<Int, Int>
fun<T> Iterable<T>.counts() = this.toSet().associateWith { number -> this.count { it == number } }
enum class Direction(val offset: Point, val arrow: Char) {
    North(-1 to 0, '^'),
    East(0 to 1, '>'),
    South(1 to 0, 'v'),
    West(0 to -1, '<');

    fun neighbours(): List<Direction> {
        return when(this) {
            North -> listOf(West, East)
            East -> listOf(North, South)
            South -> listOf(East, West)
            West -> listOf(South, North)
        }
    }

    companion object {
        val pointToDirection = Direction.entries.associateBy { it.offset }
        val arrowToDirection = Direction.entries.associateBy { it.arrow }
    }
}

typealias LongPoint = Pair<Long, Long>
@JvmName("LongPointPlus")
operator fun LongPoint.plus(other: LongPoint) = first + other.first to second + other.second
@JvmName("LongPointMinus")
operator fun LongPoint.minus(other: LongPoint) = first - other.first to second - other.second
@JvmName("LongPointTimes")
operator fun LongPoint.times(other: Int) = first * other to second * other
fun<T, S> Pair<T, S>.swap() = this.second to this.first

fun lcm(a: Long, b: Long) = if (a == 0L || b == 0L) 0 else a * b / gcd(a, b)
fun gcd(a: Long, b: Long): Long {
    var aRegister = a
    var bRegister = b
    require(!(aRegister < 1 || bRegister < 1)) { "a or b is less than 1" }
    var remainder: Int
    do {
        remainder = aRegister.toInt() % bRegister.toInt()
        aRegister = bRegister
        bRegister = remainder.toLong()
    } while (bRegister != 0L)
    return aRegister
}

//fun bigLCM(a: BigInteger, b: BigInteger) = if (a == BigInteger.ZERO || b == BigInteger.ZERO) BigInteger.ZERO else a * b / bigGCD(a, b)
//fun bigGCD(a: BigInteger, b: BigInteger): BigInteger {
//    var aRegister = a
//    var bRegister = b
//    require(!(aRegister < BigInteger.ONE || bRegister < BigInteger.ONE)) { "a or b is less than 1" }
//    var remainder: BigInteger
//    do {
//        remainder = aRegister % bRegister
//        aRegister = bRegister
//        bRegister = remainder
//    } while (bRegister != BigInteger.ZERO)
//    return aRegister
//}

fun<T> Point.isInside(grid: List<List<T>>): Boolean {
    if(first < 0 || second < 0) return false
    if(first >= grid.size || second >= grid[0].size) return false
    return true
}