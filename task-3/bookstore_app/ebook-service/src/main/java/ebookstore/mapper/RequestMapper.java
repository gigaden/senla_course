package ebookstore.mapper;

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
}
