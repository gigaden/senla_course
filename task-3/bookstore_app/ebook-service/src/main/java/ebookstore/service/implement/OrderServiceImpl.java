package ebookstore.service.implement;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.order.OrderCreateDto;
import ebookstore.dto.order.OrderDetailsDto;
import ebookstore.dto.order.OrderResponseDto;
import ebookstore.dto.order.OrderUpdateDto;
import ebookstore.exception.DatesValidationException;
import ebookstore.exception.message.OrderErrorMessages;
import ebookstore.exception.notfound.OrderNotFoundException;
import ebookstore.mapper.OrderMapper;
import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.BookStatus;
import ebookstore.model.enums.OrderSortField;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;
import ebookstore.service.OrderService;
import ebookstore.service.csv.writer.OrderCsvExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Service
@Validated
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ClientService clientService;
    private final BookRequestService requestService;
    private final BookService bookService;
    private final OrderCsvExporter orderCsvExporter;

    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    public OrderServiceImpl(OrderRepository orderRepository,
                            ClientService clientService,
                            BookRequestService requestService,
                            OrderCsvExporter orderCsvExporter,
                            BookService bookService) {
        this.orderRepository = orderRepository;
        this.clientService = clientService;
        this.requestService = requestService;
        this.orderCsvExporter = orderCsvExporter;
        this.bookService = bookService;
    }

    @Override
    @Transactional
    public OrderResponseDto createOrder(OrderCreateDto dto) {
        clientService.checkClientIsExist(dto.client().getId());
        Order newOrder = orderRepository.createOrder(OrderMapper.mapOrderDtoToOrder(dto));
        createRequestIfBookIsAbsent(newOrder);
        OrderResponseDto responseDto = OrderMapper.mapOrderToResponseDto(newOrder);
        log.info("Создан новый заказ {}", newOrder);

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(long orderId) {
        Order order = orderRepository.getOrderById(orderId)
                .orElseThrow(() -> {
                    log.error("Заказ с id={} не найден", orderId);
                    return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                });
        OrderResponseDto responseDto = OrderMapper.mapOrderToResponseDto(order);
        log.info("Получен заказ с id = {}", orderId);

        return responseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<OrderResponseDto> getAllOrders(int page, int size, OrderSortField sortBy) {
        Collection<Order> orders = orderRepository.getAllOrders(page, size, sortBy.getField());
        List<OrderResponseDto> response = orders.stream().map(OrderMapper::mapOrderToResponseDto).toList();
        log.info("Получен список заказов в количестве {}", orders.size());

        return response;
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
    public Collection<OrderResponseDto> getCompletedOrdersInPeriod(
            LocalDate start,
            LocalDate end,
            Comparator<Order> comparator
    ) {
        checkDates(start, end);
        Collection<Order> orders = orderRepository.getAllOrders();
        List<OrderResponseDto> response = orders.stream()
                .filter(o -> o.getOrderStatus() == OrderStatus.COMPLETED)
                .filter(o -> o.getCompletedOn() != null)
                .filter(o -> !o.getCompletedOn().isBefore(start))
                .filter(o -> !o.getCompletedOn().isAfter(end))
                .sorted(comparator)
                .map(OrderMapper::mapOrderToResponseDto)
                .toList();
        log.info("Получена коллекция завершённых заказов {}", response.size());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public double getEarnedAmountInPeriod(LocalDate start, LocalDate end) {
        checkDates(start, end);
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
        checkDates(start, end);
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
    public OrderResponseDto updateOrder(OrderUpdateDto dto) {
        Order existingOrder = orderRepository.getOrderById(dto.id())
                .orElseThrow(() -> {
                    log.error("Заказ с id={} не найден для обновления", dto.id());
                    return new OrderNotFoundException(OrderErrorMessages.FIND_ERROR);
                });

        updateOrderFields(existingOrder, dto);
        Order updatedOrder = orderRepository.updateOrder(existingOrder);
        OrderResponseDto responseDto = OrderMapper.mapOrderToResponseDto(updatedOrder);
        log.info("Заказ обновлён {}", dto);

        return responseDto;
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

    private void updateOrderFields(Order existingOrder, OrderUpdateDto dto) {
        if (dto.clientId() != null) {
            Client client = clientService.getClientById(dto.clientId());
            existingOrder.setClient(client);
        }
        if (dto.bookId() != null) {
            Book book = bookService.getBookById(dto.bookId());
            existingOrder.setBook(book);
        }
        if (dto.status() != null) {
            existingOrder.setOrderStatus(dto.status());
        }
    }

    /**
     * Метод проверяет корректность двух дат
     */
    private void checkDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            log.error("Дата начала {} позже окончания {}", start, end);
            throw new DatesValidationException("Дата начала не может быть позже окончания");
        }
    }
}