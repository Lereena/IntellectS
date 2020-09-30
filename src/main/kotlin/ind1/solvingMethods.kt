package ind1

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
            if (!newState.isAcceptable() || stateSet.contains(newState.toString()))
                continue
            if (newState.isSolved())
                return newState
            stateQueue.addLast(newState)
        }
    }

    throw Exception("Incorrect bfs")
}