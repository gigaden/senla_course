package ebookstore.repository;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

import java.util.Collection;

public interface OrderRepository {

    Order getOrderById(long orderId);

    Collection<Order> getAllOrders();

    Order createOrder(Order order);

    void changeOrderStatus(long orderId, OrderStatus orderStatus);

    boolean checkOrderIsExist(long orderId);
}
