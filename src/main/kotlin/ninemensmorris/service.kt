package ninemensmorris

fun printBoard(board: Array<GameColor>) {
    println(board[0].toString() + "(00)---------------------- " + board[1] + "(01)---------------------- " + board[2] + "(02)")
    println("|                           |                           |")
    println("|        " + board[8] + "(08)-------------- " + board[9] + "(09)-------------- " + board[10] + "(10)  |")
    println("|       |                   |                    |      |")
    println(
        "|       |         " + board[16] + "(16)----- " + board[17] + "(17)----- " + board[18] + "(18)    |      |")
    println("|       |         |                   |          |      |")
    println(board[3].toString() + "(03)--- " + board[11] + "(11)---- " + board[19] + "(19)                " + board[20] + "(20)---- "
            + board[12] + "(12)--- " + board[4] + "(04)")
    println("|       |         |                   |          |      |")
    println(
        "|       |         " + board[21] + "(21)----- " + board[22] + "(22)----- " + board[23] + "(23)    |      |")
    println("|       |                   |                    |      |")
    println(
        "|        " + board[13] + "(13)-------------- " + board[14] + "(14)-------------- " + board[15] + "(15)  |")
    println("|                           |                           |")
    println(board[5].toString() + "(05)---------------------- " + board[6] + "(06)---------------------- " + board[7] + "(07)")
}

val neighbors = arrayOf(
    arrayOf(1, 3),
    arrayOf(0, 2, 9),
    arrayOf(1, 4),
    arrayOf(0, 5, 11),
    arrayOf(2, 7, 12),
    arrayOf(3, 6),
    arrayOf(5, 7, 14),
    arrayOf(4, 6),
    arrayOf(9, 11),
    arrayOf(1, 8, 10, 17),
    arrayOf(9, 12),
    arrayOf(3, 8, 13, 19),
    arrayOf(4, 10, 15, 20),
    arrayOf(11, 14),
    arrayOf(6, 13, 15, 22),
    arrayOf(12, 14),
    arrayOf(17, 19),
    arrayOf(9, 16, 18),
    arrayOf(17, 20),
    arrayOf(11, 16, 21),
    arrayOf(12, 18, 23),
    arrayOf(19, 22),
    arrayOf(21, 23, 14),
    arrayOf(20, 22)
)

val linesNeighbors = arrayOf(
    arrayOf(1, 2, 3, 5),
    arrayOf(0, 2, 9, 17),
    arrayOf(0, 1, 4, 7),
    arrayOf(0, 5, 11, 19),
    arrayOf(2, 7, 12, 20),
    arrayOf(0, 3, 6, 7),
    arrayOf(5, 7, 14, 22),
    arrayOf(2, 4, 5, 6),
    arrayOf(9, 10, 11, 13),
    arrayOf(8, 10, 1, 17),
    arrayOf(8, 9, 12, 15),
    arrayOf(3, 19, 8, 13),
    arrayOf(20, 4, 10, 15),
    arrayOf(8, 11, 14, 15),
    arrayOf(13, 15, 6, 22),
    arrayOf(13, 14, 10, 12),
    arrayOf(17, 18, 19, 21),
    arrayOf(1, 9, 16, 18),
    arrayOf(16, 17, 20, 23),
    arrayOf(16, 21, 3, 11),
    arrayOf(12, 4, 18, 23),
    arrayOf(16, 19, 22, 23),
    arrayOf(6, 14, 21, 23),
    arrayOf(18, 20, 21, 22),
)

