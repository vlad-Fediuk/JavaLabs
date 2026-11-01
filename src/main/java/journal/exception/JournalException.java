package journal.exception;

public class JournalException extends Exception {
    private static final long serialVersionUID = 1L;
    private final Object details;

    public JournalException(String message) {
        this(message, null);
    }

    public JournalException(String message, Object details) {
        super(message);
        this.details = details;
    }

    public Object getDetails() {
        return details;
    }
}
