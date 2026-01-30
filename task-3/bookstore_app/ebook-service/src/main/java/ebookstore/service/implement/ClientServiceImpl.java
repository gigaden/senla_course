package ebookstore.service.implement;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.exception.ClientNotFoundException;
import ebookstore.exception.DatabaseException;
import ebookstore.exception.message.ClientErrorMessages;
import ebookstore.mapper.ClientMapper;
import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.service.ClientService;
import ebookstore.service.csv.writer.ClientCsvExporter;
import ebookstore.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Реализация сервиса для работы с клиентами.
 * Управляет бизнес-логикой связанной с клиентами, включая создание,
 * обновление, удаление и поиск клиентов.
 */
@Component
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientCsvExporter clientCsvExporter;

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Override
    public ClientResponseDto saveClient(Client client) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Client savedClient = clientRepository.saveClient(client);
            transaction.commit();
            return ClientMapper.mapClientToResponseDto(savedClient);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при сохранении клиента: {}", client, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при сохранении клиента: {}", client, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка сохранения клиента", e);
        }
    }

    @Override
    public Collection<ClientResponseDto> getAllClients() {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Client> clients = clientRepository.getAllClients().values();
            transaction.commit();
            return clients.stream()
                    .map(ClientMapper::mapClientToResponseDto)
                    .toList();
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении всех клиентов", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении всех клиентов", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения клиентов", e);
        }
    }

    @Override
    public Client getClientById(long clientId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Client client = clientRepository.getClient(clientId)
                    .orElseThrow(() -> {
                        log.error("Клиент не найден id={}", clientId);
                        return new ClientNotFoundException(ClientErrorMessages.FIND_ERROR);
                    });

            transaction.commit();
            return client;
        } catch (ClientNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при получении клиента с id={}", clientId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при получении клиента с id={}", clientId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка получения клиента", e);
        }
    }

    @Override
    public ClientResponseDto getClientDtoById(long clientId) {
        return ClientMapper.mapClientToResponseDto(getClientById(clientId));
    }

    @Override
    public ClientResponseDto updateClient(Client client) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            Client existingClient = clientRepository.getClient(client.getId())
                    .orElseThrow(() -> {
                        log.error("Клиент с id={} не найден для обновления", client.getId());
                        return new ClientNotFoundException(ClientErrorMessages.FIND_ERROR);
                    });

            updateClientFields(existingClient, client);

            Client updatedClient = clientRepository.updateClient(existingClient);
            transaction.commit();
            return ClientMapper.mapClientToResponseDto(updatedClient);
        } catch (ClientNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при обновлении клиента: {}", client, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при обновлении клиента: {}", client, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка обновления клиента", e);
        }
    }

    @Override
    public void deleteClientById(long clientId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            clientRepository.getClient(clientId)
                    .orElseThrow(() -> {
                        log.error("Клиент с id={} не найден для удаления", clientId);
                        return new ClientNotFoundException(ClientErrorMessages.FIND_ERROR);
                    });

            clientRepository.deleteClient(clientId);
            transaction.commit();
        } catch (ClientNotFoundException e) {
            rollbackTransaction(transaction);
            throw e;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при удалении клиента с id={}", clientId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при удалении клиента с id={}", clientId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка удаления клиента", e);
        }
    }

    @Override
    public boolean checkClientIsExist(long clientId) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            boolean result = clientRepository.checkClientIsExist(clientId);
            transaction.commit();
            return result;
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при проверке существования клиента с id={}", clientId, e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при проверке существования клиента с id={}", clientId, e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка проверки существования клиента", e);
        }
    }

    @Override
    public void exportClientsToCsv(String filePath) {
        Session session = HibernateUtil.getCurrentSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();
            Collection<Client> allClients = clientRepository.getAllClients().values();
            transaction.commit();

            clientCsvExporter.exportToCsv(allClients, filePath);
        } catch (DatabaseException e) {
            log.error("Ошибка базы данных при экспорте клиентов в CSV", e);
            rollbackTransaction(transaction);
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при экспорте клиентов в CSV", e);
            rollbackTransaction(transaction);
            throw new RuntimeException("Ошибка экспорта клиентов", e);
        }
    }

    private void updateClientFields(Client existingClient, Client newData) {
        if (newData.getName() != null) {
            existingClient.setName(newData.getName());
        }
        if (newData.getSurname() != null) {
            existingClient.setSurname(newData.getSurname());
        }
        if (newData.getEmail() != null) {
            existingClient.setEmail(newData.getEmail());
        }
        if (newData.getLogin() != null) {
            existingClient.setLogin(newData.getLogin());
        }
        if (newData.getPassword() != null) {
            existingClient.setPassword(newData.getPassword());
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (Exception rollbackEx) {
                log.error("Ошибка при откате транзакции", rollbackEx);
            }
        }
    }
}