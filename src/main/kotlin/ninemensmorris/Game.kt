package ninemensmorris

import java.util.*
import kotlin.random.Random.Default.nextInt
import kotlin.system.exitProcess

class Game(first: Player) {
    var board: Array<GameColor> = Array(24) { GameColor.F }
    private val userColor: GameColor = if (first == Player.USER) GameColor.W else GameColor.B
    private val aiColor: GameColor = if (first == Player.AI) GameColor.W else GameColor.B
    private var turn: Player = first
    private var nextStepPoint = 0
    private val depth = 3
    private val alpha = Int.MIN_VALUE;
    private val beta = Int.MAX_VALUE
    private var pruned = 0
    private var reachedStates = 0

    fun start() {
        println("Первая стадия: начальная расстановка")
        for (step in 1..9) {
            if (turn == Player.USER) {
                printBoard(board)
                firstStageUserStep()
                printBoard(board)
                val aiStep = firstStageAIStep(step)
                println(" $aiStep")
            } else {
                printBoard(board)
                val aiStep = firstStageAIStep(step)
                println(" $aiStep")
                printBoard(board)
                firstStageUserStep()
            }
        }

        println("Вторая стадия: движение")
        while (true) {
            if (turn == Player.AI) {
                printBoard(board)
                secondStageAIStep()
            } else {
                printBoard(board)
                secondStageUserStep()
                when {
                    evaluateState() == aiColor -> println("Компьютер выиграл")
                    evaluateState() == userColor -> println("Вы выиграли")
                }
            }
        }
    }

    private fun firstStageUserStep(): Int {
        print("Ход пользователя:")
        val position = validUserTo()
        board[position] = userColor
        turn = Player.AI
        return position
    }

    private fun secondStageUserStep() {
        println("Ход пользователя (два числа на разных строках):")
        val fromPosition = validUserFrom()
        val toPosition = validUserTo(fromPosition)
        board[fromPosition] = GameColor.F
        board[toPosition] = userColor
        turn = Player.AI
    }

    private fun firstStageAIStep(step: Int): Int {
        print("Ход компьютера:")

        if (step == 0) {
            turn = Player.USER
            return randomStep()
        } else {
            while (true) {
                val point = nextStepPoint
                if (!closeMill(point)) {
                    val pointForMill = pointForMill(point)
                    if (pointForMill != -1) {
                        board[pointForMill] = aiColor
                        nextStepPoint = pointForMill
                        turn = Player.USER
                        return nextStepPoint
                    }
                } else
                    for (neighbor in neighbors(point))
                        if (board[neighbor] == GameColor.F) {
                            board[neighbor] = aiColor
                            nextStepPoint = neighbor
                            turn = Player.USER
                            return nextStepPoint
                        }
                turn = Player.USER
                return randomStep()
            }
        }
    }

    private fun secondStageAIStep() {
        print("Ход компьютера: ")
        val move: Pair<Int, Int>
        val evaluation = alphaBetaPruning()
        if (evaluation.evaluator == Int.MIN_VALUE) {
            print("Компьютер выиграл")
            exitProcess(0)
        } else {
            move = subtractBoards(board, evaluation.board)
            board = evaluation.board
        }
        println("${move.first} -> ${move.second}")
        turn = Player.USER
    }

    private fun validUserFrom(): Int {
        var position: Int?
        while (true) {
            val line = readLine()!!.trim()
            if (line == "") {
                println("Введите число от 0 до 23")
                continue
            }
            position = line.toInt()
            if (position !in 0..23) {
                println("Введите число от 0 до 23")
                continue
            }
            if (board[position] != userColor) {
                println("На этой позиции нет вашей фишки")
                continue
            }
            return position
        }
    }

    private fun validUserTo(startPosition: Int? = null): Int {
        var position: Int?
        while (true) {
            val line = readLine()!!.trim()
            if (line == "") {
                println("Введите число от 0 до 23")
                continue
            }
            position = line.toInt()
            if (position !in 0..23) {
                println("Введите число от 0 до 23")
                continue
            }
            if (!freePlace(position)) {
                println("Это место уже занято")
                continue
            }
            if (startPosition != null && position == startPosition) {
                println("Фишка уже на этой позиции")
                continue
            }
            // TODO добавить проверку на движение по линиям
            return position
        }
    }

