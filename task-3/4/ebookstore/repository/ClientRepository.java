package ebookstore.repository;

import ebookstore.model.Client;

import java.util.Map;

public interface ClientRepository {

    Map<Long, Client> getAllClients();

    Client saveClient(Client client);

    Client getClient(long clientId);

    Client updateClient(Client client);

    void deleteClient(long clientId);
}
