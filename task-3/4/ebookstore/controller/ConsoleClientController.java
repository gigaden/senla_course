package ebookstore.controller;

import ebookstore.model.Client;
import ebookstore.service.ClientService;
import ebookstore.service.csv.reader.ClientCsvReader;

import java.util.Collection;
import java.util.List;

public class ConsoleClientController {

    private final ClientService clientService;
    private final ClientCsvReader csvReader;

    public ConsoleClientController(ClientService clientService) {
        this.clientService = clientService;
        csvReader = new ClientCsvReader(clientService);
    }

    public void saveClient(Client client) {
        System.out.println("Сохраняем клиента в базу");
        Client savedClient = clientService.saveClient(client);
        System.out.printf("Сохранили клиента: %s\n", savedClient);
    }

    public void getAllClients() {
        System.out.println("Получаем всех клиентов");
        Collection<Client> clients = clientService.getAllClients();
        System.out.printf("Получили всех клиентов: %s\n", clients);
    }

    public void getClient(long clientId) {
        System.out.printf("Получаем клиента с id = %d\n", clientId);
        Client client = clientService.getClientById(clientId);
        System.out.printf("Получили клиента: %s\n", client);
    }

    public void updateClient(Client client) {
        System.out.printf("Обновляем клиента с id = %d\n", client.getId());
        Client newClient = clientService.updateClient(client);
        System.out.printf("Обновили клиента: %s\n", newClient);
    }

    public void deleteClient(long clientId) {
        System.out.printf("Удаляем клиента с id = %d\n", clientId);
        clientService.deleteClientById(clientId);
        System.out.printf("Удалили клиента: %s\n", clientId);
    }

    public void importClientsFromCsv(String filePath) {
        System.out.println("Импортируем клиентов из файла: " + filePath);
        try {
            List<List<String>> booksData = csvReader.readFromCsv(filePath);
            System.out.println("Найдено записей в файле: " + booksData.size());
            csvReader.saveClientFromCsv(booksData);
        } catch (Exception e) {
            System.out.println("Ошибка при импорте клиентов: " + e.getMessage());
        }
    }

    public void exportClientsToCsv(String filePath) {
        System.out.println("Экспортируем клиентов в CSV файл: " + filePath);
        try {
            clientService.exportClientsToCsv(filePath);
            System.out.println("Экспорт клиентов успешно завершен!");
        } catch (Exception e) {
            System.out.println("Ошибка при экспорте клиентов: " + e.getMessage());
        }
    }
}
