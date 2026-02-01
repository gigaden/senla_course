package ebookstore.service;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.model.Client;
import jakarta.validation.Valid;

import java.util.Collection;

public interface ClientService {

    ClientResponseDto saveClient(@Valid ClientCreateDto client);

    Collection<ClientResponseDto> getAllClients();

    Client getClientById(long clientId);

    ClientResponseDto getClientDtoById(long clientId);

    ClientResponseDto updateClient(Client client);

    void deleteClientById(long clientId);

    boolean checkClientIsExist(long clientId);

    void exportClientsToCsv(String filePath);
}
