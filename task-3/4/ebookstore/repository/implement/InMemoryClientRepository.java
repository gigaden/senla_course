package ebookstore.repository.implement;

import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;

import java.util.HashMap;
import java.util.Map;

public class InMemoryClientRepository implements ClientRepository {

    private static InMemoryClientRepository instance;

    private static Map<Long, Client> clients;
    private static long clientId;

    private InMemoryClientRepository() {
        clients = new HashMap<>();
        clientId = 0;
    }

    public static InMemoryClientRepository getInstance() {
        if (instance == null) {
            instance = new InMemoryClientRepository();
        }

        return instance;
    }

    public Map<Long, Client> getAllClients() {
        return clients;
    }

    @Override
    public Client saveClient(Client client) {
        long clientId = generateId();
        client.setId(clientId);
        clients.put(clientId, client);

        return client;
    }

    @Override
    public Client getClient(long clientId) {

        return clients.get(clientId);
    }

    @Override
    public Client updateClient(Client client) {
        Client oldClient = getClient(client.getId());

        Client newClient = setNewClientsField(oldClient, client);

        return newClient;
    }

    @Override
    public void deleteClient(long clientId) {
        clients.remove(clientId);
    }

    private Client setNewClientsField(Client oldClient, Client client) {
        oldClient.setName(client.getName());
        oldClient.setSurname(client.getSurname());
        oldClient.setEmail(client.getEmail());

        return oldClient;
    }

    private long generateId() {
        long newId = clientId;
        clientId++;

        return newId;
    }
}
