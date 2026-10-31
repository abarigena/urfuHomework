package ru.urfu.studentmanager;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;

/** Console entry point for the student manager. */
public final class Main {
    private static final double PASSING_GRADE = 60.0;

    private Main() {
    }

    public static void main(String[] args) {
        StudentService service = new StudentService();

        try (Scanner scanner = new Scanner(System.in)) {
            while (true) {
                printMenu();
                if (!scanner.hasNextLine()) {
                    System.out.println("Ввод завершён. Программа закрыта.");
                    return;
                }

                String command = scanner.nextLine().trim();
                switch (command) {
                    case "1" -> {
                        Student student = readStudent(scanner);
                        if (student == null) {
                            return;
                        }
                        service.add(student);
                        System.out.println("Студент добавлен.");
                    }
                    case "2" -> {
                        System.out.print("Путь к CSV-файлу: ");
                        if (!scanner.hasNextLine()) {
                            System.out.println("Ввод завершён. Программа закрыта.");
                            return;
                        }
                        loadStudents(service, scanner.nextLine().trim());
                    }
                    case "3" -> printProcessedStudents(service);
                    case "4" -> printAllStudents(service.getStudents());
                    case "0" -> {
                        System.out.println("Работа программы завершена.");
                        return;
                    }
                    default -> System.out.println("Ошибка: выберите пункт меню от 0 до 4.");
                }
            }
        }
    }

    private static void printMenu() {
        System.out.println("""

                ===== МЕНЮ =====
                1. Добавить студента вручную
                2. Загрузить студентов из CSV
                3. Обработать данные (средний балл от 60, сортировка по возрастанию)
                4. Показать всех студентов
                0. Выход""");
        System.out.print("Выберите пункт меню: ");
    }

    private static Student readStudent(Scanner scanner) {
        while (true) {
            System.out.print("ФИО: ");
            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершён. Программа закрыта.");
                return null;
            }
            String fullName = scanner.nextLine();

            System.out.print("Средний балл (0–100): ");
            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершён. Программа закрыта.");
                return null;
            }
            String gradeInput = scanner.nextLine().trim();

            System.out.print("Номер зачётной книжки: ");
            if (!scanner.hasNextLine()) {
                System.out.println("Ввод завершён. Программа закрыта.");
                return null;
            }
            String recordBookNumber = scanner.nextLine();

            try {
                double grade = Double.parseDouble(gradeInput);
                return new Student(fullName, grade, recordBookNumber);
            } catch (NumberFormatException exception) {
                System.out.println("Ошибка: средний балл должен быть числом. Повторите ввод.");
            } catch (IllegalArgumentException exception) {
                System.out.println("Ошибка: " + exception.getMessage() + " Повторите ввод.");
            }
        }
    }

    private static void loadStudents(StudentService service, String fileName) {
        try {
            StudentService.LoadResult result = service.loadFromCsv(Path.of(fileName));
            System.out.println("Загружено студентов: " + result.loadedCount());
            result.errors().forEach(error -> System.out.println("Ошибка: " + error));
        } catch (IOException | RuntimeException exception) {
            System.out.println("Не удалось прочитать файл: " + exception.getMessage());
        }
    }

    private static void printProcessedStudents(StudentService service) {
        List<String> results = service.processStudents(PASSING_GRADE);
        if (results.isEmpty()) {
            System.out.println("Нет студентов со средним баллом от 60.");
            return;
        }

        System.out.println("Студенты со средним баллом от 60, по возрастанию:");
        results.forEach(System.out::println);
    }

    private static void printAllStudents(List<Student> students) {
        if (students.isEmpty()) {
            System.out.println("Список студентов пуст.");
            return;
        }

        students.forEach(System.out::println);
    }
}
