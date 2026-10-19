package journal.domain.grade;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;

public class LabGrade extends AbstractGrade {
    public static final double ABSENCE_PENALTY_COEFFICIENT = 0.8;
    public static final double STANDARD_COEFFICIENT = 1.0;
    public static final double PASSING_THRESHOLD = 40.0;

    private final int labNumber;

    public LabGrade(double value, Attendance attendance, Subject subject, int labNumber)
            throws InvalidGradeException {
        super(value, attendance, subject);
        this.labNumber = labNumber;
    }

    public int getLabNumber() {
        return labNumber;
    }

    @Override
    public double getEffectiveScore() {
        if (!wasPresent()) {
            return getRawValue() * ABSENCE_PENALTY_COEFFICIENT;
        }
        return getRawValue() * STANDARD_COEFFICIENT;
    }

    @Override
    public boolean isPassed() {
        return getEffectiveScore() >= PASSING_THRESHOLD;
    }

    @Override
    public String getWorkType() {
        return "Лабораторна #" + labNumber;
    }
}
