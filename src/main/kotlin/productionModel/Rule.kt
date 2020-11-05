package productionModel

class Rule(val antecedents: List<String>, val consequent: String, val final: Boolean = false) {
    override fun toString(): String {
        var antenc = antecedents[0]
        for (i in 1 until antecedents.size)
            antenc += "," + antecedents[i]
        return antecedents.joinToString(", ") + " -> " + consequent
    }
}