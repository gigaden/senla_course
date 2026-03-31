package ebookstore.service.implement;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.dto.client.ClientUpdateDto;
import ebookstore.exception.notfound.ClientNotFoundException;
import ebookstore.mapper.ClientMapper;
import ebookstore.model.Client;
import ebookstore.model.enums.ClientRole;
import ebookstore.repository.ClientRepository;
import ebookstore.service.csv.writer.ClientCsvExporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса пользователей")
class ClientServiceImplTest {

    @InjectMocks
    private ClientServiceImpl clientService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientCsvExporter clientCsvExporter;

    @Test
    @DisplayName("Сохранение клиента с валидными данными")
    void shouldSaveClientSuccessfully() {
        ClientCreateDto dto = new ClientCreateDto(
                "username1", "user1", "surname1",
                "email1", "login1", "password1"
        );

        Client client = ClientMapper.mapDtoCreateToClient(dto);

        Mockito.when(clientRepository.saveClient(any(Client.class)))
                .thenReturn(client);
        Mockito.when(passwordEncoder.encode(any()))
                .thenReturn("encodedPassword");

        ClientResponseDto response = clientService.saveClient(dto);

        Assertions.assertEquals(dto.name(), response.name());
        Mockito.verify(clientRepository).saveClient(any());
    }

    @Test
    @DisplayName("Получение всех клиентов")
    void shouldReturnAllClients() {
        Client client = new Client("u", "name", "surname", "email", "login", "pass", ClientRole.ROLE_USER);
        client.setId(1L);

        Mockito.when(clientRepository.getAllClients())
                .thenReturn(Map.of(1L, client));

        Collection<ClientResponseDto> result = clientService.getAllClients();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Получение всех клиентов - пустой список")
    void shouldReturnEmptyList() {
        Mockito.when(clientRepository.getAllClients())
                .thenReturn(Map.of());

        Collection<ClientResponseDto> result = clientService.getAllClients();

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Получение клиента по id")
    void shouldReturnClientById() {
        Client client = new Client("u", "name", "surname", "email", "login", "pass", ClientRole.ROLE_USER);
        client.setId(1L);

        Mockito.when(clientRepository.getClient(1L))
                .thenReturn(Optional.of(client));

        Client result = clientService.getClientById(1L);

        Assertions.assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("Ошибка: клиент не найден по id")
    void shouldThrowExceptionWhenClientNotFound() {
        Mockito.when(clientRepository.getClient(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientService.getClientById(1L));
    }

    @Test
    @DisplayName("Получение DTO клиента по id")
    void shouldReturnClientDtoById() {
        Client client = new Client("u", "name", "surname", "email", "login", "pass", ClientRole.ROLE_USER);
        client.setId(1L);

        Mockito.when(clientRepository.getClient(1L))
                .thenReturn(Optional.of(client));

        ClientResponseDto result = clientService.getClientDtoById(1L);

        Assertions.assertEquals("name", result.name());
    }

    @Test
    @DisplayName("Обновление клиента успешно")
    void shouldUpdateClientSuccessfully() {
        Client existing = new Client("u", "old", "old", "old", "old", "old", ClientRole.ROLE_USER);
        existing.setId(1L);

        ClientUpdateDto dto = new ClientUpdateDto(
                1L, "u", "newName", "newSurname",
                "newEmail", "newLogin", "newPass", ClientRole.ROLE_USER
        );

        Mockito.when(clientRepository.getClient(1L))
                .thenReturn(Optional.of(existing));

        Mockito.when(clientRepository.updateClient(any(Client.class)))
                .thenReturn(existing);

        ClientResponseDto response = clientService.updateClient(dto);

        Assertions.assertEquals("newName", response.name());
        Mockito.verify(clientRepository).updateClient(any());
    }

    @Test
    @DisplayName("Ошибка: обновление несуществующего клиента")
    void shouldThrowExceptionWhenUpdatingNonExistingClient() {
        ClientUpdateDto dto = new ClientUpdateDto(
                1L, "u", "name", "surname",
                "email", "login", "pass", ClientRole.ROLE_USER
        );

        Mockito.when(clientRepository.getClient(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientService.updateClient(dto));
    }

    @Test
    @DisplayName("Удаление клиента успешно")
    void shouldDeleteClient() {
        Client client = new Client();
        client.setId(1L);

        Mockito.when(clientRepository.getClient(1L))
                .thenReturn(Optional.of(client));

        clientService.deleteClientById(1L);

        Mockito.verify(clientRepository).deleteClient(1L);
    }

    @Test
    @DisplayName("Ошибка: удаление несуществующего клиента")
    void shouldThrowExceptionWhenDeletingNonExistingClient() {
        Mockito.when(clientRepository.getClient(1L))
                .thenReturn(Optional.empty());

        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientService.deleteClientById(1L));
    }

    @Test
    @DisplayName("Проверка существования клиента - true")
    void shouldReturnTrueWhenClientExists() {
        Mockito.when(clientRepository.checkClientIsExist(1L))
                .thenReturn(true);

        boolean result = clientService.checkClientIsExist(1L);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Проверка существования клиента - false")
    void shouldReturnFalseWhenClientNotExists() {
        Mockito.when(clientRepository.checkClientIsExist(1L))
                .thenReturn(false);

        boolean result = clientService.checkClientIsExist(1L);

        Assertions.assertFalse(result);
    }

    @Test
    @DisplayName("Экспорт клиентов в CSV")
    void shouldExportClientsToCsv() {
        Client client = new Client();
        client.setId(1L);

        Mockito.when(clientRepository.getAllClients())
                .thenReturn(Map.of(1L, client));

        clientService.exportClientsToCsv("file.csv");

        Mockito.verify(clientCsvExporter)
                .exportToCsv(Mockito.anyCollection(), Mockito.eq("file.csv"));
    }
}