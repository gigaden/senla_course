package ebookstore.repository.implement.hiber;

import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Primary
public class ClientRepositoryHiber extends BaseRepositoryHiber<Client, Long> implements ClientRepository {

    private final SessionFactory sessionFactory;

    public ClientRepositoryHiber(SessionFactory sessionFactory) {
        super(Client.class);
        this.sessionFactory = sessionFactory;
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

    @Override
    public Optional<Client> findClientByUserName(String username) {
        try (Session session = sessionFactory.openSession()) {
            Client client = session.createQuery("""
                            SELECT c FROM Client c
                            WHERE c.username = :username
                            """, Client.class)
                    .setParameter("username", username)
                    .uniqueResultOptional()
                    .orElse(null);
            return Optional.ofNullable(client);
        }
    }
}
