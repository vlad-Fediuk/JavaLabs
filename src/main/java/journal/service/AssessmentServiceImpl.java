package journal.service;

import java.util.List;
import java.util.Objects;

import journal.adapter.SchoolGradeAdapter;
import journal.adapter.external.SchoolAssessment;
import journal.domain.grade.GradedWork;
import journal.domain.model.Attendance;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.JournalException;
import journal.factory.GradedWorkCreator;
import journal.repository.Journal;

public class AssessmentServiceImpl implements AssessmentService {
    private final Journal journal;
    private final JdbcGradeRecordService gradeService;
    private final JdbcAttendanceService attendanceService;

    public AssessmentServiceImpl(Journal journal) {
        this(journal, new JdbcGradeRecordService(), new JdbcAttendanceService());
    }

    public AssessmentServiceImpl(Journal journal, JdbcGradeRecordService gradeService, JdbcAttendanceService attendanceService) {
        this.journal = Objects.requireNonNull(journal, "Journal must not be null");
        this.gradeService = Objects.requireNonNull(gradeService, "Grade service must not be null");
        this.attendanceService = Objects.requireNonNull(attendanceService, "Attendance service must not be null");
    }

    @Override
    public GradedWork recordAcademicGrade(
            Student student,
            Subject subject,
            double score,
            boolean wasPresent,
            String date,
            GradedWorkCreator creator) throws JournalException {
        Attendance attendance = new Attendance(wasPresent, subject, date);
        GradedWork work = creator.createGrade(score, attendance, subject);
        attendanceService.recordAttendance(student.getId(), attendance);
        gradeService.recordGradedWork(student.getId(), work, subject.getName(), date);
        journal.addAttendance(student, attendance);
        journal.addGrade(student, work);
        return work;
    }

    @Override
    public GradedWork importSchoolAssessment(
            Student student,
            int grade12,
            boolean wasPresent,
            String disciplineCode,
            String date) throws JournalException {
        SchoolAssessment assessment = new SchoolAssessment(grade12, wasPresent, disciplineCode);
        SchoolGradeAdapter work = new SchoolGradeAdapter(assessment, date);
        attendanceService.recordAttendance(student.getId(), work.getAttendance());
        gradeService.recordGradedWork(student.getId(), work, disciplineCode, date);
        journal.addAttendance(student, work.getAttendance());
        journal.addGrade(student, work);
        return work;
    }

    @Override
    public List<GradedWork> getStudentGrades(Student student) {
        if (student.getGradedWorks().isEmpty()) {
            return gradeService.findGradedWorksByStudentId(student.getId());
        }
        return student.getGradedWorks();
    }

    @Override
    public void recordAttendance(Student student, Subject subject, boolean wasPresent, String date) {
        Attendance attendance = new Attendance(wasPresent, subject, date);
        attendanceService.recordAttendance(student.getId(), attendance);
        journal.addAttendance(student, attendance);
    }

    @Override
    public List<Attendance> getStudentAttendances(Student student) {
        if (student.getAttendances().isEmpty()) {
            return attendanceService.findDomainAttendancesByStudentId(student.getId());
        }
        return student.getAttendances();
    }
}
