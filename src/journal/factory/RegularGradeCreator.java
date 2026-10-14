package journal.factory;

import journal.domain.grade.RegularGrade;
import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.JournalException;

public class RegularGradeCreator extends GradedWorkCreator {
    @Override
    public RegularGrade createGrade(double value, Attendance attendance, Subject subject) throws JournalException {
        return new RegularGrade(value, attendance, subject);
    }
}
