package lab2

import java.lang.Exception
import java.util.*

fun bfs(startState: State, operations: Array<StateOperation>): State {
    if (!startState.isSolvable())
        throw Exception("Game is not solvable.")
    val solvedState = solvedState().toString()
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
            //stateSet.add(newState.toString())
            stateQueue.addLast(newState)
        }
    }

    throw Exception("Incorrect bfs")
}
