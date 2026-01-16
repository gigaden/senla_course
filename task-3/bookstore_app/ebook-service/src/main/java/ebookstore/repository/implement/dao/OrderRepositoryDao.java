package ebookstore.repository.implement.dao;

import di.annotation.Component;
import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;
import ebookstore.util.ConnectionManager;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

@Component
public class OrderRepositoryDao extends BaseRepositoryDao implements OrderRepository {

    private static final String GET_ALL_QUERY =
            "SELECT * FROM orders";

    private static final String GET_BY_ID_QUERY =
            "SELECT * FROM orders WHERE id = ?";

    private static final String CREATE_ORDER_QUERY = """
            INSERT INTO orders(book_id, client_id, created_on, order_status)
            VALUES (?, ?, ?, ?)
            """;

    private static final String UPDATE_ORDER_QUERY = """
            UPDATE orders
            SET book_id = ?, client_id = ?, created_on = ?, completed_on = ?, order_status = ?
            WHERE id = ?
            """;

    private static final String UPDATE_STATUS_QUERY = """
            UPDATE orders
            SET order_status = ?, completed_on = ?
            WHERE id = ?
            """;

    private static final String EXISTS_QUERY =
            "SELECT 1 FROM orders WHERE id = ?";

    public OrderRepositoryDao(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public Optional<Order> getOrderById(long orderId) {
        return findOne(GET_BY_ID_QUERY, this::mapOrder, orderId);
    }

    @Override
    public Collection<Order> getAllOrders() {
        return findAll(GET_ALL_QUERY, this::mapOrder);
    }

    @Override
    public Order createOrder(Order order) {
        return save(
                CREATE_ORDER_QUERY,
                (ps, o) -> {
                    ps.setLong(1, o.getBook().getId());
                    ps.setLong(2, o.getClient().getId());
                    ps.setDate(3, Date.valueOf(o.getCreatedOn()));
                    ps.setString(4, o.getOrderStatus().name());
                },
                (keys, o) -> o.setId(keys.getLong(1)),
                order
        );
    }

    @Override
    public void changeOrderStatus(long orderId, OrderStatus status) {
        update(
                UPDATE_STATUS_QUERY,
                (ps, v) -> {
                    ps.setString(1, status.name());
                    ps.setDate(2,
                            status == OrderStatus.COMPLETED
                                    ? Date.valueOf(LocalDate.now())
                                    : null);
                    ps.setLong(3, orderId);
                }
        );
    }

    @Override
    public boolean checkOrderIsExist(long orderId) {
        return exists(EXISTS_QUERY, orderId);
    }

    @Override
    public Order updateOrder(Order order) {
        return update(
                UPDATE_ORDER_QUERY,
                (ps, o) -> {
                    ps.setLong(1, o.getBook().getId());
                    ps.setLong(2, o.getClient().getId());
                    ps.setDate(3, Date.valueOf(o.getCreatedOn()));
                    ps.setDate(4,
                            o.getCompletedOn() != null
                                    ? Date.valueOf(o.getCompletedOn())
                                    : null);
                    ps.setString(5, o.getOrderStatus().name());
                    ps.setLong(6, o.getOrderId());
                },
                order
        );
    }

    // ---------- mapper ----------

    private Order mapOrder(ResultSet rs) throws SQLException {
        Order order = new Order(null, null);
        order.setId(rs.getLong("id"));
        order.setCreatedOn(rs.getDate("created_on").toLocalDate());

        Date completed = rs.getDate("completed_on");
        if (completed != null) {
            order.setCompletedOn(completed.toLocalDate());
        }

        order.setOrderStatus(OrderStatus.valueOf(rs.getString("order_status")));

        // book_id / client_id подтянет сервис
        Book book = new Book(null, null, null, null, 0);
        book.setId(rs.getLong("book_id"));

        Client client = new Client(null, null, null, null, null);
        client.setId(rs.getLong("client_id"));

        order.setBook(book);
        order.setClient(client);

        return order;
    }
}
