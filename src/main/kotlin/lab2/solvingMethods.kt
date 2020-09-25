package lab2

import java.lang.Exception
import java.util.*
import kotlin.collections.HashSet
import kotlin.math.abs

fun bfs(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")
    if (startState.isSolved())
        return startState
    val stateQueue = LinkedList<State>()
    val stateSet = HashSet<Int>()
    stateQueue.add(startState)

    while (!stateQueue.isEmpty()) {
        val current = stateQueue.pop()
        stateSet.add(current.getHash())

        for (operation in operations) {
            if (!operation.isApplicable(current))
                continue
            val newState = operation.applyTo(current)

            if (stateSet.contains(newState.getHash())
                || isStepDown(current, newState))
                continue
            if (newState.isSolved())
                return newState
            stateQueue.addLast(newState)
        }
    }

    throw Exception("Incorrect bfs")
}

fun isStepDown(current: State, new: State): Boolean {
    return if (current.parent != null)
        new.getHash() == current.parent.getHash()
    else false
}

fun dfsStep(state: State, operations: Array<StateOperation>, limit: Int = 80): Pair<State?, Int> {
    if (state.isSolved())
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
        if (solution.first!!.isSolved())
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

fun astar(startState: State, operations: Array<StateOperation>): State {
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
                if (isStepDown(currentState, newState))
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

fun idastar(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")

    var treshold = heuristics(startState)

    while (true) {
        val temp = idastarStep(startState, operations, 0, treshold)
        if (temp.second.isSolved())
            return temp.second
        treshold = temp.first
    }
}

fun idastarStep(state: State, operations: Array<StateOperation>, g: Int, treshold: Short): Pair<Short, State> {
    val f = g + heuristics(state)
    if (f > treshold)
        return Pair(f.toShort(), state)
    if (state.isSolved())
        return Pair(-1, state)

    var min = Int.MAX_VALUE
    for (operation in operations) {
        if (!operation.isApplicable(state))
            continue
        val newState = operation.applyTo(state)
        if (isStepDown(state, newState))
            continue

        val temp = idastarStep(newState, operations, g + 1, treshold)
        if (temp.first == (-1).toShort())
            return temp
        if (temp.first < min)
            min = temp.first.toInt()
    }
    return Pair(min.toShort(), state)
}
