package ebookstore;

import ebookstore.console.ConsoleMenu;
import ebookstore.console.MenuController;
import ebookstore.controller.ConsoleBookController;
import ebookstore.controller.ConsoleBookRequestController;
import ebookstore.controller.ConsoleClientController;
import ebookstore.controller.ConsoleOrderController;
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
import ebookstore.service.csv.reader.BookCsvReader;
import ebookstore.service.csv.reader.BookRequestCsvReader;
import ebookstore.service.csv.reader.OrderCsvReader;
import ebookstore.service.csv.writer.BookCsvExporter;
import ebookstore.service.csv.writer.BookRequestCsvExporter;
import ebookstore.service.csv.writer.ClientCsvExporter;
import ebookstore.service.csv.writer.OrderCsvExporter;
import ebookstore.service.implement.BookRequestServiceImpl;
import ebookstore.service.implement.BookServiceImpl;
import ebookstore.service.implement.ClientServiceImpl;
import ebookstore.service.implement.OrderServiceImpl;
import ebookstore.util.DataSerializer;

public class EBookStoreAppConsole {
    private static final String BOOKS_FILE = "books.dat";
    private static final String ORDERS_FILE = "orders.dat";
    private static final String CLIENTS_FILE = "clients.dat";
    private static final String REQUESTS_FILE = "requests.dat";

    public static void main(String[] args) {

        BookRepository bookRepository = loadBookRepository();
        ClientRepository clientRepository = loadClientRepository();
        OrderRepository orderRepository = loadOrderRepository();
        BookRequestRepository requestRepository = loadRequestRepository();

        BookCsvExporter bookCsvExporter = new BookCsvExporter();
        ClientCsvExporter clientCsvExporter = new ClientCsvExporter();
        OrderCsvExporter orderCsvExporter = new OrderCsvExporter();
        BookRequestCsvExporter requestCsvExporter = new BookRequestCsvExporter();

        ClientService clientService = new ClientServiceImpl(clientRepository, clientCsvExporter);
        BookService bookService = new BookServiceImpl(bookRepository, orderRepository, null, bookCsvExporter);
        BookRequestService requestService = new BookRequestServiceImpl(
                requestRepository, bookService, clientService, requestCsvExporter
        );
        OrderService orderService = new OrderServiceImpl(
                clientService, orderRepository, requestService, orderCsvExporter
        );

        bookService = new BookServiceImpl(
                bookRepository, orderRepository, requestService, bookCsvExporter
        );

        BookCsvReader bookCsvReader = new BookCsvReader(bookService);
        OrderCsvReader orderCsvReader = new OrderCsvReader(orderService, bookService, clientService);
        BookRequestCsvReader requestCsvReader = new BookRequestCsvReader(requestService);

        ConsoleBookController bookController = new ConsoleBookController(bookService, bookCsvReader);
        ConsoleClientController clientController = new ConsoleClientController(clientService);
        ConsoleOrderController orderController = new ConsoleOrderController(orderService, orderCsvReader);
        ConsoleBookRequestController requestController = new ConsoleBookRequestController(requestService, requestCsvReader);

        MenuController menuController = new MenuController(
                bookController, clientController, orderController, requestController
        );
        ConsoleMenu consoleMenu = new ConsoleMenu(menuController);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\nСохранение данных перед закрытием...");
            saveData(bookRepository, clientRepository, orderRepository, requestRepository);
        }));

        consoleMenu.start();
    }

    private static BookRepository loadBookRepository() {
        return DataSerializer.<BookRepository>deserializeData(BOOKS_FILE)
                .orElse(InMemoryBookRepository.getInstance());
    }

    private static ClientRepository loadClientRepository() {
        return DataSerializer.<ClientRepository>deserializeData(CLIENTS_FILE)
                .orElse(InMemoryClientRepository.getInstance());
    }

    private static OrderRepository loadOrderRepository() {
        return DataSerializer.<OrderRepository>deserializeData(ORDERS_FILE)
                .orElse(InMemoryOrderRepository.getInstance());
    }

    private static BookRequestRepository loadRequestRepository() {
        return DataSerializer.<BookRequestRepository>deserializeData(REQUESTS_FILE)
                .orElse(InMemoryBookRequestRepository.getInstance());
    }

    private static void saveData(BookRepository bookRepository,
                                 ClientRepository clientRepository,
                                 OrderRepository orderRepository,
                                 BookRequestRepository requestRepository) {
        DataSerializer.serializeData(bookRepository, BOOKS_FILE);
        DataSerializer.serializeData(clientRepository, CLIENTS_FILE);
        DataSerializer.serializeData(orderRepository, ORDERS_FILE);
        DataSerializer.serializeData(requestRepository, REQUESTS_FILE);
    }
}