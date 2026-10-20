package journal.domain.grade;

import java.util.Objects;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;

public final class DefenseLabGrade extends LabGrade {
    public static final double DEFAULT_DEFENSE_BONUS = 10.0;
    public static final double MIN_DEFENSE_BONUS = 0.0;
    public static final double MAX_DEFENSE_BONUS = 30.0;

    private final double defenseBonus;

    public DefenseLabGrade(double value, Attendance attendance, Subject subject, int labNumber, double defenseBonus)
            throws InvalidGradeException {
        super(value, attendance, subject, labNumber);
        if (defenseBonus < MIN_DEFENSE_BONUS || defenseBonus > MAX_DEFENSE_BONUS) {
            throw new InvalidGradeException(
                    "Бонус за захист має бути від " + MIN_DEFENSE_BONUS + " до " + MAX_DEFENSE_BONUS,
                    defenseBonus);
        }
        this.defenseBonus = defenseBonus;
        System.out.println("  [Конструктор 3-й рівень] DefenseLabGrade: бонус за захист +" + defenseBonus);
    }

    public DefenseLabGrade(double value, Attendance attendance, Subject subject, int labNumber)
            throws InvalidGradeException {
        this(value, attendance, subject, labNumber, DEFAULT_DEFENSE_BONUS);
    }

    public double getDefenseBonus() {
        return defenseBonus;
    }

    @Override
    public double getEffectiveScore() {
        return Math.min(MAX_SCORE, super.getEffectiveScore() + defenseBonus);
    }

    @Override
    public String getWorkType() {
        return "Захист лаб. #" + getLabNumber();
    }

    @Override
    public String toString() {
        return super.toString() + String.format(" | Бонус захисту: +%.1f", defenseBonus);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }
        DefenseLabGrade other = (DefenseLabGrade) obj;
        return Double.compare(this.defenseBonus, other.defenseBonus) == 0;
    }

    @Override
    public boolean equalsUsingInstanceof(Object obj) {
        if (!super.equalsUsingInstanceof(obj)) {
            return false;
        }
        if (!(obj instanceof DefenseLabGrade other)) {
            return false;
        }
        return Double.compare(this.defenseBonus, other.defenseBonus) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), defenseBonus);
    }
}
