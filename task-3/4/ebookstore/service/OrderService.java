package ebookstore.service;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

public interface OrderService {

    Order createOrder(Order order);

    Order getOrderById(long orderId);

    void changeStatus(long orderId, OrderStatus orderStatus);

    void cancelOrder(long orderId);
}
