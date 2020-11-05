package productionModel

import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Model(factsPath: String, rulesPath: String) {
    private val descriptionsToFacts = HashMap<String, Fact>()
    private val facts = ArrayList<Fact>()
    private val rules = ArrayList<Rule>()
    private val factIdToRules = ArrayList<ArrayList<Rule>>()

    init {
        val factsLines = File(factsPath).readLines()
        for ((factsCounter, line) in factsLines.withIndex()) {
            val newFact = Fact(factsCounter, line)
            facts.add(newFact)
            descriptionsToFacts[newFact.description] = newFact
            factIdToRules.add(ArrayList())
        }

        val rulesLines = File(rulesPath).readLines()
        for ((rulesCounter, line) in rulesLines.withIndex()) {
            val splitLine = line.split("->")
            val left = splitLine[0].trim()
            val right = splitLine[1].trim()

            val antecedents = left.split(',').asIterable().map { str -> descriptionsToFacts[str.trim()] }
            val consequent = descriptionsToFacts[right]

            val rule = Rule(rulesCounter, antecedents, consequent)
            rules.add(rule)
            factIdToRules[consequent!!.id].add(rule)
        }
    }

    fun forward(startFacts: Array<String>): ArrayList<Fact> {
        val state = Array(facts.size) { false }
        val rulesSet = HashSet<Rule>(rules)
        startFacts.forEach { desc -> state[descriptionsToFacts[desc]!!.id] = true }

        var flag = true
        while (flag) {
            val toRemove = ArrayList<Rule>()
            flag = false
            for (rule in rulesSet) {
                val newFact = rule.apply(state)
                if (newFact != null) {
                    state[newFact.id] = true
                    toRemove.add(rule)
                    flag = true
                    break
                }
            }
            toRemove.forEach { rule -> rulesSet.remove(rule) }
        }

        val result = ArrayList<Fact>()
        for (i in state.indices)
            if (state[i])
                result.add(facts[i])

        return result
    }

    fun backward(startFacts: Array<String>, endFacts: Array<String>) {
        val factNodes = Array<Node?>(facts.size) { null }
        val ruleNodes = Array<Node?>(rules.size) { null }
        val queue = LinkedList<Node>()

        for (description in startFacts)
            factNodes[descriptionsToFacts[description]!!.id] =
                Node(descriptionsToFacts[description], null, ArrayList(), 0)

        val temp = Rule(-1, endFacts.map { description -> descriptionsToFacts[description] }, null)
        val firstNode = Node(null, temp, ArrayList(), Int.MIN_VALUE, temp.antecedents.size)
        queue.addLast(firstNode)

        while (queue.size != 0 && firstNode.wrongChildren != 0) {
            val currentNode = queue.removeFirst()
            if (currentNode.fact != null) {
                if (currentNode.wrongChildren <= 0) {
                    riseToRoot(currentNode)
                    continue
                }

                val applicable = factIdToRules[currentNode.fact.id]
                currentNode.wrongChildren = 1
                for (rule in applicable) {
                    if (ruleNodes[rule.id] != null) {
                        currentNode.addChild(ruleNodes[rule.id])
                        continue
                    }

                    val newNode = Node(null, rule, ArrayList(), Int.MAX_VALUE, rule.antecedents.size)
                    newNode.parents!!.add(currentNode)
                    ruleNodes[rule.id] = newNode
                    currentNode.addChild(newNode)
                    queue.addLast(newNode)
                }
            } else {
                for (fact in currentNode.rule!!.antecedents) {
                    val factNode = factNodes[fact!!.id]
                    if (factNode != null) {
                        factNode.parents!!.add(currentNode)
                        currentNode.addChild(factNode)
                        if (factNode.wrongChildren == 0)
                            currentNode.wrongChildren--
                        continue
                    }

                    val newNode = Node(fact, null, ArrayList(), Int.MIN_VALUE, 1)
                    newNode.parents!!.add(currentNode)
                    factNodes[fact.id] = newNode
                    currentNode.addChild(newNode)
                    queue.addLast(newNode)
                }
                if (currentNode.wrongChildren <= 0)
                    riseToRoot(currentNode)
            }
        }
        if (firstNode.wrongChildren == 0) {
            val toPrint = Array(rules.size) { false }
            firstNode.children.map { child -> unwrapNode(child!!, toPrint) }
        } else {
            println("Не удалось найти путь")
        }
    }

    fun riseToRoot(node: Node?) {
        if (node == null || node.wrongChildren != 0)
            return
        node.depth = if (node.fact != null)
            node.children.minOf { child -> child!!.depth }
        else node.children.maxOf { child -> child!!.depth } + 1

        for (parent in node.parents!!.toTypedArray())
            if (parent.wrongChildren-- > 0)
                riseToRoot(parent)
    }

    fun unwrapNode(node: Node, printed: Array<Boolean>) {
        if (node.fact != null) {
            if (node.children.size != 0) {
                var min = node.children.first()!!
                for (child in node.children)
                    if (child!!.depth < min.depth)
                        min = child
                unwrapNode(min, printed)
            }
        } else {
            if (printed[node.rule!!.id])
                return
            printed[node.rule.id] = true
            for (child in node.children)
                unwrapNode(child!!, printed)
            println(node.rule)
        }
    }
}