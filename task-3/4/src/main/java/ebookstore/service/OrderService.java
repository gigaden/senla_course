package ebookstore.service;

import ebookstore.dto.OrderDetailsDto;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

public interface OrderService {

    Order createOrder(Order order);

    Order getOrderById(long orderId);

    Collection<Order> getAllOrders();

    Collection<Order> getAllOrders(Comparator<Order> comparator);

    void changeStatus(long orderId, OrderStatus orderStatus);

    void cancelOrder(long orderId);

    Collection<Order> getCompletedOrdersInPeriod(LocalDate start, LocalDate end, Comparator<Order> comparator);

    double getEarnedAmountInPeriod(LocalDate start, LocalDate end);

    int getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end);

    OrderDetailsDto getOrderDetails(long orderId);

    boolean checkOrderIsExist(long orderId);

    Order updateOrder(Order order);

    void exportOrdersToCsv(String filePath);
}
