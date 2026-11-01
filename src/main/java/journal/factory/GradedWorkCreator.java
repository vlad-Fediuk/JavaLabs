package journal.factory;

import journal.domain.grade.GradedWork;
import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.JournalException;

public abstract class GradedWorkCreator {
    public abstract GradedWork createGrade(double value, Attendance attendance, Subject subject) throws JournalException;
}
