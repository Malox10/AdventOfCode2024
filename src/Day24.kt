fun main() {


    fun parse(input: List<String>): Pair<Map<String, Boolean>,  List<Pair<Pair<String, String>, Pair<(Boolean, Boolean) -> Boolean, String>>>> {
        val (wires, gates) = input.joinToString("\n").split("\n\n").map { it.lines() }
        val parsedWires = wires.map { wire ->
            val (name, boolean) = wire.split(": ")
            val bool = boolean.trim() == "1"
            name to bool
        }.toMap()

        val parsedGates = gates.map { gate ->
            val parts = gate.split(" ")
            val (a, logicGate, b, _arrow, c) = parts
            val transform = when(logicGate) {
                "OR" -> { x: Boolean, y: Boolean -> x or y }
                "AND" -> { x: Boolean, y: Boolean -> x and y }
                "XOR" -> { x: Boolean, y: Boolean -> x xor y }
                else -> error("gate: $logicGate should not exist")
            }
            (a to b) to (transform to c)
        }

        return parsedWires to parsedGates
    }

    fun solveWires(wires: Map<String, Boolean>, gates: List<Pair<Pair<String, String>, Pair<(Boolean, Boolean) -> Boolean, String>>>)
        :Map<String, Boolean> {
        val solvedWires = wires.toMutableMap()
        val unsolvedGates = gates.toMutableList()
        while(unsolvedGates.isNotEmpty()) {
            val iterator = unsolvedGates.iterator()
            iterator.forEach { (inputs, lambdaPair) ->
                val (lambda, output) = lambdaPair
                val a = solvedWires[inputs.first]
                val b = solvedWires[inputs.second]
                if(a != null && b != null) {
                    solvedWires[output] = lambda(a, b)
                    iterator.remove()
                }
            }
        }
        return solvedWires
    }

    fun calculateScore(result: Map<String, Boolean>): Long {
        val zs = result.entries.filter { it.key.startsWith("z") }.sortedBy { it.key }
        val string = zs.joinToString("") { if (it.value) "1" else "0" }.reversed()
        return string.toLong(2)
    }

    fun part1(input: List<String>): Long {
        val (wires, gates) = parse(input)
        val result = solveWires(wires, gates)
        return calculateScore(result)
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }



    val testInput = readInput("Day24Test")
    checkDebug(part1(testInput), 2024)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day24")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}