package journal.domain.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import journal.adapter.SchoolGradeAdapter;
import journal.adapter.external.SchoolAssessment;
import journal.domain.grade.DefenseLabGrade;
import journal.domain.grade.ExamGrade;
import journal.domain.grade.GradedWork;
import journal.domain.grade.LabGrade;
import journal.domain.grade.RegularGrade;

public class GradeRecord extends BaseEntity {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1000);
    private final long studentId;
    private final String subjectName;
    private final String workType;
    private final double score;
    private final String workDate;
    private final boolean wasPresent;
    private final double extraInfo;

    public GradeRecord(long id, long studentId, String subjectName, String workType, double score, String workDate, boolean wasPresent, double extraInfo) {
        super(id);
        this.studentId = studentId;
        this.subjectName = Objects.requireNonNull(subjectName, "Subject name must not be null");
        this.workType = Objects.requireNonNull(workType, "Work type must not be null");
        this.score = score;
        this.workDate = Objects.requireNonNull(workDate, "Work date must not be null");
        this.wasPresent = wasPresent;
        this.extraInfo = extraInfo;
    }

    public GradeRecord(long id, long studentId, String subjectName, String workType, double score, String workDate) {
        this(id, studentId, subjectName, workType, score, workDate, true, 0.0);
    }

    public GradeRecord(long studentId, String subjectName, String workType, double score, String workDate, boolean wasPresent, double extraInfo) {
        this(ID_GENERATOR.getAndIncrement(), studentId, subjectName, workType, score, workDate, wasPresent, extraInfo);
    }

    public GradeRecord(long studentId, String subjectName, String workType, double score, String workDate) {
        this(studentId, subjectName, workType, score, workDate, true, 0.0);
    }

    public long getStudentId() {
        return studentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public String getWorkType() {
        return workType;
    }

    public double getScore() {
        return score;
    }

    public String getWorkDate() {
        return workDate;
    }

    public boolean isWasPresent() {
        return wasPresent;
    }

    public double getExtraInfo() {
        return extraInfo;
    }

    public GradedWork toGradedWork() {
        Subject subject = new Subject(subjectName);
        Attendance attendance = new Attendance(wasPresent, subject, workDate);
        try {
            if (workType.equalsIgnoreCase("Звичайна пара") || workType.equalsIgnoreCase("REGULAR")) {
                if (wasPresent) {
                    return new RegularGrade(getId(), score, attendance, subject);
                }
                return new LabGrade(getId(), score, attendance, subject, 1);
            } else if (workType.startsWith("Захист лаб") || workType.equalsIgnoreCase("DEFENSE_LAB")) {
                int labNum = (extraInfo > 0) ? (int) extraInfo : 1;
                return new DefenseLabGrade(getId(), score, attendance, subject, labNum, 10.0);
            } else if (workType.startsWith("Лабораторна") || workType.equalsIgnoreCase("LAB")) {
                int labNum = (extraInfo > 0) ? (int) extraInfo : 1;
                return new LabGrade(getId(), score, attendance, subject, labNum);
            } else if (workType.equalsIgnoreCase("Іспит") || workType.equalsIgnoreCase("EXAM")) {
                int ticket = (extraInfo > 0) ? (int) extraInfo : 1;
                if (wasPresent) {
                    return new ExamGrade(getId(), score, attendance, subject, ticket);
                }
                return new LabGrade(getId(), score, attendance, subject, ticket);
            } else if (workType.startsWith("Шкільна оцінка") || workType.equalsIgnoreCase("SCHOOL")) {
                int g12 = (extraInfo > 0) ? (int) extraInfo : Math.max(1, (int) Math.round(score * 12.0 / 100.0));
                return new SchoolGradeAdapter(getId(), new SchoolAssessment(g12, wasPresent, subjectName), workDate);
            } else {
                return new LabGrade(getId(), score, attendance, subject, 1);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert GradeRecord to GradedWork", e);
        }
    }

    public static GradeRecord fromGradedWork(long studentId, GradedWork work, String fallbackSubject, String fallbackDate) {
        String subjectName = (work.getAttendance() != null && work.getAttendance().getSubject() != null)
                ? work.getAttendance().getSubject().getName()
                : fallbackSubject;
        String workDate = (work.getAttendance() != null && work.getAttendance().getDate() != null)
                ? work.getAttendance().getDate()
                : fallbackDate;
        boolean wasPresent = work.getAttendance() != null && work.getAttendance().isWasPresent();
        double extraInfo = 0.0;

        if (work instanceof DefenseLabGrade dlg) {
            extraInfo = dlg.getLabNumber();
        } else if (work instanceof LabGrade lg) {
            extraInfo = lg.getLabNumber();
        } else if (work instanceof ExamGrade eg) {
            extraInfo = eg.getTicketNumber();
        }

        long recordId = (work instanceof BaseEntity be) ? be.getId() : ID_GENERATOR.getAndIncrement();
        return new GradeRecord(recordId, studentId, subjectName, work.getWorkType(), work.getRawValue(), workDate, wasPresent, extraInfo);
    }

    @Override
    public String toString() {
        return String.format("GradeRecord [ID: %d, StudentID: %d, Subject: %s, Type: %s, Score: %.2f, Date: %s, Present: %b, Extra: %.2f]",
                getId(), studentId, subjectName, workType, score, workDate, wasPresent, extraInfo);
    }
}
