package journal.service;

import java.util.List;

import journal.domain.model.Student;

public interface StudentService extends NameableCRUDService<Student> {
    List<Student> findAllSortedByNameAndByGroup();
}
