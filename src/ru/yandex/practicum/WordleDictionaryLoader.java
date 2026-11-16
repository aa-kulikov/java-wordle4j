package ru.yandex.practicum;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class WordleDictionaryLoader {
    private final PrintWriter log;

    public WordleDictionaryLoader(PrintWriter log) {
        if (log == null) {
            throw new IllegalArgumentException("Объект лога не передан");
        }
        this.log = log;
    }

    public WordleDictionary load(String filePath)  throws DictionaryFileNotFoundException, EmptyDictionaryException,
            IOException {

        if (filePath == null || filePath.isBlank()) {
            throw new IllegalArgumentException("Путь к файлу словаря не передан");
        }

        Path path = Path.of(filePath);
        log.println("Попытка загрузки словаря из '" + path.toAbsolutePath() + "'");

        if (!Files.exists(path) || Files.isDirectory(path)) {
            log.println("Файл не найден по пути " + path.toAbsolutePath());
            throw new DictionaryFileNotFoundException(path.toAbsolutePath().toString());
        }

        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }

        return new WordleDictionary(lines, log);
    }
}