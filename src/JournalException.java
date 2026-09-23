import java.time.LocalDateTime;

public class JournalException extends Exception {
    private final LocalDateTime timestamp;
    private final Object details;

    public JournalException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.details = null;
    }

    public JournalException(String message, Object details) {
        super(message);
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Object getDetails() {
        return details;
    }
}
