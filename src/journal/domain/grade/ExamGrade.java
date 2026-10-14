package journal.domain.grade;

import journal.domain.model.Attendance;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;
import journal.exception.JournalException;

public class ExamGrade extends AbstractGrade {
    public static final double WEIGHT_MULTIPLIER = 2.0;
    public static final double PASSING_THRESHOLD = 60.0;

    private final int ticketNumber;

    public ExamGrade(double value, Attendance attendance, Subject subject, int ticketNumber)
            throws InvalidGradeException, JournalException {
        super(value, attendance, subject);
        if (!wasPresent()) {
            throw new JournalException("Не можна виставити оцінку за іспит, якщо студент не з'явився");
        }
        this.ticketNumber = ticketNumber;
    }

    public int getTicketNumber() {
        return ticketNumber;
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
    public String getStatusText() {
        return isPassed() ? "Зараховано" : "Незалік: академічна заборгованість!";
    }

    @Override
    public String getWorkType() {
        return "Іспит (білет #" + ticketNumber + ")";
    }
}
