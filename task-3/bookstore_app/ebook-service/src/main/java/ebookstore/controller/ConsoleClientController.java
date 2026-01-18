package ebookstore.controller;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.model.Client;
import ebookstore.service.ClientService;
import ebookstore.service.csv.reader.ClientCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;

@Component
public class ConsoleClientController {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientCsvReader csvReader;

    private static final Logger log = LoggerFactory.getLogger(ConsoleClientController.class);

    public ConsoleClientController() {
    }

    public void saveClient(Client client) {
        log.info("Сохраняем клиента");
        clientService.saveClient(client);
        log.info("Клиент успешно сохранён");
    }

    public void getAllClients() {
        log.info("Получаем всех клиентов");
        Collection<Client> clients = clientService.getAllClients();
        log.info("Получено клиентов: {}", clients.size());
    }

    public void getClient(long clientId) {
        log.info("Получаем клиента с id={}", clientId);
        clientService.getClientById(clientId);
        log.info("Клиент получен с id={}", clientId);
    }

    public void updateClient(Client client) {
        log.info("Обновляем клиента с id={}", client.getId());
        clientService.updateClient(client);
        log.info("Клиент обновлён с id={}", client.getId());
    }

    public void deleteClient(long clientId) {
        log.info("Удаляем клиента с id={}", clientId);
        clientService.deleteClientById(clientId);
        log.info("Клиент удалён с id={}", clientId);
    }

    public void importClientsFromCsv(String filePath) {
        log.info("Импортируем клиентов из CSV файла {}", filePath);
        try {
            List<List<String>> clientsData = csvReader.readFromCsv(filePath);
            log.info("Найдено записей в CSV файле: {}", clientsData.size());
            csvReader.saveClientFromCsv(clientsData);
            log.info("Импорт клиентов завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при импорте клиентов из CSV файла {}", filePath, e);
        }
    }

    public void exportClientsToCsv(String filePath) {
        log.info("Экспортируем клиентов в CSV файл {}", filePath);
        try {
            clientService.exportClientsToCsv(filePath);
            log.info("Экспорт клиентов завершён успешно");
        } catch (Exception e) {
            log.error("Ошибка при экспорте клиентов в CSV файл {}", filePath, e);
        }
    }
}
