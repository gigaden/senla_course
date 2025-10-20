package ebookstore.controller;

import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.service.OrderService;

public class ConsoleOrderController {

    private final OrderService orderService;

    public ConsoleOrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    public void saveOrder(Order order) {
        System.out.println("Сохраняем заказ в базу");
        Order savedOrder = orderService.createOrder(order);
        System.out.printf("Сохранили заказ: %s\n", savedOrder);
    }

    public void getOrder(long orderId) {
        System.out.printf("Получаем заказ с id = %d\n", orderId);
        Order order = orderService.getOrderById(orderId);
        System.out.printf("Получили заказ: %s\n", order);
    }

    public void changeOrderStatus(long orderId, OrderStatus orderStatus) {
        System.out.printf("Меняем статус заказа с id = %d\n", orderId);
        orderService.changeStatus(orderId, orderStatus);
        System.out.printf("Изменили статус заказа с id = %d\n", orderId);
    }

    public void cancelOrder(long orderId) {
        System.out.printf("Меняем статус заказа с id = %d на отменённый\n", orderId);
        orderService.cancelOrder(orderId);
        System.out.printf("Изменили статус заказа с id = %d на отменённый\n", orderId);
    }
}
