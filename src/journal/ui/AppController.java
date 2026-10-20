package journal.ui;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import journal.adapter.SchoolGradeAdapter;
import journal.adapter.external.SchoolAssessment;
import journal.domain.grade.AbstractGrade;
import journal.domain.grade.DefenseLabGrade;
import journal.domain.grade.ExamGrade;
import journal.domain.grade.GradedWork;
import journal.domain.grade.LabGrade;
import journal.domain.grade.RegularGrade;
import journal.domain.model.Attendance;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.InvalidGradeException;
import journal.exception.JournalException;
import journal.exception.StudentNotFoundException;
import journal.repository.Journal;
import journal.strategy.GradingStrategy;
import journal.ui.command.MenuCommand;
import journal.ui.dto.GradeDto;
import journal.ui.dto.SchoolAssessmentDto;
import journal.ui.dto.SearchCriteriaDto;
import journal.ui.dto.StudentDto;
import journal.ui.view.AppView;

public class AppController {
    private static final String SECRET_COMMAND = "SecretNextDay";
    private static final double PASSING_THRESHOLD = 60.0;
    private static final double DEMO_SCORE = 80.0;
    private static final int DEMO_LAB_NUMBER = 4;
    private static final double DEMO_DEFENSE_BONUS = 15.0;
    private static final int DEMO_EXAM_TICKET = 12;
    private static final int DEMO_SCHOOL_GRADE = 10;
    private static final String DEMO_DATE = "27.09.26";
    private static final String DEMO_SUBJECT_NAME = "Java";

    private final AppView view;
    private final Journal journal;
    private final List<Subject> subjects;
    private final Map<Integer, MenuCommand> commands = new LinkedHashMap<>();
    private String todayDate;

    public AppController(AppView view, Journal journal, List<Subject> subjects) {
        this.view = view;
        this.journal = journal;
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
    }

    public void run() {
        todayDate = view.requestDate();
        while (true) {
            String input = view.readMenuChoice(todayDate, journal.getGradingStrategy().getStrategyName());

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
        journal.addStudent(new Student(data.name(), data.group()));
        view.showStudentAdded();
    }

    private void showAllStudents() {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        view.displayAllStudents(journal.getStudents(), journal::calculateStudentScore);
    }

    private void addGrade() throws JournalException {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(journal.getStudents(), journal::calculateStudentScore);
        Subject subject = view.chooseSubject(subjects);
        GradeDto data = view.promptGradeData();
        Attendance attendance = new Attendance(data.wasPresent(), subject, todayDate);
        GradedWork work = data.creator().createGrade(data.score(), attendance, subject);
        journal.addAttendance(student, attendance);
        journal.addGrade(student, work);
        view.showGradeAdded(work);
    }

    private void importSchoolAssessment() throws JournalException {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(journal.getStudents(), journal::calculateStudentScore);
        SchoolAssessmentDto data = view.promptSchoolAssessmentData();
        SchoolAssessment assessment = new SchoolAssessment(data.grade12(), data.wasPresent(), data.disciplineCode());
        SchoolGradeAdapter work = new SchoolGradeAdapter(assessment, todayDate);
        journal.addAttendance(student, work.getAttendance());
        journal.addGrade(student, work);
        view.showSchoolAssessmentImported(work);
    }

    private void showGrades() {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(journal.getStudents(), journal::calculateStudentScore);
        view.displayStudentGrades(student);
    }

    private void addAttendance() {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(journal.getStudents(), journal::calculateStudentScore);
        Subject subject = view.chooseSubject(subjects);
        boolean wasPresent = view.promptAttendancePresence();
        journal.addAttendance(student, new Attendance(wasPresent, subject, todayDate));
        view.showAttendanceRecorded();
    }

    private void showAttendances() {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        Student student = view.chooseStudent(journal.getStudents(), journal::calculateStudentScore);
        view.displayStudentAttendances(student);
    }

    private void sortStudents() {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        view.displayStudentsBeforeSorting(journal.getStudents(), journal::calculateStudentScore);
        journal.bubbleSortByStrategyScore();
        view.displayStudentsAfterSorting(journal.getStudents(), journal::calculateStudentScore);
    }

    private void showSummary() {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        int count = journal.countPassedStudents(PASSING_THRESHOLD);
        view.displaySummary(count, journal.size(), PASSING_THRESHOLD);
    }

    private void searchStudent() {
        if (journal.isEmpty()) {
            view.showEmptyJournalWarning();
            return;
        }
        SearchCriteriaDto criteria = view.promptSearchCriteria();
        try {
            Student found = journal.findStudent(criteria.name(), criteria.group());
            double score = journal.calculateStudentScore(found);
            view.displayFoundStudent(found, score);
        } catch (StudentNotFoundException e) {
            view.showStudentNotFoundError(e.getMessage(), e.getSearchTarget());
        }
    }

    private void changeStrategy() {
        GradingStrategy strategy = view.promptStrategyChoice();
        if (strategy != null) {
            journal.setGradingStrategy(strategy);
            view.showStrategyChanged(strategy.getStrategyName());
        }
    }

    private void demonstratePolymorphism() throws JournalException {
        Subject java = new Subject(DEMO_SUBJECT_NAME);
        Attendance present = new Attendance(true, java, DEMO_DATE);
        Attendance absent = new Attendance(false, java, DEMO_DATE);

        RegularGrade regularGrade = new RegularGrade(present, java);
        LabGrade presentLab = new LabGrade(DEMO_SCORE, present, java, DEMO_LAB_NUMBER);
        LabGrade absentLab = new LabGrade(DEMO_SCORE, absent, java);
        DefenseLabGrade defenseLab = new DefenseLabGrade(
                DEMO_SCORE, present, java, DEMO_LAB_NUMBER, DEMO_DEFENSE_BONUS);
        ExamGrade examGrade = new ExamGrade(DEMO_SCORE, present, java, DEMO_EXAM_TICKET);
        SchoolGradeAdapter schoolAdapter = new SchoolGradeAdapter(
                new SchoolAssessment(DEMO_SCHOOL_GRADE, true, DEMO_SUBJECT_NAME), DEMO_DATE);

        List<AbstractGrade> gradeHierarchy = List.of(regularGrade, presentLab, absentLab, defenseLab, examGrade);
        GradedWork[] works = {
            regularGrade,
            presentLab,
            absentLab,
            defenseLab,
            examGrade,
            schoolAdapter
        };
        LabGrade duplicateLab = new LabGrade(DEMO_SCORE, present, java, DEMO_LAB_NUMBER);

        view.displayPolymorphismDemo(works);
        view.displayInheritanceHierarchyDemo(gradeHierarchy, presentLab, duplicateLab, defenseLab);
    }
}
