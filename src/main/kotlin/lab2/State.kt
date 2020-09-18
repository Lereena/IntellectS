package lab2

import java.lang.StringBuilder

class State constructor(val value: IntArray, val parent: State?, val zeroPosition: Int) {
    fun isSolvable(): Boolean {
        var inv = 0
        for (i in value.indices)
            if (value[i] != 0)
                for (j in 0 until i)
                    if (value[j] > value[i])
                        inv++
        for (i in value.indices)
            if (value[i] == 0)
                inv += 1 + i / 4

        return inv % 2 == 0
    }

    fun move(steps: Int): State {
        val newValue = IntArray(value.size)
        value.copyInto(newValue)
        newValue[zeroPosition] = newValue[zeroPosition + steps]
        newValue[zeroPosition + steps] = 0
        val newZeroPosition = zeroPosition + steps

        return State(newValue, this, newZeroPosition)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (pos in value.indices)
            builder.append("(" + value[pos] + ")")
        return builder.toString()
    }

    fun gridView(): String {
        val builder = StringBuilder()
        for (steps in 0 until 4)
            builder.append("————")
        builder.append("\n")
        for (pos in 0 until 16) {
            val value = value[pos]
            if (value < 10) {
                builder.append("| ")
                if (value != 0)
                    builder.append(value)
                builder.append("\t")
            } else {
                builder.append("|")
                if (value != 0)
                    builder.append(value)
                builder.append(" ")
            }
            if ((pos + 1) % 4 == 0) {
                builder.append("|\n")
                for (steps in 0 until 4)
                    builder.append("————")
                builder.append("\n")
            }
        }

        return builder.toString()
    }

    fun restoreRoute(): String {
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
        return result
    }
}

class StateOperation(val isApplicable: (State) -> Boolean, val applyTo: (State) -> State) {
    override fun toString(): String {
        return applyTo.toString()
    }
}
