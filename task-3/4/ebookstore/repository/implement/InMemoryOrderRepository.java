package ebookstore.repository.implement;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryOrderRepository implements OrderRepository {

    private static InMemoryOrderRepository instance;

    private static Map<Long, Order> orders;
    private static long orderId;

    private InMemoryOrderRepository() {
        orders = new HashMap<>();
        orderId = 0;
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
    public boolean checkOrderIsExist(long orderId) {
        return orders.containsKey(orderId);
    }

    @Override
    public void changeOrderStatus(long orderId, OrderStatus orderStatus) {
        Order order = orders.get(orderId);
        order.setOrderStatus(orderStatus);
    }

    private long generateId() {
        long newId = orderId;
        orderId++;

        return newId;
    }
}
