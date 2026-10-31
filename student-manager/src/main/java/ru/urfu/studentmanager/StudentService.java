package ru.urfu.studentmanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** Stores students and provides file import and stream-based processing. */
public final class StudentService {
    private final List<Student> students = new ArrayList<>();

    public void add(Student student) {
        if (student == null) {
            throw new IllegalArgumentException("Студент не должен быть null.");
        }
        students.add(student);
    }

    public List<Student> getStudents() {
        return List.copyOf(students);
    }

    /** Loads valid rows from CSV and reports malformed rows without stopping the import. */
    public LoadResult loadFromCsv(Path path) throws IOException {
        List<String> errors = new ArrayList<>();
        int loadedCount = 0;
        int lineNumber = 0;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) {
                    continue;
                }
                if (lineNumber == 1 && line.equalsIgnoreCase("fullName,averageGrade,recordBookNumber")) {
                    continue;
                }

                String[] fields = line.split(",", -1);
                if (fields.length != 3) {
                    errors.add("Строка " + lineNumber + ": ожидалось 3 поля.");
                    continue;
                }

                try {
                    Student student = new Student(
                            fields[0].trim(),
                            Double.parseDouble(fields[1].trim()),
                            fields[2].trim());
                    add(student);
                    loadedCount++;
                } catch (IllegalArgumentException exception) {
                    errors.add("Строка " + lineNumber + ": " + exception.getMessage());
                }
            }
        }

        return new LoadResult(loadedCount, List.copyOf(errors));
    }

    /** Filters by minimum grade, maps each student to text, and sorts by grade ascending. */
    public List<String> processStudents(double minimumGrade) {
        return students.stream()
                .filter(student -> student.getAverageGrade() >= minimumGrade)
                .map(student -> new StudentSummary(student.getFullName(),
                        student.getAverageGrade(), student.getRecordBookNumber()))
                .sorted((left, right) -> Double.compare(left.averageGrade(), right.averageGrade()))
                .map(summary -> String.format("%-24s | балл: %6.2f | №: %s",
                        summary.fullName(), summary.averageGrade(), summary.recordBookNumber()))
                .collect(Collectors.toList());
    }

    public record LoadResult(int loadedCount, List<String> errors) {
    }

    private record StudentSummary(String fullName, double averageGrade, String recordBookNumber) {
    }
}
