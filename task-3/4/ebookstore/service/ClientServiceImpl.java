package ebookstore.service;

import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.repository.InMemoryClientRepository;

import java.util.Collection;

public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Client saveClient(Client client) {
        Client newClient = clientRepository.saveClient(client);

        return newClient;
    }

    @Override
    public Collection<Client> getAllClients() {

        return clientRepository.getAllClients().values();
    }

    @Override
    public Client getClientById(long clientId) {
        checkClientIsExist(clientId);

        return clientRepository.getClient(clientId);
    }

    @Override
    public Client updateClient(Client client) {
        checkClientIsExist(client.getId());
        Client newClient = clientRepository.updateClient(client);

        return newClient;
    }

    @Override
    public void deleteClientById(long clientId) {
        checkClientIsExist(clientId);
        clientRepository.deleteClient(clientId);
    }

    @Override
    public void checkClientIsExist(long clientId) {
        if (!clientRepository.getAllClients().containsKey(clientId)) {
            System.out.printf("Клиента с id = %d не существует", clientId);
            throw new RuntimeException();
        }
    }
}
