package journal.strategy;

import java.util.ArrayList;
import java.util.List;

import journal.domain.grade.ExamGrade;
import journal.domain.grade.GradedWork;
import journal.domain.model.Student;

public class ExamPriorityStrategy implements GradingStrategy {
    public static final double EXAM_WEIGHT = 0.7;
    public static final double REGULAR_WEIGHT = 0.3;

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
        double regularScore;

        if (!regularWorks.isEmpty()) {
            double sum = 0.0;
            for (GradedWork w : regularWorks) {
                sum += w.getEffectiveScore();
            }
            regularScore = sum / regularWorks.size();
        } else {
            regularScore = examScore;
        }

        return (examScore * EXAM_WEIGHT) + (regularScore * REGULAR_WEIGHT);
    }

    @Override
    public String getStrategyName() {
        return "Екзаменаційний пріоритет (іспит 70%, лаби 30%, обов'язковий іспит)";
    }
}
