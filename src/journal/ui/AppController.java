package journal.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import journal.domain.grade.AbstractGrade;
import journal.domain.grade.DefenseLabGrade;
import journal.domain.grade.GradedWork;
import journal.domain.grade.LabGrade;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;
import journal.exception.JournalException;
import journal.exception.StudentNotFoundException;
import journal.service.AssessmentService;
import journal.service.EducationalAnalyticsService;
import journal.service.JournalService;
import journal.service.StudentManagementService;
import journal.strategy.GradingStrategy;
import journal.ui.command.MenuCommand;
import journal.ui.dto.GradeDto;
import journal.ui.dto.SchoolAssessmentDto;
import journal.ui.dto.SearchCriteriaDto;
import journal.ui.dto.StudentDto;
import journal.ui.view.AppView;

public class AppController {
    private static final String SECRET_COMMAND = "SecretNextDay";
    private static final double DEMO_SCORE = 80.0;
    private static final int DEMO_LAB_NUMBER = 4;
    private static final double DEMO_DEFENSE_BONUS = 15.0;
    private static final String DEMO_DATE = "27.09.26";
    private static final String DEMO_SUBJECT_NAME = "Java";

    private final AppView view;
    private final StudentManagementService studentService;
    private final AssessmentService assessmentService;
    private final JournalService journalService;
    private final EducationalAnalyticsService analyticsService;
    private final List<Subject> subjects;
    private final Map<Integer, MenuCommand> commands = new LinkedHashMap<>();
    private String todayDate;

    public AppController(
            AppView view,
            StudentManagementService studentService,
            AssessmentService assessmentService,
            JournalService journalService,
            EducationalAnalyticsService analyticsService,
            List<Subject> subjects) {
        this.view = view;
        this.studentService = studentService;
        this.assessmentService = assessmentService;
        this.journalService = journalService;
        this.analyticsService = analyticsService;
        this.subjects = subjects;
        registerCommands();
    }

    private void registerCommands() {
        commands.put(1, this::addStudent);
        commands.put(2, this::showAllStudents);
        commands.put(3, this::addGrade);
        commands.put(4, this::showGrades);
        commands.put(5, this::addAttendance);
        commands.put(6, this::showAttendances);
        commands.put(7, this::sortStudents);
        commands.put(8, this::showSummary);
        commands.put(9, this::searchStudent);
        commands.put(10, this::changeStrategy);
        commands.put(11, this::demonstratePolymorphism);
        commands.put(12, this::importSchoolAssessment);
        commands.put(13, this::demonstrateCrudService);
    }

    public void run() {
        todayDate = view.requestDate();
        while (true) {
            String input = view.readMenuChoice(todayDate, journalService.getActiveStrategyName());

            if (input.equalsIgnoreCase(SECRET_COMMAND)) {
                todayDate = view.requestSecretNewDate();
                view.showSecretDateChanged(todayDate);
                continue;
            }

            int option;
            try {
                option = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                view.showInvalidNumberError();
                continue;
            }

            if (option == 0) {
                view.showExitMessage();
                return;
            }

            MenuCommand command = commands.get(option);
            if (command == null) {
                view.showInvalidMenuOption();
                continue;
            }
            execute(command);
        }
    }

