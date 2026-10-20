package journal.domain.grade;

import java.util.Objects;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;

public abstract sealed class AbstractGrade implements GradedWork
        permits RegularGrade, LabGrade, ExamGrade {
    public static final double MIN_SCORE = 0.0;
    public static final double MAX_SCORE = 100.0;
    public static final double DEFAULT_SCORE = 60.0;

    private final double value;
    private final Attendance attendance;
    private final Subject subject;

    public AbstractGrade(double value, Attendance attendance, Subject subject) throws InvalidGradeException {
        if (value < MIN_SCORE || value > MAX_SCORE) {
            throw new InvalidGradeException("Бал оцінки має бути в діапазоні від " + MIN_SCORE + " до " + MAX_SCORE, value);
        }
        this.value = value;
        this.attendance = attendance;
        this.subject = subject;
        System.out.println("  [Конструктор 1-й рівень] AbstractGrade: базовий бал=" + value + ", предмет=" + subject);
    }

    public AbstractGrade(Attendance attendance, Subject subject) throws InvalidGradeException {
        this(DEFAULT_SCORE, attendance, subject);
    }

    @Override
    public final double getRawValue() {
        return value;
    }

    @Override
    public final Attendance getAttendance() {
        return attendance;
    }

    public final Subject getSubject() {
        return subject;
    }

    public final String getDate() {
        return attendance != null ? attendance.getDate() : "-";
    }

    public final boolean wasPresent() {
        return attendance != null && attendance.isWasPresent();
    }

    @Override
    public abstract double getEffectiveScore();

    @Override
    public abstract boolean isPassed();

    @Override
    public abstract String getWorkType();

    @Override
    public String toString() {
        return String.format("Предмет: %-6s | Тип: %-28s | Дата: %-8s | Базовий: %5.1f | Ефективний: %5.1f | Статус: %s",
                subject, getWorkType(), getDate(), getRawValue(), getEffectiveScore(), getStatusText());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AbstractGrade other = (AbstractGrade) obj;
        return Double.compare(this.value, other.value) == 0
                && Objects.equals(this.attendance, other.attendance)
                && Objects.equals(this.subject, other.subject);
    }

    public boolean equalsUsingInstanceof(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AbstractGrade other)) {
            return false;
        }
        return Double.compare(this.value, other.value) == 0
                && Objects.equals(this.attendance, other.attendance)
                && Objects.equals(this.subject, other.subject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, attendance, subject);
    }
}
