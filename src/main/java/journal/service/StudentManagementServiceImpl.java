package journal.service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import journal.domain.model.Student;
import journal.exception.StudentNotFoundException;
import journal.repository.Journal;

public class StudentManagementServiceImpl implements StudentManagementService {
    private final Journal journal;
    private final StudentService studentService;

    public StudentManagementServiceImpl(Journal journal) {
        this(journal, new JdbcStudentService());
    }

    public StudentManagementServiceImpl(Journal journal, StudentService studentService) {
        this.journal = Objects.requireNonNull(journal, "Journal must not be null");
        this.studentService = Objects.requireNonNull(studentService, "StudentService must not be null");
    }

    @Override
    public void registerStudent(String name, String group) {
        Student student = new Student(name, group);
        journal.addStudent(student);
    }

    @Override
    public boolean hasStudents() {
        return !studentService.findAll().isEmpty();
    }

    @Override
    public int getStudentCount() {
        return studentService.findAll().size();
    }

    @Override
    public List<Student> getAllStudents() {
        return studentService.findAll();
    }

    @Override
    public Student findStudent(String name, String group) throws StudentNotFoundException {
        return journal.findStudent(name, group);
    }

    @Override
    public Student findById(long id) {
        return studentService.findById(id);
    }

    @Override
    public void save(Student entity) {
        studentService.save(entity);
    }

    @Override
    public boolean delete(long id) {
        return studentService.delete(id);
    }

    @Override
    public List<Student> findAll() {
        return studentService.findAll();
    }

    @Override
    public List<Student> findAllSorted(Comparator<? super Student> comparator) {
        return studentService.findAllSorted(comparator);
    }

    @Override
    public List<Student> findAllSortedByName() {
        return studentService.findAllSortedByName();
    }

    @Override
    public List<Student> findAllSortedByNameAndByGroup() {
        return studentService.findAllSortedByNameAndByGroup();
    }
}
