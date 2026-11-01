package journal.service;

import java.util.List;

import journal.domain.model.Student;
import journal.strategy.GradingStrategy;

public interface JournalService {
    String getActiveStrategyName();

    void changeStrategy(GradingStrategy strategy);

    double calculateStudentScore(Student student);

    List<Student> getStudentsSortedByActiveStrategy();

    JournalSummary getPassingSummary();

    record JournalSummary(int passedCount, int totalCount, double threshold) {}
}
