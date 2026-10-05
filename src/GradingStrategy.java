public interface GradingStrategy {
    double calculateFinalScore(Student student);

    String getStrategyName();
}
