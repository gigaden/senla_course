package ebookstore.service.implement;

import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.service.ClientService;

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

        return clientRepository.getClient(clientId).orElseThrow(() -> {
            System.out.printf("Клиента с id = %s не существует\n", clientId);
            return new RuntimeException();
        });
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
