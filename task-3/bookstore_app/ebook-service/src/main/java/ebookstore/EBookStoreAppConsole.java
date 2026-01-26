package ebookstore;

import config.processor.ConfigurationInjector;
import di.annotation.Autowired;
import di.annotation.Component;
import di.container.DIContainer;
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
import ebookstore.repository.implement.dao.BookRepositoryDao;
import ebookstore.repository.implement.dao.BookRequestRepositoryDao;
import ebookstore.repository.implement.dao.ClientRepositoryDao;
import ebookstore.repository.implement.dao.OrderRepositoryDao;
import ebookstore.repository.implement.hiber.BookRepositoryHiber;
import ebookstore.repository.implement.hiber.ClientRepositoryHiber;
import ebookstore.service.BookRequestService;
import ebookstore.service.BookService;
import ebookstore.service.ClientService;
import ebookstore.service.OrderService;
import ebookstore.service.csv.reader.BookCsvReader;
import ebookstore.service.csv.reader.BookRequestCsvReader;
import ebookstore.service.csv.reader.ClientCsvReader;
import ebookstore.service.csv.reader.OrderCsvReader;
import ebookstore.service.csv.writer.BookCsvExporter;
import ebookstore.service.csv.writer.BookRequestCsvExporter;
import ebookstore.service.csv.writer.ClientCsvExporter;
import ebookstore.service.csv.writer.OrderCsvExporter;
import ebookstore.service.implement.BookRequestServiceImpl;
import ebookstore.service.implement.BookServiceImpl;
import ebookstore.service.implement.ClientServiceImpl;
import ebookstore.service.implement.OrderServiceImpl;
import ebookstore.util.ConnectionManager;
import ebookstore.util.DataSerializer;

import java.util.Map;

/**
 * Класс регистрирует бины, инициализирует их и запускает консольное приложение
 */
@Component
public class EBookStoreAppConsole {

    @Autowired
    private ConsoleMenu consoleMenu;

    @Autowired
    private DataSerializer dataSerializer;

    public void start() {
        consoleMenu.start();
    }

    public static void main(String[] args) {
        try {
            DIContainer container = DIContainer.getInstance();
            ConnectionManager connectionManager = new ConnectionManager();
            ConfigurationInjector.inject(connectionManager);

            container.registerBean(ConnectionManager.class, connectionManager);

            container.registerBean(
                    BookRepository.class,
                    new BookRepositoryHiber()
            );

            container.registerBean(
                    ClientRepository.class,
                    new ClientRepositoryHiber());

            container.registerBean(
                    OrderRepository.class,
                    new OrderRepositoryDao(connectionManager));

            container.registerBean(
                    BookRequestRepository.class,
                    new BookRequestRepositoryDao(connectionManager));

            // Регистрируем CSV экспортеры
            container.registerBean(BookCsvExporter.class, new BookCsvExporter());
            container.registerBean(ClientCsvExporter.class, new ClientCsvExporter());
            container.registerBean(OrderCsvExporter.class, new OrderCsvExporter());
            container.registerBean(BookRequestCsvExporter.class, new BookRequestCsvExporter());

            // Регистрируем сервисы
            container.registerBean(BookService.class, new BookServiceImpl());
            container.registerBean(ClientService.class, new ClientServiceImpl());
            container.registerBean(OrderService.class, new OrderServiceImpl());
            container.registerBean(BookRequestService.class, new BookRequestServiceImpl());

            // Регистрируем CSV ридеры
            container.registerBean(BookCsvReader.class, new BookCsvReader());
            container.registerBean(ClientCsvReader.class, new ClientCsvReader());
            container.registerBean(OrderCsvReader.class, new OrderCsvReader());
            container.registerBean(BookRequestCsvReader.class, new BookRequestCsvReader());

            // Регистрируем контроллеры
            container.registerBean(ConsoleBookController.class, new ConsoleBookController());
            container.registerBean(ConsoleClientController.class, new ConsoleClientController());
            container.registerBean(ConsoleOrderController.class, new ConsoleOrderController());
            container.registerBean(ConsoleBookRequestController.class, new ConsoleBookRequestController());

            // Регистрируем MenuController и ConsoleMenu
            container.registerBean(MenuController.class, new MenuController());
            container.registerBean(ConsoleMenu.class, new ConsoleMenu());

            container.registerBean(EBookStoreAppConsole.class, new EBookStoreAppConsole());
            container.registerBean(DataSerializer.class, new DataSerializer());

            // Инициализируем контейнер (внедряет зависимости через @Autowired)
            container.initialize();

            // Вручную применяем конфигурацию ко всем бинам
            applyConfiguration(container);

            EBookStoreAppConsole ebookStoreApp = container.getBean(EBookStoreAppConsole.class);

            ebookStoreApp.start();

        } catch (Exception e) {
            System.err.println("Ошибка запуска приложения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void applyConfiguration(DIContainer container) {
        // Применяем конфигурацию ко всем зарегистрированным бинам
        Map<Class<?>, Object> beansMap = container.getBeansMap();
        for (Object bean : beansMap.values()) {
            ConfigurationInjector.inject(bean);
        }
    }
}