package journal;

import java.util.List;

import journal.domain.model.GradeRecord;
import journal.domain.model.Student;
import journal.repository.db.DatabaseInitializer;
import journal.service.JdbcGradeRecordService;
import journal.service.JdbcStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcGradeRecordServiceTest {
    private JdbcStudentService studentService;
    private JdbcGradeRecordService gradeService;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialize();
        studentService = new JdbcStudentService();
        gradeService = new JdbcGradeRecordService();

        for (GradeRecord g : gradeService.findAll()) {
            gradeService.delete(g.getId());
        }
        for (Student s : studentService.findAll()) {
            studentService.delete(s.getId());
        }
    }

    @Test
    void testSaveAndFindByStudentId() {
        Student student = new Student(100L, "Дмитро Сидорчук", "КН-21");
        studentService.save(student);

        GradeRecord grade1 = new GradeRecord(201L, 100L, "Java", "LAB", 95.0, "2026-10-28");
        GradeRecord grade2 = new GradeRecord(202L, 100L, "Java", "EXAM", 88.0, "2026-10-28");
        gradeService.save(grade1);
        gradeService.save(grade2);

        List<GradeRecord> grades = gradeService.findByStudentId(100L);
        assertEquals(2, grades.size());
    }

    @Test
    void testCascadeDeleteOnStudentRemoval() {
        Student student = new Student(101L, "Василь Кравчук", "КН-21");
        studentService.save(student);

        GradeRecord grade = new GradeRecord(203L, 101L, "Бази даних", "LAB", 90.0, "2026-10-28");
        gradeService.save(grade);

        assertTrue(gradeService.existsById(203L));

        studentService.delete(101L);

        assertFalse(studentService.existsById(101L));
        assertFalse(gradeService.existsById(203L));
        assertEquals(0, gradeService.findByStudentId(101L).size());
    }

    @Test
    void testTransactionCommitRegistration() {
        Student student = new Student(102L, "Ольга Литвин", "КН-22");
        List<GradeRecord> grades = List.of(
                new GradeRecord(204L, 102L, "Java", "LAB", 92.0, "2026-10-28"),
                new GradeRecord(205L, 102L, "Cs", "EXAM", 85.0, "2026-10-28")
        );

        gradeService.registerStudentWithGrades(student, grades);

        Student registeredStudent = studentService.findById(102L);
        assertNotNull(registeredStudent);
        assertEquals("Ольга Литвин", registeredStudent.getName());

        List<GradeRecord> savedGrades = gradeService.findByStudentId(102L);
        assertEquals(2, savedGrades.size());
    }

    @Test
    void testTransactionRollbackOnFailure() {
        Student student1 = new Student(103L, "Максим Ткач", "КН-21");
        Student student2 = new Student(104L, "Павло Мороз", "КН-21");
        studentService.save(student1);
        studentService.save(student2);

        GradeRecord grade = new GradeRecord(206L, 103L, "Java", "LAB", 78.0, "2026-10-28");
        gradeService.save(grade);

        assertEquals(1, gradeService.findByStudentId(103L).size());
        assertEquals(0, gradeService.findByStudentId(104L).size());

        assertThrows(IllegalStateException.class, () ->
                gradeService.transferStudentGrades(103L, 104L, true));

        assertEquals(1, gradeService.findByStudentId(103L).size());
        assertEquals(0, gradeService.findByStudentId(104L).size());
        assertEquals(103L, gradeService.findById(206L).getStudentId());
    }
}
