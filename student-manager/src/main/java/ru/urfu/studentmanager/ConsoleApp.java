package ru.urfu.studentmanager;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Консольное меню: читает команды пользователя и вызывает нужные действия. */
public final class ConsoleApp {
    private static final double PASSING_GRADE = 60;

    private final Scanner scanner;
    private final StudentService service = new StudentService();

    public ConsoleApp(Scanner scanner) {
        this.scanner = scanner;
    }

    public void run() {
        try {
            while (true) {
                printMenu();
                switch (readLine("Выберите пункт меню: ")) {
                    case "1" -> addStudentManually();
                    case "2" -> loadStudentsFromFile();
                    case "3" -> printProcessedStudents();
                    case "4" -> printAllStudents();
                    case "0" -> {
                        System.out.println("Работа программы завершена.");
                        return;
                    }
                    default -> System.out.println("Ошибка: выберите пункт меню от 0 до 4.");
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println();
            System.out.println("Ввод завершён, программа закрыта.");
        }
    }

    private void printMenu() {
        System.out.println("""

                ===== МЕНЮ =====
                1. Добавить студента вручную
                2. Загрузить студентов из CSV-файла
                3. Обработать данные (балл от 60, по возрастанию)
                4. Показать всех студентов
                0. Выход""");
    }

    private void addStudentManually() {
        String fullName = readLine("ФИО: ");
        String grade = readLine("Средний балл (0–100): ");
        String recordBookNumber = readLine("Номер зачётной книжки: ");

        try {
            service.add(new Student(fullName, StudentService.parseGrade(grade), recordBookNumber));
            System.out.println("Студент добавлен.");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void loadStudentsFromFile() {
        String fileName = readLine("Путь к CSV-файлу (например, data/students.csv): ");

        try {
            StudentService.LoadResult result = service.loadFromCsv(Path.of(fileName));
            System.out.println("Загружено студентов: " + result.loadedCount());
            if (!result.errors().isEmpty()) {
                System.out.println("Пропущено строк с ошибками: " + result.errors().size());
                result.errors().forEach(error -> System.out.println("  - " + error));
            }
        } catch (NoSuchFileException | InvalidPathException e) {
            System.out.println("Ошибка: файл «" + fileName + "» не найден или путь указан неверно.");
        } catch (CharacterCodingException e) {
            System.out.println("Ошибка: файл должен быть сохранён в кодировке UTF-8.");
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

    private void printProcessedStudents() {
        List<String> results = service.findStudentsWithGradeAtLeast(PASSING_GRADE);
        if (results.isEmpty()) {
            System.out.println("Нет студентов со средним баллом от 60.");
            return;
        }
        System.out.println("Студенты со средним баллом от 60 (по возрастанию балла):");
        results.forEach(System.out::println);
    }

    private void printAllStudents() {
        List<Student> students = service.getStudents();
        if (students.isEmpty()) {
            System.out.println("Список студентов пуст.");
            return;
        }
        System.out.println("Всего студентов: " + students.size());
        students.forEach(System.out::println);
    }

    private String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
