import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.net.http.HttpResponse.BodyHandlers


const val name = "13"
fun main() {
    val fullName = "Day$name"

    createDay(fullName)
    createFiles(fullName, name.toInt())
}

fun createDay(name: String) {
    val file = File("src/$name.kt")
    if(!file.createNewFile()) return

    val part1 = "\${part1(input)}"
    val part2 = "\${part2(input)}"

    val template = """fun main() {
    fun parse(input: List<String>) {

    }

    fun part1(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("${name}Test")
    checkDebug(part1(testInput), 1)
//    checkDebug(part2(testInput), 1)

    val input = readInput("$name")
    "part1: $part1".println()
    "part2: $part2".println()
}"""
    file.writeText(template)
}

fun createFiles(name: String, day: Int) {
    val input = File("src/input/$name.txt")
    input.createNewFile()
    input.writeText(getInput(day))

    val testInput = File("src/input/${name}Test.txt")
    testInput.createNewFile()
}

fun getInput(day: Int): String {
    val secret = runCatching {
        File("secret.txt").readText()
    }.getOrNull() ?: throw Error("Couldn't read secret.txt")
    val cookie = "session=$secret"

    val request = HttpRequest.newBuilder()
        .uri(URI("https://adventofcode.com/2024/day/$day/input"))
        .header("cookie", cookie)
        .GET()
        .build()

    val client: HttpClient = HttpClient.newHttpClient()
    val response: HttpResponse<String> = client.send(request, BodyHandlers.ofString())

    return response.body().reader().readText()
}
