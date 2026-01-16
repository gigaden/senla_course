package ebookstore.repository.implement.dao;

import ebookstore.exception.DatabaseException;
import ebookstore.mapper.KeyMapper;
import ebookstore.mapper.RowMapper;
import ebookstore.mapper.StatementSetter;
import ebookstore.util.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class BaseRepositoryDao {

    protected final ConnectionManager connectionManager;

    protected BaseRepositoryDao(ConnectionManager connectionManager) {
        this.connectionManager = connectionManager;
    }

    protected <T> List<T> findAll(String sql, RowMapper<T> mapper
    ) {
        List<T> result = new ArrayList<>();

        try (Connection connection = connectionManager.get();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                result.add(mapper.map(rs));
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка выполнения запроса: " + e.getMessage());
        }

        return result;
    }

    protected <T> T save(String sql, StatementSetter<T> setter, KeyMapper<T> keyMapper, T entity
    ) {
        try (Connection connection = connectionManager.get();
             PreparedStatement ps = connection.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS
             )) {

            setter.set(ps, entity);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    keyMapper.map(keys, entity);
                } else {
                    throw new DatabaseException("Не удалось получить сгенерированный id");
                }
            }

            return entity;

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка сохранения сущности" + e);
        }
    }

    protected <T> Optional<T> findOne(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection connection = connectionManager.get();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper.map(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка выполнения findOne" + e);
        }
    }

    protected <T> T update(String sql, StatementSetter<T> setter, T entity) {
        try (Connection connection = connectionManager.get();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            setter.set(ps, entity);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DatabaseException("Обновление не затронуло ни одной строки");
            }

            return entity;

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка обновления сущности" + e);
        }
    }

    protected void update(String sql, StatementSetter<Void> setter) {
        try (Connection connection = connectionManager.get();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            setter.set(ps, null);

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new DatabaseException("Обновление не затронуло ни одной строки");
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка update" + e);
        }
    }


    protected void delete(String sql, Object... params) {
        try (Connection connection = connectionManager.get();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка удаления" + e);
        }
    }


    protected boolean exists(String sql, Object... params) {
        try (Connection connection = connectionManager.get();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new DatabaseException("Ошибка проверки существования" + e);
        }
    }


}
