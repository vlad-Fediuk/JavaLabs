package journal.exception;

public class InvalidGradeException extends JournalException {
    private static final long serialVersionUID = 1L;
    private final double invalidScore;

    public InvalidGradeException(String message, double invalidScore) {
        super(message, invalidScore);
        this.invalidScore = invalidScore;
    }

    public double getInvalidScore() {
        return invalidScore;
    }
}
