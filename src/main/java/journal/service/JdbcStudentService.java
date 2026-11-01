package journal.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import journal.domain.grade.GradedWork;
import journal.domain.model.Attendance;
import journal.domain.model.Student;
import journal.exception.StudentNotFoundException;

public class JdbcStudentService extends AbstractJdbcCrudService<Student> implements StudentService {
    private static final String FIND_BY_ID_SQL = "SELECT id, name, student_group FROM students WHERE id = ?";
    private static final String INSERT_SQL = "INSERT INTO students (id, name, student_group) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE students SET name = ?, student_group = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM students WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id, name, student_group FROM students ORDER BY id ASC";
    private static final String FIND_ALL_PAGED_SQL = "SELECT id, name, student_group FROM students ORDER BY id ASC LIMIT ? OFFSET ?";
    private static final String EXISTS_BY_ID_SQL = "SELECT 1 FROM students WHERE id = ?";
    private static final String FIND_ALL_SORTED_BY_NAME_SQL = "SELECT id, name, student_group FROM students ORDER BY name ASC";
    private static final String FIND_ALL_SORTED_BY_NAME_AND_GROUP_SQL = "SELECT id, name, student_group FROM students ORDER BY name ASC, student_group ASC";
    private static final String FIND_BY_NAME_AND_GROUP_SQL = "SELECT id, name, student_group FROM students WHERE LOWER(name) = LOWER(?) AND LOWER(student_group) = LOWER(?)";

    private final JdbcAttendanceService attendanceService;
    private final JdbcGradeRecordService gradeService;

    public JdbcStudentService() {
        this(new JdbcAttendanceService(), new JdbcGradeRecordService());
    }

    public JdbcStudentService(JdbcAttendanceService attendanceService, JdbcGradeRecordService gradeService) {
        this.attendanceService = attendanceService;
        this.gradeService = gradeService;
    }

    @Override
    protected String getFindByIdSql() {
        return FIND_BY_ID_SQL;
    }

    @Override
    protected String getInsertSql() {
        return INSERT_SQL;
    }

    @Override
    protected String getUpdateSql() {
        return UPDATE_SQL;
    }

    @Override
    protected String getDeleteSql() {
        return DELETE_SQL;
    }

    @Override
    protected String getFindAllSql() {
        return FIND_ALL_SQL;
    }

    @Override
    protected String getFindAllPagedSql() {
        return FIND_ALL_PAGED_SQL;
    }

    @Override
    protected String getExistsByIdSql() {
        return EXISTS_BY_ID_SQL;
    }

    @Override
    protected Student mapRow(ResultSet rs) throws SQLException {
        return new Student(
                rs.getLong("id"),
                rs.getString("name"),
                rs.getString("student_group")
        );
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, Student entity) throws SQLException {
        ps.setLong(1, entity.getId());
        ps.setString(2, entity.getName());
        ps.setString(3, entity.getGroup());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, Student entity) throws SQLException {
        ps.setString(1, entity.getName());
        ps.setString(2, entity.getGroup());
        ps.setLong(3, entity.getId());
    }

    private void hydrate(Student student) {
        if (student == null) {
            return;
        }
        if (student.getAttendances().isEmpty()) {
            List<Attendance> attendances = attendanceService.findDomainAttendancesByStudentId(student.getId());
            for (Attendance attendance : attendances) {
                student.addAttendance(attendance);
            }
        }
        if (student.getGradedWorks().isEmpty()) {
            List<GradedWork> works = gradeService.findGradedWorksByStudentId(student.getId());
            for (GradedWork work : works) {
                student.addGrade(work);
            }
        }
    }

    @Override
    public Student findById(long id) {
        Student student = super.findById(id);
        hydrate(student);
        return student;
    }

    @Override
    public List<Student> findAll() {
        List<Student> students = super.findAll();
        for (Student student : students) {
            hydrate(student);
        }
        return students;
    }

    @Override
    public List<Student> findAllPaged(int limit, int offset) {
        List<Student> students = super.findAllPaged(limit, offset);
        for (Student student : students) {
            hydrate(student);
        }
        return students;
    }

    @Override
    public List<Student> findAllSortedByName() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SORTED_BY_NAME_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            List<Student> list = new ArrayList<>();
            while (resultSet.next()) {
                Student student = mapRow(resultSet);
                hydrate(student);
                list.add(student);
            }
            return Collections.unmodifiableList(list);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query students sorted by name", e);
        }
    }

    @Override
    public List<Student> findAllSortedByNameAndByGroup() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SORTED_BY_NAME_AND_GROUP_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            List<Student> list = new ArrayList<>();
            while (resultSet.next()) {
                Student student = mapRow(resultSet);
                hydrate(student);
                list.add(student);
            }
            return Collections.unmodifiableList(list);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query students sorted by name and group", e);
        }
    }

    public Student findByNameAndGroup(String name, String group) throws StudentNotFoundException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_NAME_AND_GROUP_SQL)) {
            statement.setString(1, name);
            statement.setString(2, group);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Student student = mapRow(resultSet);
                    hydrate(student);
                    return student;
                }
                throw new StudentNotFoundException("Студента не знайдено в базі даних", name + " (" + group + ")");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find student by name and group", e);
        }
    }
}
