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
import ebookstore.repository.implement.InMemoryBookRepository;
import ebookstore.repository.implement.InMemoryBookRequestRepository;
import ebookstore.repository.implement.InMemoryClientRepository;
import ebookstore.repository.implement.InMemoryOrderRepository;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookRequestService;
import ebookstore.service.implement.BookRequestServiceImpl;
import ebookstore.service.BookService;
import ebookstore.service.implement.BookServiceImpl;
import ebookstore.service.ClientService;
import ebookstore.service.implement.ClientServiceImpl;
import ebookstore.service.OrderService;
import ebookstore.service.implement.OrderServiceImpl;

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
        bookService = new BookServiceImpl(bookRepository);
        bookController = new ConsoleBookController(bookService);

        clientRepository = InMemoryClientRepository.getInstance();
        clientService = new ClientServiceImpl(clientRepository);
        clientController = new ConsoleClientController(clientService);

        orderRepository = InMemoryOrderRepository.getInstance();
        requestRepository = InMemoryBookRequestRepository.getInstance();
        requestService = new BookRequestServiceImpl(requestRepository, bookService, clientService);
        orderService = new OrderServiceImpl(clientService, orderRepository, requestService);
        orderController = new ConsoleOrderController(orderService);
        requestController = new ConsoleBookRequestController(requestService);

    }

    public static void check() {
        Book book = new Book("book1", "author1", "description1");
        Book book2 = new Book("book2", "author2", "description2");
        Book book3 = new Book("book3", "author3", "description3");
        Book book4 = new Book("book4", "author4", "description4");
        Book book5 = new Book("book5", "author5", "description5");

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

        // чекаем запросы
        BookRequest bookRequest = new BookRequest(0, 0);

        try {
            requestController.createRequest(bookRequest);
        } catch (RuntimeException e) {
            System.out.printf("Сработало исключение: %s", e);
        }

        bookController.changeBookStatusToAbsent(0);
        requestController.createRequest(bookRequest);
    }
}
