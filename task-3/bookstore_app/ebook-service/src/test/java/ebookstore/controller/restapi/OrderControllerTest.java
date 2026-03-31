package ebookstore.controller.restapi;

import ebookstore.dto.order.OrderCreateDto;
import ebookstore.dto.order.OrderDetailsDto;
import ebookstore.dto.order.OrderResponseDto;
import ebookstore.dto.order.OrderUpdateDto;
import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderSortField;
import ebookstore.model.enums.OrderStatus;
import ebookstore.service.OrderService;
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

import java.time.LocalDate;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты контроллера заказов")
class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private OrderService orderService;

    @Test
    @DisplayName("Создание заказа")
    void shouldCreateOrder() {
        Book book = new Book();
        Client client = new Client();

        OrderCreateDto dto = new OrderCreateDto(book, client);
        OrderResponseDto responseDto = new OrderResponseDto(1L, 1L, 1L,
                LocalDate.now(), null, "NEW");

        Mockito.when(orderService.createOrder(dto)).thenReturn(responseDto);

        ResponseEntity<OrderResponseDto> response = orderController.createOrder(dto);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("Получение заказа")
    void shouldGetOrder() {
        OrderResponseDto dto = new OrderResponseDto(1L, 1L, 1L,
                LocalDate.now(), null, "NEW");

        Mockito.when(orderService.getOrderById(1L)).thenReturn(dto);

        ResponseEntity<OrderResponseDto> response = orderController.getOrder(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Получение деталей заказа")
    void shouldGetDetails() {
        OrderDetailsDto dto = new OrderDetailsDto(new Order(), new Client(), new Book());

        Mockito.when(orderService.getOrderDetails(1L)).thenReturn(dto);

        ResponseEntity<OrderDetailsDto> response =
                orderController.getOrderDetails(1L);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Обновление заказа")
    void shouldUpdateOrder() {
        OrderUpdateDto dto = new OrderUpdateDto(1L, 2L, 3L, OrderStatus.NEW);
        OrderResponseDto responseDto = new OrderResponseDto(1L, 2L, 3L,
                LocalDate.now(), null, "NEW");

        Mockito.when(orderService.updateOrder(dto)).thenReturn(responseDto);

        ResponseEntity<OrderResponseDto> response =
                orderController.updateOrder(dto);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Изменение статуса")
    void shouldChangeStatus() {
        ResponseEntity<String> response =
                orderController.changeOrderStatus(1L, OrderStatus.COMPLETED);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Mockito.verify(orderService).changeStatus(1L, OrderStatus.COMPLETED);
    }

    @Test
    @DisplayName("Отмена заказа")
    void shouldCancelOrder() {
        ResponseEntity<String> response =
                orderController.cancelOrder(1L);

        Assertions.assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        Mockito.verify(orderService).cancelOrder(1L);
    }

    @Test
    @DisplayName("Получение всех заказов")
    void shouldGetAll() {
        Mockito.when(orderService.getAllOrders(0, 5, OrderSortField.DATE))
                .thenReturn(List.of());

        ResponseEntity<Collection<OrderResponseDto>> response =
                orderController.getAll(0, 5, "DATE");

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Получение завершённых заказов")
    void shouldGetCompleted() {
        Mockito.when(orderService.getCompletedOrdersInPeriod(
                Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(List.of());

        ResponseEntity<Collection<OrderResponseDto>> response =
                orderController.getCompletedOrders(
                        LocalDate.now().minusDays(1),
                        LocalDate.now(),
                        "date"
                );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("Получение суммы")
    void shouldGetAmount() {
        Mockito.when(orderService.getEarnedAmountInPeriod(Mockito.any(), Mockito.any()))
                .thenReturn(100.0);

        ResponseEntity<Double> response =
                orderController.getEarnedAmount(LocalDate.now(), LocalDate.now());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(100.0, response.getBody());
    }

    @Test
    @DisplayName("Получение количества")
    void shouldGetCount() {
        Mockito.when(orderService.getCompletedOrdersCountInPeriod(Mockito.any(), Mockito.any()))
                .thenReturn(5);

        ResponseEntity<Integer> response =
                orderController.getCompletedOrdersCount(LocalDate.now(), LocalDate.now());

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(5, response.getBody());
    }
}