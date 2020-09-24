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
    val open = PriorityQueue<State>(compareBy { x -> x.f })
//    val h = heuristics(startState)
//    val g = 0

    var currentState = startState
    currentState.h = heuristics(currentState)
    open.add(currentState)
    while (open.isNotEmpty()) {
        currentState = open.remove()
        val stringState = currentState.toString()
        if (stringState == solvedState)
            return currentState

        if (!closed.contains(stringState)) {
            closed.add(stringState)
            for (operation in operations) {
                if (!operation.isApplicable(currentState))
                    continue
                val newState = operation.applyTo(currentState)
                newState.g = (currentState.g + 1).toShort()
                newState.h = heuristics(newState)
                open.add(newState)
            }
        }
    }

    throw Exception("Incorrect astar")
}

fun heuristics(state: State): Short {
    var result = 0
    for (i in 0 until 16) {
        if (state.value[i] == 0.toByte())
            continue
        var first: Byte = (state.value[i] - 1).toByte()
        if (first == (-1).toByte())
            first = 15
        result += abs(first % 4 - i % 4) + abs(first / 4 - i / 4)
    }

    for (y in 0 until 4)
        for (x in 0 until 4) {
            for (x1 in (x + 1) until 4)
                if (state.value[y * 4 + x] != 0.toByte() && state.value[y * 4 + x1] != 0.toByte())
                    if ((state.value[y * 4 + x] - 1) / 4 == y
                        && (state.value[y * 4 + x1] - 1) / 4 == y
                        && state.value[y * 4 + x] > state.value[y * 4 + x1]
                    )
                        result += 2
            for (y1 in (y + 1) until 4)
                if (state.value[y * 4 + x] != 0.toByte() && state.value[y1 * 4 + x] != 0.toByte())
                    if ((state.value[y * 4 + x] - 1) % 4 == x
                        && (state.value[y1 * 4 + x] - 1) % 4 == x
                        && state.value[y * 4 + x] > state.value[y1 * 4 + x]
                    )
                        result += 2;

        }
    return result.toShort()
}

fun astar2(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")

    var currentState = State(startState.value, heuristics(startState))
    var hashState: Int
    val closed = HashSet<Int>()
    val open = PriorityQueue<State>(compareBy { x -> x.f })
    open.add(currentState)

    while (open.isNotEmpty()) {
        currentState = open.remove()
        hashState = currentState.getHash()

        if (currentState.isSolved())
            return currentState

        if (!closed.contains(hashState)) {
            closed.add(hashState)
            for (operation in operations) {
                if (!operation.isApplicable(currentState))
                    continue
                val newState = operation.applyTo(currentState)
                if (if (currentState.parent != null)
                        newState.getHash() == currentState.parent!!.getHash()
                    else
                        false
                )
                    continue
                newState.g = (currentState.g + 1).toShort()
                newState.h = heuristics(newState)
                newState.f = newState.g + newState.h
                open.add(newState)
            }
        }
    }

    throw Exception("Incorrect astar")
}