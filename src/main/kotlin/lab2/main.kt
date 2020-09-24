package lab2

fun main() {
    var game = solvedState()

    val operations = arrayOf(
        StateOperation({ state -> state.zeroPosition > 3 }, { state -> state.move(-4) }),
        StateOperation({ state -> state.zeroPosition < 12 }, { state -> state.move(4) }),
        StateOperation({ state -> state.zeroPosition % 4 != 0 }, { state -> state.move(-1) }),
        StateOperation({ state -> state.zeroPosition % 4 != 3 }, { state -> state.move(1) })
    )

//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0)) // 0
//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 0, 9, 10, 11, 8, 13, 14, 15, 12)) // 2
//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 11, 12, 13, 14, 15, 10)) // 11
//    game = State(intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 12, 9, 11, 10, 13, 0, 14, 15)) // 22
//    game = State(byteArrayOf(15, 2, 3, 4, 5, 6, 7, 8, 10, 0, 11, 14, 9, 1, 13, 12)) // 33
    game = State(byteArrayOf(3, 4, 6, 1, 8, 13, 11, 12, 15, 2, 5, 10, 7, 0, 14, 19)) // 42
//    game = State(byteArrayOf(15, 14, 1, 6, 9, 11, 4, 12, 0, 10, 7, 3, 13, 8, 5, 2)) // 52
//    game = State(byteArrayOf(5, 1, 14, 10, 15, 13, 7, 11, 9, 4, 12, 8, 3, 2, 6, 0)) // 52
    println("Is game solvable: " + if (game.isSolvable()) "Yes" else "No")

//    val ast = astar2(game, operations)

    println("\nA*:")
    val astarStart = System.currentTimeMillis()
    val astarSolved = astar2(game, operations)
    val astarEnd = System.currentTimeMillis()
    println(astarSolved.restoreRoute())
    println((astarEnd - astarStart) / 1000.0)

//    println("BFS:")
//    val bfsStart = System.currentTimeMillis()
//    val bfsSolved = bfs(game, operations)
//    val bfsEnd = System.currentTimeMillis()
//    println(bfsSolved.restoreRoute())
//    println((bfsEnd - bfsStart) / 1000.0)

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
    val state = State(ByteArray(16) { 0 })
    for (x in 0 until 16)
        state.value[x] = (x + 1).toByte()
    state.value[15] = 0

    return state
}