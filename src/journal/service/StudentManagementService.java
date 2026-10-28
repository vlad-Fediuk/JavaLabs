package journal.service;

import java.util.List;

import journal.domain.model.Student;
import journal.exception.StudentNotFoundException;

public interface StudentManagementService extends StudentService {
    void registerStudent(String name, String group);

    boolean hasStudents();

    int getStudentCount();

    List<Student> getAllStudents();

    Student findStudent(String name, String group) throws StudentNotFoundException;
}
