package lab2

import java.lang.Exception
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.abs

val solvedState = solvedState().toString()

fun bfs(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")
    if (startState.toString() == solvedState)
        return startState
    val stateQueue = LinkedList<State>()
    val stateSet = HashSet<String>()
    stateQueue.add(startState)

    while (!stateQueue.isEmpty()) {
        val current = stateQueue.pop()
        stateSet.add(current.toString())
        for (operation in operations) {
            if (!operation.isApplicable(current))
                continue
            val newState = operation.applyTo(current)
            if (stateSet.contains(newState.toString()))
                continue
            if (newState.toString() == solvedState)
                return newState
            stateQueue.addLast(newState)
        }
    }

    throw Exception("Incorrect bfs")
}

fun dfsStep(state: State, operations: Array<StateOperation>, limit: Int = 80): Pair<State?, Int> {
    if (state.toString() == solvedState)
        return Pair(state, limit)

    if (limit == 0)
        return Pair(null, 0)

    for (operation in operations) {
        if (!operation.isApplicable(state))
            continue
        val newState = operation.applyTo(state)
        val result = dfsStep(newState, operations, limit - 1)
        if (result.first != null)
            return result
    }
    return Pair(null, limit)
}

fun dfs(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")

    var currentTry = dfsStep(startState, operations)
    var solution = currentTry
    while (currentTry.first != null) {
        solution = currentTry
        currentTry = dfsStep(startState, operations, 80 - currentTry.second - 1)
    }

    while (solution.first != null) {
        if (solution.first.toString() == solvedState)
            return solution.first!!
    }
    throw Exception("Incorrect outer dfs")
}

fun idfs(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")

    for (limit in 1..80) {
        val currentTry = dfsStep(startState, operations, limit)
        if (currentTry.first != null)
            return currentTry.first!!
    }

    throw Exception("Incorrect outer idfs")
}

fun astar(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")

    val closed = HashSet<String>()
    val open = PriorityQueue<Pair<State, AstarCharacteristics>>(compareBy { x -> x.second.f })
    var h = heuristics(startState)
    var g = 0

    var currentState = Pair(startState, AstarCharacteristics(g + h, g, h))
    open.add(currentState)
    while (open.isNotEmpty()) {
        currentState = open.remove()
        val stringState = currentState.first.toString()
        if (stringState == solvedState)
            return currentState.first

        if (!closed.contains(stringState)) {
            closed.add(stringState)
            for (operation in operations) {
                if (!operation.isApplicable(currentState.first))
                    continue
                val newState = operation.applyTo(currentState.first)
                h = heuristics(newState)
                g = currentState.second.g + 1
                open.add(Pair(newState, AstarCharacteristics(g + h, g, h)))
            }
        }
    }

    throw Exception("Incorrect astar")
}

fun heuristics(state: State): Int {
    var result = 0
    for (i in 0 until 16) {
        var first = state.value[i] - 1
        val second = i
        if (first == -1)
            first = 15
        result += abs(first % 4 - second % 4) + abs(first / 4 - second / 4)
    }
    return result
}

class AstarCharacteristics(val f: Int, val g: Int, val h: Int)
