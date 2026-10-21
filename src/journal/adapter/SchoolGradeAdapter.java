package journal.adapter;

import java.util.concurrent.atomic.AtomicLong;

import journal.adapter.external.SchoolAssessment;
import journal.domain.grade.GradedWork;
import journal.domain.model.Attendance;
import journal.domain.model.BaseEntity;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;

public class SchoolGradeAdapter extends BaseEntity implements GradedWork {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(9000);
    public static final double MIN_GRADE_12 = 0.0;
    public static final double MAX_GRADE_12 = 12.0;
    public static final double MAX_PERCENT_SCORE = 100.0;
    public static final double PASSING_SCORE = 60.0;

    private final SchoolAssessment assessment;
    private final Attendance attendance;

    public SchoolGradeAdapter(long id, SchoolAssessment assessment, String date) throws InvalidGradeException {
        super(id);
        int grade12 = assessment.getGrade12();
        if (grade12 < MIN_GRADE_12 || grade12 > MAX_GRADE_12) {
            throw new InvalidGradeException(
                    "Оцінка за 12-бальною шкалою має бути від " + (int) MIN_GRADE_12 + " до " + (int) MAX_GRADE_12,
                    grade12);
        }
        this.assessment = assessment;
        this.attendance = new Attendance(assessment.wasPresent(), new Subject(assessment.getDisciplineCode()), date);
    }

    public SchoolGradeAdapter(SchoolAssessment assessment, String date) throws InvalidGradeException {
        this(ID_GENERATOR.getAndIncrement(), assessment, date);
    }

    @Override
    public double getRawValue() {
        return (assessment.getGrade12() / MAX_GRADE_12) * MAX_PERCENT_SCORE;
    }

    @Override
    public double getEffectiveScore() {
        return getRawValue();
    }

    @Override
    public boolean isPassed() {
        return attendance.isWasPresent() && getRawValue() >= PASSING_SCORE;
    }

    @Override
    public String getWorkType() {
        return "Шкільна оцінка (12 балів: " + assessment.getGrade12() + ")";
    }

    @Override
    public Attendance getAttendance() {
        return attendance;
    }

    public String getDate() {
        return attendance.getDate();
    }

    @Override
    public String getStatusText() {
        return isPassed() ? "Зараховано" : "Незалік";
    }

    @Override
    public String toString() {
        return String.format("Предмет: %-6s | Тип: %-28s | Дата: %-8s | Базовий: %5.1f | Ефективний: %5.1f | Статус: %s",
                attendance.getSubject(), getWorkType(), attendance.getDate(), getRawValue(), getEffectiveScore(),
                getStatusText());
    }
}
