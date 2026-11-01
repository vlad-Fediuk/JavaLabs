package journal.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import journal.domain.model.BaseEntity;
import journal.exception.EntityNotFoundException;
import journal.repository.db.DBConnection;

public abstract class AbstractJdbcCrudService<T extends BaseEntity> implements CRUDService<T> {

    protected abstract String getFindByIdSql();

    protected abstract String getInsertSql();

    protected abstract String getUpdateSql();

    protected abstract String getDeleteSql();

    protected abstract String getFindAllSql();

    protected abstract String getFindAllPagedSql();

    protected abstract String getExistsByIdSql();

    protected abstract T mapRow(ResultSet rs) throws SQLException;

    protected abstract void setInsertParameters(PreparedStatement ps, T entity) throws SQLException;

    protected abstract void setUpdateParameters(PreparedStatement ps, T entity) throws SQLException;

    protected Connection getConnection() throws SQLException {
        return DBConnection.getInstance().getConnection();
    }

    @Override
    public T findById(long id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getFindByIdSql())) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRow(resultSet);
                }
                throw new EntityNotFoundException(id);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find entity with id " + id, e);
        }
    }

    @Override
    public void save(T entity) {
        Objects.requireNonNull(entity, "Entity must not be null");
        if (existsById(entity.getId())) {
            update(entity);
        } else {
            insert(entity);
        }
    }

    private void insert(T entity) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getInsertSql())) {
            setInsertParameters(statement, entity);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to insert entity with id " + entity.getId(), e);
        }
    }

    private void update(T entity) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getUpdateSql())) {
            setUpdateParameters(statement, entity);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to update entity with id " + entity.getId(), e);
        }
    }

    @Override
    public boolean delete(long id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getDeleteSql())) {
            statement.setLong(1, id);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to delete entity with id " + id, e);
        }
    }

    @Override
    public List<T> findAll() {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getFindAllSql());
             ResultSet resultSet = statement.executeQuery()) {
            List<T> list = new ArrayList<>();
            while (resultSet.next()) {
                list.add(mapRow(resultSet));
            }
            return Collections.unmodifiableList(list);
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to find all entities", e);
        }
    }

    @Override
    public List<T> findAllSorted(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator, "Comparator must not be null");
        List<T> list = new ArrayList<>(findAll());
        list.sort(comparator);
        return Collections.unmodifiableList(list);
    }

    public List<T> findAllPaged(int limit, int offset) {
        if (limit < 0 || offset < 0) {
            throw new IllegalArgumentException("Limit and offset must be non-negative");
        }
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getFindAllPagedSql())) {
            statement.setInt(1, limit);
            statement.setInt(2, offset);
            try (ResultSet resultSet = statement.executeQuery()) {
                List<T> list = new ArrayList<>();
                while (resultSet.next()) {
                    list.add(mapRow(resultSet));
                }
                return Collections.unmodifiableList(list);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to execute paged query", e);
        }
    }

    public void saveBatch(List<T> entities) {
        Objects.requireNonNull(entities, "Entities must not be null");
        if (entities.isEmpty()) {
            return;
        }
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(getInsertSql())) {
                for (T entity : entities) {
                    setInsertParameters(statement, entity);
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw new IllegalStateException("Failed to execute batch insert, transaction rolled back", e);
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Database error during batch insert", e);
        }
    }

    public boolean existsById(long id) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(getExistsByIdSql())) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to check entity existence for id " + id, e);
        }
    }
}
