package ebookstore.repository.implement.hiber;

import di.annotation.Component;
import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientRepositoryHiber extends BaseRepositoryHiber<Client, Long> implements ClientRepository {

    public ClientRepositoryHiber() {
        super(Client.class);
    }

    @Override
    public Map<Long, Client> getAllClients() {
        return findAll().stream()
                .collect(Collectors.toMap(Client::getId, client -> client));
    }

    @Override
    public Client saveClient(Client client) {
        return save(client);
    }

    @Override
    public Optional<Client> getClient(long clientId) {
        return Optional.ofNullable(find(clientId));
    }

    @Override
    public Client updateClient(Client client) {
        return update(client);
    }

    @Override
    public void deleteClient(long clientId) {
        delete(clientId);
    }

    @Override
    public boolean checkClientIsExist(long bookId) {
        return exists(bookId);
    }
}
