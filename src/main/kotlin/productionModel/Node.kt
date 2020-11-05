package productionModel

class Node(val fact: Fact?, val rule: Rule?, val parents: ArrayList<Node>?, var depth: Int, var wrongChildren: Int = 0) {
    val children: ArrayList<Node?> = ArrayList()

    fun addChild(node: Node?) = children.add(node)
}