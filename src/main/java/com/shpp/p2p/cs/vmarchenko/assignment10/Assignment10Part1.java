package com.shpp.p2p.cs.vmarchenko.assignment10;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * Assignment 10: Calculator.
 * The expression to be evaluated and, if available,
 * the parameters for this expression are taken from the args array.
 * The expression is then evaluated and the result is printed to the console.
 */
public class Assignment10Part1 {
    private static final HashMap<String, IAction> actions = new HashMap<>();
    public Assignment10Part1() {
        actions.put("+", new AddAction());
        actions.put("-", new MinusAction());
        actions.put("*", new MultipleAction());
        actions.put("/", new DivideAction());
        actions.put("^", new PowAction());
    }

    public static void main(String[] args) {
        if (args.length == 0 || args[0] == null) {
            System.out.println("Please, write a mathematical expression and parameters " +
                    "if it's necessary");
            return;
        }

        Assignment10Part1 mainClass = new Assignment10Part1();

        String formula = args[0];
        HashMap<String, Double> variables = mainClass.determineParameters(args);

        double result = mainClass.calculate(formula, variables);
        System.out.println(result);
    }

    /**
     * Take the parameters and write them to a HashMap.
     * Parameters are taken from args.
     *
     * @param args where the parameters come from
     * @return map, where the key is the name of the parameter, the value is its number
     */
    private HashMap<String,Double> determineParameters(String[] args) {
        HashMap<String,Double> parameters = new HashMap<>();

        for(int i = 1; i < args.length; i++) {
            String[] elements = args[i].replace(" ", "").split("=");
            parameters.put(elements[0], Double.parseDouble(elements[1]));
        }

        return parameters;
    }

    /**
     * Method for calculating an expression.
     * This is where reverse Polish notation is used.
     * I saw the idea of its implementation on the Internet.
     * Source: https://cutt.ly/qyqTkvnj
     * Here we use the IAction interface, which has a calculate method for each operation.
     *
     * @param formula expression (first argument)
     * @param variables map with parameters and their values
     * @return result of calculating the expression
     */
    public double calculate(String formula, HashMap<String,Double> variables){
        ArrayList<String> elements = parseFormula(formula);
        substituteNumbers(elements, variables);

        Stack<Double> stack = new Stack<>();

        for (String element : elements) {
            if (actions.containsKey(element)) {

                IAction action = actions.get(element);
                if(element.length() > 1){
                    double a = stack.pop();
                    stack.push(action.execute(a));
                }else {
                    double b = stack.pop();
                    double a = stack.pop();
                    stack.push(action.execute(a, b));
                }
            } else {
                stack.push(Double.parseDouble(element));
            }
        }
        return stack.isEmpty() ? 0 : stack.pop();
    }

    /**
     * Splits the formula string into logical separate parts (numbers, operators).
     *
     * @param formula the mathematical expression string
     * @return an ArrayList of elements ordered in Reverse Polish Notation.
     */
    private ArrayList<String> parseFormula(String formula) {
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
     * Determines the precedence of mathematical operators.
     */
    private int getPriority(String operator) {
        if (operator.equals("+") || operator.equals("-")) return 1;
        if (operator.equals("*") || operator.equals("/")) return 2;
        if (operator.equals("^")) return 3;
        return 0;
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

    private boolean shouldPop(String currentOperator, int currentPriority, int priority) {
        if (currentOperator.equals("^")) {
            return priority > currentPriority;
        } else {
            return priority >= currentPriority;
        }
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

    /**
     * Replaces variable names in the formula with their corresponding numeric values.
     */
    private void substituteNumbers(ArrayList<String> formula, HashMap<String, Double> variables) {
        for (int i = 0; i < formula.size(); i++) {
            if (variables.containsKey(formula.get(i))) {
                formula.set(i, String.valueOf(variables.get(formula.get(i))));
            }
        }
    }
}