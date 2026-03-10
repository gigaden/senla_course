package ebookstore.repository;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

import java.util.Collection;
import java.util.Optional;

public interface OrderRepository {

    Optional<Order> getOrderById(long orderId);

    Collection<Order> getAllOrders();

    Collection<Order> getAllOrders(int page, int size, String sortBy);

    Order createOrder(Order order);

    void changeOrderStatus(long orderId, OrderStatus orderStatus);

    boolean checkOrderIsExist(long orderId);

    Order updateOrder(Order order);
}
