package ebookstore.controller;

import ebookstore.dto.OrderDetailsDto;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.service.OrderService;
import ebookstore.service.csv.reader.OrderCsvReader;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class ConsoleOrderController {

    private final OrderService orderService;
    private final OrderCsvReader csvReader;

    public ConsoleOrderController(OrderService orderService,
                                  OrderCsvReader csvReader) {
        this.orderService = orderService;
        this.csvReader = csvReader;
    }

    public void saveOrder(Order order) {
        System.out.println("Сохраняем заказ в базу");
        Order savedOrder = orderService.createOrder(order);
        System.out.printf("Сохранили заказ: %s\n", savedOrder);
    }

    public void getOrder(long orderId) {
        System.out.printf("Получаем заказ с id = %d\n", orderId);
        Order order = orderService.getOrderById(orderId);
        System.out.printf("Получили заказ: %s\n", order);
    }

    public void getAllOrdersByDateOfCompleting() {
        System.out.println("Получаем все заказы, отсортированные по дате завершения");
        Collection<Order> orders = orderService.getAllOrders(
                Comparator.comparing(
                        Order::getCompletedOn,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
        System.out.printf("Получили все заказы, отсортированные по дате завершения: %s\n", orders);
    }

    /**
     * Надо допилить Ордер и сделать внутри коллекцию, т.к. в одном заказе может быть
     * несколько книг и уже считать сумму этих книг, как сумму заказа.
     * Сейчас у меня один заказ - одна книга
     */
    public void getAllOrdersByPrice() {
        System.out.println("Получаем все заказы, отсортированные по цене");
        Collection<Order> orders = orderService.getAllOrders(new Comparator<Order>() {
            @Override
            public int compare(Order o1, Order o2) {
                return (int) o1.getBook().getPrice() - (int) o2.getBook().getPrice();
            }
        });
        System.out.printf("Получили все заказы, отсортированные по цене: %s\n", orders);
    }

    public void getAllOrdersByStatus() {
        System.out.println("Получаем все заказы, отсортированные по статусу");
        Collection<Order> orders = orderService.getAllOrders(Comparator.comparing(Order::getOrderStatus));
        System.out.printf("Получили все заказы, отсортированные по статусу: %s\n", orders);
    }

    public void changeOrderStatus(long orderId, OrderStatus orderStatus) {
        System.out.printf("Меняем статус заказа с id = %d\n", orderId);
        orderService.changeStatus(orderId, orderStatus);
        System.out.printf("Изменили статус заказа с id = %d\n", orderId);
    }

    public void cancelOrder(long orderId) {
        System.out.printf("Меняем статус заказа с id = %d на отменённый\n", orderId);
        orderService.cancelOrder(orderId);
        System.out.printf("Изменили статус заказа с id = %d на отменённый\n", orderId);
    }

    public void getCompletedOrdersByDate(LocalDate start, LocalDate end) {
        System.out.printf("Получаем выполненные заказы за период с %s по %s, отсортированные по дате%n", start, end);
        Collection<Order> orders = orderService.getCompletedOrdersInPeriod(
                start,
                end,
                Comparator.comparing(
                        Order::getCompletedOn,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
        System.out.printf("Получили выполненные заказы, отсортированные по дате: %s\n", orders);
    }

    public void getCompletedOrdersByPrice(LocalDate start, LocalDate end) {
        System.out.printf("Получаем выполненные заказы за период с %s по %s, отсортированные по цене\n", start, end);
        Collection<Order> orders = orderService.getCompletedOrdersInPeriod(
                start,
                end,
                Comparator.comparingInt(o -> (int) o.getBook().getPrice())
        );
        System.out.printf("Получили выполненные заказы, отсортированные по цене: %s\n", orders);
    }

    public void getEarnedAmountInPeriod(LocalDate start, LocalDate end) {
        System.out.printf("Получаем сумму заработанных средств за период с %s по %s\n", start, end);
        double earnedAmount = orderService.getEarnedAmountInPeriod(start, end);
        System.out.printf("Получили сумму заработанных средств за период: %.2f\n", earnedAmount);
    }

    public void getCompletedOrdersCountInPeriod(LocalDate start, LocalDate end) {
        System.out.printf("Получаем количество выполненных заказов за период с %s по %s\n", start, end);
        int ordersCount = orderService.getCompletedOrdersCountInPeriod(start, end);
        System.out.printf("Получили количество выполненных заказов за период: %d\n", ordersCount);
    }

    public void getOrderDetails(long orderId) {
        System.out.printf("Получаем детали заказа с id = %d\n", orderId);
        OrderDetailsDto orderDetails = orderService.getOrderDetails(orderId);
        System.out.printf("Получили детали заказа: %s\n", orderDetails);
    }

    public void importOrdersFromCsv(String filePath) {
        System.out.println("Импортируем заказы из файла: " + filePath);
        try {
            List<List<String>> ordersData = csvReader.readFromCsv(filePath);
            System.out.println("Найдено записей в файле: " + ordersData.size());
            csvReader.saveOrderFromCsv(ordersData);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте заказов: " + e.getMessage());
        }
    }

    public void exportOrdersToCsv(String filePath) {
        System.out.println("Экспортируем заказы в CSV файл: " + filePath);
        try {
            orderService.exportOrdersToCsv(filePath);
            System.out.println("Экспорт заказов успешно завершен!");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте заказов: " + e.getMessage());
        }
    }
}
