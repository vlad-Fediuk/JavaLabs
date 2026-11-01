package journal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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

class SqlInjectionSafetyTest {
    private JdbcStudentService studentService;

    @BeforeEach
    void setUp() {
        DatabaseInitializer.initialize();
        studentService = new JdbcStudentService();

        for (Student s : studentService.findAll()) {
            studentService.delete(s.getId());
        }

        studentService.save(new Student(1L, "Олександр", "КН-21"));
        studentService.save(new Student(2L, "Світлана", "КН-22"));
        studentService.save(new Student(3L, "Михайло", "КН-21"));
    }

    private List<Student> vulnerableSearchByName(String userInput) throws SQLException {
        List<Student> results = new ArrayList<>();
        String query = "SELECT id, name, student_group FROM students WHERE name = '" + userInput + "'";
        try (Connection connection = DBConnection.getInstance().getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                results.add(new Student(
                        resultSet.getLong("id"),
                        resultSet.getString("name"),
                        resultSet.getString("student_group")
                ));
            }
        }
        return results;
    }

    private List<Student> secureSearchByName(String userInput) throws SQLException {
        List<Student> results = new ArrayList<>();
        String query = "SELECT id, name, student_group FROM students WHERE name = ?";
        try (Connection connection = DBConnection.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, userInput);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    results.add(new Student(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            resultSet.getString("student_group")
                    ));
                }
            }
        }
        return results;
    }

    @Test
    void testSqlInjectionVulnerabilityOnStatement() throws SQLException {
        String injectionPayload = "' OR '1'='1";

        List<Student> leakedStudents = vulnerableSearchByName(injectionPayload);

        assertEquals(3, leakedStudents.size());
        assertTrue(leakedStudents.stream().anyMatch(s -> s.getName().equals("Олександр")));
        assertTrue(leakedStudents.stream().anyMatch(s -> s.getName().equals("Світлана")));
        assertTrue(leakedStudents.stream().anyMatch(s -> s.getName().equals("Михайло")));
    }

    @Test
    void testSqlInjectionPreventionOnPreparedStatement() throws SQLException {
        String injectionPayload = "' OR '1'='1";

        List<Student> securedResult = secureSearchByName(injectionPayload);

        assertTrue(securedResult.isEmpty());
    }
}
