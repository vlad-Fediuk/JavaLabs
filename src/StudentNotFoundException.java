public class StudentNotFoundException extends JournalException {
    private final String searchTarget;

    public StudentNotFoundException(String message, String searchTarget) {
        super(message, searchTarget);
        this.searchTarget = searchTarget;
    }

    public String getSearchTarget() {
        return searchTarget;
    }
}
