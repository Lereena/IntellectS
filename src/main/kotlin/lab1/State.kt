package lab1

class State constructor(val value: Int, val parent: State?, val operation: Operation?) {
    override fun toString(): String {
        return "$value (${parent?.value})"
    }
}