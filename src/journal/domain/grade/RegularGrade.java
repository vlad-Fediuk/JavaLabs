package journal.domain.grade;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;
import journal.exception.JournalException;

public final class RegularGrade extends AbstractGrade {
    public static final double WEIGHT_MULTIPLIER = 1.0;
    public static final double PASSING_THRESHOLD = 60.0;

    public RegularGrade(double value, Attendance attendance, Subject subject)
            throws InvalidGradeException, JournalException {
        super(value, attendance, subject);
        if (!wasPresent()) {
            throw new JournalException("Оцінка за звичайну пару не допускається, якщо студент не був присутній");
        }
        System.out.println("  [Конструктор 2-й рівень] RegularGrade: звичайне аудиторне заняття");
    }

    public RegularGrade(Attendance attendance, Subject subject)
            throws InvalidGradeException, JournalException {
        this(DEFAULT_SCORE, attendance, subject);
    }

    @Override
    public double getEffectiveScore() {
        return getRawValue() * WEIGHT_MULTIPLIER;
    }

    @Override
    public boolean isPassed() {
        return wasPresent() && getRawValue() >= PASSING_THRESHOLD;
    }

    @Override
    public String getWorkType() {
        return "Звичайна пара";
    }

    @Override
    public String toString() {
        return super.toString() + " | Формат: Аудиторна робота";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
