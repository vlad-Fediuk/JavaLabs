package journal.domain.model;

public class Attendance {
    private final boolean wasPresent;
    private final Subject subject;
    private final String date;

    public Attendance(boolean wasPresent, Subject subject, String date) {
        this.wasPresent = wasPresent;
        this.subject = subject;
        this.date = date;
    }

    public boolean isWasPresent() {
        return wasPresent;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Дата: " + date + " | Предмет: " + subject + " | Присутність: " + (wasPresent ? "Був" : "Не був");
    }
}
