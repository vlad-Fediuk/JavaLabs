package journal.exception;

public class StudentNotFoundException extends JournalException {
    private static final long serialVersionUID = 1L;
    private final String searchTarget;

    public StudentNotFoundException(String message, String searchTarget) {
        super(message, searchTarget);
        this.searchTarget = searchTarget;
    }

    public String getSearchTarget() {
        return searchTarget;
    }
}
