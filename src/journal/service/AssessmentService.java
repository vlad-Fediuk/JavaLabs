package journal.service;

import java.util.List;

import journal.domain.grade.GradedWork;
import journal.domain.model.Attendance;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.JournalException;
import journal.factory.GradedWorkCreator;

public interface AssessmentService {
    GradedWork recordAcademicGrade(
            Student student,
            Subject subject,
            double score,
            boolean wasPresent,
            String date,
            GradedWorkCreator creator) throws JournalException;

    GradedWork importSchoolAssessment(
            Student student,
            int grade12,
            boolean wasPresent,
            String disciplineCode,
            String date) throws JournalException;

    List<GradedWork> getStudentGrades(Student student);

    void recordAttendance(Student student, Subject subject, boolean wasPresent, String date);

    List<Attendance> getStudentAttendances(Student student);
}
