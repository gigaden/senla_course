package ebookstore.service.csv.writer;

import di.annotation.Component;
import ebookstore.model.Order;
import ebookstore.service.CsvExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Component
public class OrderCsvExporter implements CsvExporter<Order> {

    @Override
    public void exportToCsv(Collection<Order> orders, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,bookId,clientId,createdOn,completedOn,orderStatus");

            for (Order order : orders) {
                String line = String.format("%d,%d,%d,%s,%s,%s",
                        order.getOrderId(),
                        order.getBook().getId(),
                        order.getClient().getId(),
                        order.getCreatedOn(),
                        order.getCompletedOn() != null ? order.getCompletedOn() : "",
                        order.getOrderStatus()
                );
                writer.println(line);
            }

            System.out.println("Экспорт заказов завершен. Файл: " + filePath);
            System.out.println("Экспортировано заказов: " + orders.size());

        } catch (IOException e) {
            System.out.println("Ошибка при экспорте заказов в CSV: " + e.getMessage());
            throw new RuntimeException("Ошибка экспорта", e);
        }
    }
}