package ebookstore.service.csv.writer;

import di.annotation.Component;
import ebookstore.model.Client;
import ebookstore.service.CsvExporter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@Component
public class ClientCsvExporter implements CsvExporter<Client> {

    @Override
    public void exportToCsv(Collection<Client> clients, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("id,name,surname,email,login,password");

            for (Client client : clients) {
                String line = String.format("%d,%s,%s,%s,%s,%s",
                        client.getId(),
                        escapeCsv(client.getName()),
                        escapeCsv(client.getSurname()),
                        escapeCsv(client.getEmail()),
                        escapeCsv(client.getLogin()),
                        escapeCsv(client.getPassword())
                );
                writer.println(line);
            }

            System.out.println("Экспорт клиентов завершен. Файл: " + filePath);
            System.out.println("Экспортировано клиентов: " + clients.size());
        } catch (IOException e) {
            System.out.println("Ошибка при экспорте клиентов в CSV: " + e.getMessage());
            throw new RuntimeException("Ошибка экспорта", e);
        }
    }

    private String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}