    private void execute(MenuCommand command) {
        try {
            command.execute();
        } catch (InvalidGradeException e) {
            view.showInvalidGradeError(e.getMessage(), e.getInvalidScore());
        } catch (JournalException e) {
            view.showJournalError(e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            view.showIndexOutOfBoundsError();
        }
    }

    private void addStudent() {
        StudentDto data = view.promptStudentData();
        studentService.registerStudent(data.name(), data.group());
        view.showStudentAdded();
    }

    private void showAllStudents() {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        view.displayAllStudents(studentService.getAllStudents(), journalService::calculateStudentScore);
    }

    private void addGrade() throws JournalException {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(studentService.getAllStudents(), journalService::calculateStudentScore);
        Subject subject = view.chooseSubject(subjects);
        GradeDto data = view.promptGradeData();
        GradedWork work = assessmentService.recordAcademicGrade(
                student, subject, data.score(), data.wasPresent(), todayDate, data.creator());
        view.showGradeAdded(work);
    }

    private void importSchoolAssessment() throws JournalException {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(studentService.getAllStudents(), journalService::calculateStudentScore);
        SchoolAssessmentDto data = view.promptSchoolAssessmentData();
        GradedWork work = assessmentService.importSchoolAssessment(
                student, data.grade12(), data.wasPresent(), data.disciplineCode(), todayDate);
        view.showSchoolAssessmentImported(work);
    }

    private void showGrades() {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(studentService.getAllStudents(), journalService::calculateStudentScore);
        view.displayStudentGrades(student);
    }

    private void addAttendance() {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(studentService.getAllStudents(), journalService::calculateStudentScore);
        Subject subject = view.chooseSubject(subjects);
        boolean wasPresent = view.promptAttendancePresence();
        assessmentService.recordAttendance(student, subject, wasPresent, todayDate);
        view.showAttendanceRecorded();
    }

    private void showAttendances() {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(studentService.getAllStudents(), journalService::calculateStudentScore);
        view.displayStudentAttendances(student);
    }

    private void sortStudents() {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        view.displayStudentsBeforeSorting(studentService.getAllStudents(), journalService::calculateStudentScore);
        List<Student> sorted = journalService.getStudentsSortedByActiveStrategy();
        view.displayStudentsAfterSorting(sorted, journalService::calculateStudentScore);
    }

    private void showSummary() {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        JournalService.JournalSummary summary = journalService.getPassingSummary();
        view.displaySummary(summary.passedCount(), summary.totalCount(), summary.threshold());
    }

    private void searchStudent() {
        if (!studentService.hasStudents()) {
            view.showEmptyJournalWarning();
            return;
        }
        SearchCriteriaDto criteria = view.promptSearchCriteria();
        try {
            Student found = studentService.findStudent(criteria.name(), criteria.group());
            double score = journalService.calculateStudentScore(found);
            view.displayFoundStudent(found, score);
        } catch (StudentNotFoundException e) {
            view.showStudentNotFoundError(e.getMessage(), e.getSearchTarget());
        }
    }

    private void changeStrategy() {
        GradingStrategy strategy = view.promptStrategyChoice();
        if (strategy != null) {
            journalService.changeStrategy(strategy);
            view.showStrategyChanged(strategy.getStrategyName());
        }
    }

    private void demonstratePolymorphism() throws JournalException {
        GradedWork[] works = analyticsService.preparePolymorphicWorksDemo();
        List<AbstractGrade> gradeHierarchy = analyticsService.prepareInheritanceHierarchyDemo();

        Subject java = new Subject(DEMO_SUBJECT_NAME);
        journal.domain.model.Attendance present = new journal.domain.model.Attendance(true, java, DEMO_DATE);
        LabGrade presentLab = new LabGrade(DEMO_SCORE, present, java, DEMO_LAB_NUMBER);
        LabGrade duplicateLab = new LabGrade(DEMO_SCORE, present, java, DEMO_LAB_NUMBER);
        DefenseLabGrade defenseLab = new DefenseLabGrade(
                DEMO_SCORE, present, java, DEMO_LAB_NUMBER, DEMO_DEFENSE_BONUS);

        view.displayPolymorphismDemo(works);
        view.displayInheritanceHierarchyDemo(gradeHierarchy, presentLab, duplicateLab, defenseLab);
    }

    private void demonstrateCrudService() {
        EducationalAnalyticsService.CrudDemoResult result = analyticsService.executeCrudLabDemo();
        view.displayCrudServiceDemo(
                result.insertedStudents(),
                result.updatedStudent(),
                result.sizeAfterUpdate(),
                result.deleteSuccess(),
                result.deleteFailed(),
                result.foundStudent(),
                result.notFoundMessage(),
                result.demoSubjects(),
                result.initialStorageOrder(),
                result.sortedByName(),
                result.sortedByNameAndGroup(),
                result.finalStorageOrder());
    }
}
