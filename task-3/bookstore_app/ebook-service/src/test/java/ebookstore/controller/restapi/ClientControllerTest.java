package ebookstore.controller.restapi;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.dto.client.ClientUpdateDto;
import ebookstore.exception.notfound.ClientNotFoundException;
import ebookstore.model.enums.ClientRole;
import ebookstore.service.ClientService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collection;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты REST контроллера клиентов")
class ClientControllerTest {

    @InjectMocks
    private ClientController clientController;

    @Mock
    private ClientService clientService;

    @Test
    @DisplayName("Создание клиента успешно")
    void shouldCreateClient() {
        ClientCreateDto dto = new ClientCreateDto(
                "username", "name", "surname",
                "email", "login", "password"
        );

        ClientResponseDto responseDto = new ClientResponseDto(
                1L, "name", "surname", "email", "login"
        );

        Mockito.when(clientService.saveClient(dto))
                .thenReturn(responseDto);

        ResponseEntity<ClientResponseDto> response = clientController.saveClient(dto);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("name", response.getBody().name());
        Mockito.verify(clientService).saveClient(dto);
    }

    @Test
    @DisplayName("Получение всех клиентов")
    void shouldReturnAllClients() {
        ClientResponseDto dto = new ClientResponseDto(
                1L, "name", "surname", "email", "login"
        );

        Mockito.when(clientService.getAllClients())
                .thenReturn(List.of(dto));

        ResponseEntity<Collection<ClientResponseDto>> response = clientController.getAllClients();

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(1, response.getBody().size());
        Mockito.verify(clientService).getAllClients();
    }

    @Test
    @DisplayName("Получение клиента по id")
    void shouldReturnClientById() {
        ClientResponseDto dto = new ClientResponseDto(
                1L, "name", "surname", "email", "login"
        );

        Mockito.when(clientService.getClientDtoById(1L))
                .thenReturn(dto);

        ResponseEntity<ClientResponseDto> response = clientController.getClient(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("name", response.getBody().name());
        Mockito.verify(clientService).getClientDtoById(1L);
    }

    @Test
    @DisplayName("Ошибка при получении клиента")
    void shouldThrowExceptionWhenClientNotFound() {
        Mockito.when(clientService.getClientDtoById(1L))
                .thenThrow(new ClientNotFoundException("error"));

        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientController.getClient(1L));
    }

    @Test
    @DisplayName("Обновление клиента успешно")
    void shouldUpdateClient() {
        ClientUpdateDto dto = new ClientUpdateDto(
                1L, "username", "name", "surname",
                "email", "login", "password", ClientRole.ROLE_USER
        );

        ClientResponseDto responseDto = new ClientResponseDto(
                1L, "name", "surname", "email", "login"
        );

        Mockito.when(clientService.updateClient(dto))
                .thenReturn(responseDto);

        ResponseEntity<ClientResponseDto> response = clientController.updateClient(dto);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("name", response.getBody().name());
        Mockito.verify(clientService).updateClient(dto);
    }

    @Test
    @DisplayName("Ошибка при обновлении клиента")
    void shouldThrowExceptionWhenUpdateFails() {
        ClientUpdateDto dto = new ClientUpdateDto(
                1L, "username", "name", "surname",
                "email", "login", "password", ClientRole.ROLE_USER
        );

        Mockito.when(clientService.updateClient(dto))
                .thenThrow(new ClientNotFoundException("error"));

        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientController.updateClient(dto));
    }

    @Test
    @DisplayName("Удаление клиента успешно")
    void shouldDeleteClient() {
        ResponseEntity<Void> response = clientController.deleteClient(1L);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(clientService).deleteClientById(1L);
    }

    @Test
    @DisplayName("Ошибка при удалении клиента")
    void shouldThrowExceptionWhenDeleteFails() {
        Mockito.doThrow(new ClientNotFoundException("error"))
                .when(clientService).deleteClientById(1L);

        Assertions.assertThrows(ClientNotFoundException.class,
                () -> clientController.deleteClient(1L));
    }
}