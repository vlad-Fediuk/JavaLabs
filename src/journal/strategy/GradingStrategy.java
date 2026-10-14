package journal.strategy;

import journal.domain.model.Student;

public interface GradingStrategy {
    double calculateFinalScore(Student student);

    String getStrategyName();
}
