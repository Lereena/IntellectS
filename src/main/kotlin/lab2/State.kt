package lab2

import java.lang.StringBuilder

class State constructor(
    val value: ByteArray, var g: Short = 0, var h: Short = 0, val parent: State? = null
) {
    var f = g + h
    val zeroPosition = getZero()

    private fun getZero(): Int {
        return value.indexOf(0)
    }

    fun isSolvable(): Boolean {
        var inv = 0
        for (i in value.indices)
            if (value[i] != (0u).toByte())
                for (j in 0 until i)
                    if (value[j] > value[i])
                        inv++
        for (i in value.indices)
            if (value[i] == (0u).toByte())
                inv += 1 + i / 4

        return inv % 2 == 0
    }

    fun isSolved(): Boolean {
        return getHash() == 82800377
    }

    fun move(steps: Int): State {
        val newValue = ByteArray(value.size)
        value.copyInto(newValue)
        newValue[zeroPosition] = newValue[zeroPosition + steps]
        newValue[zeroPosition + steps] = 0

        return State(newValue, parent = this)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        for (pos in value.indices)
            builder.append("(" + value[pos] + ")")
        return builder.toString()
    }

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

    fun getHash(): Int {
        return value.contentHashCode()
    }
}

class StateOperation(val isApplicable: (State) -> Boolean, val applyTo: (State) -> State) {
    override fun toString(): String {
        return applyTo.toString()
    }
}
