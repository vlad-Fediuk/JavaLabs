package journal.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import journal.domain.model.Attendance;
import journal.domain.model.AttendanceRecord;

public class JdbcAttendanceService extends AbstractJdbcCrudService<AttendanceRecord> {
    private static final String FIND_BY_ID_SQL = "SELECT id, student_id, subject_name, was_present, attendance_date FROM attendances WHERE id = ?";
    private static final String INSERT_SQL = "INSERT INTO attendances (id, student_id, subject_name, was_present, attendance_date) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE attendances SET student_id = ?, subject_name = ?, was_present = ?, attendance_date = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM attendances WHERE id = ?";
    private static final String FIND_ALL_SQL = "SELECT id, student_id, subject_name, was_present, attendance_date FROM attendances";
    private static final String FIND_ALL_PAGED_SQL = "SELECT id, student_id, subject_name, was_present, attendance_date FROM attendances ORDER BY id ASC LIMIT ? OFFSET ?";
    private static final String EXISTS_BY_ID_SQL = "SELECT 1 FROM attendances WHERE id = ?";
    private static final String FIND_BY_STUDENT_ID_SQL = "SELECT id, student_id, subject_name, was_present, attendance_date FROM attendances WHERE student_id = ? ORDER BY id ASC";
    private static final String DELETE_BY_STUDENT_ID_SQL = "DELETE FROM attendances WHERE student_id = ?";

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
    protected AttendanceRecord mapRow(ResultSet rs) throws SQLException {
        return new AttendanceRecord(
                rs.getLong("id"),
                rs.getLong("student_id"),
                rs.getString("subject_name"),
                rs.getBoolean("was_present"),
                rs.getString("attendance_date")
        );
    }

    @Override
    protected void setInsertParameters(PreparedStatement ps, AttendanceRecord entity) throws SQLException {
        ps.setLong(1, entity.getId());
        ps.setLong(2, entity.getStudentId());
        ps.setString(3, entity.getSubjectName());
        ps.setBoolean(4, entity.isWasPresent());
        ps.setString(5, entity.getAttendanceDate());
    }

    @Override
    protected void setUpdateParameters(PreparedStatement ps, AttendanceRecord entity) throws SQLException {
        ps.setLong(1, entity.getStudentId());
        ps.setString(2, entity.getSubjectName());
        ps.setBoolean(3, entity.isWasPresent());
        ps.setString(4, entity.getAttendanceDate());
        ps.setLong(5, entity.getId());
    }

    public List<AttendanceRecord> findByStudentId(long studentId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_STUDENT_ID_SQL)) {
            statement.setLong(1, studentId);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<AttendanceRecord> list = new ArrayList<>();
                while (resultSet.next()) {
                    list.add(mapRow(resultSet));
                }
                return Collections.unmodifiableList(list);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to query attendances for student " + studentId, e);
        }
    }

    public List<Attendance> findDomainAttendancesByStudentId(long studentId) {
        List<AttendanceRecord> records = findByStudentId(studentId);
        List<Attendance> attendances = new ArrayList<>();
        for (AttendanceRecord record : records) {
            attendances.add(record.toDomainAttendance());
        }
        return Collections.unmodifiableList(attendances);
    }

    public void recordAttendance(long studentId, Attendance attendance) {
        AttendanceRecord record = new AttendanceRecord(
                attendance.getId(),
                studentId,
                attendance.getSubject() != null ? attendance.getSubject().getName() : "N/A",
                attendance.isWasPresent(),
                attendance.getDate()
        );
        save(record);
    }

    public void deleteByStudentId(long studentId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_BY_STUDENT_ID_SQL)) {
            statement.setLong(1, studentId);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete attendances for student " + studentId, e);
        }
    }
}
