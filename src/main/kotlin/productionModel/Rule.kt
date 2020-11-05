package productionModel

import java.lang.StringBuilder

class Rule(val id: Int, val antecedents: List<Fact?>, val consequent: Fact?) {
    fun apply(state: Array<Boolean>): Fact? {
        for (antecedent in antecedents)
            if (!state[antecedent!!.id])
                return null

        return consequent
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (antecedent in antecedents)
            builder.append("${antecedent!!.description}, ");
        builder.removeRange((builder.length - 2) until builder.length)
        builder.append(" -> ${consequent!!.description}")
        return builder.toString()
    }
}