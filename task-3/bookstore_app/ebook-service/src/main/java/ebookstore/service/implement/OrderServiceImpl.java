package ebookstore.service.implement;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.OrderDetailsDto;
import ebookstore.exception.OrderNotFoundException;
import ebookstore.exception.message.OrderErrorMessages;
import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.BookStatus;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.ClientService;
import ebookstore.service.OrderService;
import ebookstore.service.csv.writer.OrderCsvExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientService clientService;

    @Autowired
    private BookRequestService requestService;

    @Autowired
    private OrderCsvExporter orderCsvExporter;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    public Order createOrder(Order order) {
        clientService.checkClientIsExist(order.getClient().getId());
        Order newOrder = orderRepository.createOrder(order);
        createRequestIfBookIsAbsent(newOrder);
        return newOrder;
    }

    @Override
    public Order getOrderById(long orderId) {
        return orderRepository.getOrderById(orderId)
                .orElseThrow(() -> {
                    log.error("Заказ не найден id={}", orderId);
                    return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                });
    }

    @Override
    public Collection<Order> getAllOrders() {
        return List.copyOf(orderRepository.getAllOrders());
    }

    @Override
    public Collection<Order> getAllOrders(Comparator<Order> comparator) {
        List<Order> orders = new ArrayList<>(orderRepository.getAllOrders());
        orders.sort(comparator);
        return List.copyOf(orders);
    }

    @Override
    public void changeStatus(long orderId, OrderStatus orderStatus) {
        Order order = getOrderById(orderId);

        if (orderStatus == OrderStatus.COMPLETED) {
            checkRequestIfOrderStatusCompleted(order);
            order.setCompletedOn(LocalDate.now());
        }

        order.setOrderStatus(orderStatus);
        log.info("Статус заказа id={} изменён на {}", orderId, orderStatus);
    }

    @Override
    public void cancelOrder(long orderId) {
        changeStatus(orderId, OrderStatus.CANCELED);
    }

    @Override
    public Collection<Order> getCompletedOrdersInPeriod(
            LocalDate start,
            LocalDate end,
            Comparator<Order> comparator
    ) {
        return orderRepository.getAllOrders().stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .sorted(comparator)
                .toList();
    }

    @Override
    public double getEarnedAmountInPeriod(LocalDate start, LocalDate end) {
        return orderRepository.getAllOrders().stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .mapToDouble(o -> o.getBook().getPrice())
                .sum();
    }

    @Override
    public int getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end) {
        return (int) orderRepository.getAllOrders().stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .count();
    }

    @Override
    public OrderDetailsDto getOrderDetails(long orderId) {
        Order order = getOrderById(orderId);
        Client client = clientService.getClientById(order.getClient().getId());
        Book book = order.getBook();
        return new OrderDetailsDto(order, client, book);
    }

    @Override
    public boolean checkOrderIsExist(long orderId) {
        return orderRepository.checkOrderIsExist(orderId);
    }

    @Override
    public Order updateOrder(Order order) {
        Order oldOrder = getOrderById(order.getOrderId());
        return orderRepository.updateOrder(oldOrder);
    }

    @Override
    public void exportOrdersToCsv(String filePath) {
        Collection<Order> allOrders = orderRepository.getAllOrders();
        orderCsvExporter.exportToCsv(allOrders, filePath);
    }

    private void createRequestIfBookIsAbsent(Order order) {
        if (order.getBook().getStatus() == BookStatus.ABSENT) {
            requestService.createRequest(
                    new BookRequest(order.getBook().getId(), order.getClient().getId())
            );
            log.info("Создан запрос на отсутствующую книгу id={}", order.getBook().getId());
        }
    }

    private void checkRequestIfOrderStatusCompleted(Order order) {
        if (requestService.requestIsOpenForBookWithId(order.getBook().getId())) {
            log.error("Нельзя завершить заказ id={}, есть активный запрос на книгу",
                    order.getOrderId());
            throw new RuntimeException();
        }
    }
}