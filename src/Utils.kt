import java.math.BigInteger
import java.security.MessageDigest
import kotlin.io.path.Path
import kotlin.io.path.readText

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = Path("src/input/$name.txt").readText().trim().lines()

/**
 * Converts string to md5 hash.
 */
fun String.md5() = BigInteger(1, MessageDigest.getInstance("MD5").digest(toByteArray()))
    .toString(16)
    .padStart(32, '0')

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
operator fun<T> List<List<T>>.get(pair: Pair<Int, Int>): T? = this.getOrNull(pair.first)?.getOrNull(pair.second)
