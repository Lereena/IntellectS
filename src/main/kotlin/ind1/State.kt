package ind1

class State(val missionaries: Int, val cannibals: Int, val boatOnStartBank: Boolean, val fromMinus: Boolean = false, val parent: State? = null) {
    val missionariesMax = 3
    val cannibalsMax = 3

    override fun toString(): String {
        return ("(м: $missionaries, к: $cannibals, "
                + if (boatOnStartBank) "лодка на начальном береге)" else "лодка на целевом береге)")
    }

    fun isSolved(): Boolean {
        return missionaries == 0 && cannibals == 0 && !boatOnStartBank
    }

    fun isAcceptable(): Boolean {
        return (missionaries == cannibals || missionaries == missionariesMax || missionaries == 0)
                && missionaries in 0..missionariesMax
                && cannibals in 0..cannibalsMax
    }

    fun minus(m: Int, c: Int): State {
        return State(missionaries - m, cannibals - c, !boatOnStartBank, true, this)
    }

    fun plus(m: Int, c: Int): State {
        return State(missionaries + m, cannibals + c, !boatOnStartBank, false, this)
    }

    val minusOps = arrayOf(
        StateOperation { state -> state.minus(1, 0) },
        StateOperation { state -> state.minus(2, 0) },
        StateOperation { state -> state.minus(0, 1) },
        StateOperation { state -> state.minus(0, 2) },
        StateOperation { state -> state.minus(1, 1) },
    )

    val plusOps = arrayOf(
        StateOperation { state -> state.plus(1, 0) },
        StateOperation { state -> state.plus(2, 0) },
        StateOperation { state -> state.plus(0, 1) },
        StateOperation { state -> state.plus(0, 2) },
        StateOperation { state -> state.plus(1, 1) },
    )

    fun restoreRoute(): Pair<String, Int> {
        var current: State? = this
        var result = current.toString()
        current = current?.parent
        var count = 0
        while (current != null) {
            val parent = current.parent
            result = "$current\n->$result"
            current = parent
            count++
        }
        result += "\n$count шагов"
        return Pair(result, count)
    }
}

class StateOperation(val applyTo: (State) -> State) {
    override fun toString(): String {
        return applyTo.toString()
    }
}