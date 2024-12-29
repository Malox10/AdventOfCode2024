enum class GateEnum(val string: String) {
    AND("AND"),
    XOR("XOR"),
    OR("OR");

    companion object {
        val map = GateEnum.entries.associateBy { it.name }
    }
}

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
            val (a, logicGate, b, _, c) = parts
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

    data class Component(val position: Int, val gate: GateEnum, val number: Int)
    data class Gate(val a: String, val b: String, val name: String, val component: Component)
    data class UnknownGate(val a: String, val b: String, val result: String, val gate: GateEnum)

    fun List<UnknownGate>.areTwoDifferent() = (size == 2) && (this[0].gate != this[1].gate)

    data class GateState(val found: List<Gate>, val unknown: List<UnknownGate>, val invalid: List<Gate>, val completed: List<Gate>)
    fun findNextGates(state: GateState): GateState {
        val newFound = mutableListOf<Gate>()
        val remainingFound = state.found.toMutableList()
        val iterator = remainingFound.iterator()

        val completed = mutableListOf<Gate>()
        completed.addAll(state.completed)

        val invalid = mutableListOf<Gate>()
        invalid.addAll(state.invalid)

        iterator.forEach { currentGate ->
            if(currentGate.component.number == 0) return@forEach
            if(currentGate.name == "ccn") {
                currentGate.println()
            }
            when(currentGate.component.gate) {
                GateEnum.AND -> {
                    if(completed.contains(currentGate)) {
                        iterator.remove()
                        return@forEach //fix for completing both gates at once but can only remove the current one from the iterator
                    }
                    val otherGateTarget = if(currentGate.component.position == 1) 2 else 1
                    val nextGates = state.unknown.filter { it.a == currentGate.name || it.b == currentGate.name }

                    if(nextGates.size != 1 || nextGates.any { it.gate != GateEnum.OR }) {
                        iterator.remove()
                        invalid.add(currentGate)
                        return@forEach
                    }

                    val nextGate = nextGates.first()
                    val otherName = if(nextGate.a == currentGate.name) nextGate.b else nextGate.a

                    val otherComponent = Component(otherGateTarget, GateEnum.AND, currentGate.component.number)
                    val otherGate = remainingFound.find { it.component == otherComponent } ?: return@forEach
                    if(otherGate.name != otherName) {
                        println("And gates didn't match up $currentGate, $otherGate")
                        iterator.remove()
                        invalid.add(currentGate)
                        return@forEach
                    }
                    if(nextGate.gate != GateEnum.OR) {
                        println("next gate must be OR")
                        iterator.remove()
                        invalid.add(currentGate)
                        return@forEach
                    }

                    val nextComponent = Component(1, nextGate.gate, currentGate.component.number + 1)
                    newFound.add(Gate(nextGate.a, nextGate.b, nextGate.result, nextComponent))
                    completed.add(currentGate)
                    completed.add(otherGate)
                    iterator.remove()
                }
                GateEnum.XOR -> {
                    if(currentGate.component.position == 1) { //if xor1 search OR1[number - 1]
                        val nextGates = state.unknown.filter { it.a == currentGate.name || it.b == currentGate.name }
                        val isValid = nextGates.areTwoDifferent()
                        if(!isValid || nextGates.any { it.gate == GateEnum.OR }) {
                            invalid.add(currentGate)
                            iterator.remove()
                            return@forEach
                        }
//                        if(nextGates.any { it.gate == GateEnum.OR }) error("XOR1 can't be connected to OR")
                        val new = nextGates.mapNotNull {
                            val otherGate = if(it.a == currentGate.name) it.b else it.a
                            state.found.find { gate -> gate.name == otherGate } ?: return@mapNotNull null

                            val gate = Gate(it.a, it.b, it.result, Component(2, it.gate, currentGate.component.number))
                            newFound.add(gate)
                        }
                        if(new.size == 2) {
                            iterator.remove()
                            completed.add(currentGate)
                        }
                    } else { //xor2 check if z[number]
                        val output = "z${currentGate.component.number.toString().padStart(2, '0')}"
                        if(currentGate.name != output) {
                            iterator.remove()
                            invalid.add(currentGate)
                            println("XOR2 not connected to right output")
                            return@forEach
                        }
                        iterator.remove(); completed.add(currentGate)
                    }
                }


                GateEnum.OR -> {}
                //if or1 search for XOR2[number +1] / And2[number + 1]
            }
        }
        "invalid: $invalid".println()
        "new: $newFound".println()
        "completed: $completed".println()

        val allFound = remainingFound + newFound
        val newUnknown = state.unknown.filter { gate ->
            !allFound.any { it.name == gate.result } && !invalid.any { it.name == gate.result }
        }

        return GateState(allFound, newUnknown, invalid, completed)
    }

    fun part2(input: List<String>) {
        val (_, gates) = input.joinToString("\n").split("\n\n").map { it.lines() }
        val foundGates = mutableListOf<Gate>()
        val unknown = gates.mapNotNull { gate ->
            val parts = gate.split(" ")
            val (a, logicGate, b, _, c) = parts
            val gateEnum = GateEnum.map[logicGate]!!
            val unknownGate = UnknownGate(a, b, c, gateEnum)
            val set = setOf(a[0], b[0])
            if(set != setOf('x', 'y')) return@mapNotNull unknownGate

            val numbers = setOf(a.substring(1..2), b.substring(1..2))
            if(numbers.size != 1) return@mapNotNull unknownGate



            val newComponent = Component(1, gateEnum, a.substring(1..2).toInt())
            val newGate = Gate(a, b, c, newComponent)
            foundGates.add(newGate)
            null
        }
        val grouped = foundGates.groupBy { foundGate ->
            if(foundGate.a.startsWith("x")) return@groupBy foundGate.a
            if(foundGate.b.startsWith("x")) return@groupBy foundGate.b
            null
        }
        assert(grouped.size == 45)
        val testSet = setOf(GateEnum.AND, GateEnum.XOR)
        assert(grouped.all { (_, value) ->
            testSet == setOf(value[0].component.gate, value[1].component.gate)
        })


        //swapped:
        //Gate(a=y09, b=x09, name=nnt, component=Component(position=1, gate=XOR, number=9))
        //Gate(a=x09, b=y09, name=gws, component=Component(position=1, gate=AND, number=9))
        //
        //npf <-> z13
        //
        //Gate(a=y19, b=x19, name=z19, component=Component(position=1, gate=AND, number=19))
        //Gate(a=rsm, b=fnq, name=cph, component=Component(position=2, gate=XOR, number=19))
        //
        //hgj
        //z33
        var state = findNextGates(GateState(foundGates, unknown, emptyList(), emptyList()))
        do {
            val nextState = findNextGates(state)
            val isDifferent = nextState != state
            state = nextState
        } while(isDifferent)
        if(state.invalid.isEmpty() && state.unknown.isEmpty()) {
            println("solved")
            listOf("nnt", "gws", "npf", "z13", "z19", "cph", "hgj", "z33")
                .sorted()
                .joinToString(",")
                .println()
        }
    }



    val testInput = readInput("Day24Test")
    checkDebug(part1(testInput), 2024)

    val input = readInput("Day24")
    "part1: ${part1(input)}".println()

    val inputEdited = readInput("Day24Edited")
    part2(inputEdited)
}
