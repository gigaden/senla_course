package ebookstore.repository.implement;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryOrderRepository implements OrderRepository, Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private static InMemoryOrderRepository instance;

    private Map<Long, Order> orders;
    private long orderId;

    private InMemoryOrderRepository() {
        orders = new HashMap<>();
        orderId = 1;
    }

    public static InMemoryOrderRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryOrderRepository();
        }

        return instance;
    }

    @Override
    public Optional<Order> getOrderById(long orderId) {
        return Optional.ofNullable(orders.get(orderId));
    }

    @Override
    public Collection<Order> getAllOrders() {
        return orders.values();
    }

    @Override
    public Order createOrder(Order order) {
        long orderId = generateId();
        order.setId(orderId);
        orders.put(orderId, order);

        return order;
    }

    @Override
    public Order updateOrder(Order order) {
        Order existingOrder = orders.get(order.getOrderId());
        if (existingOrder != null) {
            existingOrder.setBook(order.getBook());
            existingOrder.setClient(order.getClient());
            existingOrder.setCreatedOn(order.getCreatedOn());
            existingOrder.setCompletedOn(order.getCompletedOn());
            existingOrder.setOrderStatus(order.getOrderStatus());
            return existingOrder;
        }
        return null;
    }

    @Override
    public boolean checkOrderIsExist(long orderId) {
        return orders.containsKey(orderId);
    }

    @Override
    public void changeOrderStatus(long orderId, OrderStatus orderStatus) {
        Order order = orders.get(orderId);
        order.setOrderStatus(orderStatus);
    }

    private long generateId() {
        return orderId++;
    }
}
