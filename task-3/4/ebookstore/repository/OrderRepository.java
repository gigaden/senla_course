package ebookstore.repository;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

import java.util.Map;

public interface OrderRepository {

    Order getOrderById(long orderId);

    Map<Long, Order> getAllOrders();

    Order createOrder(Order order);

    void changeOrderStatus(long orderId, OrderStatus orderStatus);
}
