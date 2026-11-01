package journal.factory;

import journal.domain.grade.LabGrade;
import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.JournalException;

public class LabGradeCreator extends GradedWorkCreator {
    private final int labNumber;

    public LabGradeCreator(int labNumber) {
        this.labNumber = labNumber;
    }

    @Override
    public LabGrade createGrade(double value, Attendance attendance, Subject subject) throws JournalException {
        return new LabGrade(value, attendance, subject, labNumber);
    }
}
