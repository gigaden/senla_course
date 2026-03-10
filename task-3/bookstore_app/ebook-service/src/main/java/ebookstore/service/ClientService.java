package ebookstore.service;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.dto.client.ClientUpdateDto;
import ebookstore.model.Client;

import java.util.Collection;

public interface ClientService {

    ClientResponseDto saveClient(ClientCreateDto client);

    Collection<ClientResponseDto> getAllClients();

    Client getClientById(long clientId);

    ClientResponseDto getClientDtoById(long clientId);

    ClientResponseDto updateClient(ClientUpdateDto dto);

    void deleteClientById(long clientId);

    boolean checkClientIsExist(long clientId);

    void exportClientsToCsv(String filePath);
}
