package ebookstore.mapper;

import ebookstore.dto.client.ClientCreateDto;
import ebookstore.dto.client.ClientResponseDto;
import ebookstore.model.Client;

/**
 * Маппер для юзеров
 */
public final class ClientMapper {

    private ClientMapper() {
    }

    /**
     * Метод мапит из юзера в дто
     *
     * @param client - юзер
     * @return - дто
     */
    public static ClientResponseDto mapClientToResponseDto(Client client) {
        return new ClientResponseDto(client.getId(),
                client.getName(),
                client.getSurname(),
                client.getEmail(),
                client.getLogin());
    }

    /**
     * Метод мапит из дто создания в болванку юзера
     *
     * @param dto - dto юзера
     * @return - болванку юзера
     */
    public static Client mapDtoCreateToClient(ClientCreateDto dto) {
        return new Client(
                dto.name(),
                dto.surname(),
                dto.email(),
                dto.login(),
                dto.password()
        );
    }
}
