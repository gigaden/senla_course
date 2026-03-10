package ebookstore.mapper;

import ebookstore.dto.order.OrderCreateDto;
import ebookstore.dto.order.OrderResponseDto;
import ebookstore.model.Order;

/**
 * Маппер для заказов
 */
public final class OrderMapper {

    /**
     * Метод мапит из дто в заказ
     *
     * @param dto - дто заказа
     * @return - заказ
     */
    public static Order mapOrderDtoToOrder(OrderCreateDto dto) {
        return new Order(
                dto.book(),
                dto.client()
        );
    }

    /**
     * Метод мапит из заказа в дто для ответа клиенту
     *
     * @param order - заказ
     * @return - дто
     */
    public static OrderResponseDto mapOrderToResponseDto(Order order) {
        return new OrderResponseDto(order.getOrderId(),
                order.getBook().getId(),
                order.getClient().getId(),
                order.getCreatedOn(),
                order.getCompletedOn(),
                order.getOrderStatus().name());
    }
}
