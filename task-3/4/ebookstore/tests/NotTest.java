package ebookstore.tests;

import ebookstore.controller.ConsoleBookController;
import ebookstore.controller.ConsoleBookRequestController;
import ebookstore.controller.ConsoleClientController;
import ebookstore.controller.ConsoleOrderController;
import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;
import ebookstore.repository.BookRepository;
import ebookstore.repository.BookRequestRepository;
import ebookstore.repository.ClientRepository;
import ebookstore.repository.OrderRepository;
import ebookstore.repository.implement.InMemoryBookRepository;
import ebookstore.repository.implement.InMemoryBookRequestRepository;
import ebookstore.repository.implement.InMemoryClientRepository;
import ebookstore.repository.implement.InMemoryOrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;
import ebookstore.service.OrderService;
import ebookstore.service.implement.BookRequestServiceImpl;
import ebookstore.service.implement.BookServiceImpl;
import ebookstore.service.implement.ClientServiceImpl;
import ebookstore.service.implement.OrderServiceImpl;

import java.time.LocalDate;

public final class NotTest {

    static BookRepository bookRepository;
    static OrderRepository orderRepository;
    static ClientRepository clientRepository;
    static BookRequestRepository requestRepository;

    static BookService bookService;
    static OrderService orderService;
    static ClientService clientService;
    static BookRequestService requestService;

    static ConsoleBookController bookController;
    static ConsoleOrderController orderController;
    static ConsoleClientController clientController;
    static ConsoleBookRequestController requestController;


    static {
        bookRepository = InMemoryBookRepository.getInstance();
        clientRepository = InMemoryClientRepository.getInstance();
        orderRepository = InMemoryOrderRepository.getInstance();
        requestRepository = InMemoryBookRequestRepository.getInstance();

        clientService = new ClientServiceImpl(clientRepository);
        bookService = new BookServiceImpl(bookRepository, orderRepository);
        requestService = new BookRequestServiceImpl(requestRepository, bookService, clientService);
        orderService = new OrderServiceImpl(clientService, orderRepository, requestService);

        bookController = new ConsoleBookController(bookService);
        clientController = new ConsoleClientController(clientService);
        orderController = new ConsoleOrderController(orderService);
        requestController = new ConsoleBookRequestController(requestService);

    }

    public static void check() {
        Book book = new Book("e_book1", "author1", "description1",
                LocalDate.of(2022, 5, 11), 100L);
        Book book2 = new Book("a_book2", "author2", "description2",
                LocalDate.of(2019, 5, 11), 80L);
        Book book3 = new Book("d_book3", "author3", "description3",
                LocalDate.of(2025, 5, 11), 50L);
        Book book4 = new Book("c_book4", "author4", "description4",
                LocalDate.of(2000, 5, 11), 700L);
        Book book5 = new Book("b_book5", "author5", "description5",
                LocalDate.of(1800, 5, 11), 650L);

        // чекаем книги
        bookController.saveBook(book);
        bookController.saveBook(book2);
        bookController.saveBook(book3);
        bookController.saveBook(book4);
        bookController.deleteBook(1);
        bookController.saveBook(book5);
        bookController.getAllBooks();
        bookController.getBook(4);
        bookController.changeBookStatusToAbsent(4);
        bookController.getBook(4);
        try {
            bookController.deleteBook(1000);
        } catch (RuntimeException e) {
            System.out.printf("Сработало исключение: %s", e);
        }

        // чекаем клиентов
        Client client = new Client("clientName", "clientSurname",
                "client@ya.ru", "login", "password");

        clientController.saveClient(client);
        clientController.getAllClients();

        // чекаем заказы
        Order order = new Order(book, client);
        orderController.saveOrder(order);
        orderController.getOrder(0);
        orderController.changeOrderStatus(0, OrderStatus.COMPLETED);
        orderController.getOrder(0);
        orderController.cancelOrder(0);
        orderController.getOrder(0);

        Order order1 = new Order(book2, client);
        orderController.saveOrder(order1);
        Order order2 = new Order(book4, client);
        orderController.saveOrder(order2);
        orderController.changeOrderStatus(2, OrderStatus.COMPLETED);
        Order order3 = new Order(book3, client);
        orderController.saveOrder(order3);
        orderController.changeOrderStatus(3, OrderStatus.COMPLETED);

        // чекаем запросы
        BookRequest bookRequest = new BookRequest(0, 0);

        try {
            requestController.createRequest(bookRequest);
        } catch (RuntimeException e) {
            System.out.printf("Сработало исключение: %s", e);
        }

        bookController.changeBookStatusToAbsent(0);
        requestController.createRequest(bookRequest);

        // проверяем сортировку по алфавиту
        bookController.getAllBooks();
        bookController.getAllBooksByAlphabet();

        // проверяем сортировку по дате выпуска
        bookController.getAllBooks();
        bookController.getAllBooksByDateOfPublish();

        // проверяем сортировку по цене
        bookController.getAllBooks();
        bookController.getAllBooksByPrice();

        // проверяем сортировку по наличию
        bookController.getAllBooks();
        bookController.getAllBooksByAvailability();

        // проверяем заказы

        // проверяем сортировку по дате завершения
        orderController.getAllOrdersByDateOfCompleting();

        // проверяем сортировку по цене
        orderController.getAllOrdersByPrice();

        // проверяем сортировку по статусу
        orderController.getAllOrdersByStatus();

        // проверяем запросы
        Client client1 = new Client("client1", "clientSurname",
                "client@ya.ru", "login", "password");
        Client client2 = new Client("client2", "clientSurname",
                "client@ya.ru", "login", "password");
        Client client3 = new Client("client3", "clientSurname",
                "client@ya.ru", "login", "password");
        clientController.saveClient(client1);
        clientController.saveClient(client2);
        clientController.saveClient(client3);
        bookController.changeBookStatusToAbsent(3);
        bookController.changeBookStatusToAbsent(2);
        BookRequest bookRequest1 = new BookRequest(0, 1);
        BookRequest bookRequest2 = new BookRequest(0, 2);
        BookRequest bookRequest3 = new BookRequest(3, 3);
        BookRequest bookRequest4 = new BookRequest(2, 0);
        requestController.createRequest(bookRequest1);
        requestController.createRequest(bookRequest2);
        requestController.createRequest(bookRequest3);
        requestController.createRequest(bookRequest4);
        requestController.getAllBookRequestByCountOfRequest();
        requestController.getAllBookRequestByTitleOfBookByAlphabet();

    }
}
