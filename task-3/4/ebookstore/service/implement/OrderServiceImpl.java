package ebookstore.service.implement;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final BookRequestService requestService;
    private final OrderCsvExporter orderCsvExporter;

    public OrderServiceImpl(ClientService clientService,
                            OrderRepository orderRepository,
                            BookRequestService requestService,
                            OrderCsvExporter orderCsvExporter) {
        this.clientService = clientService;
        this.orderRepository = orderRepository;
        this.requestService = requestService;
        this.orderCsvExporter = orderCsvExporter;
    }

    @Override
    public Order createOrder(Order order) {
        clientService.checkClientIsExist(order.getClient().getId());
        Order newOrder = orderRepository.createOrder(order);
        createRequestIfBookIsAbsent(newOrder);

        return newOrder;
    }

    @Override
    public Order getOrderById(long orderId) {
        return orderRepository.getOrderById(orderId).orElseThrow(() -> new OrderNotFoundException(OrderErrorMessages.FIND_ERROR));
    }

    @Override
    public Collection<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>(orderRepository.getAllOrders());

        return List.copyOf(orders);
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

        // чекаем есть ли запросы на эту книгу, если выбран статус "Завершить заказ"
        if (orderStatus.equals(OrderStatus.COMPLETED)) {
            checkRequestIfOrderStatusCompleted(order);
            order.setCompletedOn(LocalDate.now());
        }

        order.setOrderStatus(orderStatus);
    }

    @Override
    public void cancelOrder(long orderId) {
        Order order = getOrderById(orderId);
        changeStatus(order.getOrderId(), OrderStatus.CANCELED);
    }

    @Override
    public Collection<Order> getCompletedOrdersInPeriod(LocalDate start, LocalDate end, Comparator<Order> comparator) {

        Collection<Order> allOrders = orderRepository.getAllOrders().stream()
                .filter(o -> o.getOrderStatus().equals(OrderStatus.COMPLETED))
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .sorted(comparator)
                .toList();

        return allOrders;
    }

    @Override
    public double getEarnedAmountInPeriod(LocalDate start, LocalDate end) {

        double totalAmount = orderRepository.getAllOrders().stream()
                .filter(order -> order.getOrderStatus().equals(OrderStatus.COMPLETED))
                .filter(order -> order.getCompletedOn() != null)
                .filter(order -> !order.getCompletedOn().isBefore(start))
                .filter(order -> !order.getCompletedOn().isAfter(end))
                .mapToDouble(order -> order.getBook().getPrice())
                .sum();

        return totalAmount;
    }

    @Override
    public int getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end) {
        int count = (int) orderRepository.getAllOrders().stream()
                .filter(order -> order.getOrderStatus().equals(OrderStatus.COMPLETED))
                .filter(order -> order.getCompletedOn() != null)
                .filter(order -> !order.getCompletedOn().isBefore(start))
                .filter(order -> !order.getCompletedOn().isAfter(end))
                .count();

        return count;
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
        Order newOrder = orderRepository.updateOrder(oldOrder);

        return newOrder;
    }

    @Override
    public void exportOrdersToCsv(String filePath) {
        Collection<Order> allOrders = orderRepository.getAllOrders();
        orderCsvExporter.exportToCsv(allOrders, filePath);
    }

    private void createRequestIfBookIsAbsent(Order newOrder) {
        if (newOrder.getBook().getStatus().equals(BookStatus.ABSENT)) {
            BookRequest bookRequest = new BookRequest(newOrder.getBook().getId(),
                    newOrder.getClient().getId());
            requestService.createRequest(bookRequest);
        }
    }

    private void checkRequestIfOrderStatusCompleted(Order order) {
        if (requestService.requestIsOpenForBookWithId(order.getBook().getId())) {
            System.out.printf("Нельзя завершить заказ с id = %d, т.к. есть активный запрос на книгу", order.getOrderId());
            throw new RuntimeException();
        }
    }
}
