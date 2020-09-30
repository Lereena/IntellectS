package ind1

import lab2.StateOperation
import java.lang.Exception
import java.util.*
import kotlin.collections.HashSet

fun bfs(startState: State): State {
    if (!startState.isAcceptable())
        throw Exception("Starting from unacceptable state")

    if (startState.isSolved())
        return startState

    val stateQueue = LinkedList<State>()
    val stateSet = HashSet<String>()
    stateQueue.add(startState)

    while (!stateQueue.isEmpty()) {
        val current = stateQueue.pop()
        stateSet.add(current.toString())

        for (operation in
        (if (current.fromMinus) current.plusOps
        else current.minusOps)) {
            val newState = operation.applyTo(current)
            if (!newState.isAcceptable() || stateSet.contains(newState.toString()) || isStepDown(newState))
                continue
            if (newState.isSolved())
                return newState
            stateQueue.addLast(newState)
        }
    }

    throw Exception("Incorrect bfs")
}

fun isStepDown(current: State): Boolean {
    return if (current.parent?.parent != null)
        current.toString() == current.parent.parent.toString()
    else false
}

fun dfsStep(state: State, limit: Int = 15): Pair<State?, Int> {
    if (state.isSolved())
        return Pair(state, limit)

    if (limit == 0)
        return Pair(null, 0)

    for (operation in
    (if (state.fromMinus) state.plusOps
    else state.minusOps)) {
        val newState = operation.applyTo(state)
        if (!newState.isAcceptable() || isStepDown(newState))
            continue
        val result = dfsStep(newState, limit - 1)
        if (result.first != null)
            return result
    }
    return Pair(null, limit)
}

fun dfs(startState: State): State {
    if (!startState.isAcceptable())
        throw Exception("Starting from unacceptable state")

    var currentTry = dfsStep(startState)
    var solution = currentTry
    while (currentTry.first != null) {
        solution = currentTry
        currentTry = dfsStep(startState, 80 - currentTry.second - 1)
    }

    while (solution.first != null) {
        if (solution.first!!.isSolved())
            return solution.first!!
    }
    throw Exception("Incorrect outer dfs")
}

fun idfs(startState: State): State {
    if (!startState.isAcceptable())
        throw Exception("Starting from unacceptable state")

    for (limit in 1..80) {
        val currentTry = dfsStep(startState,  limit)
        if (currentTry.first != null)
            return currentTry.first!!
    }

    throw Exception("Incorrect outer idfs")
}