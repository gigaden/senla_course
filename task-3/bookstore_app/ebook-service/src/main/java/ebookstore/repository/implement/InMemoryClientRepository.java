package ebookstore.repository.implement;

import di.annotation.Component;
import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;

import java.io.Serial;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryClientRepository implements ClientRepository, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private Map<Long, Client> clients;
    private long clientId;

    public InMemoryClientRepository() {
        clients = new HashMap<>();
        clientId = 1;
    }

    @Override
    public Map<Long, Client> getAllClients() {
        return clients;
    }

    @Override
    public Client saveClient(Client client) {
        long newId = generateId();
        client.setId(newId);
        clients.put(newId, client);
        return client;
    }

    @Override
    public Optional<Client> getClient(long clientId) {
        return Optional.ofNullable(clients.get(clientId));
    }

    @Override
    public Client updateClient(Client client) {
        Client oldClient = clients.get(client.getId());
        if (oldClient != null) {
            oldClient.setName(client.getName());
            oldClient.setSurname(client.getSurname());
            oldClient.setEmail(client.getEmail());
            oldClient.setLogin(client.getLogin());
            oldClient.setPassword(client.getPassword());
        }
        return oldClient;
    }

    @Override
    public void deleteClient(long clientId) {
        clients.remove(clientId);
    }

    @Override
    public boolean checkClientIsExist(long clientId) {
        return clients.containsKey(clientId);
    }

    private long generateId() {
        return clientId++;
    }
}