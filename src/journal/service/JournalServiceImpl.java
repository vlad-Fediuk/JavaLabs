package journal.service;

import java.util.List;

import journal.domain.model.Student;
import journal.repository.Journal;
import journal.strategy.GradingStrategy;

public class JournalServiceImpl implements JournalService {
    private static final double PASSING_THRESHOLD = 60.0;
    private final Journal journal;

    public JournalServiceImpl(Journal journal) {
        this.journal = journal;
    }

    @Override
    public String getActiveStrategyName() {
        return journal.getGradingStrategy().getStrategyName();
    }

    @Override
    public void changeStrategy(GradingStrategy strategy) {
        if (strategy != null) {
            journal.setGradingStrategy(strategy);
        }
    }

    @Override
    public double calculateStudentScore(Student student) {
        return journal.calculateStudentScore(student);
    }

    @Override
    public List<Student> getStudentsSortedByActiveStrategy() {
        journal.bubbleSortByStrategyScore();
        return journal.getStudents();
    }

    @Override
    public JournalSummary getPassingSummary() {
        int passedCount = journal.countPassedStudents(PASSING_THRESHOLD);
        return new JournalSummary(passedCount, journal.size(), PASSING_THRESHOLD);
    }
}
