package ru.urfu.studentmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Хранит студентов, загружает их из CSV и обрабатывает список через Stream API. */
public final class StudentService {
    private static final String CSV_HEADER = "fullName,averageGrade,recordBookNumber";
    private static final String CSV_SEPARATOR = ",";
    private static final int CSV_FIELDS = 3;

    private final List<Student> students = new ArrayList<>();

    public void add(Student student) {
        boolean duplicate = students.stream()
                .anyMatch(existing -> existing.recordBookNumber().equalsIgnoreCase(student.recordBookNumber()));
        if (duplicate) {
            throw new IllegalArgumentException(
                    "студент с зачётной книжкой " + student.recordBookNumber() + " уже есть в списке.");
        }
        students.add(student);
    }

    public List<Student> getStudents() {
        return List.copyOf(students);
    }

    /** Загружает корректные строки файла; ошибки в остальных строках собираются в отчёт. */
    public LoadResult loadFromCsv(Path path) throws IOException {
        List<String> errors = new ArrayList<>();
        int loaded = 0;

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.replace("﻿", "").trim();
                if (line.isEmpty() || (lineNumber == 1 && line.equalsIgnoreCase(CSV_HEADER))) {
                    continue;
                }

                try {
                    add(parseLine(line));
                    loaded++;
                } catch (IllegalArgumentException e) {
                    errors.add("строка " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        return new LoadResult(loaded, errors);
    }

    /** Оставляет студентов с баллом не ниже порога, сортирует по баллу и возвращает строки для вывода. */
    public List<String> findStudentsWithGradeAtLeast(double minGrade) {
        return students.stream()
                .filter(student -> student.averageGrade() >= minGrade)
                .sorted(Comparator.comparingDouble(Student::averageGrade))
                .map(student -> String.format("%-28s | балл: %6.2f", student.fullName(), student.averageGrade()))
                .toList();
    }

    private static Student parseLine(String line) {
        String[] fields = line.split(CSV_SEPARATOR, -1);
        if (fields.length != CSV_FIELDS) {
            throw new IllegalArgumentException("ожидалось 3 поля через запятую, найдено " + fields.length + ".");
        }
        return new Student(fields[0], parseGrade(fields[1]), fields[2]);
    }

    /** Разбирает средний балл; дробную часть можно отделять точкой или запятой. */
    static double parseGrade(String text) {
        String value = text.trim();
        if (value.isEmpty()) {
            throw new IllegalArgumentException("средний балл не указан.");
        }
        try {
            return Double.parseDouble(value.replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("средний балл «" + value + "» не является числом.");
        }
    }

    public record LoadResult(int loadedCount, List<String> errors) {
        public LoadResult {
            errors = List.copyOf(errors);
        }
    }
}
