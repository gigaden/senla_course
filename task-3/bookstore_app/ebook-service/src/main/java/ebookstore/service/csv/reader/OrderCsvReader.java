package ebookstore.service.csv.reader;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;
import ebookstore.service.OrderService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderCsvReader {

    @Autowired
    private OrderService orderService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ClientService clientService;

    public OrderCsvReader() {
    }

    public List<List<String>> readFromCsv(String fileName) {
        List<List<String>> importOrders = new ArrayList<>();

        try (BufferedReader bf = new BufferedReader(new FileReader(fileName))) {
            String line;
            boolean isFirstLine = true;
            while ((line = bf.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                String[] values = line.split(",");
                List<String> lineData = Arrays.stream(values)
                        .map(String::trim)
                        .collect(Collectors.toList());
                importOrders.add(lineData);
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return importOrders;
    }

    public void saveOrderFromCsv(List<List<String>> ordersFromCsv) {
        int successCount = 0;
        int errorCount = 0;

        for (List<String> orderArr : ordersFromCsv) {
            try {
                if (orderArr.size() < 6) {
                    System.out.println("Пропущена строка: недостаточно данных - " + orderArr);
                    errorCount++;
                    continue;
                }

                long id = Long.parseLong(orderArr.get(0).trim());
                long bookId = Long.parseLong(orderArr.get(1).trim());
                long clientId = Long.parseLong(orderArr.get(2).trim());
                LocalDate createdOn = LocalDate.parse(orderArr.get(3).trim());
                LocalDate completedOn = orderArr.get(4).isEmpty() ? null : LocalDate.parse(orderArr.get(4).trim());
                OrderStatus orderStatus = OrderStatus.valueOf(orderArr.get(5).trim());

                Book book = bookService.getBookById(bookId);
                Client client = clientService.getClientById(clientId);

                Order order = new Order(book, client);
                order.setId(id);
                order.setCreatedOn(createdOn);
                order.setCompletedOn(completedOn);
                order.setOrderStatus(orderStatus);

                if (orderService.checkOrderIsExist(id)) {
                    orderService.updateOrder(order);
                    System.out.println("Обновлен заказ: " + id);
                } else {
                    orderService.createOrder(order);
                    System.out.println("Добавлен заказ: " + id);
                }
                successCount++;

            } catch (Exception e) {
                System.out.println("Ошибка при обработке строки: " + orderArr + " - " + e.getMessage());
                errorCount++;
            }
        }

        System.out.printf("Импорт завершен. Успешно: %d, Ошибок: %d\n", successCount, errorCount);
    }
}