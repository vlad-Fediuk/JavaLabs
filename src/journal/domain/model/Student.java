package journal.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import journal.domain.grade.GradedWork;

public class Student implements Comparable<Student>, Auditable {
    private final String name;
    private final String group;
    private final List<Attendance> attendances;
    private final List<GradedWork> grades;

    public Student(String name, String group) {
        this.name = name;
        this.group = group;
        this.attendances = new ArrayList<>();
        this.grades = new ArrayList<>();
    }

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
        return Double.compare(other.getAverageGrade(), this.getAverageGrade());
    }

    @Override
    public String getAuditSummary() {
        return String.format("Студент %s (%s): %d занять, %d оцінок, сер. бал: %.2f",
                name, group, attendances.size(), grades.size(), getAverageGrade());
    }

    @Override
    public String toString() {
        return String.format("Студент: %-18s | Група: %-6s | Відвідано: %2d/%-2d | Сер. бал: %5.2f | Оцінок: %2d",
                name, group, getAttendedCount(), attendances.size(), getAverageGrade(), grades.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Student other = (Student) obj;
        return Objects.equals(this.name, other.name) && Objects.equals(this.group, other.group);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, group);
    }
}
