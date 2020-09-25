package lab2

fun main() {
    var game: State

    val operations = arrayOf(
        StateOperation({ state -> state.zeroPosition > 3 }, { state -> state.move(-4) }),
        StateOperation({ state -> state.zeroPosition < 12 }, { state -> state.move(4) }),
        StateOperation({ state -> state.zeroPosition % 4 != 0 }, { state -> state.move(-1) }),
        StateOperation({ state -> state.zeroPosition % 4 != 3 }, { state -> state.move(1) })
    )

//    game = State(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0)) // 0
//    game = State(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 0, 9, 10, 11, 8, 13, 14, 15, 12)) // 2
//    game = State(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12, 13, 14, 15, 10)) // 11
//    game = State(byteArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 12, 9, 11, 10, 13, 0, 14, 15)) // 22
//    game = State(byteArrayOf(15, 2, 3, 4, 5, 6, 7, 8, 10, 0, 11, 14, 9, 1, 13, 12)) // 33
//    game = State(byteArrayOf(2, 15, 0, 3, 7, 1, 6, 10, 5, 12, 13, 4, 9, 14, 8, 11)) // 40
//    game = State(byteArrayOf(3, 6, 5, 12, 10, 4, 8, 14, 13, 9, 15, 11, 0, 2, 1, 7)) // 49
    game = State(byteArrayOf(13, 11, 14, 8, 7, 10, 2, 12, 9, 1, 15, 6, 5, 0, 3, 4)) // 58
    println("Is game solvable: " + if (game.isSolvable()) "Yes" else "No")

    println("\nIDA*:")
    measureTime(game, operations) { x, y -> idastar(x, y) }

    println("\nA*:")
    measureTime(game, operations) { x, y -> astar(x, y) }

    println("\nBFS:")
    measureTime(game, operations) { x, y -> bfs(x, y) }

    println("\nIDFS:")
    measureTime(game, operations) { x, y -> idfs(x, y) }

    println("\nDFS:")
    measureTime(game, operations) { x, y -> dfs(x, y) }
}

fun measureTime(state: State, operations: Array<StateOperation>, action: (State, Array<StateOperation>) -> State) {
    try {
        val startTime = System.currentTimeMillis()
        val solution = action(state, operations)
        val endTime = System.currentTimeMillis()
        val route = solution.restoreRoute()
//        println(route.first)
        println(route.second.toString() + " шагов")
        println((endTime - startTime) / 1000.0)
    } catch (e: OutOfMemoryError) {
        println("Съел всю память")
    }
}
