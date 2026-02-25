package ebookstore.repository.implement.hiber;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
@Primary
public class OrderRepositoryHiber extends BaseRepositoryHiber<Order, Long> implements OrderRepository {

    private final SessionFactory sessionFactory;

    public OrderRepositoryHiber(SessionFactory sessionFactory) {
        super(Order.class);
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Optional<Order> getOrderById(long orderId) {
        Session session = sessionFactory.getCurrentSession();
        Order order = session.createQuery(
                        "select o from Order o " +
                                "left join fetch o.book " +
                                "left join fetch o.client " +
                                "where o.id = :id", Order.class)
                .setParameter("id", orderId)
                .uniqueResultOptional()
                .orElse(null);
        return Optional.ofNullable(order);
    }

    @Override
    public Collection<Order> getAllOrders() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery(
                        "select distinct o from Order o " +
                                "left join fetch o.book " +
                                "left join fetch o.client", Order.class)
                .getResultList();
    }

    @Override
    public Order createOrder(Order order) {
        return save(order);
    }

    @Override
    public void changeOrderStatus(long orderId, OrderStatus orderStatus) {
        Order order = find(orderId);
        if (order != null) {
            order.setOrderStatus(orderStatus);
            update(order);
        }
    }

    @Override
    public boolean checkOrderIsExist(long orderId) {
        return exists(orderId);
    }

    @Override
    public Order updateOrder(Order order) {
        return update(order);
    }
}
