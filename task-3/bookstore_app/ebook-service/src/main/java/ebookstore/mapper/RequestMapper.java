package ebookstore.mapper;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.bookrequest.BookRequestDto;
import ebookstore.model.BookRequest;

/**
 * Маппер для запросов
 */
public final class RequestMapper {

    /**
     * Мапит запросы в дто
     *
     * @param bookRequest - запрос
     * @return - дто запроса
     */
    public static BookRequestDto mapRequestToDto(BookRequest bookRequest) {
        return new BookRequestDto(bookRequest.getRequestId(),
                bookRequest.getBookId(),
                bookRequest.getClientId(),
                bookRequest.getRequestStatus(),
                bookRequest.getCreatedOn());
    }

    /**
     * Мапит из дто в запросы
     *
     * @param dto - дто запроса
     * @return - болванка запроса
     */
    public static BookRequest mapDtoToBookRequest(BookRequestCreateDto dto) {
        return new BookRequest(
                dto.bookId(),
                dto.clientId());
    }
}
