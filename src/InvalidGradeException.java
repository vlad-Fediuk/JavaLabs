public class InvalidGradeException extends JournalException {
    private final double invalidScore;

    public InvalidGradeException(String message, double invalidScore) {
        super(message, invalidScore);
        this.invalidScore = invalidScore;
    }

    public double getInvalidScore() {
        return invalidScore;
    }
}
