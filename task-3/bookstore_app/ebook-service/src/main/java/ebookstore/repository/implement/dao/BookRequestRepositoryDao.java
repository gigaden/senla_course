package ebookstore.repository.implement.dao;

import di.annotation.Component;
import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookRequestStatus;
import ebookstore.repository.BookRequestRepository;
import ebookstore.util.ConnectionManager;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
public class BookRequestRepositoryDao extends BaseRepositoryDao implements BookRequestRepository {

    private static final String GET_ALL_QUERY =
            "SELECT * FROM requests";

    private static final String GET_BY_ID_QUERY =
            "SELECT * FROM requests WHERE id = ?";

    private static final String SAVE_QUERY = """
            INSERT INTO requests(book_id, client_id, request_status, created_on)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_QUERY = """
            UPDATE requests
            SET book_id = ?, client_id = ?, request_status = ?, created_on = ?
            WHERE id = ?
            """;

    private static final String UPDATE_STATUS_QUERY =
            "UPDATE requests SET request_status = ? WHERE id = ?";

    private static final String EXISTS_QUERY =
            "SELECT 1 FROM requests WHERE id = ?";

    public BookRequestRepositoryDao(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public Optional<BookRequest> getRequestById(long requestId) {
        return findOne(GET_BY_ID_QUERY, this::mapRequest, requestId);
    }

    @Override
    public Collection<BookRequest> getAllRequests() {
        return findAll(GET_ALL_QUERY, this::mapRequest);
    }

    @Override
    public BookRequest saveRequest(BookRequest request) {
        return save(
                SAVE_QUERY,
                (ps, r) -> {
                    ps.setLong(1, r.getBookId());
                    ps.setLong(2, r.getClientId());
                    ps.setString(3, r.getRequestStatus().name());
                    ps.setDate(4, Date.valueOf(r.getCreatedOn()));
                },
                (keys, r) -> r.setRequestId(keys.getLong(1)),
                request
        );
    }

    @Override
    public void changeRequestStatus(long requestId, BookRequestStatus status) {
        update(
                UPDATE_STATUS_QUERY,
                (ps, v) -> {
                    ps.setString(1, status.name());
                    ps.setLong(2, requestId);
                }
        );
    }

    @Override
    public boolean checkRequestIsExist(long requestId) {
        return exists(EXISTS_QUERY, requestId);
    }

    @Override
    public BookRequest updateRequest(BookRequest request) {
        return update(
                UPDATE_QUERY,
                (ps, r) -> {
                    ps.setLong(1, r.getBookId());
                    ps.setLong(2, r.getClientId());
                    ps.setString(3, r.getRequestStatus().name());
                    ps.setDate(4, Date.valueOf(r.getCreatedOn()));
                    ps.setLong(5, r.getRequestId());
                },
                request
        );
    }

    private BookRequest mapRequest(ResultSet rs) throws SQLException {
        BookRequest request = new BookRequest(
                rs.getLong("book_id"),
                rs.getLong("client_id")
        );
        request.setRequestId(rs.getLong("id"));
        request.setRequestStatus(
                BookRequestStatus.valueOf(rs.getString("request_status"))
        );
        request.setCreatedOn(rs.getDate("created_on").toLocalDate());
        return request;
    }
}
