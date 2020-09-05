package lab1

import java.lang.Exception
import java.util.*

fun main() {
    val startValue = 2
    val endValue = 100
//    val endValue = 1210495
    val operations1 = arrayOf(
        Operation("+", 3),
        Operation("*", 2),
    )
    val reverseOperations1 = arrayOf(
        Operation("-", 3),
        Operation("/", 2),
    )
    val operations2 = arrayOf(
        Operation("+", 3),
        Operation("*", 2),
        Operation("-", 2)
    )

    println("+3, *2")
    val straightforwardStartTime = System.currentTimeMillis()
    val straightforwardRouteState = findStraightforwardRoute(startValue, endValue, operations1)
    val straightforwardEndTime = System.currentTimeMillis()
    val straightforwardRoute = restoreRoute(straightforwardRouteState)
    println(straightforwardRoute)
    println((straightforwardEndTime - straightforwardStartTime) / 1000.0)

    println("\n+3, *2 обратным поиском")
    val reverseStartTime = System.currentTimeMillis()
    val reverseRouteState = findStraightforwardRoute(endValue, startValue, reverseOperations1)
    val reverseEndTime = System.currentTimeMillis()
    val reverseRoute = restoreRoute(reverseRouteState, true)
    println(reverseRoute)
    println((reverseEndTime - reverseStartTime) / 1000.0)

    println("\n+3, *2, -2")
    val threeOpsStartTime = System.currentTimeMillis()
    val threeOpsState = findStraightforwardRoute(startValue, endValue, operations2)
    val threeOpsEndTime = System.currentTimeMillis()
    val threeOpsRoute = restoreRoute(threeOpsState)
    println(threeOpsRoute)
    println((threeOpsEndTime - threeOpsStartTime) / 1000.0)
}

fun findStraightforwardRoute(start: Int, result: Int, operations: Array<Operation>): State {
    val startState = State(start, null, null)

    val stateQueue = LinkedList<State>()
    val stateSet = HashSet<Int>()
    stateQueue.add(startState)

    while (!stateQueue.isEmpty()) {
        val current = stateQueue.pop()
        stateSet.add(current.value)
        for (operation in operations) {
            if (operation.sign == "/" && !divides(current.value, operation.operand))
                continue
            val newState = operation.applyTo(current)
            if (stateSet.contains(newState.value))
                continue
            if (newState.value == result)
                return newState

            stateSet.add(newState.value)
            stateQueue.addLast(newState)
        }
    }

    throw Exception("Не найдена подходящая последовательность операций")
}

fun restoreRoute(lastState: State, reverse: Boolean = false): String {
    var current: State? = lastState
    var route = if (reverse) current!!.value.toString() else ""
    var operationsCount = 0
    while (current != null) {
        if (current.operation != null) {
            route =
                if (reverse) route + ops[current.operation!!.sign] + current.operation!!.operand
                else current.operation.toString() + route
            operationsCount++
        } else
            if (!reverse) route = current.value.toString() + route
        current = current.parent
    }

    return "$route ($operationsCount операций)"
}

val ops = hashMapOf(
    "+" to "-",
    "-" to "+",
    "/" to "*",
    "*" to "/"
)

fun divides(leftOp: Int, rightOp: Int): Boolean {
    return leftOp % rightOp == 0
}