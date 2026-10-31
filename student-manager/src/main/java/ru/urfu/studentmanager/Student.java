package ru.urfu.studentmanager;

/** Студент: ФИО, средний балл и номер зачётной книжки. Значения проверяются при создании. */
public record Student(String fullName, double averageGrade, String recordBookNumber) {
    public static final double MIN_GRADE = 0;
    public static final double MAX_GRADE = 100;

    public Student {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("ФИО не должно быть пустым.");
        }
        if (!Double.isFinite(averageGrade) || averageGrade < MIN_GRADE || averageGrade > MAX_GRADE) {
            throw new IllegalArgumentException("средний балл должен быть числом от 0 до 100.");
        }
        if (recordBookNumber == null || recordBookNumber.isBlank()) {
            throw new IllegalArgumentException("номер зачётной книжки не должен быть пустым.");
        }

        fullName = fullName.trim();
        recordBookNumber = recordBookNumber.trim();
    }

    @Override
    public String toString() {
        return String.format("%-28s | балл: %6.2f | зачётка: %s", fullName, averageGrade, recordBookNumber);
    }
}
