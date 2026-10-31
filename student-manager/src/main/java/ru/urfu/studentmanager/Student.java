package ru.urfu.studentmanager;

/** Describes a student and validates the values used by the application. */
public final class Student {
    private final String fullName;
    private final double averageGrade;
    private final String recordBookNumber;

    public Student(String fullName, double averageGrade, String recordBookNumber) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Имя студента не должно быть пустым.");
        }
        if (!Double.isFinite(averageGrade) || averageGrade < 0 || averageGrade > 100) {
            throw new IllegalArgumentException("Средний балл должен быть от 0 до 100.");
        }
        if (recordBookNumber == null || recordBookNumber.isBlank()) {
            throw new IllegalArgumentException("Номер зачётной книжки не должен быть пустым.");
        }

        this.fullName = fullName.trim();
        this.averageGrade = averageGrade;
        this.recordBookNumber = recordBookNumber.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public double getAverageGrade() {
        return averageGrade;
    }

    public String getRecordBookNumber() {
        return recordBookNumber;
    }

    @Override
    public String toString() {
        return String.format("%s | средний балл: %.2f | зачётная книжка: %s",
                fullName, averageGrade, recordBookNumber);
    }
}
