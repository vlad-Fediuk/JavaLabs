package journal.domain.model;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

public class AttendanceRecord extends BaseEntity {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(2000);
    private final long studentId;
    private final String subjectName;
    private final boolean wasPresent;
    private final String attendanceDate;

    public AttendanceRecord(long id, long studentId, String subjectName, boolean wasPresent, String attendanceDate) {
        super(id);
        this.studentId = studentId;
        this.subjectName = Objects.requireNonNull(subjectName, "Subject name must not be null");
        this.wasPresent = wasPresent;
        this.attendanceDate = Objects.requireNonNull(attendanceDate, "Attendance date must not be null");
    }

    public AttendanceRecord(long studentId, String subjectName, boolean wasPresent, String attendanceDate) {
        this(ID_GENERATOR.getAndIncrement(), studentId, subjectName, wasPresent, attendanceDate);
    }

    public long getStudentId() {
        return studentId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public boolean isWasPresent() {
        return wasPresent;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public Attendance toDomainAttendance() {
        return new Attendance(getId(), wasPresent, new Subject(subjectName), attendanceDate);
    }

    @Override
    public String toString() {
        return String.format("AttendanceRecord [ID: %d, StudentID: %d, Subject: %s, Present: %b, Date: %s]",
                getId(), studentId, subjectName, wasPresent, attendanceDate);
    }
}
