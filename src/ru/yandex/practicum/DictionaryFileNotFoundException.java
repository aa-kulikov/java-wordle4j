package ru.yandex.practicum;

public class DictionaryFileNotFoundException extends WordleException {
    public DictionaryFileNotFoundException(String filePath) {
        super("Файл словаря не найден по пути: " + filePath);
    }
}