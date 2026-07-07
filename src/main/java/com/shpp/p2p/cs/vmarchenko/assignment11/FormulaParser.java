package com.shpp.p2p.cs.vmarchenko.assignment11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class FormulaParser {
    private final HashMap<String, IAction> actions;

    public FormulaParser(HashMap<String, IAction> actions) {
        this.actions = actions;
    }

    /**
     * Splits the formula string into logical separate parts (numbers, functions, operators).
     *
     * @param formula the mathematical expression string
     * @return an ArrayList of elements ordered.
     */
    public ArrayList<String> parseFormula(String formula) {
        ArrayList<String> parts = new ArrayList<>();
        String changedFormula = formula.replace(" ", "");

        for (int i = 0; i < changedFormula.length(); i++) {
            char c = changedFormula.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                StringBuilder number = new StringBuilder();
                while (i < changedFormula.length() && (Character.isDigit(changedFormula.charAt(i)) ||
                        changedFormula.charAt(i) == '.')) {
                    number.append(changedFormula.charAt(i));
                    i++;
                }
                parts.add(number.toString());
                i--;
            }
            else if (Character.isLetter(c)) {
                StringBuilder word = new StringBuilder();
                while (i < changedFormula.length() &&
                        (Character.isLetter(changedFormula.charAt(i)) || Character.isDigit(changedFormula.charAt(i)))) {
                    word.append(changedFormula.charAt(i));
                    i++;
                }
                parts.add(word.toString());
                i--;
            }
            else {
                parts.add(String.valueOf(c));
            }
        }

        return convertToPostFix(parts);
    }

    /**
     * Converts the extended version of the formula to the form required for reverse Polish notation.
     * Ensures that higher-priority operations are placed before lower-priority ones
     * in the final output list.
     *
     * @param parts a list of strings of the formula.
     * @return list of elements ordered for reverse Polish notation
     */
    private ArrayList<String> convertToPostFix(ArrayList<String> parts) {
        ArrayList<String> rpn = new ArrayList<>();
        Stack<String> operators = new Stack<>();

        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);

            if (isMinusBeforeOperator(part, i, parts)) {
                String nextPart = parts.get(i + 1);
                rpn.add("-" + nextPart);
                i++;
                continue;
            }

            if (isNumber(part)) {
                rpn.add(part);
            }
            else if (isFunction(part)) {
                operators.push(part);
            }
            else if (part.equals("(")) {
                operators.push(part);
            }
            else if (part.equals(")")) {
                handleClosingBracket(rpn, operators);
            }
            else if (isOperator(part)) {
                handleOperator(part, rpn, operators);
            }
        }

        while (!operators.isEmpty()) {
            rpn.add(operators.pop());
        }

        return rpn;
    }

    /**
     * Checks if this part is a number
     *
     * @param part part of a line from a formula
     * @return comparison result (true/false)
     */
    private boolean isNumber(String part) {
        return Character.isDigit(part.charAt(0)) ||
                (Character.isLetter(part.charAt(0)) && !actions.containsKey(part));
    }

    /**
     * Checks if this part is a function
     *
     * @param part part of a line from a formula
     * @return comparison result (true/false)
     */
    private boolean isFunction(String part) {
        return actions.containsKey(part) && part.length() > 1;
    }

    /**
     * Checks if this part is an operator
     *
     * @param part part of a line from a formula
     * @return comparison result (true/false)
     */
    private boolean isOperator(String part) {
        return "+-*/^".contains(part);
    }

    /**
     * Pushes operators off the stack to the nearest opening parenthesis.
     * If there was a function before the parenthesis, it also pushes it out.
     *
     * @param rpn a list of elements in reverse Polish notation, where operators are added.
     * @param operators a stack of operators processed during expression traversal.
     */
    private void handleClosingBracket(ArrayList<String> rpn, Stack<String> operators) {
        while (!operators.isEmpty() && !operators.peek().equals("(")) {
            rpn.add(operators.pop());
        }

        if (!operators.isEmpty()) {
            operators.pop();
        }

        if (!operators.isEmpty() && isFunction(operators.peek())) {
            rpn.add(operators.pop());
        }
    }

    /**
     * Determines the order in which operators are popped from the stack
     * based on precedence and associativity.
     *
     * @param currentOperator current operator read from the input expression
     * @param rpn result list, where operators that have already worked out their priority are moved
     * @param operators a stack in which operators are temporarily stored while waiting for operands
     */
    private void handleOperator(String currentOperator, ArrayList<String> rpn, Stack<String> operators) {
        while (!operators.isEmpty() && !operators.peek().equals("(")) {
            int stackPriority = getPriority(operators.peek());
            int currentPriority = getPriority(currentOperator);

            if (shouldPop(currentOperator, currentPriority, stackPriority)) {
                rpn.add(operators.pop());
            } else {
                break;
            }
        }
        operators.push(currentOperator);
    }

    /**
     * Determines whether the operator on top of the stack should be popped.
     * For +, -, *, /, an operator is popped if it has greater than or equal precedence.
     * For ^, it is popped only if it has strictly greater precedence.
     *
     * @param currentOperator       The operator currently being processed from the expression.
     * @param currentPriority The precedence level of the current operator.
     * @param priority   The precedence level of the operator at the top of the stack.
     * @return true if the operator from the stack should be moved to the RPN output,
     * false if the current operator should be pushed onto the stack.
     */
    private boolean shouldPop(String currentOperator, int currentPriority, int priority) {
        if (currentOperator.equals("^")) {
            return priority > currentPriority;
        } else {
            return priority >= currentPriority;
        }
    }

    /**
     * Checking if a minus sign is before the operator
     *
     * @param part   The current string being evaluated.
     * @param index  The current position of the part in the formula list.
     * @param parts  The complete list of formula parts for contextual lookup.
     * @return true if the minus is part of a negative number; false if it is a subtraction.
     */
    private boolean isMinusBeforeOperator(String part, int index, ArrayList<String> parts) {
        if (!part.equals("-")) return false;
        String prevPart = parts.get(index - 1);
        return isOperator(prevPart) || prevPart.equals("(");
    }

    /**
     * Determines the precedence of mathematical operators and functions.
     * Functions have the highest priority, followed by
     * multiplication/division, and then addition/subtraction.
     *
     * @param operator the operator or function name to check
     * @return an integer representing the priority level
     */
    private int getPriority(String operator) {
        if (operator.equals("+") || operator.equals("-")) return 1;
        if (operator.equals("*") || operator.equals("/")) return 2;
        if (operator.equals("^")) return 3;
        if (actions.containsKey(operator) && operator.length() > 1) return 4;
        return 0;
    }
}