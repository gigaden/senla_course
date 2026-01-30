package ebookstore.mapper;

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
}
