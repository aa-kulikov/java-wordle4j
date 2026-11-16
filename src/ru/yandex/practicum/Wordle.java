package ru.yandex.practicum;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Wordle {
    private static final int WORD_LENGTH = 5;
    private static final int MAX_ATTEMPTS = 6;
    private static final String DICTIONARY_FILE = "words_ru.txt";
    private static final String LOG_FILE = "log.txt";

    public static void main(String[] args) {
        try (PrintWriter log = new PrintWriter(new FileWriter(LOG_FILE, StandardCharsets.UTF_8, true), true);
             Scanner scanner = new Scanner(System.in)) {

            log.println("\nЗапуск игры Wordle");

            try {
                WordleDictionaryLoader dictionaryLoader = new WordleDictionaryLoader(log);
                WordleDictionary dictionary = dictionaryLoader.load(DICTIONARY_FILE);

                WordleGame game = new WordleGame(dictionary, WORD_LENGTH, MAX_ATTEMPTS, log);

                playGame(game, scanner, log);

            } catch (DictionaryFileNotFoundException | EmptyDictionaryException e) {
                System.err.println("Ошибка запуска игры: " + e.getMessage());
            }

            log.println("Завершение игры Wordle");

        } catch (IOException ioExp) {
            System.err.println("Ошибка ввода-вывода: " + ioExp.getMessage());
        } catch (IllegalArgumentException illExp) {
            System.err.println("Ошибка передачи аргумента: " + illExp.getMessage());
        }
    }

    private static void playGame(WordleGame game, Scanner scanner, PrintWriter log) {

        System.out.println("Сыграем в игру");
        System.out.println("Угадай слово из " + WORD_LENGTH + " букв за " + MAX_ATTEMPTS + " попыток");
        System.out.println("Обозначения:\n+ — буква на своем месте\n^ — буква не на своем месте\n- — буквы нет в слове");
        System.out.println("Enter в пустой строке выведет подходящее слово из словаря.\n");

        while (!game.isGameOver()) {
            System.out.printf("Осталось попыток: %d. Введи слово:\n", game.getRemainingAttempts());
            String input = scanner.nextLine();

            try {
                String wordToGuess;

                if (input.isBlank()) {
                    wordToGuess = game.getHint();
                    System.out.println(wordToGuess);
                } else {
                    wordToGuess = input;
                }

                String symbolsPrompt = game.makeMove(wordToGuess);
                System.out.println(symbolsPrompt);

            } catch (InvalidWordLengthException | InvalidCharactersException | WordNotFoundInDictionaryException e) {
                System.out.println("Некорректный ввод: " + e.getMessage());

            } catch (GameOverException | EmptyDictionaryException e) {
                System.out.println("Ошибка состояния игры: " + e.getMessage());
            }
        }

        if (game.isWin()) {
            System.out.println("Победа! Слово угадано: " + game.getSecretWord());
            log.println("Игра завершена победой. Слово: '" + game.getSecretWord() + "'.");
        } else {
            System.out.println("Попытки закончились. Загаданное слово: " + game.getSecretWord());
            log.println("Игра завершена поражением. Загаданное слово: '" + game.getSecretWord() + "'.");
        }
    }
}
