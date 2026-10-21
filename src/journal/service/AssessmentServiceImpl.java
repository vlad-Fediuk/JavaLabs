package journal.service;

import java.util.List;

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

    public AssessmentServiceImpl(Journal journal) {
        this.journal = journal;
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
        journal.addAttendance(student, work.getAttendance());
        journal.addGrade(student, work);
        return work;
    }

    @Override
    public List<GradedWork> getStudentGrades(Student student) {
        return student.getGradedWorks();
    }

    @Override
    public void recordAttendance(Student student, Subject subject, boolean wasPresent, String date) {
        journal.addAttendance(student, new Attendance(wasPresent, subject, date));
    }

    @Override
    public List<Attendance> getStudentAttendances(Student student) {
        return student.getAttendances();
    }
}
