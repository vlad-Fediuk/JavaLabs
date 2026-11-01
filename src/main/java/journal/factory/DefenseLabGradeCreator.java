package journal.factory;

import journal.domain.grade.DefenseLabGrade;
import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.JournalException;

public class DefenseLabGradeCreator extends GradedWorkCreator {
    private final int labNumber;
    private final double defenseBonus;

    public DefenseLabGradeCreator(int labNumber, double defenseBonus) {
        this.labNumber = labNumber;
        this.defenseBonus = defenseBonus;
    }

    public DefenseLabGradeCreator(int labNumber) {
        this(labNumber, DefenseLabGrade.DEFAULT_DEFENSE_BONUS);
    }

    @Override
    public DefenseLabGrade createGrade(double value, Attendance attendance, Subject subject) throws JournalException {
        return new DefenseLabGrade(value, attendance, subject, labNumber, defenseBonus);
    }
}
