package ebookstore.service.implement;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.order.OrderCreateDto;
import ebookstore.dto.order.OrderDetailsDto;
import ebookstore.exception.notfound.OrderNotFoundException;
import ebookstore.exception.message.OrderErrorMessages;
import ebookstore.mapper.OrderMapper;
import ebookstore.model.Book;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Validated
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final BookRequestService requestService;
    private final OrderCsvExporter orderCsvExporter;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientService clientService,
                            BookRequestService requestService,
                            OrderCsvExporter orderCsvExporter) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.requestService = requestService;
        this.orderCsvExporter = orderCsvExporter;
    }

    @Override
    @Transactional
    public Order createOrder(OrderCreateDto dto) {
        clientService.checkClientIsExist(dto.client().getId());
        Order newOrder = orderRepository.createOrder(OrderMapper.mapOrderDtoToOrder(dto));
        createRequestIfBookIsAbsent(newOrder);
        log.info("Создан новый заказ {}", newOrder);

        return newOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(long orderId) {
        Order order = orderRepository.getOrderById(orderId)
                .orElseThrow(() -> {
                    log.error("Заказ с id={} не найден", orderId);
                    return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                });
        log.info("Получен заказ с id = {}", orderId);

        return order;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Order> getAllOrders() {
        Collection<Order> orders = orderRepository.getAllOrders();
        List<Order> response = List.copyOf(orders);
        log.info("Получен список заказов в количестве {}", orders.size());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Order> getAllOrders(Comparator<Order> comparator) {
        List<Order> orders = new ArrayList<>(orderRepository.getAllOrders());
        orders.sort(comparator);

        return List.copyOf(orders);
    }

    @Override
    @Transactional
    public void changeStatus(long orderId, OrderStatus orderStatus) {
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

        log.info("Статус заказа id={} изменён на {}", orderId, orderStatus);
    }

    @Override
    @Transactional
    public void cancelOrder(long orderId) {
        changeStatus(orderId, OrderStatus.CANCELED);
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<Order> getCompletedOrdersInPeriod(
            LocalDate start,
            LocalDate end,
            Comparator<Order> comparator
    ) {
        Collection<Order> orders = orderRepository.getAllOrders();
        List<Order> response = orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .sorted(comparator)
                .toList();
        log.info("Получена коллекция завершённых заказов {}", response.size());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public double getEarnedAmountInPeriod(LocalDate start, LocalDate end) {
        Collection<Order> orders = orderRepository.getAllOrders();
        double response = orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .mapToDouble(o -> o.getBook().getPrice())
                .sum();
        log.info("Получена сумма за период = {}", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public int getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end) {
        Collection<Order> orders = orderRepository.getAllOrders();
        int response = (int) orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .count();
        log.info("Получено количество завершённых заказов = {}", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailsDto getOrderDetails(long orderId) {
        Order order = orderRepository.getOrderById(orderId)
                .orElseThrow(() -> {
                    log.error("Заказ с id={} не найден", orderId);
                    return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                });

        Client client = clientService.getClientById(order.getClient().getId());
        Book book = order.getBook();
        OrderDetailsDto response = new OrderDetailsDto(order, client, book);
        log.info("Получены детали по заказу id = {}", orderId);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkOrderIsExist(long orderId) {
        boolean result = orderRepository.checkOrderIsExist(orderId);
        log.info("Проверка существует ли заказ id {} = {}", orderId, result);

        return result;
    }

    @Override
    @Transactional
    public Order updateOrder(Order order) {
        Order existingOrder = orderRepository.getOrderById(order.getOrderId())
                .orElseThrow(() -> {
                    log.error("Заказ с id={} не найден для обновления", order.getOrderId());
                    return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                });

        updateOrderFields(existingOrder, order);
        Order updatedOrder = orderRepository.updateOrder(existingOrder);
        log.info("Заказ обновлён {}", order);

        return updatedOrder;
    }

    @Override
    @Transactional(readOnly = true)
    public void exportOrdersToCsv(String filePath) {
        Collection<Order> orders = orderRepository.getAllOrders();

        orderCsvExporter.exportToCsv(orders, filePath);
    }

    private void createRequestIfBookIsAbsent(Order order) {
        if (order.getBook().getStatus() == BookStatus.ABSENT) {
            requestService.createRequest(
                    new BookRequestCreateDto(order.getBook().getId(), order.getClient().getId())
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
}