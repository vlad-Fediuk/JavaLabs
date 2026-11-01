package journal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import journal.domain.model.Student;
import journal.repository.db.DBConnection;
import journal.repository.db.DatabaseInitializer;
import journal.service.JdbcStudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BatchInsertBenchmarkTest {
    private static final int RECORD_COUNT = 1000;
    private JdbcStudentService studentService;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialize();
        studentService = new JdbcStudentService();
        clearTable();
    }

    private void clearTable() {
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM students")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to clear table", e);
        }
    }

    private long measureSingleInserts(List<Student> students) {
        long startTime = System.currentTimeMillis();
        for (Student student : students) {
            studentService.save(student);
        }
        return System.currentTimeMillis() - startTime;
    }

    private long measureBatchInsert(List<Student> students) {
        long startTime = System.currentTimeMillis();
        studentService.saveBatch(students);
        return System.currentTimeMillis() - startTime;
    }

    @Test
    void testBenchmarkSingleVsBatchInsert() {
        List<Student> singleInsertBatch = new ArrayList<>();
        for (int i = 1; i <= RECORD_COUNT; i++) {
            singleInsertBatch.add(new Student(i, "Single Student " + i, "КН-21"));
        }

        long singleTimeMs = measureSingleInserts(singleInsertBatch);
        assertEquals(RECORD_COUNT, studentService.findAll().size());

        clearTable();

        List<Student> batchInsertList = new ArrayList<>();
        for (int i = 1; i <= RECORD_COUNT; i++) {
            batchInsertList.add(new Student(i, "Batch Student " + i, "КН-22"));
        }

        long batchTimeMs = measureBatchInsert(batchInsertList);
        assertEquals(RECORD_COUNT, studentService.findAll().size());

        System.out.println("Single insert duration for " + RECORD_COUNT + " records: " + singleTimeMs + " ms");
        System.out.println("Batch insert duration for " + RECORD_COUNT + " records: " + batchTimeMs + " ms");

        assertTrue(batchTimeMs <= singleTimeMs, "Batch insert should be faster or comparable to single inserts");
    }
}
