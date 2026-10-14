package journal.domain.grade;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;

public abstract class AbstractGrade implements GradedWork {
    public static final double MIN_SCORE = 0.0;
    public static final double MAX_SCORE = 100.0;

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
    }

    @Override
    public double getRawValue() {
        return value;
    }

    @Override
    public Attendance getAttendance() {
        return attendance;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getDate() {
        return attendance != null ? attendance.getDate() : "-";
    }

    public boolean wasPresent() {
        return attendance != null && attendance.isWasPresent();
    }

    @Override
    public String toString() {
        return String.format("Предмет: %-6s | Тип: %-18s | Дата: %-8s | Базовий: %5.1f | Ефективний: %5.1f | Статус: %s",
                subject, getWorkType(), getDate(), getRawValue(), getEffectiveScore(), getStatusText());
    }
}
