package com.example.compose_workshop_1

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs

/**
 * Improved CalculatorViewModel:
 * - Handles decimals when input is empty (creates "0.")
 * - Prevents multiple decimals
 * - Allows chaining operations (calculate then set next operation)
 * - Handles divide-by-zero safely (sets "Error" to number1)
 * - Formats result to remove trailing zeros and limits precision
 * - Uses safe string concatenation and length checks
 */
class CalculatorViewModel : ViewModel() {

    var state by mutableStateOf(CalculatorState())
        private set

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> enterNumber(action.number)
            is CalculatorAction.Decimal -> enterDecimal()
            is CalculatorAction.Clear -> state = CalculatorState()
            is CalculatorAction.Operation -> enterOperation(action.operation)
            is CalculatorAction.Calculate -> performCalculation()
            is CalculatorAction.Delete -> performDeletion()
        }
    }

    private fun performDeletion() {
        when {
            state.number2.isNotBlank() -> state = state.copy(number2 = state.number2.dropLast(1))
            state.operation != null -> state = state.copy(operation = null)
            state.number1.isNotBlank() -> state = state.copy(number1 = state.number1.dropLast(1))
        }
    }

    private fun performCalculation() {
        val n1 = state.number1.toBigDecimalOrNull()
        val n2 = state.number2.toBigDecimalOrNull()

        // If there's no operation or no first number, nothing to calculate
        val op = state.operation ?: return
        if (n1 == null) return

        // If second number is missing, treat as n2 = n1 for some calculators or ignore.
        // Here we ignore calculate unless n2 exists (keeps behavior predictable)
        if (n2 == null) return

        val result: BigDecimal = try {
            when (op) {
                is CalculatorOperation.Add -> n1 + n2
                is CalculatorOperation.Subtract -> n1 - n2
                is CalculatorOperation.Multiply -> n1 * n2
                is CalculatorOperation.Divide -> {
                    if (n2.compareTo(BigDecimal.ZERO) == 0) {
                        // handle division by zero - set an error state
                        state = state.copy(number1 = "Error", number2 = "", operation = null)
                        return
                    } else {
                        // set scale to avoid infinite repeating decimals, we'll format later
                        n1.divide(n2, CALC_SCALE, RoundingMode.HALF_UP)
                    }
                }
            }
        } catch (e: ArithmeticException) {
            // fallback in case of bad math
            state = state.copy(number1 = "Error", number2 = "", operation = null)
            return
        }

        val resultText = formatResult(result)
        state = state.copy(number1 = resultText, number2 = "", operation = null)
    }

    private fun enterOperation(operation: CalculatorOperation) {
        // If user has entered number1 and number2, perform calculation first (chain operations)
        if (state.number1.isNotBlank() && state.number2.isNotBlank()) {
            performCalculation()
            // After performCalculation, number1 holds the result (or "Error"). Set the new operation if result isn't Error.
            if (state.number1 != "Error") {
                state = state.copy(operation = operation)
            }
            return
        }

        // If number1 is blank but user presses an operation, ignore (or you could set number1 = "0")
        if (state.number1.isBlank()) return

        // Set the operation (replace existing op if any)
        state = state.copy(operation = operation)
    }

    private fun enterDecimal() {
        if (state.operation == null) {
            // Add decimal to number1
            if (!state.number1.contains(".")) {
                val new = if (state.number1.isBlank()) "0." else state.number1 + "."
                // enforce max length
                if (new.length <= MAX_NUM_LENGTH) {
                    state = state.copy(number1 = new)
                }
            }
        } else {
            // Add decimal to number2
            if (!state.number2.contains(".")) {
                val new = if (state.number2.isBlank()) "0." else state.number2 + "."
                if (new.length <= MAX_NUM_LENGTH) {
                    state = state.copy(number2 = new)
                }
            }
        }
    }

    private fun enterNumber(number: Int) {
        require(number in 0..9) { "number must be 0..9" }

        if (state.operation == null) {
            // entering number1
            val current = state.number1
            // prevent leading zeros like "00"
            val new = when {
                current == "0" && number == 0 -> "0" // still "0"
                current == "0" && number != 0 -> number.toString() // replace leading zero
                else -> current + number.toString()
            }
            if (new.length <= MAX_NUM_LENGTH) state = state.copy(number1 = new)
            return
        }

        // entering number2
        val current2 = state.number2
        val new2 = when {
            current2 == "0" && number == 0 -> "0"
            current2 == "0" && number != 0 -> number.toString()
            else -> current2 + number.toString()
        }
        if (new2.length <= MAX_NUM_LENGTH) state = state.copy(number2 = new2)
    }

    companion object {
        private const val MAX_NUM_LENGTH = 12
        private const val CALC_SCALE = 16 // internal scale for division before formatting
    }

    // -------------------------
    // Helpers
    // -------------------------
    private fun BigDecimal.stripTrailingZerosPlainString(): String {
        // Avoid scientific notation for large/small numbers that BigDecimal.toPlainString handles
        val plain = this.stripTrailingZeros().toPlainString()
        // If plain contains a '.', trim extra zeros at end (already strippedTrailingZeros did that),
        // but ensure we don't end with '.'.
        return if (plain.contains('.') && plain.endsWith('.')) plain.dropLast(1) else plain
    }

    private fun formatResult(value: BigDecimal): String {
        // If it's effectively an integer, show without decimal
        val intPart = value.setScale(0, RoundingMode.DOWN)
        val fractional = value.subtract(intPart).abs()
        return if (fractional.compareTo(BigDecimal.ZERO) == 0) {
            intPart.toPlainString()
        } else {
            // Limit to reasonable number of decimal places for display; trim trailing zeros
            val scaled = value.setScale(8, RoundingMode.HALF_UP)
            // strip trailing zeros and return plain string
            scaled.stripTrailingZerosPlainString()
        }
    }

    // extension to safely convert string to BigDecimal
    private fun String.toBigDecimalOrNull(): BigDecimal? = try {
        BigDecimal(this)
    } catch (e: Exception) {
        null
    }
}

data class CalculatorState(
    val number1: String = "",
    val number2: String = "",
    val operation: CalculatorOperation? = null
)

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    object Decimal : CalculatorAction()
    object Calculate : CalculatorAction()
    data class Operation(val operation: CalculatorOperation) : CalculatorAction()
}

sealed class CalculatorOperation(val symbol: String) {
    object Add : CalculatorOperation("+")
    object Subtract : CalculatorOperation("-")
    object Multiply : CalculatorOperation("*")
    object Divide : CalculatorOperation("/")
}