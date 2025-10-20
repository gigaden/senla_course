package ebookstore.controller;

import ebookstore.model.Client;
import ebookstore.service.ClientService;

import java.util.Collection;

public class ConsoleClientController {

    private final ClientService clientService;

    public ConsoleClientController(ClientService clientService) {
        this.clientService = clientService;
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
}
