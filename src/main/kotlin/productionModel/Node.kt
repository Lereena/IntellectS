package productionModel

class Node(val set: HashSet<String>, val parent: Node?, val rule: Rule?) {
    override fun equals(other: Any?): Boolean {
        val node = other as Node? ?: return false
        return set == node.set
    }

    override fun hashCode(): Int {
        var hash = 0
        for (elem in set)
            hash += elem.hashCode()
        return hash
    }
}