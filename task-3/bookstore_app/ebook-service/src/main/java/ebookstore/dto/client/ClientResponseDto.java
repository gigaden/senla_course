package ebookstore.dto.client;

public record ClientResponseDto(long id,
                                String name,
                                String surname,
                                String email,
                                String login) {
}
