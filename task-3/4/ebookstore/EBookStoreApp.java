package ebookstore;

import ebookstore.controller.ConsoleBookController;
import ebookstore.controller.ConsoleClientController;
import ebookstore.controller.ConsoleOrderController;
import ebookstore.model.Book;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.OrderStatus;
import ebookstore.repository.BookRepository;
import ebookstore.repository.ClientRepository;
import ebookstore.repository.InMemoryBookRepository;
import ebookstore.repository.InMemoryClientRepository;
import ebookstore.repository.InMemoryOrderRepository;
import ebookstore.repository.OrderRepository;
import ebookstore.service.BookService;
import ebookstore.service.BookServiceImpl;
import ebookstore.service.ClientService;
import ebookstore.service.ClientServiceImpl;
import ebookstore.service.OrderService;
import ebookstore.service.OrderServiceImpl;

public class EBookStoreApp {
    public static void main(String[] args) {

        Book book = new Book("book1", "author1", "description1");
        Book book2 = new Book("book2", "author2", "description2");
        Book book3 = new Book("book3", "author3", "description3");
        Book book4 = new Book("book4", "author4", "description4");
        Book book5 = new Book("book5", "author5", "description5");

        BookRepository bookRepository = InMemoryBookRepository.getInstance();
        BookService bookService = new BookServiceImpl(bookRepository);
        ConsoleBookController bookController = new ConsoleBookController(bookService);

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

        ClientRepository clientRepository = InMemoryClientRepository.getInstance();
        ClientService clientService = new ClientServiceImpl(clientRepository);
        ConsoleClientController clientController = new ConsoleClientController(clientService);

        Client client = new Client("clientName", "clientSurname",
                "client@ya.ru", "login", "password");

        clientController.saveClient(client);
        clientController.getAllClients();

        OrderRepository orderRepository = InMemoryOrderRepository.getInstance();
        OrderService orderService = new OrderServiceImpl(clientService, orderRepository);
        ConsoleOrderController orderController = new ConsoleOrderController(orderService);

        Order order = new Order(book, client);
        orderController.saveOrder(order);
        orderController.getOrder(0);
        orderController.changeOrderStatus(0, OrderStatus.COMPLETED);
        orderController.getOrder(0);
        orderController.cancelOrder(0);
        orderController.getOrder(0);

    }
}
