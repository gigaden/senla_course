package ebookstore.service;

import ebookstore.dto.client.ClientResponseDto;
import ebookstore.model.Client;

import java.util.Collection;

public interface ClientService {

    ClientResponseDto saveClient(Client client);

    Collection<ClientResponseDto> getAllClients();

    Client getClientById(long clientId);

    ClientResponseDto getClientDtoById(long clientId);

    ClientResponseDto updateClient(Client client);

    void deleteClientById(long clientId);

    boolean checkClientIsExist(long clientId);

    void exportClientsToCsv(String filePath);
}
