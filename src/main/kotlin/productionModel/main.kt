package productionModel

import java.io.File

fun main() {
    val rulesFile = File("resources/ruleBase.txt").readLines()
    val ruleBase = parseRules(rulesFile)
//    val finalStates = File("resources/finalStates.txt").readLines().toHashSet()

    println("Forward (0) или backward (1)?")
    var searchType = readLine()!!.toInt()
    while (searchType !in 0..1) {
        println("Введите 0 или 1")
        searchType = readLine()!!.toInt()
    }
    if (searchType == 0) {
        println("Введите факты через двоеточие:")
        var factsLine = readLine()
        while (factsLine == null || factsLine.isEmpty()) {
            println("Некорректные факты, попробуйте ещё раз")
            factsLine = readLine()
        }
        val facts = HashSet<String>()
        for (fact in factsLine.split(':'))
            facts.add(fact.trim())

        println(forwardPass(facts, ruleBase))
    } else {
        val facts = File("resources/facts.txt").readLines().toHashSet()
        println("Введите результат для поиска:")
        var fact = readLine()
        while (fact == null || fact.isEmpty()) {
            println("Некорректный запрос, попробуйте ещё раз")
            fact = readLine()
        }

        println(backwardPass(fact, ruleBase, facts))
    }
}

fun forwardPass(facts: HashSet<String>, rules: List<Rule>): String {
    var currentSet = HashSet<Node>()
    currentSet.add(Node(facts, null, null))
    val final = rules.asSequence().filter { x -> x.final }

    for (target in final) {
        var targetNode: Node? = null
        while (targetNode == null) {
            val nextSet = HashSet<Node>()
            for (node in currentSet) {
                for (rule in rules) {
                    if (rule.antecedents.any { fact -> !node.set.contains(fact) })
                        continue
                    val newNode = Node(HashSet(node.set), node, rule)
                    newNode.set.add(rule.consequent)
                    nextSet.add(newNode)
                    if (newNode.set.contains(target.consequent))
                        targetNode = newNode
                }
            }
            if (currentSet == nextSet)
                break
            currentSet = nextSet
        }
        if (targetNode == null)
            return "Не найдено"

        return inference(targetNode)
    }

    return "Не найдено"
}

fun backwardPass(fact: String, rules: List<Rule>, facts: HashSet<String>): String {
    var currentSet = HashSet<Node>()
    val initial = HashSet<String>()
    initial.add(fact)
    currentSet.add(Node(initial, null, null))

    var sourceNode: Node? = null
    while (true) {
        val nextSet = HashSet<Node>()
        for (node in currentSet) {
            val activeRules =
                rules.filter { rule -> !facts.contains(rule.consequent) && node.set.contains(rule.consequent) }
            for (rule in activeRules) {
                val set = HashSet<String>(node.set)
                set.remove(rule.consequent)
                for (antecedent in rule.antecedents)
                    set.add(antecedent)
                val newNode = Node(set, node, rule)
                nextSet.add(newNode)
                if (set.all { f -> facts.contains(f) }) {
                    sourceNode = newNode
//                    if (sourceNode == null)
//                        return "Вывод невозможен"
                    return inference(sourceNode)
                }
                if (currentSet == nextSet)
                    break
                currentSet = nextSet
            }
        }
        if (sourceNode == null)
            return "Вывод невозможен"
        return inference(sourceNode)
    }
}

fun inference(targetNode: Node?): String {
    var targetNode = targetNode
    val inference = ArrayList<Rule>()
    while (targetNode?.parent != null) {
        inference.add(targetNode.rule!!)
        targetNode = targetNode.parent
    }
    inference.reverse()
    return inference.joinToString { "\n\r" }
}

fun parseRules(rulesFile: List<String>): List<Rule> {
    val result = ArrayList<Rule>()
    for (line in rulesFile) {
        val splitRule = line.split(" -> ")
        val antecedents = splitRule[0].split(", ").toList()
        val rule = Rule(antecedents, splitRule[1])
        result.add(rule)
    }

    return result
}