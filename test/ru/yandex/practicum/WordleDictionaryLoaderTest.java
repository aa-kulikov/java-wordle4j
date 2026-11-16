package ru.yandex.practicum;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordleDictionaryLoaderTest {

    private static PrintWriter log;
    private static WordleDictionaryLoader loader;

    @BeforeAll
    static void setUpAll() {
        log = new PrintWriter(System.out, true);
        loader = new WordleDictionaryLoader(log);
    }

    @Test
    void shouldLoadValidFile() throws Exception {
        Path tempFile = Files.createTempFile("test_words_", ".txt");
        try {
            Files.write(tempFile, List.of("груша", "лимон", "Тёрка"), StandardCharsets.UTF_8);

            WordleDictionary dictionary = loader.load(tempFile.toString());

            //Проверяем, что из файла успешно загрузились все 3 слова
            assertEquals(3, dictionary.getAllWords().size());

            //Проверяем наличие загруженного слова "груша"
            assertTrue(dictionary.contains("груша"));

            //Проверяем нормализацию буквы 'ё' в 'е' при загрузке слова "Тёрка"
            assertTrue(dictionary.contains("терка"));
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void shouldThrowWhenFileNotFound() throws Exception {
        try {
            //Проверяем вызов исключения при попытке загрузить несуществующий файл
            loader.load("non_existent_file_12345.txt");
            fail("Ожидалось исключение DictionaryFileNotFoundException для несуществующего файла");
        } catch (DictionaryFileNotFoundException expected) {
            //все ок
        }
    }

    @Test
    void shouldThrowWhenPathIsDirectory() throws Exception {
        Path tempDir = Files.createTempDirectory("test_dir_");
        try {
            //Проверяем вызов исключения, если вместо пути к файлу передан путь к директории
            loader.load(tempDir.toString());
            fail("Ожидалось исключение DictionaryFileNotFoundException при передаче пути к папке");
        } catch (DictionaryFileNotFoundException expected) {
            //все ок
        } finally {
            Files.deleteIfExists(tempDir);
        }
    }

    @Test
    void shouldThrowWhenFileIsEmpty() throws Exception {
        Path tempFile = Files.createTempFile("empty_words_", ".txt");
        try {
            Files.write(tempFile, List.of("123", "lemon"), StandardCharsets.UTF_8);
            //Проверяем вызов исключения, если в файле нет ни одного подходящего русского слова
            loader.load(tempFile.toString());
            fail("Ожидалось исключение EmptyDictionaryException для файла без русских слов");
        } catch (EmptyDictionaryException expected) {
            //все ок
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void shouldThrowOnInvalidPath() throws Exception {
        try {
            //Проверяем вызов исключения при передаче путой строки вместо пути к файлу
            loader.load("   ");
            fail("Ожидалось исключение IllegalArgumentException для пустой строки пути");
        } catch (IllegalArgumentException expected) {
            //все ок
        }
        try {
            //Проверяем вызов исключения при передаче null вместо пути к файлу
            loader.load(null);
            fail("Ожидалось исключение IllegalArgumentException при передаче null");
        } catch (IllegalArgumentException expected) {
            //все ок
        }
    }
}