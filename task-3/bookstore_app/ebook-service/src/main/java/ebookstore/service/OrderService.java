package ebookstore.service;

import ebookstore.dto.order.OrderCreateDto;
import ebookstore.dto.order.OrderDetailsDto;
import ebookstore.dto.order.OrderResponseDto;
import ebookstore.dto.order.OrderUpdateDto;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderSortField;
import ebookstore.model.enums.OrderStatus;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

public interface OrderService {

    OrderResponseDto createOrder(OrderCreateDto dto);

    OrderResponseDto getOrderById(long orderId);

    Collection<OrderResponseDto> getAllOrders(int page, int size, OrderSortField sortBy);

    void changeStatus(long orderId, OrderStatus orderStatus);

    void cancelOrder(long orderId);

    Collection<OrderResponseDto> getCompletedOrdersInPeriod(LocalDate start, LocalDate end, Comparator<Order> comparator);

    double getEarnedAmountInPeriod(LocalDate start, LocalDate end);

    int getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end);

    OrderDetailsDto getOrderDetails(long orderId);

    boolean checkOrderIsExist(long orderId);

    OrderResponseDto updateOrder(OrderUpdateDto order);

    void exportOrdersToCsv(String filePath);
}
