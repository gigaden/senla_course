package ebookstore.service;

import ebookstore.model.Order;
import ebookstore.model.OrderStatus;
import ebookstore.repository.InMemoryOrderRepository;
import ebookstore.repository.OrderRepository;

public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;

    public OrderServiceImpl(ClientService clientService, OrderRepository orderRepository) {
        this.clientService = clientService;
        this.orderRepository = orderRepository;
    }

    @Override
    public Order createOrder(Order order) {
        clientService.checkClientIsExist(order.getClient().getId());
        Order newOrder = orderRepository.createOrder(order);

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
}
