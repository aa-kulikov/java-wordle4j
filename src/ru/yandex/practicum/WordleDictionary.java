package ru.yandex.practicum;

import java.util.List;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

public class WordleDictionary {

    private List<String> words;
    private final Random random = new Random();
    private final PrintWriter log;

    public WordleDictionary(List<String> rawWords, PrintWriter log) throws EmptyDictionaryException {
        if (rawWords == null) {
            throw new IllegalArgumentException("Список слов не передан");
        }
        if (log == null) {
            throw new IllegalArgumentException("Объект лога не передан");
        }
        this.log = log;

        Set<String> uniqueWords = new LinkedHashSet<>();
        int skipped = 0;

        for (String word : rawWords) {
            String normalized = normalize(word);
            if (isRussianWord(normalized)) {
                if (!uniqueWords.add(normalized)) {
                    skipped++;
                }
            } else {
                skipped++;
            }
        }
        this.words = new ArrayList<>(uniqueWords);

        if (this.words.isEmpty()) {
            String message = "В словаре не передано ни одного подходящего русского слова.";
            log.println(message);
            throw new EmptyDictionaryException(message);
        }

        log.println(String.format("Загружено %d уникальных русских слов. Отброшено слов: %d.",
                this.words.size(), skipped));
    }

    public WordleDictionary filterByLength(int wordLength) {
        if (wordLength <= 0) {
            throw new IllegalArgumentException("Длина слова должна быть больше нуля");
        }

        List<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            if (word.length() == wordLength) {
                filteredWords.add(word);
            }
        }

        this.words = filteredWords;
        log.println(String.format("Отфильтрован словарь по длине в %d букв: осталось %d слов.",
                wordLength, this.words.size()));
        return this;
    }

    public static String normalize(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Не передано слово для преобразования.");
        }
        return word.trim().toLowerCase().replace('ё', 'е');
    }

    public static boolean isRussianWord(String word) {
        String normalized = normalize(word);
        return !normalized.isEmpty() && normalized.matches("[а-я]+");
    }

    public boolean contains(String word) {
        return words.contains(normalize(word));
    }

    public String getRandomWord() throws EmptyDictionaryException {
        if (words.isEmpty()) {
            String message = "Словарь пуст! Невозможно подобрать слово.";
            log.println(message);
            throw new EmptyDictionaryException(message);
        }
        String chosen = words.get(random.nextInt(words.size()));
        log.println("Выбрано случайное слово - " + chosen);
        return chosen;
    }

    public List<String> getAllWords() {
        return Collections.unmodifiableList(words);
    }
}
