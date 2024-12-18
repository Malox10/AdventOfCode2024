import java.util.PriorityQueue

fun main() {
    fun parse(input: List<String>): List<Point> {
        return input.map { line -> line.split(",").map { it.trim().toInt() }.let { (a, b) -> a to b } }
    }


    data class Node(val point: Point, val steps: Int, val score: Int)
    fun getScore(location: Point, end: Point, steps: Int): Int {
        val delta = end - location
        return delta.first + delta.second + steps
    }
    fun Node.getNeighbours(errors: Set<Point>, end: Point): List<Node> {
        return Direction.entries.mapNotNull { direction ->
            val newDirection = point + direction.offset
            if(errors.contains(newDirection)) return@mapNotNull null

            if(newDirection.first < 0 || newDirection.second < 0) return@mapNotNull null
            if(newDirection.first > end.first || newDirection.second > end.second) return@mapNotNull null
            Node(newDirection, steps + 1, getScore(newDirection, end, steps))
        }
    }

    fun aStar(errors: Set<Point>, end: Point = 70 to 70): Int? {
        val priorityQueue = PriorityQueue<Node>(compareBy { it.score })
        priorityQueue.add(Node(Point(0, 0), 0, 0))
        val closedNodes = mutableMapOf<Point, Int>()

        while(priorityQueue.isNotEmpty()) {
            val currentNode = priorityQueue.remove()
            if(currentNode.point == end) return currentNode.steps
            val neighbours = currentNode.getNeighbours(errors, end)
                .filter { node ->
                    val value = closedNodes[node.point] ?: return@filter true
                    value > node.steps
                }
            //add filtering by closedNodes here
            priorityQueue.addAll(neighbours)
            closedNodes[currentNode.point] = currentNode.steps
        }

        return null
    }

    fun part1(input: List<String>, end: Point = 70 to 70, errorCount: Int = 1024): Int {
        val errors = parse(input).take(errorCount)
        return aStar(errors.toSet(), end)!!
    }

    fun part2(input: List<String>, end: Point = 70 to 70): String {
        var counter = 0
        while(true) {
            counter.println()
            counter++
            val errors = parse(input).take(counter)
            aStar(errors.toSet(), end) ?: return errors[counter - 1].let { (a, b) -> "$a,$b" }
        }
    }



    val testInput = readInput("Day18Test")
    checkDebug(part1(testInput, 6 to 6, 12), 22)
    checkDebug(part2(testInput, 6 to 6), "6,1")
    println("test done")

    val input = readInput("Day18")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}