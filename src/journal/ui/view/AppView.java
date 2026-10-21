package journal.ui.view;

import java.util.List;
import java.util.function.ToDoubleFunction;

import journal.domain.grade.AbstractGrade;
import journal.domain.grade.DefenseLabGrade;
import journal.domain.grade.GradedWork;
import journal.domain.grade.LabGrade;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.JournalException;
import journal.strategy.GradingStrategy;
import journal.ui.dto.GradeDto;
import journal.ui.dto.SchoolAssessmentDto;
import journal.ui.dto.SearchCriteriaDto;
import journal.ui.dto.StudentDto;

public interface AppView {
    String requestDate();

    String readMenuChoice(String todayDate, String activeStrategyName);

    String requestSecretNewDate();

    void showSecretDateChanged(String newDate);

    void showInvalidMenuOption();

    void showInvalidNumberError();

    void showExitMessage();

    void showEmptyJournalWarning();

    void showIndexOutOfBoundsError();

    void showJournalError(String message);

    void showInvalidGradeError(String message, double invalidScore);

    void showStudentNotFoundError(String message, String searchTarget);

    StudentDto promptStudentData();

    void showStudentAdded();

    void displayAllStudents(List<Student> students, ToDoubleFunction<Student> scoreCalculator);

    Student chooseStudent(List<Student> students, ToDoubleFunction<Student> scoreCalculator);

    Subject chooseSubject(List<Subject> subjects);

    GradeDto promptGradeData() throws JournalException;

    void showGradeAdded(GradedWork work);

    SchoolAssessmentDto promptSchoolAssessmentData();

    void showSchoolAssessmentImported(GradedWork work);

    void displayStudentGrades(Student student);

    boolean promptAttendancePresence();

    void showAttendanceRecorded();

    void displayStudentAttendances(Student student);

    void displayStudentsBeforeSorting(List<Student> students, ToDoubleFunction<Student> scoreCalculator);

    void displayStudentsAfterSorting(List<Student> students, ToDoubleFunction<Student> scoreCalculator);

    void displaySummary(int passedCount, int totalCount, double threshold);

    SearchCriteriaDto promptSearchCriteria();

    void displayFoundStudent(Student student, double score);

    GradingStrategy promptStrategyChoice();

    void showStrategyChanged(String strategyName);

    void displayPolymorphismDemo(GradedWork[] works);

    void displayInheritanceHierarchyDemo(
            List<AbstractGrade> grades,
            LabGrade baseLab,
            LabGrade duplicateLab,
            DefenseLabGrade subLab);

    void displayCrudServiceDemo(
            List<Student> insertedStudents,
            Student updatedStudent,
            int sizeAfterUpdate,
            boolean deleteSuccess,
            boolean deleteFailed,
            Student foundStudent,
            String notFoundMessage,
            List<Subject> demoSubjects,
            List<Student> initialStorageOrder,
            List<Student> sortedByName,
            List<Student> sortedByNameAndGroup,
            List<Student> finalStorageOrder);
}
