package journal.domain.grade;

import java.util.Objects;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;

public sealed class LabGrade extends AbstractGrade permits DefenseLabGrade {
    public static final double ABSENCE_PENALTY_COEFFICIENT = 0.8;
    public static final double STANDARD_COEFFICIENT = 1.0;
    public static final double PASSING_THRESHOLD = 40.0;
    public static final int DEFAULT_LAB_NUMBER = 1;

    private final int labNumber;

    public LabGrade(long id, double value, Attendance attendance, Subject subject, int labNumber)
            throws InvalidGradeException {
        super(id, value, attendance, subject);
        this.labNumber = labNumber;
        System.out.println("  [Конструктор 2-й рівень] LabGrade: лабораторна робота №" + labNumber);
    }

    public LabGrade(double value, Attendance attendance, Subject subject, int labNumber)
            throws InvalidGradeException {
        super(value, attendance, subject);
        this.labNumber = labNumber;
        System.out.println("  [Конструктор 2-й рівень] LabGrade: лабораторна робота №" + labNumber);
    }

    public LabGrade(double value, Attendance attendance, Subject subject)
            throws InvalidGradeException {
        this(value, attendance, subject, DEFAULT_LAB_NUMBER);
    }

    public final int getLabNumber() {
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

    @Override
    public String toString() {
        return super.toString() + " | Лаб №" + labNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        LabGrade other = (LabGrade) obj;
        return this.labNumber == other.labNumber;
    }

    @Override
    public boolean equalsUsingInstanceof(Object obj) {
        if (!super.equalsUsingInstanceof(obj)) {
            return false;
        }
        if (!(obj instanceof LabGrade other)) {
            return false;
        }
        return this.labNumber == other.labNumber;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), labNumber);
    }
}
