package com.shpp.p2p.cs.vmarchenko.assignment11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Assignment11Part1Test {
    private Assignment11Part1 instance;

    @BeforeEach
    public void setup() {
        instance = new Assignment11Part1();
    }

    /**
     * Допоміжний метод, який імітує логіку main:
     * бере масив рядків, дістає формулу та парсить параметри.
     */
    private double runCalculate(String[] args) {
        String formula = args[0];
        // Викликаємо твій метод для отримання мапи з args
        HashMap<String, Double> variables = invokeDetermineParameters(args);
        return instance.calculate(formula, variables);
    }

    // Оскільки determineParameters приватний, ми можемо або зробити його package-private,
    // або просто продублювати логіку парсингу для тестів тут.
    private HashMap<String, Double> invokeDetermineParameters(String[] args) {
        HashMap<String, Double> parameters = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            String[] elements = args[i].replace(" ", "").split("=");
            if (elements.length == 2) {
                parameters.put(elements[0], Double.parseDouble(elements[1]));
            }
        }
        return parameters;
    }

    @DisplayName("Base cases")
    @ParameterizedTest
    @MethodSource("baseArgs")
    public void baseCasesTest(String[] args, double expected) {
        assertEquals(expected, runCalculate(args), 0.0001);
    }

    @DisplayName("Operator Precedence")
    @ParameterizedTest
    @MethodSource("precedenceArgs")
    public void precedenceTest(String[] args, double expected) {
        assertEquals(expected, runCalculate(args), 0.0001);
    }

    @DisplayName("Variables substitution")
    @ParameterizedTest
    @MethodSource("variablesArgs")
    public void variablesTest(String[] args, double expected) {
        assertEquals(expected, runCalculate(args), 0.0001);
    }

    @DisplayName("Trigonometry and Functions")
    @ParameterizedTest
    @MethodSource("functionArgs")
    public void functionTest(String[] args, double expected) {
        assertEquals(expected, runCalculate(args), 0.0001);
    }

    @DisplayName("Logarithms and Exponentiation (including Power Towers)")
    @ParameterizedTest
    @MethodSource("advancedMathArgs")
    public void advancedMathTest(String[] args, double expected) {
        // Використовуємо дельту 0.0001 для порівняння double
        assertEquals(expected, runCalculate(args), 0.0001);
    }

    static Stream<Arguments> advancedMathArgs() {
        return Stream.of(
                // --- Log10 ---
                Arguments.of(new String[]{"log10(100)"}, 2.0),
                Arguments.of(new String[]{"log10(1)"}, 0.0),
                Arguments.of(new String[]{"log10(0.1)"}, -1.0),

                // --- Log2 ---
                Arguments.of(new String[]{"log2(8)"}, 3.0),
                Arguments.of(new String[]{"log2(1024)"}, 10.0),
                Arguments.of(new String[]{"log2(1)"}, 0.0),
                Arguments.of(new String[]{"log2(0.5)"}, -1.0),

                // --- Exponentiation (^) ---
                Arguments.of(new String[]{"2^3"}, 8.0),
                Arguments.of(new String[]{"5^0"}, 1.0),
                Arguments.of(new String[]{"4^0.5"}, 2.0), // Дробовий степінь (корінь)
                Arguments.of(new String[]{"2^-1"}, 0.5),  // Від'ємний степінь

                // --- "Вежі степенів" (Right-associativity) ---
                // 2^3^2 має бути 2^(3^2) = 2^9 = 512
                Arguments.of(new String[]{"2^3^2"}, 512.0),
                // 3^2^2 має бути 3^(2^2) = 3^4 = 81
                Arguments.of(new String[]{"3^2^2"}, 81.0),

                // --- Комбінації та змінні ---
                Arguments.of(new String[]{"log2(a^b)", "a=2", "b=5"}, 5.0),
                Arguments.of(new String[]{"log10(x) + log2(y)", "x=100", "y=4"}, 4.0),

                // --- Складні вкладення ---
                // log2(log10(100)) = log2(2) = 1
                Arguments.of(new String[]{"log2(log10(100))"}, 1.0),
                // 2^(2+1)^2 = 2^3^2 = 2^9 = 512
                Arguments.of(new String[]{"2^(2+1)^2"}, 512.0)
        );
    }

    // --- Джерела даних (Arguments Streams) ---

    static Stream<Arguments> baseArgs() {
        return Stream.of(
                Arguments.of(new String[]{"2"}, 2.0),
                Arguments.of(new String[]{"a", "a=2"}, 2.0),
                Arguments.of(new String[]{"1+2+3"}, 6.0),
                Arguments.of(new String[]{"10/2/2"}, 2.5)
        );
    }

    static Stream<Arguments> precedenceArgs() {
        return Stream.of(
                Arguments.of(new String[]{"2+3*4"}, 14.0),
                Arguments.of(new String[]{"10-2*3"}, 4.0),
                Arguments.of(new String[]{"(2+3)*4"}, 20.0),
                Arguments.of(new String[]{"2+3*4-6/2"}, 11.0)
        );
    }

    static Stream<Arguments> variablesArgs() {
        return Stream.of(
                Arguments.of(new String[]{"a*b", "a=3", "b=4"}, 12.0),
                Arguments.of(new String[]{"a+b*c", "a=1", "b=2", "c=3"}, 7.0),
                Arguments.of(new String[]{"a+b", "a=1.5", "b=2.5"}, 4.0),
                Arguments.of(new String[]{"a/b", "a=9", "b=3"}, 3.0)
        );
    }

    static Stream<Arguments> functionArgs() {
        return Stream.of(
                Arguments.of(new String[]{"sqrt(16)"}, 4.0),
                Arguments.of(new String[]{"sin(0)"}, 0.0),
                Arguments.of(new String[]{"sqrt(a) + b", "a=9", "b=2"}, 5.0),
                Arguments.of(new String[]{"log10(100)"}, 2.0)
        );
    }
}