package lab2

fun main() {
    var game = solvedState()
    println("Is game solvable: " + if (game.isSolvable()) "Yes" else "No")
    //println(game)
    //println(game.gridView())

    val operations = arrayOf(
        StateOperation({ state -> state.zeroPosition > 3 }, { state -> state.move(-4) }),
        StateOperation({ state -> state.zeroPosition < 12 }, { state -> state.move(4) }),
        StateOperation({ state -> state.zeroPosition % 4 != 0 }, { state -> state.move(-1) }),
        StateOperation({ state -> state.zeroPosition % 4 != 3 }, { state -> state.move(1) })
    )

//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0), null, 15) // 0
//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 0, 9, 10, 11, 8, 13, 14, 15, 12), null, 7) // 2
//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12, 13, 14, 15, 10), null, 9) // 11
//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 12, 9, 11, 10, 13, 0, 14, 15), null, 13) // 22
//    game = State(intArrayOf(15, 2, 3, 4, 5, 6, 7, 8, 10, 0, 11, 14, 9, 1, 13, 12), null, 9) // 33
    game = State(intArrayOf(5, 1, 14, 10, 15, 13, 7, 11, 9, 4, 12, 8, 3, 2, 6, 0), null, 15) // 52

    println("\nA*:")
    val astarStart = System.currentTimeMillis()
    val astarSolved = astar(game, operations)
    val astarEnd = System.currentTimeMillis()
    println(astarSolved.restoreRoute())
    println((astarEnd - astarStart) / 1000.0)

    println("BFS:")
    val bfsStart = System.currentTimeMillis()
    val bfsSolved = bfs(game, operations)
    val bfsEnd = System.currentTimeMillis()
    println(bfsSolved.restoreRoute())
    println((bfsEnd - bfsStart) / 1000.0)

    println("\nDFS:")
    val dfsStart = System.currentTimeMillis()
    val dfsSolved = dfs(game, operations)
    val dfsEnd = System.currentTimeMillis()
    println(dfsSolved.restoreRoute())
    println((dfsEnd - dfsStart) / 1000.0)

    println("\nIDFS:")
    val idfsStart = System.currentTimeMillis()
    val idfsSolved = idfs(game, operations)
    val idfsEnd = System.currentTimeMillis()
    println(idfsSolved.restoreRoute())
    println((idfsEnd - idfsStart) / 1000.0)
}

fun solvedState(): State {
    val state = State(IntArray(16) { 0 }, null, 15)
    for (x in 0 until 16)
        state.value[x] = x + 1
    state.value[15] = 0

    return state
}