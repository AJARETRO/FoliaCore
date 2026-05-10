package dev.ajaretro.foliaCore.utils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Lightweight expression evaluator for /calc.
 * Supports +, -, *, /, ^ and parentheses.
 */
public final class ExpressionEvaluator {

    private ExpressionEvaluator() {
    }

    public static double evaluate(String expression) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Expression cannot be empty.");
        }

        Deque<Double> values = new ArrayDeque<>();
        Deque<Character> operators = new ArrayDeque<>();

        String normalized = expression.replace(" ", "");
        int index = 0;
        while (index < normalized.length()) {
            char token = normalized.charAt(index);

            if (Character.isDigit(token) || token == '.') {
                int end = index + 1;
                while (end < normalized.length()) {
                    char next = normalized.charAt(end);
                    if (!Character.isDigit(next) && next != '.') {
                        break;
                    }
                    end++;
                }

                values.push(Double.parseDouble(normalized.substring(index, end)));
                index = end;
                continue;
            }

            if (token == '(') {
                operators.push(token);
                index++;
                continue;
            }

            if (token == ')') {
                while (!operators.isEmpty() && operators.peek() != '(') {
                    applyTopOperator(values, operators);
                }
                if (operators.isEmpty() || operators.pop() != '(') {
                    throw new IllegalArgumentException("Mismatched parentheses in expression.");
                }
                index++;
                continue;
            }

            if (isOperator(token)) {
                if (token == '-' && isUnaryMinus(normalized, index)) {
                    values.push(0.0);
                }

                while (!operators.isEmpty() && operators.peek() != '(') {
                    char top = operators.peek();
                    if (hasHigherPrecedence(top, token)) {
                        applyTopOperator(values, operators);
                    } else {
                        break;
                    }
                }

                operators.push(token);
                index++;
                continue;
            }

            throw new IllegalArgumentException("Invalid token in expression: '" + token + "'");
        }

        while (!operators.isEmpty()) {
            if (operators.peek() == '(') {
                throw new IllegalArgumentException("Mismatched parentheses in expression.");
            }
            applyTopOperator(values, operators);
        }

        if (values.size() != 1) {
            throw new IllegalArgumentException("Invalid expression format.");
        }

        return values.pop();
    }

    private static boolean isUnaryMinus(String expression, int index) {
        if (index == 0) {
            return true;
        }

        char previous = expression.charAt(index - 1);
        return isOperator(previous) || previous == '(';
    }

    private static boolean isOperator(char token) {
        return token == '+' || token == '-' || token == '*' || token == '/' || token == '^';
    }

    private static boolean hasHigherPrecedence(char top, char incoming) {
        int topPrecedence = precedence(top);
        int incomingPrecedence = precedence(incoming);

        if (topPrecedence > incomingPrecedence) {
            return true;
        }

        // Exponentiation is right-associative.
        return topPrecedence == incomingPrecedence && incoming != '^';
    }

    private static int precedence(char operator) {
        return switch (operator) {
            case '+', '-' -> 1;
            case '*', '/' -> 2;
            case '^' -> 3;
            default -> -1;
        };
    }

    private static void applyTopOperator(Deque<Double> values, Deque<Character> operators) {
        if (values.size() < 2) {
            throw new IllegalArgumentException("Invalid expression format.");
        }

        double right = values.pop();
        double left = values.pop();
        char operator = operators.pop();

        double result;
        switch (operator) {
            case '+' -> result = left + right;
            case '-' -> result = left - right;
            case '*' -> result = left * right;
            case '/' -> {
                if (right == 0.0) {
                    throw new IllegalArgumentException("Division by zero is not allowed.");
                }
                result = left / right;
            }
            case '^' -> result = Math.pow(left, right);
            default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
        }

        values.push(result);
    }
}
