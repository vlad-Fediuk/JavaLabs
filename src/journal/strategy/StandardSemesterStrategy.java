package journal.strategy;

import java.util.List;

import journal.domain.grade.GradedWork;
import journal.domain.model.Student;

public class StandardSemesterStrategy implements GradingStrategy {
    @Override
    public double calculateFinalScore(Student student) {
        List<GradedWork> works = student.getGradedWorks();
        if (works.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (GradedWork work : works) {
            sum += work.getEffectiveScore();
        }
        return sum / works.size();
    }

    @Override
    public String getStrategyName() {
        return "Стандартна семестрова (лаби зі штрафом -20% за пропуски, ваги робіт)";
    }
}
