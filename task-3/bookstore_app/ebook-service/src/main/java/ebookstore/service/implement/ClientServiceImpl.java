package ebookstore.service.implement;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.dto.client.ClientUpdateDto;
import ebookstore.exception.message.ClientErrorMessages;
import ebookstore.exception.notfound.ClientNotFoundException;
import ebookstore.mapper.ClientMapper;
import ebookstore.model.Client;
import ebookstore.repository.ClientRepository;
import ebookstore.service.ClientService;
import ebookstore.service.csv.writer.ClientCsvExporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

/**
 * Реализация сервиса для работы с клиентами.
 * Управляет бизнес-логикой связанной с клиентами, включая создание,
 * обновление, удаление и поиск клиентов.
 */
@Service
@Validated
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final ClientCsvExporter clientCsvExporter;

    public ClientServiceImpl(ClientRepository clientRepository,
                             ClientCsvExporter clientCsvExporter) {
        this.clientRepository = clientRepository;
        this.clientCsvExporter = clientCsvExporter;
    }

    private static final Logger log = LoggerFactory.getLogger(ClientServiceImpl.class);

    @Override
    @Transactional
    public ClientResponseDto saveClient(ClientCreateDto dto) {
        Client savedClient = clientRepository.saveClient(ClientMapper.mapDtoCreateToClient(dto));
        ClientResponseDto response = ClientMapper.mapClientToResponseDto(savedClient);
        log.info("Сохранили клиента {}", response);

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<ClientResponseDto> getAllClients() {
        Collection<Client> clients = clientRepository.getAllClients().values();
        List<ClientResponseDto> response = clients.stream()
                .map(ClientMapper::mapClientToResponseDto)
                .toList();
        log.info("Получили список клинтов в размере {}", response.size());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientById(long clientId) {
        Client client = clientRepository.getClient(clientId)
                .orElseThrow(() -> {
                    log.error("Клиент не найден id={}", clientId);
                    return new ClientNotFoundException(ClientErrorMessages.FIND_ERROR);
                });
        log.info("Получили клиента id = {}", client.getId());

        return client;
    }

    @Override
    @Transactional(readOnly = true)
    public ClientResponseDto getClientDtoById(long clientId) {
        return ClientMapper.mapClientToResponseDto(getClientById(clientId));
    }

    @Override
    @Transactional
    public ClientResponseDto updateClient(ClientUpdateDto dto) {
        Client existingClient = clientRepository.getClient(dto.id())
                .orElseThrow(() -> {
                    log.error("Клиент с id={} не найден для обновления", dto.id());
                    return new ClientNotFoundException(ClientErrorMessages.FIND_ERROR);
                });


        updateClientFields(existingClient, ClientMapper.mapClientUpdateToClient(dto));
        Client updatedClient = clientRepository.updateClient(existingClient);
        ClientResponseDto response = ClientMapper.mapClientToResponseDto(updatedClient);
        log.info("Обновили клиента с id = {}", response.id());

        return response;
    }

    @Override
    @Transactional
    public void deleteClientById(long clientId) {
        clientRepository.getClient(clientId)
                .orElseThrow(() -> {
                    log.error("Клиент с id={} не найден для удаления", clientId);
                    return new ClientNotFoundException(ClientErrorMessages.FIND_ERROR);
                });
        clientRepository.deleteClient(clientId);

        log.info("Удалили клиента с id = {}", clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean checkClientIsExist(long clientId) {
        boolean result = clientRepository.checkClientIsExist(clientId);
        log.info("Проверка существования клиента с id {} = {}", clientId, result);

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public void exportClientsToCsv(String filePath) {
        Collection<Client> allClients = clientRepository.getAllClients().values();
        clientCsvExporter.exportToCsv(allClients, filePath);
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
}