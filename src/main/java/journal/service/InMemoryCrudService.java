package journal.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import journal.domain.model.BaseEntity;
import journal.exception.EntityNotFoundException;

public class InMemoryCrudService<T extends BaseEntity> implements CRUDService<T> {
    private final Map<Long, T> storage = new LinkedHashMap<>();

    @Override
    public T findById(long id) {
        T entity = storage.get(id);
        if (entity == null) {
            throw new EntityNotFoundException(id);
        }
        return entity;
    }

    @Override
    public void save(T entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        storage.put(entity.getId(), entity);
    }

    @Override
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }

    @Override
    public List<T> findAll() {
        return Collections.unmodifiableList(new ArrayList<>(storage.values()));
    }

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "Comparator must not be null");
        List<T> list = new ArrayList<>(storage.values());
        list.sort(comparator);
        return Collections.unmodifiableList(list);
    }

    public int size() {
        return storage.size();
    }
}
