package ebookstore.service.implement;

import ebookstore.dto.OrderDetailsDto;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final BookRequestService requestService;

    public OrderServiceImpl(ClientService clientService,
                            OrderRepository orderRepository,
                            BookRequestService requestService) {
        this.clientService = clientService;
        this.orderRepository = orderRepository;
        this.requestService = requestService;
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
        checkOrderIsExist(orderId);

        return orderRepository.getOrderById(orderId);
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
        checkOrderIsExist(orderId);
        Order order = orderRepository.getOrderById(orderId);

        // чекаем есть ли запросы на эту книгу, если выбран статус "Завершить заказ"
        if (orderStatus.equals(OrderStatus.COMPLETED)) {
            checkRequestIfOrderStatusCompleted(order);
            order.setCompletedOn(LocalDateTime.now());
        }

        order.setOrderStatus(orderStatus);
    }

    @Override
    public void cancelOrder(long orderId) {
        changeStatus(orderId, OrderStatus.CANCELED);
    }

    private void checkOrderIsExist(long orderId) {
        if (!orderRepository.checkOrderIsExist(orderId)) {
            System.out.printf("Заказ с id = %d не существует", orderId);
            throw new RuntimeException();
        }
    }

    @Override
    public Collection<Order> getCompletedOrdersInPeriod(LocalDateTime start, LocalDateTime end, Comparator<Order> comparator) {
        List<Order> completedOrders = new ArrayList<>();
        Collection<Order> allOrders = orderRepository.getAllOrders();

        for (Order order : allOrders) {
            if (order.getOrderStatus().equals(OrderStatus.COMPLETED)
                && order.getCompletedOn() != null
                && !order.getCompletedOn().isBefore(start)
                && !order.getCompletedOn().isAfter(end)) {
                completedOrders.add(order);
            }
        }

        completedOrders.sort(comparator);

        return List.copyOf(completedOrders);
    }

    @Override
    public double getEarnedAmountInPeriod(LocalDateTime start, LocalDateTime end) {
        double totalAmount = 0.0;
        Collection<Order> allOrders = orderRepository.getAllOrders();

        for (Order order : allOrders) {
            if (order.getOrderStatus().equals(OrderStatus.COMPLETED)
                && order.getCompletedOn() != null
                && !order.getCompletedOn().isBefore(start)
                && !order.getCompletedOn().isAfter(end)) {
                totalAmount += order.getBook().getPrice();
            }
        }

        return totalAmount;
    }

    @Override
    public int getCompletedOrdersCountInPeriod(LocalDateTime start, LocalDateTime end) {
        int count = 0;
        Collection<Order> allOrders = orderRepository.getAllOrders();

        for (Order order : allOrders) {
            if (order.getOrderStatus().equals(OrderStatus.COMPLETED)
                && order.getCompletedOn() != null
                && !order.getCompletedOn().isBefore(start)
                && !order.getCompletedOn().isAfter(end)) {
                count++;
            }
        }

        return count;
    }

    @Override
    public OrderDetailsDto getOrderDetails(long orderId) {
        checkOrderIsExist(orderId);

        Order order = orderRepository.getOrderById(orderId);
        Client client = clientService.getClientById(order.getClient().getId());
        Book book = order.getBook();

        return new OrderDetailsDto(order, client, book);
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
