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

    @Test
    void shouldBuildPrompt() throws InvalidWordLengthException {
        //Проверяем маску при полном совпадении всех букв на своих местах
        assertEquals("+++++", WordleDictionary.buildPrompt("арбуз", "арбуз"));

        //Проверяем маску при полном отсутствии общих букв
        assertEquals("-----", WordleDictionary.buildPrompt("лимон", "арбуз"));

        //Проверяем смешанную маску
        assertEquals("-+^-^", WordleDictionary.buildPrompt("груша", "арбуз"));
    }

    @Test
    void shouldBuildPromptWithDuplicates() throws InvalidWordLengthException {
        //Проверяем обработку дублей: в "какао" одна буква "о", поэтому в "кокос" должна посдвечиваться только первая
        assertEquals("+^+--", WordleDictionary.buildPrompt("кокос", "какао"));

        //Проверяем обработку дублей: в "арбуз" одна буква "а", поэтому в "банан" должна подсвечивается только первая
        assertEquals("^^---", WordleDictionary.buildPrompt("банан", "арбуз"));
    }

    @Test
    void shouldThrowOnDifferentWordLengths() {
        try {
            //Проверяем вызов исключения при сравнении слов разной длины (3 и 5)
            WordleDictionary.buildPrompt("дом", "арбуз");
            fail("Ожидалось исключение InvalidWordLengthException при разной длине слов");
        } catch (InvalidWordLengthException expected) {
            //все ок
        }
    }

    @Test
    void shouldFilterCandidates() throws InvalidWordLengthException {
        List<String> initialWords = dictionary.getAllWords();
        String guess = "тыква";
        String expectedPrompt = WordleDictionary.buildPrompt(guess, "арбуз");

        List<String> candidates = dictionary.filterCandidates(initialWords, guess, expectedPrompt);

        //Проверяем, что после фильтра по маске остался ровно 1 кандидат
        assertEquals(1, candidates.size());

        //Проверяем, что этот единственный кандидат является загаданным словом "арбуз"
        assertEquals("арбуз", candidates.get(0));
    }
}