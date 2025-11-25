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

public class EBookStoreAppConsole {

    public static void main(String[] args) {
        BookRepository bookRepository;
        OrderRepository orderRepository;
        ClientRepository clientRepository;
        BookRequestRepository requestRepository;

        BookService bookService;
        OrderService orderService;
        ClientService clientService;
        BookRequestService requestService;

        BookCsvReader bookCsvReader;
        BookCsvExporter bookCsvExporter;
        ClientCsvExporter clientCsvExporter;
        OrderCsvExporter orderCsvExporter;
        BookRequestCsvExporter requestCsvExporter;
        OrderCsvReader orderCsvReader;
        BookRequestCsvReader requestCsvReader;

        ConsoleBookController bookController;
        ConsoleOrderController orderController;
        ConsoleClientController clientController;
        ConsoleBookRequestController requestController;

        ConsoleMenu consoleMenu;
        MenuController menuController;

        bookRepository = InMemoryBookRepository.getInstance();
        clientRepository = InMemoryClientRepository.getInstance();
        orderRepository = InMemoryOrderRepository.getInstance();
        requestRepository = InMemoryBookRequestRepository.getInstance();

        bookCsvExporter = new BookCsvExporter();
        clientCsvExporter = new ClientCsvExporter();
        orderCsvExporter = new OrderCsvExporter();
        requestCsvExporter = new BookRequestCsvExporter();


        clientService = new ClientServiceImpl(clientRepository, clientCsvExporter);
        bookService = new BookServiceImpl(bookRepository, orderRepository, bookCsvExporter);
        requestService = new BookRequestServiceImpl(requestRepository, bookService, clientService, requestCsvExporter);
        orderService = new OrderServiceImpl(clientService, orderRepository, requestService, orderCsvExporter);
        bookCsvReader = new BookCsvReader(bookService);
        orderCsvReader = new OrderCsvReader(orderService, bookService, clientService);
        requestCsvReader = new BookRequestCsvReader(requestService);


        bookController = new ConsoleBookController(bookService, bookCsvReader);
        clientController = new ConsoleClientController(clientService);
        orderController = new ConsoleOrderController(orderService, orderCsvReader);
        requestController = new ConsoleBookRequestController(requestService, requestCsvReader);

        menuController = new MenuController(bookController, clientController, orderController, requestController);
        consoleMenu = new ConsoleMenu(menuController);

        consoleMenu.start();
    }
}
