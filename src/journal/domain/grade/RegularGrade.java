package journal.domain.grade;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;
import journal.exception.JournalException;

public class RegularGrade extends AbstractGrade {
    public static final double WEIGHT_MULTIPLIER = 1.0;
    public static final double PASSING_THRESHOLD = 60.0;

    public RegularGrade(double value, Attendance attendance, Subject subject)
            throws InvalidGradeException, JournalException {
        super(value, attendance, subject);
        if (!wasPresent()) {
            throw new JournalException("Оцінка за звичайну пару не допускається, якщо студент не був присутній");
        }
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
}
