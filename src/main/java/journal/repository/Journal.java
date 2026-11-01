package journal.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import journal.domain.grade.GradedWork;
import journal.domain.model.Attendance;
import journal.domain.model.Student;
import journal.exception.StudentNotFoundException;
import journal.observer.JournalEvent;
import journal.observer.JournalObserver;
import journal.service.JdbcStudentService;
import journal.service.StudentService;
import journal.strategy.GradingStrategy;
import journal.strategy.StandardSemesterStrategy;

public class Journal {
    private final StudentService studentService;
    private final List<JournalObserver> observers;
    private GradingStrategy gradingStrategy;

    public Journal() {
        this(new StandardSemesterStrategy());
    }

    public Journal(GradingStrategy gradingStrategy) {
        this(gradingStrategy, new JdbcStudentService());
    }

    public Journal(GradingStrategy gradingStrategy, StudentService studentService) {
        this.gradingStrategy = Objects.requireNonNull(gradingStrategy, "Grading strategy must not be null");
        this.studentService = Objects.requireNonNull(studentService, "Student service must not be null");
        this.observers = new ArrayList<>();
    }

    public void registerObserver(JournalObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(JournalObserver observer) {
        observers.remove(observer);
    }

    public void notifyObservers(JournalEvent event) {
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
        studentService.save(student);
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
        return studentService.findAll();
    }

    public boolean isEmpty() {
        return getStudents().isEmpty();
    }

    public int size() {
        return getStudents().size();
    }

    public Student get(int index) {
        return getStudents().get(index);
    }

    public double calculateStudentScore(Student student) {
        return gradingStrategy.calculateFinalScore(student);
    }

    public Student findStudent(String name, String group) throws StudentNotFoundException {
        if (studentService instanceof JdbcStudentService jdbcStudentService) {
            return jdbcStudentService.findByNameAndGroup(name, group);
        }
        for (Student s : getStudents()) {
            if (s.getName().equalsIgnoreCase(name) && s.getGroup().equalsIgnoreCase(group)) {
                return s;
            }
        }
        throw new StudentNotFoundException("Студента не знайдено в базі даних", name + " (" + group + ")");
    }

    public List<Student> sortStudentsByStrategy(List<Student> students) {
        List<Student> sorted = new ArrayList<>(students);
        sorted.sort(Comparator.comparingDouble(this::calculateStudentScore).reversed()
                .thenComparing(Student::getName));
        return Collections.unmodifiableList(sorted);
    }

    public int countPassedStudents(double threshold) {
        int count = 0;
        for (Student s : getStudents()) {
            if (calculateStudentScore(s) >= threshold) {
                count++;
            }
        }
        return count;
    }
}
