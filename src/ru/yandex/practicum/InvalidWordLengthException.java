package ru.yandex.practicum;

public class InvalidWordLengthException extends WordleException {
    public InvalidWordLengthException(int expectedLength, int actualLength) {
        super("Неверная длина слова: ожидалось " + expectedLength + " букв, получено " + actualLength + ".");
    }
}