    private fun randomStep(): Int {
        var position = nextInt(0, 23)
        while (!freePlace(position))
            position = nextInt(0, 23)
        board[position] = aiColor
        nextStepPoint = position
        return position
    }

    private fun neighbors(point: Int): Array<Int> {
        return neighbors[point]
    }

    private fun subtractBoards(begin: Array<GameColor>, end: Array<GameColor>): Pair<Int, Int> {
        var from = 0
        var to = 0
        for (i in begin.indices) {
            if (begin[i] != end[i])
                if (end[i] == GameColor.F)
                    from = i
                else
                    to = i
        }
        return Pair(from, to)
    }

    private fun pointForMill(point: Int): Int {
        val neighbors = linesNeighbors[point]
        val first = checkTriple(neighbors[0], neighbors[1])
        val second = checkTriple(neighbors[2], neighbors[3])
        return if (first != -1) first else second
    }

    private fun checkTriple(p1: Int, p2: Int): Int {
        if (board[p1] == userColor && board[p2] == GameColor.F)
            return p2
        if (board[p2] == userColor && board[p1] == GameColor.F)
            return p1
        return -1
    }

    private fun closeMill(point: Int, board: Array<GameColor> = this.board): Boolean {
        val playerColor = board[point]
        val neighbors = linesNeighbors[point]
        val first = checkMillFormation(playerColor, neighbors[0], neighbors[1])
        val second = checkMillFormation(playerColor, neighbors[2], neighbors[3])
        return first || second
    }

    private fun checkMillFormation(color: GameColor, p1: Int, p2: Int): Boolean {
        return board[p1] == color && board[p2] == color
    }

    private fun alphaBetaPruning(
        pl1: Boolean = false,
        board: Array<GameColor> = this.board,
        depth: Int = this.depth,
        alpha: Int = this.alpha,
        beta: Int = this.beta,
        heuristics: (Array<GameColor>) -> Int
        = { x -> this.heuristics(x) }
    ): Evaluation {
        val finalEvaluation = Evaluation()
        var currentAlpha = alpha
        var currentBeta = beta
        reachedStates += 1
        if (depth != 0) {
            var currentEvaluation: Evaluation
            val possibleConfigurations =
                if (pl1) stage23Moves()
                else invertedBoardList(stage23Moves(invertedBoard(board)))
            for (configuration in possibleConfigurations) {
                if (pl1) {
                    currentEvaluation = alphaBetaPruning(false, configuration, depth - 1, currentAlpha, currentBeta)
                    if (currentEvaluation.evaluator > currentAlpha) {
                        currentAlpha = currentEvaluation.evaluator
                        finalEvaluation.board = configuration
                    }
                } else {
                    currentEvaluation = alphaBetaPruning(true, configuration, depth - 1, currentAlpha, currentBeta)
                    if (currentEvaluation.evaluator < currentBeta) {
                        currentBeta = currentEvaluation.evaluator
                        finalEvaluation.board = configuration
                    }
                }
                if (currentAlpha >= currentBeta) {
                    pruned += 1
                    break
                }
            }
            finalEvaluation.evaluator =
                if (pl1) alpha else beta
        } else {
            finalEvaluation.evaluator =
                if (pl1) heuristics(board)
                else heuristics(invertedBoard(board))
        }
        return finalEvaluation
    }

    private fun heuristics(board: Array<GameColor>): Int {
        var evaluation = 0
        val aiPieces = board.count { x -> x == aiColor }
        val userPieces = board.count { x -> x == userColor }
        val possibleMillsAI = possibleMillsCount(board, aiColor)
        val possibleMillsUser = possibleMillsCount(board, userColor)
        val movablePieces = stage23Moves(board).size
        val potentialMillsAI = potentialMillsPieces(board, aiColor)
        val potentialMillsUser = potentialMillsPieces(board, userColor)
        if (userPieces <= 2 || movablePieces == 0)
            evaluation = Int.MAX_VALUE
        else if (aiPieces <= 2)
            evaluation = Int.MIN_VALUE
        else {
            if (aiPieces < 4) {
                evaluation += 100 * possibleMillsAI
                evaluation += 200 * potentialMillsUser
            } else {
                evaluation += 200 * possibleMillsAI
                evaluation += 100 * potentialMillsUser
            }
            evaluation -= 25 * movablePieces
            evaluation += 50 * (aiPieces - userPieces)
        }

        return evaluation
    }

