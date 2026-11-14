package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    private static PrintWriter log;
    private WordleDictionary dictionary;

    @BeforeAll
    static void setUpAll() {
        log = new PrintWriter(System.out, true);
    }

    @BeforeEach
    void setUp() throws EmptyDictionaryException {
        dictionary = new WordleDictionary(List.of("арбуз", "лимон", "груша"), log);
    }

    @Test
    void shouldWinGame() throws Exception {
        WordleDictionary singleWordDictionary = new WordleDictionary(List.of("арбуз"), log);
        WordleGame game = new WordleGame(singleWordDictionary, 5, 6, log);

        String prompt = game.makeMove("АРБУЗ");

        //Проверяем, что для угаданного слова вернулась маска "+++++"
        assertEquals("+++++", prompt);

        //Проверяем, что установлен флаг победы
        assertTrue(game.isWin());

        //Проверяем, что игра завершена
        assertTrue(game.isGameOver());

        //Проверяем, что была потрачена только 1 попытка
        assertEquals(5, game.getRemainingAttempts());
    }

    @Test
    void shouldLoseGame() throws Exception {
        WordleGame game = new WordleGame(dictionary, 5, 2, log);

        String wrongWord = "арбуз";
        if (game.getSecretWord().equals("арбуз")) {
            wrongWord = "лимон";
        }
        game.makeMove(wrongWord);

        //Проверяем, что после первой неудачной попытки игра не окончена
        assertFalse(game.isGameOver());

        //Проверяем, что осталась 1 попытка из 2
        assertEquals(1, game.getRemainingAttempts());

        game.makeMove(wrongWord);

        //Проверяем, что после использования всех попыток игра завершилась
        assertTrue(game.isGameOver());

        //Проверяем, что флаг победы не установлен
        assertFalse(game.isWin());

        //Проверяем, что попыток больше не осталось
        assertEquals(0, game.getRemainingAttempts());
    }

    @Test
    void shouldNotSpendAttemptOnInvalidInput() throws Exception {
        WordleGame game = new WordleGame(dictionary, 5, 6, log);

        try {
            //Проверяем вызов исключения при вводе цифр
            game.makeMove("12345");
            fail("Ожидалось исключение InvalidCharactersException для цифр");
        } catch (InvalidCharactersException expected) {
            //все ок
        }
        try {
            //Проверяем вызов исключения при вводе латинских букв
            game.makeMove("lemon");
            fail("Ожидалось исключение InvalidCharactersException для латиницы");
        } catch (InvalidCharactersException expected) {
            //все ок
        }
        try {
            //Проверяем вызов исключения при вводе слова неподходящей длины
            game.makeMove("дом");
            fail("Ожидалось исключение InvalidWordLengthException для короткого слова");
        } catch (InvalidWordLengthException expected) {
            //все ок
        }
        try {
            //Проверяем вызов исключения при вводе слова, которого нет в словаре
            game.makeMove("тыква");
            fail("Ожидалось исключение WordNotFoundInDictionaryException для слова не из словаря");
        } catch (WordNotFoundInDictionaryException expected) {
            //все ок
        }

        //Проверяем, что ошибочные попытки не уменьшают счетчик ходов
        assertEquals(6, game.getRemainingAttempts());

        //Проверяем, что игра не завершена
        assertFalse(game.isGameOver());
    }

    @Test
    void shouldThrowWhenMovingAfterGameOver() throws Exception {
        WordleDictionary singleWordDictionary = new WordleDictionary(List.of("арбуз"), log);
        WordleGame game = new WordleGame(singleWordDictionary, 5, 6, log);

        game.makeMove("арбуз");

        //Проверяем, что игра завершена
        assertTrue(game.isGameOver());

        //Проверяем вызов исключения при попытке сделать ещё один ход после окончания игры
        try {
            game.makeMove("арбуз");
            fail("Ожидалось исключение GameOverException при попытке хода после победы");
        } catch (GameOverException expected) {
            //все ок
        }
    }

    @Test
    void shouldReturnRelevantHint() throws Exception {
        WordleDictionary twoWordsDictionary = new WordleDictionary(List.of("арбуз", "лимон"), log);
        WordleGame game = new WordleGame(twoWordsDictionary, 5, 6, log);

        String firstGuess = "арбуз";
        if (game.getSecretWord().equals("арбуз")) {
            firstGuess = "лимон";
        }
        game.makeMove(firstGuess);
        String hint = game.getHint();

        //Проверяем, что подсказка удалила использованное слово и предложила единственное оставшееся загаданное слово
        assertEquals(game.getSecretWord(), hint);
    }

    @Test
    void shouldThrowWhenNoWordsOfRequiredLength() throws EmptyDictionaryException {
        WordleDictionary shortWordsDictionary = new WordleDictionary(List.of("дом", "лес"), log);

        //Проверяем вызов исключения при попытке начать игру, когда в словаре нет слов с 5 буквами
        try {
            new WordleGame(shortWordsDictionary, 5, 6, log);
            fail("Ожидалось исключение EmptyDictionaryException при отсутствии слов нужной длины");
        } catch (EmptyDictionaryException expected) {
            //все ок
        }
    }
}