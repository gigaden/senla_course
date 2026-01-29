package ebookstore.controller;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.order.OrderDetailsDto;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.service.OrderService;
import ebookstore.service.csv.reader.OrderCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@Component
public class ConsoleOrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderCsvReader csvReader;

    private static final Logger log = LoggerFactory.getLogger(ConsoleOrderController.class);

    public ConsoleOrderController() {
    }

    public void saveOrder(Order order) {
        log.info("Создаём заказ");
        orderService.createOrder(order);
        log.info("Заказ успешно создан");
    }

    public void getOrder(long orderId) {
        log.info("Получаем заказ с id={}", orderId);
        orderService.getOrderById(orderId);
        log.info("Заказ получен с id={}", orderId);
    }

    public void getAllOrdersByDateOfCompleting() {
        log.info("Получаем заказы, отсортированные по дате завершения");
        Collection<Order> orders = orderService.getAllOrders(
                Comparator.comparing(
                        Order::getCompletedOn,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
        log.info("Получено заказов: {}", orders.size());
    }

    public void getAllOrdersByPrice() {
        log.info("Получаем заказы, отсортированные по цене");
        Collection<Order> orders = orderService.getAllOrders(
                Comparator.comparingInt(o -> (int) o.getBook().getPrice())
        );
        log.info("Получено заказов: {}", orders.size());
    }

    public void getAllOrdersByStatus() {
        log.info("Получаем заказы, отсортированные по статусу");
        Collection<Order> orders = orderService.getAllOrders(
                Comparator.comparing(Order::getOrderStatus)
        );
        log.info("Получено заказов: {}", orders.size());
    }

    public void changeOrderStatus(long orderId, OrderStatus orderStatus) {
        log.info("Меняем статус заказа id={} на {}", orderId, orderStatus);
        orderService.changeStatus(orderId, orderStatus);
        log.info("Статус заказа изменён id={}", orderId);
    }

    public void cancelOrder(long orderId) {
        log.info("Отменяем заказ id={}", orderId);
        orderService.cancelOrder(orderId);
        log.info("Заказ отменён id={}", orderId);
    }

    public void getCompletedOrdersByDate(LocalDate start, LocalDate end) {
        log.info("Получаем выполненные заказы за период {} - {}, сортировка по дате", start, end);
        Collection<Order> orders = orderService.getCompletedOrdersInPeriod(
                start,
                end,
                Comparator.comparing(
                        Order::getCompletedOn,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
        log.info("Получено выполненных заказов: {}", orders.size());
    }

    public void getCompletedOrdersByPrice(LocalDate start, LocalDate end) {
        log.info("Получаем выполненные заказы за период {} - {}, сортировка по цене", start, end);
        Collection<Order> orders = orderService.getCompletedOrdersInPeriod(
                start,
                end,
                Comparator.comparingInt(o -> (int) o.getBook().getPrice())
        );
        log.info("Получено выполненных заказов: {}", orders.size());
    }

    public void getEarnedAmountInPeriod(LocalDate start, LocalDate end) {
        log.info("Получаем сумму заработанных средств за период {} - {}", start, end);
        double amount = orderService.getEarnedAmountInPeriod(start, end);
        log.info("Получена сумма заработанных средств: {}", amount);
    }

    public void getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end) {
        log.info("Получаем количество выполненных заказов за период {} - {}", start, end);
        int count = orderService.getCompletedOrdersCountInPeriod(start, end);
        log.info("Получено количество выполненных заказов: {}", count);
    }

    public void getOrderDetails(long orderId) {
        log.info("Получаем детали заказа id={}", orderId);
        OrderDetailsDto details = orderService.getOrderDetails(orderId);
        log.info("Получены детали заказа id={}", orderId);
    }

    public void importOrdersFromCsv(String filePath) {
        log.info("Импортируем заказы из CSV файла {}", filePath);
        try {
            List<List<String>> ordersData = csvReader.readFromCsv(filePath);
            log.info("Найдено записей в CSV файле: {}", ordersData.size());
            csvReader.saveOrderFromCsv(ordersData);
            log.info("Импорт заказов завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при импорте заказов из CSV файла {}", filePath, e);
        }
    }

    public void exportOrdersToCsv(String filePath) {
        log.info("Экспортируем заказы в CSV файл {}", filePath);
        try {
            orderService.exportOrdersToCsv(filePath);
            log.info("Экспорт заказов завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при экспорте заказов в CSV файл {}", filePath, e);
        }
    }
}
