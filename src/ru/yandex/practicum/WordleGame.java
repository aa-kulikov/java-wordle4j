package ru.yandex.practicum;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class WordleGame {
    private final String secretWord;
    private final int wordLength;
    private final int maxAttempts;
    private int currentAttempt = 0;
    private final WordleDictionary dictionary;
    private List<String> possibleWords;
    private final Set<String> usedWords;
    private final PrintWriter log;
    private boolean isWin = false;

    public WordleGame(WordleDictionary dictionary, int wordLength, int maxAttempts, PrintWriter log)
            throws EmptyDictionaryException {
        if (dictionary == null) {
            throw new IllegalArgumentException("Объект словаря не передан");
        }
        if (log == null) {
            throw new IllegalArgumentException("Объект лога не передан");
        }
        if (wordLength <= 0 || maxAttempts <= 0) {
            throw new IllegalArgumentException("Длина слова и количество попыток должны быть больше нуля");
        }

        this.dictionary = dictionary.filterByLength(wordLength);

        if (this.dictionary.getAllWords().isEmpty()) {
            String message = "Невозможно начать игру: в словаре нет слов длиной " + wordLength + " букв.";
            log.println(message);
            throw new EmptyDictionaryException(message);
        }

        this.log = log;
        this.wordLength = wordLength;
        this.maxAttempts = maxAttempts;
        this.secretWord = this.dictionary.getRandomWord();
        this.possibleWords = new ArrayList<>(this.dictionary.getAllWords());
        this.usedWords = new HashSet<>();

        log.println(String.format("Новая игра создана. Загаданное слово: '%s', попыток: %d.",
                secretWord, maxAttempts));
    }

    public String makeMove(String rawGuess)
            throws GameOverException, InvalidWordLengthException, InvalidCharactersException, WordNotFoundInDictionaryException {

        if (isGameOver()) {
            log.println("Попытка хода после завершения игры.");
            throw new GameOverException();
        }

        log.println(String.format("Попытка %d. Введено слово - %s", (currentAttempt + 1), rawGuess));
        String guess = WordleDictionary.normalize(rawGuess);

        if (!WordleDictionary.isRussianWord(guess)) {
            log.println(String.format("Недопустимые символы в '%s'.", guess));
            throw new InvalidCharactersException(guess);
        }
        if (guess.length() != wordLength) {
            log.println(String.format("Ввод '%s' (длина %d вместо %d).", guess, guess.length(), wordLength));
            throw new InvalidWordLengthException(wordLength, guess.length());
        }
        if (!dictionary.contains(guess)) {
            log.println(String.format("Слово '%s' отсутствует в словаре.", guess));
            throw new WordNotFoundInDictionaryException(guess);
        }

        currentAttempt++;
        String symbolsPrompt = WordleDictionary.buildPrompt(guess, secretWord);
        if (guess.equals(secretWord)) {
            isWin = true;
            return symbolsPrompt;
        }

        usedWords.add(guess);
        possibleWords = dictionary.filterCandidates(possibleWords, guess, symbolsPrompt);
        possibleWords.removeAll(usedWords);

        log.println(String.format("Результат: %s. Осталось попыток: %d", symbolsPrompt, getRemainingAttempts()));

        return symbolsPrompt;
    }

    public String getHint() throws EmptyDictionaryException {
        List<String> availableHints = new ArrayList<>();
        for (String candidate : possibleWords) {
            if (!usedWords.contains(candidate)) {
                availableHints.add(candidate);
            }
        }

        if (availableHints.isEmpty()) {
            String message = "В словаре не осталось подходящих слов для подсказки.";
            log.println(message);
            throw new EmptyDictionaryException(message);
        }

        String hint = availableHints.get(new Random().nextInt(availableHints.size()));
        log.println("Выбрано слово-подсказка - " + hint);
        return hint;
    }

    public boolean isGameOver() {
        return isWin || currentAttempt >= maxAttempts;
    }

    public boolean isWin() {
        return isWin;
    }

    public int getRemainingAttempts() {
        return maxAttempts - currentAttempt;
    }

    public String getSecretWord() {
        return secretWord;
    }
}