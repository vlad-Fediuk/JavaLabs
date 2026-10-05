public class ExamGrade extends AbstractGrade {
    private final int ticketNumber;

    public ExamGrade(double value, Attendance attendance, String subject, int ticketNumber)
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
        return getRawValue() * 2.0;
    }

    @Override
    public boolean isPassed() {
        return wasPresent() && getRawValue() >= 60.0;
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
