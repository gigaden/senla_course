package ebookstore.service.csv.reader;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.model.Client;
import ebookstore.service.ClientService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ClientCsvReader {

    @Autowired
    private ClientService clientService;

    public ClientCsvReader() {
    }

    public List<List<String>> readFromCsv(String fileName) {
        List<List<String>> importClients = new ArrayList<>();

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
                importClients.add(lineData);
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }

        return importClients;
    }

    public void saveClientFromCsv(List<List<String>> clientsFromCsv) {
        int successCount = 0;
        int errorCount = 0;

        for (List<String> clientArr : clientsFromCsv) {
            try {
                if (clientArr.size() < 6) {
                    System.out.println("Пропущена строка: недостаточно данных - " + clientArr);
                    errorCount++;
                    continue;
                }

                long id = Long.parseLong(clientArr.get(0).trim());
                String name = clientArr.get(1).trim();
                String surname = clientArr.get(2).trim();
                String email = clientArr.get(3).trim();
                String login = clientArr.get(4).trim();
                String password = clientArr.get(5).trim();

                Client client = new Client(name, surname, email, login, password);
                client.setId(id);

                if (clientService.checkClientIsExist(id)) {
                    clientService.updateClient(client);
                    System.out.println("Обновлен клиент: " + name + " " + surname);
                } else {
                    clientService.saveClient(client);
                    System.out.println("Добавлен клиент: " + name + " " + surname);
                }
                successCount++;

            } catch (Exception e) {
                System.out.println("Ошибка при обработке строки: " + clientArr + " - " + e.getMessage());
                errorCount++;
            }
        }

        System.out.printf("Импорт завершен. Успешно: %d, Ошибок: %d\n", successCount, errorCount);
    }
}