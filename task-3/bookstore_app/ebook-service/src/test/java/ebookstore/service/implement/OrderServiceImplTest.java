package ebookstore.service.implement;

import ebookstore.dto.bookrequest.BookRequestCreateDto;
import ebookstore.dto.order.OrderCreateDto;
import ebookstore.dto.order.OrderResponseDto;
import ebookstore.dto.order.OrderUpdateDto;
import ebookstore.exception.DatesValidationException;
import ebookstore.exception.notfound.OrderNotFoundException;
import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.BookStatus;
import ebookstore.model.enums.OrderSortField;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;
import ebookstore.service.csv.writer.OrderCsvExporter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тесты сервиса заказов")
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ClientService clientService;

    @Mock
    private BookRequestService requestService;

    @Mock
    private BookService bookService;

    @Mock
    private OrderCsvExporter orderCsvExporter;

    // 🔥 helper чтобы не плодить ошибки
    private Order createOrder() {
        Book book = new Book();
        book.setId(1L);
        book.setPrice(10.0);
        book.setStatus(BookStatus.AVAILABLE);

        Client client = new Client();
        client.setId(1L);

        Order order = new Order();
        order.setId(1L);
        order.setBook(book);
        order.setClient(client);
        order.setOrderStatus(OrderStatus.NEW);
        order.setCreatedOn(LocalDate.now());

        return order;
    }

    @Test
    @DisplayName("Создание заказа успешно")
    void shouldCreateOrder() {
        Order order = createOrder();

        OrderCreateDto dto = new OrderCreateDto(order.getBook(), order.getClient());

        Mockito.when(orderRepository.createOrder(any(Order.class))).thenReturn(order);

        OrderResponseDto response = orderService.createOrder(dto);

        Assertions.assertEquals(1L, response.bookId());
    }

    @Test
    @DisplayName("Создание заказа с отсутствующей книгой вызывает создание запроса")
    void shouldCreateRequestIfBookAbsent() {
        Order order = createOrder();
        order.getBook().setStatus(BookStatus.ABSENT);

        OrderCreateDto dto = new OrderCreateDto(order.getBook(), order.getClient());

        Mockito.when(orderRepository.createOrder(any(Order.class))).thenReturn(order);

        orderService.createOrder(dto);

        Mockito.verify(requestService).createRequest(any(BookRequestCreateDto.class));
    }

    @Test
    @DisplayName("Получение заказа по id")
    void shouldGetOrderById() {
        Order order = createOrder();

        Mockito.when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(order));

        OrderResponseDto response = orderService.getOrderById(1L);

        Assertions.assertEquals(1L, response.id());
    }

    @Test
    @DisplayName("Ошибка если заказ не найден")
    void shouldThrowWhenOrderNotFound() {
        Mockito.when(orderRepository.getOrderById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.getOrderById(1L));
    }

    @Test
    @DisplayName("Получение всех заказов")
    void shouldGetAllOrders() {
        Order order = createOrder();

        Mockito.when(orderRepository.getAllOrders(0, 5, "createdOn"))
                .thenReturn(List.of(order));

        Collection<OrderResponseDto> result =
                orderService.getAllOrders(0, 5, OrderSortField.DATE);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Изменение статуса заказа")
    void shouldChangeStatus() {
        Order order = createOrder();

        Mockito.when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(order));
        Mockito.when(requestService.requestIsOpenForBookWithId(1L)).thenReturn(false);

        orderService.changeStatus(1L, OrderStatus.COMPLETED);

        Mockito.verify(orderRepository).updateOrder(order);
    }

    @Test
    @DisplayName("Ошибка при завершении если есть открытый запрос")
    void shouldThrowWhenRequestExists() {
        Order order = createOrder();

        Mockito.when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(order));
        Mockito.when(requestService.requestIsOpenForBookWithId(1L)).thenReturn(true);

        Assertions.assertThrows(RuntimeException.class,
                () -> orderService.changeStatus(1L, OrderStatus.COMPLETED));
    }

    @Test
    @DisplayName("Ошибка дат")
    void shouldThrowWhenDatesInvalid() {
        Assertions.assertThrows(DatesValidationException.class,
                () -> orderService.getEarnedAmountInPeriod(
                        LocalDate.now(), LocalDate.now().minusDays(1)
                ));
    }

    @Test
    @DisplayName("Проверка существования заказа")
    void shouldCheckExist() {
        Mockito.when(orderRepository.checkOrderIsExist(1L)).thenReturn(true);

        boolean result = orderService.checkOrderIsExist(1L);

        Assertions.assertTrue(result);
    }

    @Test
    @DisplayName("Обновление заказа")
    void shouldUpdateOrder() {
        Order order = createOrder();

        Book newBook = new Book();
        newBook.setId(2L);

        Client newClient = new Client();
        newClient.setId(3L);

        OrderUpdateDto dto = new OrderUpdateDto(1L, 2L, 3L, OrderStatus.NEW);

        Mockito.when(orderRepository.getOrderById(1L)).thenReturn(Optional.of(order));
        Mockito.when(bookService.getBookById(2L)).thenReturn(newBook);
        Mockito.when(clientService.getClientById(3L)).thenReturn(newClient);
        Mockito.when(orderRepository.updateOrder(order)).thenReturn(order);

        OrderResponseDto result = orderService.updateOrder(dto);

        Assertions.assertEquals(1L, result.id());
    }

    @Test
    @DisplayName("Ошибка при обновлении")
    void shouldThrowWhenUpdateNotFound() {
        OrderUpdateDto dto = new OrderUpdateDto(1L, 2L, 3L, OrderStatus.NEW);

        Mockito.when(orderRepository.getOrderById(1L)).thenReturn(Optional.empty());

        Assertions.assertThrows(OrderNotFoundException.class,
                () -> orderService.updateOrder(dto));
    }

    @Test
    @DisplayName("Экспорт заказов")
    void shouldExport() {
        Mockito.when(orderRepository.getAllOrders()).thenReturn(List.of());

        orderService.exportOrdersToCsv("file.csv");

        Mockito.verify(orderCsvExporter).exportToCsv(any(), Mockito.eq("file.csv"));
    }
}