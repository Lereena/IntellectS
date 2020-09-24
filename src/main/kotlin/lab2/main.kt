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
//    game = State(byteArrayOf(2, 15, 0, 3, 7, 1, 6, 10, 5, 12, 13, 4, 9, 14, 8, 11)) // 40
//    game = State(byteArrayOf(3, 6, 5, 12, 10, 4, 8, 14, 13, 9, 15, 11, 0, 2, 1, 7)) // 49
    game = State(byteArrayOf(5, 1, 14, 10, 15, 13, 7, 11, 9, 4, 12, 8, 3, 2, 6, 0)) // 52
    println("Is game solvable: " + if (game.isSolvable()) "Yes" else "No")

    println("\nIDA*:")
    val idastarStart = System.currentTimeMillis()
    val idastarSolved = idastar(game, operations)
    val idastarEnd = System.currentTimeMillis()
    println(idastarSolved.restoreRoute())
    println((idastarEnd - idastarStart) / 1000.0)

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
    val state = State(ByteArray(16) { 0 })
    for (x in 0 until 16)
        state.value[x] = (x + 1).toByte()
    state.value[15] = 0

    return state
}