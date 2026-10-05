import java.util.List;

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
        return "Стандартна семестрова (з урахуванням штрафів за пропуски)";
    }
}
