package productionModel

class Rule(val id: Int, val antecedents: List<Fact?>, val consequent: Fact?) {
    fun apply(state: Array<Boolean>): Fact? {
        for (antecedent in antecedents)
            if (!state[antecedent!!.id])
                return null

        return consequent
    }

    override fun toString(): String {
        var result = antecedents.joinToString(", ") { ant -> ant!!.description}
        result += (" -> ${consequent!!.description}")
        return result
    }
}