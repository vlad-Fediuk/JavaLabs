import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Journal {
    private final ArrayList<Student> students;
    private GradingStrategy gradingStrategy;

    public Journal() {
        this.students = new ArrayList<>();
        this.gradingStrategy = new StandardSemesterStrategy();
    }

    public Journal(GradingStrategy gradingStrategy) {
        this.students = new ArrayList<>();
        this.gradingStrategy = gradingStrategy;
    }

    public void setGradingStrategy(GradingStrategy gradingStrategy) {
        this.gradingStrategy = gradingStrategy;
    }

    public GradingStrategy getGradingStrategy() {
        return gradingStrategy;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public List<Student> getStudents() {
        return students;
    }

    public boolean isEmpty() {
        return students.isEmpty();
    }

    public int size() {
        return students.size();
    }

    public Student get(int index) {
        return students.get(index);
    }

    public double calculateStudentScore(Student student) {
        return gradingStrategy.calculateFinalScore(student);
    }

    public Student findStudent(String name, String group) throws StudentNotFoundException {
        Student target = new Student(name, group);
        for (Student s : students) {
            if (target.equals(s)) {
                return s;
            }
        }
        throw new StudentNotFoundException("Студента не знайдено в базі даних", name + " (" + group + ")");
    }

    public void bubbleSortByStrategyScore() {
        int n = students.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                double score1 = calculateStudentScore(students.get(j));
                double score2 = calculateStudentScore(students.get(j + 1));
                if (score1 < score2) {
                    Student temp = students.get(j);
                    students.set(j, students.get(j + 1));
                    students.set(j + 1, temp);
                }
            }
        }
    }

    public void sortUsingComparable() {
        Collections.sort(students);
    }

    public int countPassedStudents(double threshold) {
        int count = 0;
        for (Student s : students) {
            if (calculateStudentScore(s) >= threshold) {
                count++;
            }
        }
        return count;
    }
}
