package ebookstore.controller.restapi;

import ebookstore.dto.order.OrderCreateDto;
import ebookstore.dto.order.OrderDetailsDto;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.service.OrderService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;

/**
 * Контроллер обрабатывает запросы, связанные с заказами
 */
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Эндпоинт для создания нового заказа
     *
     * @param order - дто для создания заказа
     * @return созданный заказ
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody @Valid OrderCreateDto order) {
        log.info("Создание нового заказа");
        Order response = orderService.createOrder(order);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Эндпоинт для получения заказа по идентификатору
     *
     * @param id - идентификатор заказа
     * @return заказ
     */
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrder(@PathVariable long id) {
        log.info("Получение заказа с id={}", id);
        Order order = orderService.getOrderById(id);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения детальной информации о заказе
     *
     * @param id - идентификатор заказа
     * @return детали заказа
     */
    @GetMapping("/{id}/details")
    public ResponseEntity<OrderDetailsDto> getOrderDetails(@PathVariable long id) {
        log.info("Получение деталей заказа id={}", id);
        OrderDetailsDto details = orderService.getOrderDetails(id);

        return new ResponseEntity<>(details, HttpStatus.OK);
    }

    /**
     * Эндпоинт для обновления заказа
     *
     * @param order - дто для обновления заказа
     * @return обновлённый заказ
     */
    @PutMapping
    public ResponseEntity<Order> updateOrder(@RequestBody @Valid Order order) {
        log.info("Обновление заказа с id={}", order.getOrderId());
        Order response = orderService.updateOrder(order);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Эндпоинт для изменения статуса заказа
     *
     * @param id     - идентификатор заказа
     * @param status - новый статус
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> changeOrderStatus(@PathVariable long id,
                                                    @RequestParam OrderStatus status) {
        log.info("Изменение статуса заказа id={} на {}", id, status);
        orderService.changeStatus(id, status);

        return new ResponseEntity<>("Статус заказа изменён", HttpStatus.OK);
    }

    /**
     * Эндпоинт для отмены заказа
     *
     * @param id - идентификатор заказа
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<String> cancelOrder(@PathVariable long id) {
        log.info("Отмена заказа id={}", id);
        orderService.cancelOrder(id);

        return new ResponseEntity<>("Заказ отменён", HttpStatus.ACCEPTED);
    }

    /**
     * Эндпоинт для получения всех заказов, отсортированных по дате завершения
     *
     * @return коллекция заказов
     */
    @GetMapping("/by_date")
    public ResponseEntity<Collection<Order>> getAllOrdersByDate() {
        log.info("Получение всех заказов, отсортированных по дате завершения");
        Collection<Order> orders = orderService.getAllOrders(
                Comparator.comparing(
                        Order::getCompletedOn,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения всех заказов, отсортированных по цене книги
     *
     * @return коллекция заказов
     */
    @GetMapping("/by_price")
    public ResponseEntity<Collection<Order>> getAllOrdersByPrice() {
        log.info("Получение всех заказов, отсортированных по цене книги");
        Collection<Order> orders = orderService.getAllOrders(
                Comparator.comparingInt(o -> (int) o.getBook().getPrice())
        );

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения всех заказов, отсортированных по статусу
     *
     * @return коллекция заказов
     */
    @GetMapping("/by_status")
    public ResponseEntity<Collection<Order>> getAllOrdersByStatus() {
        log.info("Получение всех заказов, отсортированных по статусу");
        Collection<Order> orders = orderService.getAllOrders(
                Comparator.comparing(Order::getOrderStatus)
        );

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения выполненных заказов за указанный период с возможностью сортировки
     *
     * @param start - начало периода
     * @param end   - конец периода
     * @param sort  - тип сортировки (date или price)
     * @return коллекция выполненных заказов
     */
    @GetMapping("/completed")
    public ResponseEntity<Collection<Order>> getCompletedOrders(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            @RequestParam(defaultValue = "date") String sort) {
        log.info("Получение выполненных заказов за период {} - {}, сортировка по {}", start, end, sort);
        Collection<Order> orders = orderService.getCompletedOrdersInPeriod(
                start,
                end,
                Comparator.comparing(
                        Order::getCompletedOn,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения суммы заработанных средств за указанный период
     *
     * @param start - начало периода
     * @param end   - конец периода
     * @return сумма заработка
     */
    @GetMapping("/earnings")
    public ResponseEntity<Double> getEarnedAmount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        log.info("Получение суммы заработка за период {} - {}", start, end);
        double amount = orderService.getEarnedAmountInPeriod(start, end);

        return new ResponseEntity<>(amount, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения количества выполненных заказов за указанный период
     *
     * @param start - начало периода
     * @param end   - конец периода
     * @return количество заказов
     */
    @GetMapping("/count")
    public ResponseEntity<Integer> getCompletedOrdersCount(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        log.info("Получение количества выполненных заказов за период {} - {}", start, end);
        int count = orderService.getCompletedOrdersCountInPeriod(start, end);

        return new ResponseEntity<>(count, HttpStatus.OK);
    }
}