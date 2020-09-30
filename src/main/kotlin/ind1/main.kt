package ind1

fun main() {
    val startState = State(3, 3, true)
    println("\nBFS:")
    measureTime(startState) { state -> bfs(state) }
}

fun measureTime(state: State, action: (State) -> State) {
    try {
        val startTime = System.currentTimeMillis()
        val solution = action(state)
        val endTime = System.currentTimeMillis()
        val route = solution.restoreRoute()
        println(route.first)
        println(route.second.toString() + " шагов")
        println((endTime - startTime) / 1000.0)
    } catch (e: OutOfMemoryError) {
        println("Съел всю память")
    }
}