package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryTest {

    private static PrintWriter log;
    private WordleDictionary dictionary;

    @BeforeAll
    static void setUpAll() {
        log = new PrintWriter(System.out, true);
    }

    @BeforeEach
    void setUp() throws EmptyDictionaryException {
        dictionary = new WordleDictionary(List.of("арбуз", "лимон", "груша", "тыква", "мешок"), log);
    }

    @Test
    void shouldNormalizeAndFilterWords() throws EmptyDictionaryException {
        List<String> rawWords = Arrays.asList("  Арбуз ", "АРБУЗ", "лимон", "lemon", "12345", "", "   ", "а-ля фуршет");
        WordleDictionary customDictionary = new WordleDictionary(rawWords, log);

        List<String> words = customDictionary.getAllWords();

        //Проверяем, что словарь загружается без дубликатов, латиницы, цифр и пробелов (должно остаться 2 слова)
        assertEquals(2, words.size());

        //Проверяем, что слово "  Арбуз " приведено к нижнему регистру без пробелов
        assertTrue(words.contains("арбуз"));

        //Проверяем, что слово "лимон" сохранилось в словаре
        assertTrue(words.contains("лимон"));
    }

    @Test
    void shouldThrowOnEmptyDictionary() {
        List<String> invalidWords = Arrays.asList("lemon", "123", "   ", "а-ля фуршет");

        //Проверяем вызов исключения, если ни одно слово из списка не прошло проверку
        try {
            new WordleDictionary(invalidWords, log);
            fail("Ожидалось исключение EmptyDictionaryException для списка без подходящих слов");
        } catch (EmptyDictionaryException expected) {
            //все ок
        }

        //Проверяем вызов исключения при передаче пустого списка слова
        try {
            new WordleDictionary(Collections.emptyList(), log);
            fail("Ожидалось исключение EmptyDictionaryException для пустого списка");
        } catch (EmptyDictionaryException expected) {
            //все ок
        }
    }

    @Test
    void shouldThrowOnNullArguments() throws EmptyDictionaryException {
        //Проверяем вызов исключения при передаче null вместо списка слов
        try {
            new WordleDictionary(null, log);
            fail("Ожидалось исключение IllegalArgumentException при передаче null вместо списка");
        } catch (IllegalArgumentException expected) {
            //все ок
        }

        //Проверяем вызов исключения при передаче null вместо объекта логирования
        try {
            new WordleDictionary(List.of("груша"), null);
            fail("Ожидалось исключение IllegalArgumentException при передаче null вместо лога");
        } catch (IllegalArgumentException expected) {
            //все ок
        }
    }

    @Test
    void shouldFilterByLength() throws EmptyDictionaryException {
        WordleDictionary mixedDictionary = new WordleDictionary(List.of("дом", "груша", "тыква", "персик"), log);
        mixedDictionary.filterByLength(5);

        //Проверяем, что в словаре остались только 2 слова длиной 5 букв
        assertEquals(2, mixedDictionary.getAllWords().size());

        //Проверяем наличие слова "груша" (должен отработать normalize)
        assertTrue(mixedDictionary.contains("ГРУША"));

        //Проверяем наличие слова "тыква"
        assertTrue(mixedDictionary.contains("тыква"));

        //Проверяем, отсутствие слова "дом"
        assertFalse(mixedDictionary.contains("дом"));
    }
}