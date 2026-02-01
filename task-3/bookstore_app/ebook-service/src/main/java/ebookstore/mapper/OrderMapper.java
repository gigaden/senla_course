package ebookstore.mapper;

import ebookstore.dto.order.OrderCreateDto;
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
}
