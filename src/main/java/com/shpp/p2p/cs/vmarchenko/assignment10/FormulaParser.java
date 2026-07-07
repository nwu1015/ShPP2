package com.shpp.p2p.cs.vmarchenko.assignment10;

import java.util.ArrayList;
import java.util.Stack;

public class FormulaParser {
    /**
     * Splits the formula string into logical separate parts (numbers, operators).
     *
     * @param formula the mathematical expression string
     * @return an ArrayList of elements ordered in Reverse Polish Notation.
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
     * Converts the formula to Reverse Polish Notation.
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
     * Checking if a minus sign is before the operator.
     */
    private boolean isMinusBeforeOperator(String part, int index, ArrayList<String> parts) {
        if (!part.equals("-")) return false;
        if (index == 0) return true;

        String prevPart = parts.get(index - 1);
        return isOperator(prevPart);
    }

    private boolean isNumber(String part) {
        return Character.isDigit(part.charAt(0)) || Character.isLetter(part.charAt(0));
    }

    private boolean isOperator(String part) {
        return "+-*/^".contains(part);
    }

    /**
     * Determines the order in which operators are popped from the stack.
     */
    private void handleOperator(String currentOperator, ArrayList<String> rpn, Stack<String> operators) {
        while (!operators.isEmpty()) {
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
     * Determines the precedence of mathematical operators.
     */
    private int getPriority(String operator) {
        if (operator.equals("+") || operator.equals("-")) return 1;
        if (operator.equals("*") || operator.equals("/")) return 2;
        if (operator.equals("^")) return 3;
        return 0;
    }

    private boolean shouldPop(String currentOperator, int currentPriority, int priority) {
        if (currentOperator.equals("^")) {
            return priority > currentPriority;
        } else {
            return priority >= currentPriority;
        }
    }
}