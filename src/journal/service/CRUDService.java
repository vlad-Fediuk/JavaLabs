package journal.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import journal.domain.model.BaseEntity;

public interface CRUDService<T extends BaseEntity> {
    T findById(long id);

    void save(T entity);

    boolean delete(long id);

    List<T> findAll();

    List<T> findAllSorted(Comparator<? super T> comparator);

    static void printAll(Collection<? extends BaseEntity> items) {
        if (items == null || items.isEmpty()) {
            System.out.println("Колекція порожня.");
            return;
        }
        for (BaseEntity item : items) {
            System.out.println(item);
        }
    }
}
