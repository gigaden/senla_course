package ebookstore.service;

import ebookstore.model.Client;

import java.util.Collection;

public interface ClientService {

    Client saveClient(Client client);

    Collection<Client> getAllClients();

    Client getClientById(long clientId);

    Client updateClient(Client client);

    void deleteClientById(long clientId);

    void checkClientIsExist(long clientId);
}
