import java.util.ArrayList;
import java.util.Objects;

public class Student {
    private String name;
    private String group;
    private ArrayList<Attendance> attendances;
    private ArrayList<Grade> grades;

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

    public ArrayList<Attendance> getAttendances() {
        return attendances;
    }

    public ArrayList<Grade> getGrades() {
        return grades;
    }

    public void addAttendance(Attendance attendance) {
        this.attendances.add(attendance);
    }

    public void addGrade(Grade grade) {
        this.grades.add(grade);
    }

    public double getAverageGrade() {
        if (grades.isEmpty()) return 0.0;
        double sum = 0;
        for (Grade g : grades) {
            sum += g.getValue();
        }
        return sum / grades.size();
    }

    public int getAttendedCount() {
        int count = 0;
        for (Attendance a : attendances) {
            if (a.isWasPresent()) count++;
        }
        return count;
    }

    @Override
    public String toString() {
        return String.format("Студент: %-18s | Група: %-6s | Відвідано: %2d/%-2d | Сер. бал: %5.2f | Оцінок: %2d",
                name, group, getAttendedCount(), attendances.size(), getAverageGrade(), grades.size());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Student other = (Student) obj;
        return Objects.equals(this.name, other.name) && Objects.equals(this.group, other.group);
    }
}
