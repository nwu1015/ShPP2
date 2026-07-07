package com.shpp.p2p.cs.vmarchenko.assignment11;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class Assignment10Part1Test {

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    @BeforeEach
    void setUp() {
        // Перенаправляємо System.out у буфер, щоб зчитувати результати роботи main
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        // Повертаємо стандартний вивід назад
        System.setOut(originalOut);
    }

    /**
     * Допоміжний метод для запуску програми та отримання чистого результату.
     * Оскільки твоя програма виводить списки виду [5.0], ми дістаємо число.
     */
    private double runAndGetResult(String... args) {
        outputStreamCaptor.reset(); // Очищуємо буфер перед новим тестом
        com.shpp.p2p.cs.vmarchenko.assignment10.Assignment10Part1.main(args);

        String output = outputStreamCaptor.toString().trim();
        // Твоя програма друкує результат як рядок масиву, наприклад: "[5.0]"
        // Очистимо квадратні дужки для парсингу в double
        String cleanOutput = output.replace("[", "").replace("]", "");

        // Якщо у виводі кілька рядків (через System.out.println всередині performOperations),
        // беремо останній рядок як фінальний результат
        String[] lines = cleanOutput.split("\\r?\\n");
        String finalResult = lines[lines.length - 1].trim();

        return Double.parseDouble(finalResult);
    }

    @Test
    @DisplayName("Базовий приклад із ТЗ: 1 + a * 2, де a = 2")
    void testExampleFromAssignment() {
        double result = runAndGetResult("1 + a * 2", "a = 2");
        assertEquals(5.0, result, 1e-9);
    }

    @Test
    @DisplayName("Обчислення без змінних та без пробілів")
    void testSimpleArithmetic() {
        assertEquals(7.0, runAndGetResult("1+2*3"), 1e-9);
        assertEquals(2.5, runAndGetResult("5/2"), 1e-9);
    }

    @Test
    @DisplayName("Перевірка пріоритету оператора піднесення до степеня ^")
    void testPowerOperatorPrecedence() {
        // ^ має виконуватися раніше за *, тобто: 2 * (4^2) = 2 * 16 = 32
        assertEquals(32.0, runAndGetResult("2 * 4 ^ 2"), 1e-9);
        // 3 + (4 * (2^2)) = 3 + (4 * 4) = 19
        assertEquals(19.0, runAndGetResult("3 + 4 * 2 ^ 2"), 1e-9);
    }

    @Test
    @DisplayName("Робота з унарним мінусом")
    void testUnaryMinus() {
        assertEquals(-2.0, runAndGetResult("-1 * 2"), 1e-9);
        assertEquals(5.0, runAndGetResult("3 - -2"), 1e-9);
    }

    @Test
    @DisplayName("Робота з дробовими числами у параметрах")
    void testDecimalParameters() {
        assertEquals(7.0, runAndGetResult("a * b", "a = 2.0", "b = 3.5"), 1e-9);
    }

    @Test
    @DisplayName("Помилка: Ділення на нуль")
    void testDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> {
            runAndGetResult("5 / 0");
        });
    }

    @Test
    @DisplayName("Помилка: Некоректний формат параметра (відсутній знак =)")
    void testMissingEqualityInParameter() {
        assertThrows(IllegalArgumentException.class, () -> {
            runAndGetResult("1 + a", "a2");
        });
    }

    @Test
    @DisplayName("Помилка: Два знаки дорівнює в параметрі")
    void testMultipleEqualityInParameter() {
        assertThrows(IllegalArgumentException.class, () -> {
            runAndGetResult("1 + a", "a = 2 = 3");
        });
    }

    @Test
    @DisplayName("Помилка: Невалідні символи в імені змінної")
    void testInvalidCharacterInVariableName() {
        assertThrows(IllegalArgumentException.class, () -> {
            runAndGetResult("1 + a", "a1 = 2");
        });
    }
}
