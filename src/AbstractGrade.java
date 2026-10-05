public abstract class AbstractGrade implements GradedWork {
    private final double value;
    private final Attendance attendance;
    private final String subject;

    public AbstractGrade(double value, Attendance attendance, String subject) throws InvalidGradeException {
        if (value < 0.0 || value > 100.0) {
            throw new InvalidGradeException("Бал оцінки має бути в діапазоні від 0 до 100", value);
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

    public String getSubject() {
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
