package ebookstore.service;

import ebookstore.dto.OrderDetailsDto;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Comparator;

public interface OrderService {

    Order createOrder(Order order);

    Order getOrderById(long orderId);

    Collection<Order> getAllOrders();

    Collection<Order> getAllOrders(Comparator<Order> comparator);

    void changeStatus(long orderId, OrderStatus orderStatus);

    void cancelOrder(long orderId);

    Collection<Order> getCompletedOrdersInPeriod(LocalDateTime start, LocalDateTime end, Comparator<Order> comparator);

    double getEarnedAmountInPeriod(LocalDateTime start, LocalDateTime end);

    int getCompletedOrdersCountInPeriod(LocalDateTime start, LocalDateTime end);

    OrderDetailsDto getOrderDetails(long orderId);
}
