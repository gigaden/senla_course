package ebookstore.repository;

import ebookstore.model.Order;
import ebookstore.model.OrderStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Order getOrderById(long orderId) {
        return orders.get(orderId);
    }

    @Override
    public Map<Long, Order> getAllOrders() {
        return orders;
    }

    @Override
    public Order createOrder(Order order) {
        long orderId = generateId();
        order.setId(orderId);
        orders.put(orderId, order);

        return order;
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
