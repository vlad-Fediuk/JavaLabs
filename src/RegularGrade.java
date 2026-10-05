public class RegularGrade extends AbstractGrade {
    public RegularGrade(double value, Attendance attendance, String subject)
            throws InvalidGradeException, JournalException {
        super(value, attendance, subject);
        if (!wasPresent()) {
            throw new JournalException("Оцінку за звичайну пару не можна виставити, якщо студент був відсутній");
        }
    }

    @Override
    public double getEffectiveScore() {
        return getRawValue() * 1.0;
    }

    @Override
    public boolean isPassed() {
        return wasPresent() && getRawValue() >= 60.0;
    }

    @Override
    public String getWorkType() {
        return "Звичайна пара";
    }
}
