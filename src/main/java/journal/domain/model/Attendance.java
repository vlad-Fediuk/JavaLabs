package journal.domain.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class Attendance extends BaseEntity {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(5000);
    private final boolean wasPresent;
    private final Subject subject;
    private final String date;

    public Attendance(long id, boolean wasPresent, Subject subject, String date) {
        super(id);
        this.wasPresent = wasPresent;
        this.subject = subject;
        this.date = date;
    }

    public Attendance(boolean wasPresent, Subject subject, String date) {
        this(ID_GENERATOR.getAndIncrement(), wasPresent, subject, date);
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
        return "Дата: " + date + " | Предмет: " + (subject != null ? subject.getName() : "N/A") + " | Присутність: " + (wasPresent ? "Був" : "Не був");
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
