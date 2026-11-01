package journal;

import java.util.List;

import journal.domain.model.Student;
import journal.exception.EntityNotFoundException;
import journal.repository.db.DatabaseInitializer;
import journal.service.JdbcStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcStudentServiceTest {
    private JdbcStudentService studentService;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialize();
        studentService = new JdbcStudentService();
        List<Student> all = studentService.findAll();
        for (Student s : all) {
            studentService.delete(s.getId());
        }
    }

    @Test
    void testSaveAndFindById() {
        Student student = new Student(1L, "Іван Петренко", "КН-21");
        studentService.save(student);

        Student found = studentService.findById(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("Іван Петренко", found.getName());
        assertEquals("КН-21", found.getGroup());
    }

    @Test
    void testUpdateExistingStudent() {
        Student student = new Student(2L, "Марія Коваль", "КН-21");
        studentService.save(student);

        Student updated = new Student(2L, "Марія Сидоренко", "КН-22");
        studentService.save(updated);

        Student found = studentService.findById(2L);
        assertEquals("Марія Сидоренко", found.getName());
        assertEquals("КН-22", found.getGroup());
    }

    @Test
    void testDeleteStudent() {
        Student student = new Student(3L, "Олексій Бондар", "КН-21");
        studentService.save(student);

        boolean deleted = studentService.delete(3L);
        assertTrue(deleted);
        assertFalse(studentService.existsById(3L));

        boolean deleteNonExistent = studentService.delete(999L);
        assertFalse(deleteNonExistent);
    }

    @Test
    void testFindByIdNotFoundThrowsException() {
        assertThrows(EntityNotFoundException.class, () -> studentService.findById(9999L));
    }

    @Test
    void testFindAllSortedByName() {
        studentService.save(new Student(10L, "Тарас Шевченко", "КН-21"));
        studentService.save(new Student(11L, "Андрій Мельник", "КН-21"));
        studentService.save(new Student(12L, "Богдан Хмельницький", "КН-22"));

        List<Student> sorted = studentService.findAllSortedByName();
        assertEquals(3, sorted.size());
        assertEquals("Андрій Мельник", sorted.get(0).getName());
        assertEquals("Богдан Хмельницький", sorted.get(1).getName());
        assertEquals("Тарас Шевченко", sorted.get(2).getName());
    }

    @Test
    void testFindAllSortedByNameAndByGroup() {
        studentService.save(new Student(20L, "Олександр Коваленко", "КН-22"));
        studentService.save(new Student(21L, "Олександр Коваленко", "КН-21"));
        studentService.save(new Student(22L, "Анна Бондар", "КН-21"));

        List<Student> sorted = studentService.findAllSortedByNameAndByGroup();
        assertEquals(3, sorted.size());
        assertEquals("Анна Бондар", sorted.get(0).getName());
        assertEquals("Олександр Коваленко", sorted.get(1).getName());
        assertEquals("КН-21", sorted.get(1).getGroup());
        assertEquals("Олександр Коваленко", sorted.get(2).getName());
        assertEquals("КН-22", sorted.get(2).getGroup());
    }

    @Test
    void testFindAllPaged() {
        for (long i = 1; i <= 10; i++) {
            studentService.save(new Student(i, "Студент " + i, "КН-21"));
        }

        List<Student> page1 = studentService.findAllPaged(3, 0);
        assertEquals(3, page1.size());
        assertEquals(1L, page1.get(0).getId());
        assertEquals(2L, page1.get(1).getId());
        assertEquals(3L, page1.get(2).getId());

        List<Student> page2 = studentService.findAllPaged(3, 3);
        assertEquals(3, page2.size());
        assertEquals(4L, page2.get(0).getId());
        assertEquals(5L, page2.get(1).getId());
        assertEquals(6L, page2.get(2).getId());
    }

    @Test
    void testFindByNameAndGroup() throws journal.exception.StudentNotFoundException {
        Student student = new Student(50L, "Леся Українка", "КН-23");
        studentService.save(student);

        Student found = studentService.findByNameAndGroup("Леся Українка", "КН-23");
        assertNotNull(found);
        assertEquals(50L, found.getId());
        assertEquals("Леся Українка", found.getName());

        assertThrows(journal.exception.StudentNotFoundException.class,
                () -> studentService.findByNameAndGroup("Невідомий", "КН-00"));
    }
}