    private fun potentialMillsPieces(board: Array<GameColor>, color: GameColor): Int {
        var count = 0
        for (i in board.indices)
            if (board[i] == color) {
                val neighbors = neighbors[i]
                for (position in neighbors) {
                    if (color == aiColor && board[position] == userColor) {
                        board[i] = userColor
                        if (closeMill(i, board))
                            count++
                        board[i] = color
                    } else if (board[position] == aiColor && potentialMills(position, board, aiColor))
                        count++
                }
            }

        return count
    }

    private fun potentialMills(position: Int, board: Array<GameColor>, color: GameColor): Boolean {
        val neighbors = neighbors[position]
        for (i in neighbors) {
            val linesNeighbors = linesNeighbors[i]
            if (board[i] == color
                && (checkMillFormation(color, linesNeighbors[0], linesNeighbors[1])
                        || checkMillFormation(color, linesNeighbors[2], linesNeighbors[3]))
            )
                return true
        }
        return false
    }

    private fun possibleMillsCount(board: Array<GameColor>, color: GameColor): Int {
        var count = 0
        for (i in board.indices) {
            val linesNeighbors = linesNeighbors[i]
            if (board[i] == GameColor.F
                && (checkMillFormation(color, linesNeighbors[0], linesNeighbors[1])
                        || checkMillFormation(color, linesNeighbors[2], linesNeighbors[3]))
            )
                count++
        }
        return count
    }

    private fun invertedBoard(board: Array<GameColor>): Array<GameColor> {
        val result = Array(24) { GameColor.F }
        for (i in result.indices)
            if (board[i] == userColor)
                result[i] = aiColor
            else if (board[i] == aiColor)
                result[i] = userColor

        return result
    }

    private fun invertedBoardList(boardList: Array<Array<GameColor>>): Array<Array<GameColor>> {
        val result = Array(boardList.size) { arrayOf(GameColor.F) }
        for (i in boardList.indices)
            result[i] = invertedBoard(board)

        return result
    }

    private fun stage23Moves(board: Array<GameColor> = this.board): Array<Array<GameColor>> {
        return if (board.count { x -> x == aiColor } == 3)
            stage3Moves(board)
        else
            stage2Moves(board)
    }

    private fun stage2Moves(board: Array<GameColor>): Array<Array<GameColor>> {
        var boards = LinkedList<Array<GameColor>>()
        for (i in board.indices) {
            if (board[i] == aiColor) {
                val neighbors = neighbors(i)
                for (neighbor in neighbors) {
                    if (board[neighbor] == GameColor.F) {
                        val boardClone = board.copyOf()
                        boardClone[i] = GameColor.F
                        boardClone[neighbor] = aiColor
                        if (closeMill(neighbor, boardClone))
                            boards = removePiece(boardClone, boards)
                        else
                            boards.add(boardClone)
                    }
                }
            }
        }

        return boards.toTypedArray()
    }

    private fun stage3Moves(board: Array<GameColor>): Array<Array<GameColor>> {
        var boards = LinkedList<Array<GameColor>>()
        for (i in board.indices) {
            if (board[i] == aiColor) {
                for (j in board.indices) {
                    if (board[j] == GameColor.F) {
                        val boardClone = board.copyOf()
                        boardClone[i] = GameColor.F
                        boardClone[j] = aiColor
                        if (closeMill(j, boardClone))
                            boards = removePiece(boardClone, boards)
                        else
                            boards.add(boardClone)
                    }
                }
            }
        }

        return boards.toTypedArray()
    }

    private fun removePiece(
        board: Array<GameColor>,
        boards: LinkedList<Array<GameColor>>
    ): LinkedList<Array<GameColor>> {
        for (i in board.indices)
            if (board[i] == userColor)
                if (!closeMill(i, board)) {
                    val newBoard = board.copyOf()
                    newBoard[i] = GameColor.F
                    boards.addLast(newBoard)
                }
        return boards
    }

    private fun freePlace(position: Int): Boolean {
        return board[position] == GameColor.F
    }

    private fun evaluateState(): GameColor {
        val white = board.count { x -> x == GameColor.W }
        val black = board.count { x -> x == GameColor.B }
        if (white <= 2)
            return GameColor.B
        if (black <= 2)
            return GameColor.W
        return GameColor.F
    }
}

enum class Player { AI, USER }
enum class GameColor { B, W, F }

class Evaluation(var evaluator: Int = 0, var board: Array<GameColor> = Array(0) { GameColor.F })
