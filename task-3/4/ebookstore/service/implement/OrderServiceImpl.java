package ebookstore.service.implement;

import ebookstore.model.BookRequest;
import ebookstore.model.enums.BookStatus;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.ClientService;
import ebookstore.service.OrderService;

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
    public void changeStatus(long orderId, OrderStatus orderStatus) {
        checkOrderIsExist(orderId);
        Order order = orderRepository.getOrderById(orderId);

        // чекаем есть ли запросы на эту книгу, если выбран статус "Завершить заказ"
        if (orderStatus.equals(OrderStatus.COMPLETED)) {
            checkRequestIfOrderStatusCompleted(order);
        }

        order.setOrderStatus(orderStatus);
    }

    @Override
    public void cancelOrder(long orderId) {
        changeStatus(orderId, OrderStatus.CANCELED);
    }

    private void checkOrderIsExist(long orderId) {
        if (!orderRepository.getAllOrders().containsKey(orderId)) {
            System.out.printf("Заказ с id = %d не существует", orderId);
            throw new RuntimeException();
        }
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
