package lab2

import java.lang.Exception
import java.util.*
import kotlin.collections.HashSet

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