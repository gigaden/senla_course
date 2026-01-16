package ebookstore.repository.implement.dao;

import di.annotation.Component;
import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.util.ConnectionManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientRepositoryDao extends BaseRepositoryDao implements ClientRepository {

    private static final String GET_ALL_QUERY =
            "SELECT * FROM clients";

    private static final String GET_BY_ID_QUERY =
            "SELECT * FROM clients WHERE id = ?";

    private static final String SAVE_CLIENT_QUERY = """
            INSERT INTO clients(name, surname, email, login, password)
            VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_CLIENT_QUERY = """
            UPDATE clients
            SET name = ?, surname = ?, email = ?, login = ?, password = ?
            WHERE id = ?
            """;

    private static final String DELETE_CLIENT_QUERY =
            "DELETE FROM clients WHERE id = ?";

    private static final String EXISTS_CLIENT_QUERY =
            "SELECT 1 FROM clients WHERE id = ?";

    public ClientRepositoryDao(ConnectionManager connectionManager) {
        super(connectionManager);
    }

    @Override
    public Map<Long, Client> getAllClients() {
        return findAll(GET_ALL_QUERY, this::createClientFromResultSet)
                .stream()
                .collect(Collectors.toMap(Client::getId, client -> client));
    }

    @Override
    public Client saveClient(Client client) {
        return save(
                SAVE_CLIENT_QUERY,
                (ps, c) -> {
                    ps.setString(1, c.getName());
                    ps.setString(2, c.getSurname());
                    ps.setString(3, c.getEmail());
                    ps.setString(4, c.getLogin());
                    ps.setString(5, c.getPassword());
                },
                (keys, c) -> c.setId(keys.getLong(1)),
                client
        );
    }

    @Override
    public Optional<Client> getClient(long clientId) {
        return findOne(GET_BY_ID_QUERY, this::createClientFromResultSet, clientId);
    }

    @Override
    public Client updateClient(Client client) {
        return update(
                UPDATE_CLIENT_QUERY,
                (ps, c) -> {
                    ps.setString(1, c.getName());
                    ps.setString(2, c.getSurname());
                    ps.setString(3, c.getEmail());
                    ps.setString(4, c.getLogin());
                    ps.setString(5, c.getPassword());
                    ps.setLong(6, c.getId());
                },
                client
        );
    }

    @Override
    public void deleteClient(long clientId) {
        delete(DELETE_CLIENT_QUERY, clientId);
    }

    @Override
    public boolean checkClientIsExist(long clientId) {
        return exists(EXISTS_CLIENT_QUERY, clientId);
    }

    private Client createClientFromResultSet(ResultSet rs) throws SQLException {
        Client client = new Client(
                rs.getString("name"),
                rs.getString("surname"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("password")
        );
        client.setId(rs.getLong("id"));
        return client;
    }
}
