package ru.yandex.practicum;

public class InvalidCharactersException extends WordleException {
    public InvalidCharactersException(String word) {
        super("Слово «" + word + "» содержит недопустимые символы. Разрешены только русские буквы.");
    }
}