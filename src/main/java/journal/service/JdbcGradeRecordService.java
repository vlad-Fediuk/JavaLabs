package journal.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import journal.domain.grade.GradedWork;
import journal.domain.model.GradeRecord;
import journal.domain.model.Student;

public class JdbcGradeRecordService extends AbstractJdbcCrudService<GradeRecord> {
    private static final String FIND_BY_ID_SQL = "SELECT id, student_id, subject_name, work_type, score, work_date, was_present, extra_info FROM grades WHERE id = ?";
    private static final String INSERT_SQL = "INSERT INTO grades (id, student_id, subject_name, work_type, score, work_date, was_present, extra_info) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE grades SET student_id = ?, subject_name = ?, work_type = ?, score = ?, work_date = ?, was_present = ?, extra_info = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM grades WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id, student_id, subject_name, work_type, score, work_date, was_present, extra_info FROM grades";
    private static final String FIND_ALL_PAGED_SQL = "SELECT id, student_id, subject_name, work_type, score, work_date, was_present, extra_info FROM grades ORDER BY id ASC LIMIT ? OFFSET ?";
    private static final String EXISTS_BY_ID_SQL = "SELECT 1 FROM grades WHERE id = ?";
    private static final String FIND_BY_STUDENT_ID_SQL = "SELECT id, student_id, subject_name, work_type, score, work_date, was_present, extra_info FROM grades WHERE student_id = ? ORDER BY id ASC";
    private static final String INSERT_STUDENT_SQL = "INSERT INTO students (id, name, student_group) VALUES (?, ?, ?)";
    private static final String TRANSFER_GRADES_SQL = "UPDATE grades SET student_id = ? WHERE student_id = ?";

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
    protected GradeRecord mapRow(ResultSet rs) throws SQLException {
        return new GradeRecord(
                rs.getLong("id"),
                rs.getLong("student_id"),
                rs.getString("subject_name"),
                rs.getString("work_type"),
                rs.getDouble("score"),
                rs.getString("work_date"),
                rs.getBoolean("was_present"),
                rs.getDouble("extra_info")
        );
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, GradeRecord entity) throws SQLException {
        ps.setLong(1, entity.getId());
        ps.setLong(2, entity.getStudentId());
        ps.setString(3, entity.getSubjectName());
        ps.setString(4, entity.getWorkType());
        ps.setDouble(5, entity.getScore());
        ps.setString(6, entity.getWorkDate());
        ps.setBoolean(7, entity.isWasPresent());
        ps.setDouble(8, entity.getExtraInfo());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, GradeRecord entity) throws SQLException {
        ps.setLong(1, entity.getStudentId());
        ps.setString(2, entity.getSubjectName());
        ps.setString(3, entity.getWorkType());
        ps.setDouble(4, entity.getScore());
        ps.setString(5, entity.getWorkDate());
        ps.setBoolean(6, entity.isWasPresent());
        ps.setDouble(7, entity.getExtraInfo());
        ps.setLong(8, entity.getId());
    }

    public List<GradeRecord> findByStudentId(long studentId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_STUDENT_ID_SQL)) {
            statement.setLong(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<GradeRecord> list = new ArrayList<>();
                while (resultSet.next()) {
                    list.add(mapRow(resultSet));
                }
                return Collections.unmodifiableList(list);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find grades for student id " + studentId, e);
        }
    }

    public List<GradedWork> findGradedWorksByStudentId(long studentId) {
        List<GradeRecord> records = findByStudentId(studentId);
        List<GradedWork> works = new ArrayList<>();
        for (GradeRecord record : records) {
            works.add(record.toGradedWork());
        }
        return Collections.unmodifiableList(works);
    }

    public void recordGradedWork(long studentId, GradedWork work, String fallbackSubject, String fallbackDate) {
        GradeRecord record = GradeRecord.fromGradedWork(studentId, work, fallbackSubject, fallbackDate);
        save(record);
    }

    public void registerStudentWithGrades(Student student, List<GradeRecord> grades) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement studentStatement = connection.prepareStatement(INSERT_STUDENT_SQL)) {
                    studentStatement.setLong(1, student.getId());
                    studentStatement.setString(2, student.getName());
                    studentStatement.setString(3, student.getGroup());
                    studentStatement.executeUpdate();
                }

                try (PreparedStatement gradeStatement = connection.prepareStatement(INSERT_SQL)) {
                    for (GradeRecord grade : grades) {
                        setInsertParameters(gradeStatement, grade);
                        gradeStatement.executeUpdate();
                    }
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new IllegalStateException("Transaction failed during student and grades registration, rolled back", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error during transactional operation", e);
        }
    }

    public void transferStudentGrades(long fromStudentId, long toStudentId, boolean simulateFailure) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(TRANSFER_GRADES_SQL)) {
                    statement.setLong(1, toStudentId);
                    statement.setLong(2, fromStudentId);
                    statement.executeUpdate();
                }

                if (simulateFailure) {
                    throw new IllegalStateException("Simulated business validation failure triggering rollback");
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw new IllegalStateException("Transaction failed during grade transfer, rolled back: " + e.getMessage(), e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error during grade transfer transaction", e);
        }
    }
}
