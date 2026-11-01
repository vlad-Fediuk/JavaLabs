package journal.service;

import java.util.Comparator;
import java.util.List;

import journal.domain.model.Student;

public class InMemoryStudentService extends InMemoryCrudService<Student> implements StudentService {
    @Override
    public List<Student> findAllSortedByNameAndByGroup() {
        return findAllSorted(Comparator.comparing(Student::getName).thenComparing(Student::getGroup));
    }
}
