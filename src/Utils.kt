import java.math.BigInteger
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
class LoopedList<T>(inner: List<T>) {
    private var pointer = 0
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
enum class Direction(val offset: Point) {
    North(-1 to 0),
    East(0 to 1),
    South(1 to 0),
    West(0 to -1)
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