import java.util.List;

public class AmnestyStrategy implements GradingStrategy {
    @Override
    public double calculateFinalScore(Student student) {
        List<GradedWork> works = student.getGradedWorks();
        if (works.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (GradedWork work : works) {
            sum += work.getRawValue();
        }
        return sum / works.size();
    }

    @Override
    public String getStrategyName() {
        return "Амністія (лояльний режим без штрафів за пропуски)";
    }
}
