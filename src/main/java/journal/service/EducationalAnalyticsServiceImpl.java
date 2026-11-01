package journal.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
import journal.domain.model.GradeRecord;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.exception.EntityNotFoundException;
import journal.exception.JournalException;
import journal.repository.db.DBConnection;
import journal.repository.db.DatabaseInitializer;

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
        DefenseLabGrade defenseLab = new DefenseLabGrade(
                DEMO_SCORE, present, java, DEMO_LAB_NUMBER, DEMO_DEFENSE_BONUS);
        ExamGrade examGrade = new ExamGrade(DEMO_SCORE, present, java, DEMO_EXAM_TICKET);

        return List.of(regularGrade, presentLab, defenseLab, examGrade);
    }

    @Override
    public CrudDemoResult executeCrudLabDemo() {
        StudentService studentService = new InMemoryStudentService();
        Student student1 = new Student(1L, "Іван Петренко", "КН-21");
        Student student2 = new Student(2L, "Марія Коваль", "КН-22");
        Student student3 = new Student(3L, "Олексій Бондар", "КН-21");

        studentService.save(student1);
        studentService.save(student2);
        studentService.save(student3);
        List<Student> insertedStudents = studentService.findAll();

        Student updatedStudent3 = new Student(3L, "Олексій Шевченко", "КН-23");
        studentService.save(updatedStudent3);
        int sizeAfterUpdate = studentService.findAll().size();

        boolean deleteSuccess = studentService.delete(2L);
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

    @Override
    public JdbcDemoResult executeJdbcLabDemo() {
        DatabaseInitializer.initialize();
        boolean connectionActive;
        try (Connection conn = DBConnection.getInstance().getConnection()) {
            connectionActive = conn.isValid(2);
        } catch (SQLException e) {
            connectionActive = false;
        }

        JdbcStudentService studentService = new JdbcStudentService();
        JdbcGradeRecordService gradeService = new JdbcGradeRecordService();

        for (GradeRecord g : gradeService.findAll()) {
            gradeService.delete(g.getId());
        }
        for (Student s : studentService.findAll()) {
            studentService.delete(s.getId());
        }

        Student demoStudent = new Student(501L, "Ольга Сидоренко", "КН-21");
        List<GradeRecord> demoGrades = List.of(
                new GradeRecord(601L, 501L, "Java", "LAB", 95.0, "2026-10-28"),
                new GradeRecord(602L, 501L, "Бази даних", "EXAM", 88.0, "2026-10-28")
        );

        boolean transactionCommitSuccess = false;
        try {
            gradeService.registerStudentWithGrades(demoStudent, demoGrades);
            transactionCommitSuccess = studentService.existsById(501L) && gradeService.findByStudentId(501L).size() == 2;
        } catch (Exception ignored) {
        }

        Student studentA = new Student(502L, "Андрій Кравченко", "КН-21");
        Student studentB = new Student(503L, "Богдан Мельник", "КН-22");
        studentService.save(studentA);
        studentService.save(studentB);
        GradeRecord testGrade = new GradeRecord(603L, 502L, "Java", "LAB", 82.0, "2026-10-28");
        gradeService.save(testGrade);

        boolean transactionRollbackSuccess = false;
        try {
            gradeService.transferStudentGrades(502L, 503L, true);
        } catch (Exception e) {
            transactionRollbackSuccess = gradeService.findByStudentId(502L).size() == 1
                    && gradeService.findByStudentId(503L).isEmpty();
        }

        Student studentToDelete = new Student(504L, "Василь Павленко", "КН-21");
        studentService.save(studentToDelete);
        GradeRecord cascadeGrade = new GradeRecord(604L, 504L, "Java", "LAB", 90.0, "2026-10-28");
        gradeService.save(cascadeGrade);

        studentService.delete(504L);
        boolean cascadeDeleteSuccess = !studentService.existsById(504L) && !gradeService.existsById(604L);

        for (long i = 1; i <= 6; i++) {
            studentService.save(new Student(700L + i, "Студент " + i, "КН-21"));
        }
        List<Student> paged1 = studentService.findAllPaged(3, 0);
        List<Student> paged2 = studentService.findAllPaged(3, 3);

        int benchmarkCount = 300;
        List<Student> singleStudents = new ArrayList<>();
        List<Student> batchStudents = new ArrayList<>();
        for (int i = 1; i <= benchmarkCount; i++) {
            singleStudents.add(new Student(1000L + i, "Bench Single " + i, "КН-21"));
            batchStudents.add(new Student(2000L + i, "Bench Batch " + i, "КН-22"));
        }

        long startSingle = System.currentTimeMillis();
        for (Student s : singleStudents) {
            studentService.save(s);
        }
        long singleInsertTimeMs = System.currentTimeMillis() - startSingle;

        long startBatch = System.currentTimeMillis();
        studentService.saveBatch(batchStudents);
        long batchInsertTimeMs = System.currentTimeMillis() - startBatch;

        for (Student s : singleStudents) {
            studentService.delete(s.getId());
        }
        for (Student s : batchStudents) {
            studentService.delete(s.getId());
        }

        String attack = "' OR '1'='1";
        int statementVulnerableCount = 0;
        try (Connection conn = DBConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id FROM students WHERE name = '" + attack + "'")) {
            while (rs.next()) {
                statementVulnerableCount++;
            }
        } catch (SQLException ignored) {
        }

        int preparedStatementSafeCount = 0;
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement("SELECT id FROM students WHERE name = ?")) {
            ps.setString(1, attack);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    preparedStatementSafeCount++;
                }
            }
        } catch (SQLException ignored) {
        }

        return new JdbcDemoResult(
                connectionActive,
                demoStudent,
                demoGrades,
                transactionCommitSuccess,
                transactionRollbackSuccess,
                cascadeDeleteSuccess,
                paged1,
                paged2,
                singleInsertTimeMs,
                batchInsertTimeMs,
                statementVulnerableCount,
                preparedStatementSafeCount
        );
    }
}
