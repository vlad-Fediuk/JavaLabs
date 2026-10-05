import java.util.ArrayList;
import java.util.List;

public class ExamPriorityStrategy implements GradingStrategy {
    @Override
    public double calculateFinalScore(Student student) {
        List<GradedWork> works = student.getGradedWorks();
        if (works.isEmpty()) {
            return 0.0;
        }

        ExamGrade exam = null;
        List<GradedWork> regularWorks = new ArrayList<>();

        for (GradedWork work : works) {
            if (work instanceof ExamGrade eg) {
                exam = eg;
            } else {
                regularWorks.add(work);
            }
        }

        if (exam == null || !exam.isPassed()) {
            return 0.0;
        }

        double examScore = exam.getRawValue();
        double regularScore = 0.0;

        if (!regularWorks.isEmpty()) {
            double sum = 0.0;
            for (GradedWork w : regularWorks) {
                sum += w.getEffectiveScore();
            }
            regularScore = sum / regularWorks.size();
        } else {
            regularScore = examScore;
        }

        return (examScore * 0.7) + (regularScore * 0.3);
    }

    @Override
    public String getStrategyName() {
        return "Екзаменаційний пріоритет (іспит 70%, лаби 30%, обов'язковий іспит)";
    }
}
