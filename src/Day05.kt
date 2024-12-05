import kotlin.math.floor
import kotlin.math.roundToInt

fun main() {
    fun parse(input: List<String>): List<Pair<Update, Orders>> {
        val (pairs, updates) = input.joinToString("\n")
            .split("\n\n")
            .map { it.split("\n") }

        val numberUpdates = updates.map { it.split(",").map { x -> x.trim().toInt() } }
        val orders = pairs
            .map { it.split("|").map { x -> x.trim().toInt() }
            .let { (a,b) -> a to b} }

        val updatesToOrders = numberUpdates.map { update ->
            update to orders.filter { update.contains(it.first) && update.contains(it.second) }
        }
        return updatesToOrders
    }

    fun Update.checkRule(rule: Order) = this.indexOf(rule.first) < this.indexOf(rule.second)
    fun Update.isOrdered(orders: Orders) = orders.all { this.checkRule(it) }
    fun Update.middle() = this[floor(this.size / 2.0).roundToInt()]

    fun part1(input: List<String>): Int {
        val updatesToOrders = parse(input)
        return updatesToOrders.sumOf { (update, orders) ->
            if(update.isOrdered(orders)) update.middle() else 0
        }
    }

    fun part2(input: List<String>): Int {
        val updatesToOrders = parse(input)
        val unorderedUpdates = updatesToOrders
            .filter { (update, orders) -> !update.isOrdered(orders) }

        return unorderedUpdates
            .map { (update, order) ->
                update.sortedWith(
                    Comparator { a, b ->
                        val inputSet = setOf(a, b)
                        val currentOrder = order.find { order ->
                            val set = order.toList().toSet()
                            inputSet == set
                        }

                        currentOrder ?: return@Comparator 0
                        if(currentOrder.first == a) -1 else 1
                    }
                )
            }

            .sumOf { it.middle() }
    }

    val testInput = readInput("Day05Test")
    checkDebug(part1(testInput), 143)
    checkDebug(part2(testInput), 123)

    val input = readInput("Day05")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

typealias Orders = List<Order>
typealias Order = Pair<Int, Int>
typealias Update = List<Int>