package journal.factory;

import journal.domain.grade.ExamGrade;
import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.JournalException;

public class ExamGradeCreator extends GradedWorkCreator {
    private final int ticketNumber;

    public ExamGradeCreator(int ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    @Override
    public ExamGrade createGrade(double value, Attendance attendance, Subject subject) throws JournalException {
        return new ExamGrade(value, attendance, subject, ticketNumber);
    }
}
