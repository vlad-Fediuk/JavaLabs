package journal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import journal.domain.grade.GradedWork;
import journal.domain.model.Attendance;
import journal.domain.model.Student;
import journal.exception.StudentNotFoundException;
import journal.observer.JournalEvent;
import journal.observer.JournalObserver;
import journal.strategy.GradingStrategy;
import journal.strategy.StandardSemesterStrategy;

public class Journal {
    private final List<Student> students;
    private final List<JournalObserver> observers;
    private GradingStrategy gradingStrategy;

    public Journal() {
        this(new StandardSemesterStrategy());
    }

    public Journal(GradingStrategy gradingStrategy) {
        this.students = new ArrayList<>();
        this.observers = new ArrayList<>();
        this.gradingStrategy = gradingStrategy;
    }

    public void registerObserver(JournalObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(JournalObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers(JournalEvent event) {
        for (JournalObserver observer : new ArrayList<>(observers)) {
            observer.onEvent(event);
        }
    }

    public void setGradingStrategy(GradingStrategy gradingStrategy) {
        this.gradingStrategy = gradingStrategy;
        notifyObservers(new JournalEvent(JournalEvent.EventType.STRATEGY_CHANGED, null,
                gradingStrategy.getStrategyName()));
    }

    public GradingStrategy getGradingStrategy() {
        return gradingStrategy;
    }

    public void addStudent(Student student) {
        students.add(student);
        notifyObservers(new JournalEvent(JournalEvent.EventType.STUDENT_ADDED, student, null));
    }

    public void addGrade(Student student, GradedWork work) {
        student.addGrade(work);
        notifyObservers(new JournalEvent(JournalEvent.EventType.GRADE_ADDED, student, work));
    }

    public void addAttendance(Student student, Attendance attendance) {
        student.addAttendance(attendance);
        notifyObservers(new JournalEvent(JournalEvent.EventType.ATTENDANCE_ADDED, student, attendance));
    }

    public List<Student> getStudents() {
        return Collections.unmodifiableList(students);
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
        for (Student s : students) {
            if (s.getName().equalsIgnoreCase(name) && s.getGroup().equalsIgnoreCase(group)) {
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
