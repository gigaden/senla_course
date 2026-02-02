package ebookstore.controller;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.model.Client;
import ebookstore.service.ClientService;
import ebookstore.service.csv.reader.ClientCsvReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.List;

@Controller
public class ConsoleClientController {

    private final ClientService clientService;
    private final ClientCsvReader csvReader;
    private static final Logger log = LoggerFactory.getLogger(ConsoleClientController.class);

    public ConsoleClientController(ClientService clientService, ClientCsvReader csvReader) {
        this.clientService = clientService;
        this.csvReader = csvReader;
    }

    public void saveClient(ClientCreateDto client) {
        log.info("Сохраняем клиента");
        ClientResponseDto clientResponseDto = clientService.saveClient(client);
        System.out.println(clientResponseDto);
        log.info("Клиент успешно сохранён");
    }

    public void getAllClients() {
        log.info("Получаем всех клиентов");
        Collection<ClientResponseDto> clients = clientService.getAllClients();
        System.out.println(clients);
        log.info("Получено клиентов: {}", clients.size());
    }

    public void getClient(long clientId) {
        log.info("Получаем клиента с id={}", clientId);
        ClientResponseDto clientDtoById = clientService.getClientDtoById(clientId);
        System.out.println(clientDtoById);
        log.info("Клиент получен с id={}", clientId);
    }

    public void updateClient(Client client) {
        log.info("Обновляем клиента с id={}", client.getId());
        ClientResponseDto clientResponseDto = clientService.updateClient(client);
        System.out.println(clientResponseDto);
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
