package journal;

import java.util.List;

import journal.domain.model.Attendance;
import journal.domain.model.AttendanceRecord;
import journal.domain.model.Student;
import journal.domain.model.Subject;
import journal.repository.db.DatabaseInitializer;
import journal.service.JdbcAttendanceService;
import journal.service.JdbcStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JdbcAttendanceServiceTest {
    private JdbcStudentService studentService;
    private JdbcAttendanceService attendanceService;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialize();
        studentService = new JdbcStudentService();
        attendanceService = new JdbcAttendanceService();

        for (AttendanceRecord a : attendanceService.findAll()) {
            attendanceService.delete(a.getId());
        }
        for (Student s : studentService.findAll()) {
            studentService.delete(s.getId());
        }
    }

    @Test
    void testRecordAndFindAttendancesByStudentId() {
        Student student = new Student(301L, "Тарас Григорович", "КН-25");
        studentService.save(student);

        Subject java = new Subject("Java");
        Attendance a1 = new Attendance(501L, true, java, "2026-10-30");
        Attendance a2 = new Attendance(502L, false, java, "2026-10-31");

        attendanceService.recordAttendance(301L, a1);
        attendanceService.recordAttendance(301L, a2);

        List<AttendanceRecord> records = attendanceService.findByStudentId(301L);
        assertEquals(2, records.size());
        assertTrue(records.get(0).isWasPresent());
        assertFalse(records.get(1).isWasPresent());
        assertEquals("2026-10-30", records.get(0).getAttendanceDate());
    }

    @Test
    void testCascadeDeleteAttendancesOnStudentRemoval() {
        Student student = new Student(302L, "Іван Франко", "КН-25");
        studentService.save(student);

        Subject cs = new Subject("Cs");
        Attendance attendance = new Attendance(503L, true, cs, "2026-10-30");
        attendanceService.recordAttendance(302L, attendance);

        assertEquals(1, attendanceService.findByStudentId(302L).size());

        studentService.delete(302L);

        assertEquals(0, attendanceService.findByStudentId(302L).size());
    }
}
