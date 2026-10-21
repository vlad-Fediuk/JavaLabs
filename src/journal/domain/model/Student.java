package journal.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import journal.domain.grade.GradedWork;

public class Student extends BaseEntity implements Nameable, Comparable<Student>, Auditable {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);
    private final String name;
    private final String group;
    private final List<Attendance> attendances;
    private final List<GradedWork> grades;

    public Student(long id, String name, String group) {
        super(id);
        this.name = Objects.requireNonNull(name, "Name must not be null");
        this.group = Objects.requireNonNull(group, "Group must not be null");
        this.attendances = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

    public Student(String name, String group) {
        this(ID_GENERATOR.getAndIncrement(), name, group);
    }

    @Override
    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public List<Attendance> getAttendances() {
        return Collections.unmodifiableList(attendances);
    }

    public List<GradedWork> getGradedWorks() {
        return Collections.unmodifiableList(grades);
    }

    public void addAttendance(Attendance attendance) {
        this.attendances.add(attendance);
    }

    public void addGrade(GradedWork grade) {
        this.grades.add(grade);
    }

    public double getAverageGrade() {
        if (grades.isEmpty()) {
            return 0.0;
        }
        double sum = 0.0;
        for (GradedWork g : grades) {
            sum += g.getEffectiveScore();
        }
        return sum / grades.size();
    }

    public int getAttendedCount() {
        int count = 0;
        for (Attendance a : attendances) {
            if (a.isWasPresent()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public int compareTo(Student other) {
        int nameComparison = this.name.compareTo(other.name);
        if (nameComparison != 0) {
            return nameComparison;
        }
        return this.group.compareTo(other.group);
    }

    @Override
    public String getAuditSummary() {
        return String.format("Студент [ID: %d] %s (%s): %d занять, %d оцінок, сер. бал: %.2f",
                getId(), name, group, attendances.size(), grades.size(), getAverageGrade());
    }

    @Override
    public String toString() {
        return String.format("Студент [ID: %2d]: %-18s | Група: %-6s | Відвідано: %2d/%-2d | Сер. бал: %5.2f | Оцінок: %2d",
                getId(), name, group, getAttendedCount(), attendances.size(), getAverageGrade(), grades.size());
    }
}
