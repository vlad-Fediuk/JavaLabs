package journal;

import java.util.List;
import java.util.Scanner;

import journal.domain.model.Subject;
import journal.observer.AcademicRiskObserver;
import journal.observer.AuditLogObserver;
import journal.repository.Journal;
import journal.repository.db.DatabaseInitializer;
import journal.service.AssessmentService;
import journal.service.AssessmentServiceImpl;
import journal.service.EducationalAnalyticsService;
import journal.service.EducationalAnalyticsServiceImpl;
import journal.service.JdbcAttendanceService;
import journal.service.JdbcGradeRecordService;
import journal.service.JdbcStudentService;
import journal.service.JournalService;
import journal.service.JournalServiceImpl;
import journal.service.StudentManagementService;
import journal.service.StudentManagementServiceImpl;
import journal.strategy.StandardSemesterStrategy;
import journal.ui.AppController;
import journal.ui.io.ConsoleIO;
import journal.ui.io.UserInterface;
import journal.ui.view.AppView;
import journal.ui.view.ConsoleView;

public class Main {
    public static void main(String[] args) {
        DatabaseInitializer.initialize();
        DatabaseInitializer.seedIfEmpty();

        UserInterface ui = new ConsoleIO(new Scanner(System.in));
        JdbcAttendanceService attendanceService = new JdbcAttendanceService();
        JdbcGradeRecordService gradeService = new JdbcGradeRecordService();
        JdbcStudentService studentServiceRepo = new JdbcStudentService(attendanceService, gradeService);

        Journal journal = new Journal(new StandardSemesterStrategy(), studentServiceRepo);
        journal.registerObserver(new AuditLogObserver(ui));
        journal.registerObserver(new AcademicRiskObserver(ui));

        StudentManagementService studentService = new StudentManagementServiceImpl(journal, studentServiceRepo);
        AssessmentService assessmentService = new AssessmentServiceImpl(journal, gradeService, attendanceService);
        JournalService journalService = new JournalServiceImpl(journal);
        EducationalAnalyticsService analyticsService = new EducationalAnalyticsServiceImpl();

        List<Subject> subjects = List.of(new Subject("Java"), new Subject("Cs"), new Subject("Cpp"));
        AppView view = new ConsoleView(ui);

        new AppController(
                view,
                studentService,
                assessmentService,
                journalService,
                analyticsService,
                subjects
        ).run();
    }
}
