package ebookstore.mapper;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementSetter<T> {

    void set(PreparedStatement ps, T entity) throws SQLException;
}
