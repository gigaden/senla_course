package ebookstore.controller.restapi;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.dto.client.ClientUpdateDto;
import ebookstore.service.ClientService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

/**
 * Контроллер обрабатывает запросы, связанные с клиентами
 */
@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private static final Logger log = LoggerFactory.getLogger(ClientController.class);

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Эндпоинт для сохранения нового клиента
     *
     * @param client - дто для создания клиента
     * @return сохранённый клиент
     */
    @PostMapping
    public ResponseEntity<ClientResponseDto> saveClient(@RequestBody @Valid ClientCreateDto client) {
        log.info("Сохранение нового клиента");
        ClientResponseDto response = clientService.saveClient(client);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Эндпоинт для получения всех клиентов
     *
     * @return коллекция клиентов
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Collection<ClientResponseDto>> getAllClients() {
        log.info("Получение всех клиентов");
        Collection<ClientResponseDto> clients = clientService.getAllClients();

        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    /**
     * Эндпоинт для получения клиента по идентификатору
     *
     * @param id - идентификатор клиента
     * @return клиент
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<ClientResponseDto> getClient(@PathVariable long id) {
        log.info("Получение клиента с id={}", id);
        ClientResponseDto client = clientService.getClientDtoById(id);

        return new ResponseEntity<>(client, HttpStatus.OK);
    }

    /**
     * Эндпоинт для обновления данных клиента
     *
     * @param dto - дто для обновления клиента
     * @return обновлённый клиент
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PutMapping
    public ResponseEntity<ClientResponseDto> updateClient(@RequestBody @Valid ClientUpdateDto dto) { // не забыть дто добавить
        log.info("Обновление клиента с id={}", dto.id());
        ClientResponseDto response = clientService.updateClient(dto);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Эндпоинт для удаления клиента по идентификатору
     *
     * @param id - идентификатор клиента
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable long id) {
        log.info("Удаление клиента с id={}", id);
        clientService.deleteClientById(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}