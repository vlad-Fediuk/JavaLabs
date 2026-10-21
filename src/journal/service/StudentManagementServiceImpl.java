package journal.service;

import java.util.List;

import journal.domain.model.Student;
import journal.exception.StudentNotFoundException;
import journal.repository.Journal;

public class StudentManagementServiceImpl extends InMemoryStudentService implements StudentManagementService {
    private final Journal journal;

    public StudentManagementServiceImpl(Journal journal) {
        this.journal = journal;
    }

    @Override
    public void registerStudent(String name, String group) {
        Student student = new Student(name, group);
        save(student);
        journal.addStudent(student);
    }

    @Override
    public boolean hasStudents() {
        return !journal.isEmpty();
    }

    @Override
    public int getStudentCount() {
        return journal.size();
    }

    @Override
    public List<Student> getAllStudents() {
        return journal.getStudents();
    }

    @Override
    public Student findStudent(String name, String group) throws StudentNotFoundException {
        return journal.findStudent(name, group);
    }
}
