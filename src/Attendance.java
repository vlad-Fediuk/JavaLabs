public class Attendance {
    private boolean wasPresent;
    private String subject;
    private String date;

    public Attendance(boolean wasPresent, String subject, String date) {
        this.wasPresent = wasPresent;
        this.subject = subject;
        this.date = date;
    }

    public boolean isWasPresent() {
        return wasPresent;
    }

    public String getSubject() {
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
