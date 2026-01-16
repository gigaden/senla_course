package ebookstore.repository;

import ebookstore.model.Client;

import java.util.Map;
import java.util.Optional;

public interface ClientRepository {

    Map<Long, Client> getAllClients();

    Client saveClient(Client client);

    Optional<Client> getClient(long clientId);

    Client updateClient(Client client);

    void deleteClient(long clientId);

    boolean checkClientIsExist(long bookId);
}
