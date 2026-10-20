package journal.domain.model;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Attendance other = (Attendance) obj;
        return this.wasPresent == other.wasPresent
                && Objects.equals(this.subject, other.subject)
                && Objects.equals(this.date, other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(wasPresent, subject, date);
    }
}
