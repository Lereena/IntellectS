package productionModel

import java.io.File

fun main() {
    val rulesFile = File("src/main/resources/rulesBase.txt").readLines()
    val ruleBase = parseRules(rulesFile).toMutableList()
//    val finalStates = File("resources/finalStates.txt").readLines().toHashSet()

    println("Forward (0) или backward (1)?")
    var searchType = readLine()!!.toInt()
    while (searchType !in 0..1) {
        println("Введите 0 или 1")
        searchType = readLine()!!.toInt()
    }
    if (searchType == 0) {
        println("Выберите с чем вы пойдёте в приключение: Деньги, Щит, Оружие, Латы, Дети, Отвага")
        println("Введите выбранное через двоеточие:")
        var factsLine = readLine()
        while (factsLine == null || factsLine.isEmpty()) {
            println("Некорректные факты, попробуйте ещё раз")
            factsLine = readLine()
        }
        val facts = HashSet<String>()
        for (fact in factsLine.split(':'))
            facts.add(fact.trim())

        val path = forwardPass(facts, ruleBase)
        println(path)
    } else {
        val facts = File("src/main/resources/facts.txt").readLines().toHashSet()
        println("Введите результат для поиска:")
        var fact = readLine()
        while (fact == null || fact.isEmpty()) {
            println("Некорректный запрос, попробуйте ещё раз")
            fact = readLine()
        }

//        println(backwardPass(fact, ruleBase, facts))
    }
}

fun forwardPass(facts: HashSet<String>, rules: MutableList<Rule>): String {
    var currentNode = Node(facts, null, null)
    val targetNode: Node?
    while (true) {
        var ruleToRemove: Rule? = null
        for (rule in rules) {
            if (!facts.containsAll(rule.antecedents)) {
                if (rule == rules.last())
                    break
                continue
            }
            facts.add(rule.consequent)
            ruleToRemove = rule
            currentNode = Node(facts, currentNode, rule)
            break
        }
        if (ruleToRemove != null)
            rules.remove(ruleToRemove)
        if (rules.any { rule -> facts.containsAll(rule.antecedents) })
            continue
        targetNode = currentNode
        if (targetNode.parent == null)
            return "С таким набором ничего не выйдет"
        return inference(targetNode)
    }
}

//fun backwardPass(fact: String, rules: List<Rule>, facts: HashSet<String>): String {
//    var currentSet = HashSet<Node>()
//    val initial = HashSet<String>()
//    initial.add(fact)
//    currentSet.add(Node(initial, null, null))
//
//    var sourceNode: Node? = null
//    while (true) {
//        val nextSet = HashSet<Node>()
//        for (node in currentSet) {
//            val activeRules =
//                rules.filter { rule -> !facts.contains(rule.consequent) && node.set.contains(rule.consequent) }
//            for (rule in activeRules) {
//                val set = HashSet<String>(node.set)
//                set.remove(rule.consequent)
//                for (antecedent in rule.antecedents)
//                    set.add(antecedent)
//                val newNode = Node(set, node, rule)
//                nextSet.add(newNode)
//                if (set.all { f -> facts.contains(f) }) {
//                    sourceNode = newNode
//                    return inference(sourceNode)
//                }
//                if (currentSet == nextSet)
//                    break
//                currentSet = nextSet
//            }
//        }
//        if (sourceNode == null)
//            return "Вывод невозможен"
//        return inference(sourceNode)
//    }
//}
//
//fun backwardPass(aimFact: String, rules: List<Rule>, facts: HashSet<String>): String {
//    val allGainedFacts = HashSet<String>()
//    val currentFacts = ArrayList<String>()
//    currentFacts.add(aimFact)
//
//    var currentNode: Node? = null
//    var sourceNode: Node? = null
//    for (rule in rules) {
//        for (fact in currentFacts) {
//            if (rule.consequent == fact)
//                currentNode = Node(allGainedFacts, currentNode, rule)
//            break
//        }
//    }
//}

fun inference(targetNode: Node?): String {
    var node = targetNode
    val inference = ArrayList<Rule>()
    while (node?.parent != null) {
        inference.add(node.rule!!)
        node = node.parent
    }
    inference.reverse()
    return inference.joinToString("\n\r")
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