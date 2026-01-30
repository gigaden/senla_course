package ebookstore.service.implement;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.order.OrderDetailsDto;
import ebookstore.exception.DatabaseException;
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
import ebookstore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
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
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            clientService.checkClientIsExist(order.getClient().getId());
            Order newOrder = orderRepository.createOrder(order);
            createRequestIfBookIsAbsent(newOrder);

            transaction.commit();
            return newOrder;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при сохранении заказа: {}", order, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении заказа: {}", order, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка сохранения заказа", e);
        }
    }

    @Override
    public Order getOrderById(long orderId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Order order = orderRepository.getOrderById(orderId)
                    .orElseThrow(() -> {
                        log.error("Заказ с id={} не найден", orderId);
                        return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                    });

            transaction.commit();
            return order;
        } catch (OrderNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения заказа", e);
        }
    }

    @Override
    public Collection<Order> getAllOrders() {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Order> orders = orderRepository.getAllOrders();
            transaction.commit();
            return List.copyOf(orders);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении всех заказов", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении всех заказов", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения заказов", e);
        }
    }

    @Override
    public Collection<Order> getAllOrders(Comparator<Order> comparator) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            List<Order> orders = new ArrayList<>(orderRepository.getAllOrders());
            orders.sort(comparator);
            transaction.commit();
            return List.copyOf(orders);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении всех заказов", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении всех заказов", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения заказов", e);
        }
    }

    @Override
    public void changeStatus(long orderId, OrderStatus orderStatus) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Order order = orderRepository.getOrderById(orderId)
                    .orElseThrow(() -> {
                        log.error("Заказ с id={} не найден", orderId);
                        return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                    });

            if (orderStatus == OrderStatus.COMPLETED) {
                checkRequestIfOrderStatusCompleted(order);
                order.setCompletedOn(LocalDate.now());
            }

            order.setOrderStatus(orderStatus);
            orderRepository.updateOrder(order);

            transaction.commit();
            log.info("Статус заказа id={} изменён на {}", orderId, orderStatus);
        } catch (OrderNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при изменении статуса заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при изменении статуса заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка изменения статуса заказа", e);
        }
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
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Order> orders = orderRepository.getAllOrders();
            transaction.commit();

            return orders.stream()
                    .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                    .filter(o -> o.getCompletedOn() != null)
                    .filter(o -> !o.getCompletedOn().isBefore(start))
                    .filter(o -> !o.getCompletedOn().isAfter(end))
                    .sorted(comparator)
                    .toList();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении завершенных заказов за период", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении завершенных заказов за период", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения завершенных заказов", e);
        }
    }

    @Override
    public double getEarnedAmountInPeriod(LocalDate start, LocalDate end) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Order> orders = orderRepository.getAllOrders();
            transaction.commit();

            return orders.stream()
                    .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                    .filter(o -> o.getCompletedOn() != null)
                    .filter(o -> !o.getCompletedOn().isBefore(start))
                    .filter(o -> !o.getCompletedOn().isAfter(end))
                    .mapToDouble(o -> o.getBook().getPrice())
                    .sum();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при расчете суммы за период", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при расчете суммы за период", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка расчета суммы за период", e);
        }
    }

    @Override
    public int getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Order> orders = orderRepository.getAllOrders();
            transaction.commit();

            return (int) orders.stream()
                    .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                    .filter(o -> o.getCompletedOn() != null)
                    .filter(o -> !o.getCompletedOn().isBefore(start))
                    .filter(o -> !o.getCompletedOn().isAfter(end))
                    .count();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при подсчете заказов за период", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при подсчете заказов за период", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка подсчета заказов за период", e);
        }
    }

    @Override
    public OrderDetailsDto getOrderDetails(long orderId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Order order = orderRepository.getOrderById(orderId)
                    .orElseThrow(() -> {
                        log.error("Заказ с id={} не найден", orderId);
                        return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                    });

            Client client = clientService.getClientById(order.getClient().getId());
            Book book = order.getBook();

            transaction.commit();
            return new OrderDetailsDto(order, client, book);
        } catch (OrderNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении деталей заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении деталей заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения деталей заказа", e);
        }
    }

    @Override
    public boolean checkOrderIsExist(long orderId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            boolean result = orderRepository.checkOrderIsExist(orderId);
            transaction.commit();
            return result;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при проверке существования заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при проверке существования заказа с id={}", orderId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка проверки существования заказа", e);
        }
    }

    @Override
    public Order updateOrder(Order order) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Order existingOrder = orderRepository.getOrderById(order.getOrderId())
                    .orElseThrow(() -> {
                        log.error("Заказ с id={} не найден для обновления", order.getOrderId());
                        return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                    });

            updateOrderFields(existingOrder, order);
            Order updatedOrder = orderRepository.updateOrder(existingOrder);

            transaction.commit();
            return updatedOrder;
        } catch (OrderNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при обновлении заказа: {}", order, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении заказа: {}", order, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка обновления заказа", e);
        }
    }

    @Override
    public void exportOrdersToCsv(String filePath) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Order> orders = orderRepository.getAllOrders();
            transaction.commit();

            orderCsvExporter.exportToCsv(orders, filePath);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при экспорте заказов в CSV", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при экспорте заказов в CSV", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка экспорта заказов", e);
        }
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
            throw new RuntimeException("Есть активный запрос на книгу");
        }
    }

    private void updateOrderFields(Order existingOrder, Order newData) {
        if (newData.getClient() != null) {
            existingOrder.setClient(newData.getClient());
        }
        if (newData.getBook() != null) {
            existingOrder.setBook(newData.getBook());
        }
        if (newData.getOrderStatus() != null) {
            existingOrder.setOrderStatus(newData.getOrderStatus());
        }
        if (newData.getCreatedOn() != null) {
            existingOrder.setCreatedOn(newData.getCreatedOn());
        }
        if (newData.getCompletedOn() != null) {
            existingOrder.setCompletedOn(newData.getCompletedOn());
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                log.error("Ошибка при откате транзакции", rollbackEx);
            }
        }
    }
}