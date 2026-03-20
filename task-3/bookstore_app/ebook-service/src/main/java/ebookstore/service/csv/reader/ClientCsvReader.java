package ebookstore.service.csv.reader;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientUpdateDto;
import ebookstore.model.enums.ClientRole;
import ebookstore.service.ClientService;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientCsvReader {

    private final ClientService clientService;

    public ClientCsvReader(ClientService clientService) {
        this.clientService = clientService;
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
                String username = clientArr.get(1).trim();
                String name = clientArr.get(2).trim();
                String surname = clientArr.get(3).trim();
                String email = clientArr.get(4).trim();
                String login = clientArr.get(5).trim();
                String password = clientArr.get(6).trim();
                String role = clientArr.get(7).trim();

                ClientUpdateDto client = new ClientUpdateDto(id, username, name, surname, email, login, password, ClientRole.valueOf(role));

                if (clientService.checkClientIsExist(id)) {
                    clientService.updateClient(client);
                    System.out.println("Обновлен клиент: " + name + " " + surname);
                } else {
                    clientService.saveClient(new ClientCreateDto(username, name, surname, email, login, password));
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