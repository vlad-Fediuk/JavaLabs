package journal.service;

import java.util.Comparator;
import java.util.List;

import journal.domain.model.BaseEntity;
import journal.domain.model.Nameable;

public interface NameableCRUDService<T extends BaseEntity & Nameable> extends CRUDService<T> {
    default List<T> findAllSortedByName() {
        return findAllSorted(Comparator.comparing(Nameable::getName));
    }
}
