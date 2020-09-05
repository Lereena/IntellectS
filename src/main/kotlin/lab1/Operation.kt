package lab1

import java.lang.Exception

class Operation constructor(val sign: String, val operand: Int) {
    fun applyTo(leftOperand: State): State {
        val resultValue = when (sign) {
            "+" -> leftOperand.value + operand
            "-" -> leftOperand.value - operand
            "*" -> leftOperand.value * operand
            "/" -> leftOperand.value / operand
            else -> throw Exception("Неизвестный знак операции")
        }
        return State(resultValue, leftOperand, this)
    }

    override fun toString(): String {
        return sign + operand
    }
}
