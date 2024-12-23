fun main() {

    fun parse(input: List<String>): Pair<Map<String, Links>, Set<Link>> {
        val map = mutableMapOf<String, MutableList<Pair<String, String>>>()
        val links = input.map { line ->
            val (a, b) = line.split("-")
            a to b
        }
        links.forEach { pair ->
            map.addToList(pair.first, pair)
            map.addToList(pair.second, pair)
        }

        return map to (links + links.map { it.swap() }).toSet()
    }

    fun Set<Link>.findTriple(a: Link, b: Link): Set<String>? {
        return when {
            a.first == b.first -> if(this.contains(a.second to b.second)) setOf(a.second, b.second, a.first) else null
            a.first == b.second -> if(this.contains(a.second to b.first)) setOf(a.second, b.first, a.first) else null
            a.second == b.first -> if(this.contains(a.first to b.second)) setOf(a.first, b.second, a.second) else null
            a.second == b.second -> if(this.contains(a.first to b.first)) setOf(a.first, b.first, a.second) else null
            else -> error("a and b have to match in one coordinate a: $a, b: $b")
        }
    }

    fun part1(input: List<String>): Int {
        val (map, links) = parse(input)
        val tTriples = links.flatMap { link ->
            val candidates = map[link.first]!! + map[link.second]!!
            val triples = candidates.mapNotNull { candidate ->
                if (link.toList() == candidate.toList()) return@mapNotNull null
                links.findTriple(link, candidate) ?: return@mapNotNull null
            }
            triples.filter { links ->
                links.any { it.startsWith("t") }
            }
        }
        val tTriplesSet = tTriples.toSet()
        return tTriplesSet.size
    }

    fun part2(input: List<String>): Int {
        val x = parse(input)
        return input.size
    }


    val testInput = readInput("Day23Test")
    checkDebug(part1(testInput), 7)
//    checkDebug(part2(testInput), 1)

    val input = readInput("Day23")
    "part1: ${part1(input)}".println()
    "part2: ${part2(input)}".println()
}

typealias Links = List<Link>
typealias Link = Pair<String, String>