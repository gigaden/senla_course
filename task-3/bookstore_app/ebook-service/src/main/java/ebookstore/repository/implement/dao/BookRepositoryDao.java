package ebookstore.repository.implement.dao;

import di.annotation.Component;
import ebookstore.model.Book;
import ebookstore.model.enums.BookStatus;
import ebookstore.repository.BookRepository;
import ebookstore.util.ConnectionManager;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

@Component
public class BookRepositoryDao extends BaseRepositoryDao implements BookRepository {

    private static final String GET_ALL_QUERY = "SELECT * FROM books";
    private static final String SAVE_BOOK_QUERY = """
            INSERT INTO books(title, author, description, date_of_publication, price, status)
            VALUES(?, ?, ?, ?, ?, ?);
            """;
    private static final String GET_BY_ID_QUERY =
            "SELECT * FROM books WHERE id = ?";
    private static final String UPDATE_BOOK_QUERY = """
            UPDATE books
            SET title = ?, author = ?, description = ?, date_of_publication = ?, price = ?, status = ?
            WHERE id = ?
            """;
    private static final String DELETE_BOOK_QUERY =
            "DELETE FROM books WHERE id = ?";
    private static final String EXISTS_BOOK_QUERY =
            "SELECT 1 FROM books WHERE id = ?";


    public BookRepositoryDao(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public Collection<Book> getAllBooks() {
        return findAll(GET_ALL_QUERY, this::createBookFromResultSet);
    }

    @Override
    public Book saveBook(Book book) {
        return save(
                SAVE_BOOK_QUERY,
                (ps, b) -> {
                    ps.setString(1, b.getTitle());
                    ps.setString(2, b.getAuthor());
                    ps.setString(3, b.getDescription());
                    ps.setDate(4, Date.valueOf(b.getDateOfPublication()));
                    ps.setDouble(5, b.getPrice());
                    ps.setString(6, b.getStatus().name());
                },
                (keys, b) -> b.setId(keys.getLong(1)),
                book
        );
    }


    @Override
    public Optional<Book> getBook(long bookId) {
        return findOne(GET_BY_ID_QUERY, this::createBookFromResultSet, bookId);
    }

    @Override
    public Book updateBook(Book book) {
        return update(
                UPDATE_BOOK_QUERY,
                (ps, b) -> {
                    ps.setString(1, b.getTitle());
                    ps.setString(2, b.getAuthor());
                    ps.setString(3, b.getDescription());
                    ps.setDate(4, Date.valueOf(b.getDateOfPublication()));
                    ps.setDouble(5, b.getPrice());
                    ps.setString(6, b.getStatus().name());
                    ps.setLong(7, b.getId());
                },
                book
        );
    }

    @Override
    public void deleteBook(long bookId) {
        delete(DELETE_BOOK_QUERY, bookId);
    }

    @Override
    public boolean checkBookIsExist(long bookId) {
        return exists(EXISTS_BOOK_QUERY, bookId);
    }

    private Book createBookFromResultSet(ResultSet resultSet) throws SQLException {
        Book book = new Book(
                resultSet.getString("title"),
                resultSet.getString("author"),
                resultSet.getString("description"),
                resultSet.getDate("date_of_publication").toLocalDate(),
                resultSet.getDouble("price")
        );
        book.setId(resultSet.getLong("id"));
        book.setStatus(BookStatus.valueOf(resultSet.getString("status")));

        return book;
    }
}
