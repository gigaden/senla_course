package ebookstore.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface KeyMapper<T> {

    void map(ResultSet keys, T entity) throws SQLException;
}
