package journal.service;

import java.util.List;

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
import journal.exception.EntityNotFoundException;
import journal.exception.JournalException;

public class EducationalAnalyticsServiceImpl implements EducationalAnalyticsService {
    private static final double DEMO_SCORE = 80.0;
    private static final int DEMO_LAB_NUMBER = 4;
    private static final double DEMO_DEFENSE_BONUS = 15.0;
    private static final int DEMO_EXAM_TICKET = 12;
    private static final int DEMO_SCHOOL_GRADE = 10;
    private static final String DEMO_DATE = "27.09.26";
    private static final String DEMO_SUBJECT_NAME = "Java";

    @Override
    public GradedWork[] preparePolymorphicWorksDemo() throws JournalException {
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

        return new GradedWork[] {
            regularGrade,
            presentLab,
            absentLab,
            defenseLab,
            examGrade,
            schoolAdapter
        };
    }

    @Override
    public List<AbstractGrade> prepareInheritanceHierarchyDemo() throws JournalException {
        Subject java = new Subject(DEMO_SUBJECT_NAME);
        Attendance present = new Attendance(true, java, DEMO_DATE);
        Attendance absent = new Attendance(false, java, DEMO_DATE);

        RegularGrade regularGrade = new RegularGrade(present, java);
        LabGrade presentLab = new LabGrade(DEMO_SCORE, present, java, DEMO_LAB_NUMBER);
        LabGrade absentLab = new LabGrade(DEMO_SCORE, absent, java);
        DefenseLabGrade defenseLab = new DefenseLabGrade(
                DEMO_SCORE, present, java, DEMO_LAB_NUMBER, DEMO_DEFENSE_BONUS);
        ExamGrade examGrade = new ExamGrade(DEMO_SCORE, present, java, DEMO_EXAM_TICKET);

        return List.of(regularGrade, presentLab, absentLab, defenseLab, examGrade);
    }

    @Override
    public CrudDemoResult executeCrudLabDemo() {
        StudentService studentService = new InMemoryStudentService();

        Student student1 = new Student(1L, "Олександр Коваленко", "КН-22");
        Student student2 = new Student(2L, "Олександр Коваленко", "КН-21");
        Student student3 = new Student(3L, "Богдан Мельник", "КН-21");

        studentService.save(student1);
        studentService.save(student2);
        studentService.save(student3);
        List<Student> insertedStudents = List.of(student1, student2, student3);

        Student updatedStudent3 = new Student(3L, "Богдан Шевченко", "КН-23");
        studentService.save(updatedStudent3);
        int sizeAfterUpdate = studentService.findAll().size();

        boolean deleteSuccess = studentService.delete(3L);
        boolean deleteFailed = studentService.delete(999L);

        Student foundStudent = studentService.findById(1L);
        String notFoundMessage = "";
        try {
            studentService.findById(999L);
        } catch (EntityNotFoundException e) {
            notFoundMessage = e.getMessage();
        }

        CRUDService<Subject> subjectService = new InMemoryCrudService<>();
        Subject subject1 = new Subject(101L, "Програмування Java", 5);
        Subject subject2 = new Subject(102L, "Бази даних", 4);
        Subject subject3 = new Subject(103L, "Алгоритми та структури даних", 3);
        subjectService.save(subject1);
        subjectService.save(subject2);
        subjectService.save(subject3);
        List<Subject> demoSubjects = subjectService.findAll();

        StudentService sortDemoService = new InMemoryStudentService();
        sortDemoService.save(student1);
        sortDemoService.save(student2);
        sortDemoService.save(student3);

        List<Student> initialStorageOrder = sortDemoService.findAll();
        List<Student> sortedByName = sortDemoService.findAllSortedByName();
        List<Student> sortedByNameAndGroup = sortDemoService.findAllSortedByNameAndByGroup();
        List<Student> finalStorageOrder = sortDemoService.findAll();

        return new CrudDemoResult(
                insertedStudents,
                updatedStudent3,
                sizeAfterUpdate,
                deleteSuccess,
                deleteFailed,
                foundStudent,
                notFoundMessage,
                demoSubjects,
                initialStorageOrder,
                sortedByName,
                sortedByNameAndGroup,
                finalStorageOrder
        );
    }
}
