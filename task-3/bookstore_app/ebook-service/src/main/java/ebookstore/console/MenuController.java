package ebookstore.console;

import di.annotation.Autowired;
import di.annotation.Component;
import ebookstore.controller.ConsoleBookController;
import ebookstore.controller.ConsoleBookRequestController;
import ebookstore.controller.ConsoleClientController;
import ebookstore.controller.ConsoleOrderController;
import ebookstore.exception.BookNotFoundException;
import ebookstore.exception.ClientNotFoundException;
import ebookstore.exception.OrderNotFoundException;
import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

import java.time.LocalDate;
import java.util.Scanner;

@Component
public class MenuController {

    @Autowired
    private ConsoleBookController bookController;

    @Autowired
    private ConsoleClientController clientController;

    @Autowired
    private ConsoleOrderController orderController;

    @Autowired
    private ConsoleBookRequestController requestController;

    private final Scanner scanner = new Scanner(System.in);

    public MenuController() {
    }

    public void processBookMenu() {
        boolean run = true;
        String choice;
        while (run) {
            PrintMenu.printBookMenu();
            choice = scanner.nextLine();

            switch (choice) {
                case "1" -> bookController.getAllBooksByAlphabet();
                case "2" -> bookController.getAllBooksByDateOfPublish();
                case "3" -> bookController.getAllBooksByPrice();
                case "4" -> bookController.getAllBooksByAvailability();
                case "5" -> processAddBookMenu();
                case "6" -> processChangeBookStatusToAbsent();
                case "7" -> processBookDescription();
                case "0" -> run = false;
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    public void processOrderMenu() {
        boolean run = true;
        String choice;
        while (run) {
            PrintMenu.printOrderMenu();
            choice = scanner.nextLine();

            switch (choice) {
                case "1" -> orderController.getAllOrdersByDateOfCompleting();
                case "2" -> orderController.getAllOrdersByPrice();
                case "3" -> orderController.getAllOrdersByStatus();
                case "4" -> processCreateOrderMenu();
                case "5" -> processCancelOrderMenu();
                case "6" -> processChangeOrderStatusMenu();
                case "7" -> processOrderDetailsMenu();
                case "0" -> run = false;
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    public void processRequestMenu() {
        boolean run = true;
        String choice;
        while (run) {
            PrintMenu.printRequestMenu();
            choice = scanner.nextLine();

            switch (choice) {
                case "1" -> requestController.getAllBookRequestByCountOfRequest();
                case "2" -> requestController.getAllBookRequestByTitleOfBookByAlphabet();
                case "3" -> processCreateRequestMenu();
                case "0" -> run = false;
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    public void processStatisticMenu() {
        boolean run = true;
        String choice;
        while (run) {
            PrintMenu.printStatisticMenu();
            choice = scanner.nextLine();

            switch (choice) {
                case "1" -> processCompletedOrdersByDateMenu();
                case "2" -> processCompletedOrdersByPriceMenu();
                case "3" -> processEarnedAmountMenu();
                case "4" -> processCompletedOrdersCountMenu();
                case "0" -> run = false;
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    public void processImportExportMenu() {
        boolean run = true;
        String choice;
        while (run) {
            PrintMenu.printImportExportMenu();
            choice = scanner.nextLine();

            switch (choice) {
                case "1" -> processExportBooksToCsv();
                case "2" -> processImportBookFromCsv();
                case "3" -> processExportClientsToCsv();
                case "4" -> processImportClientsFromCsv();
                case "5" -> processExportOrdersToCsv();
                case "6" -> processImportOrdersFromCsv();
                case "7" -> processExportRequestsToCsv();
                case "8" -> processImportRequestsFromCsv();
                case "0" -> run = false;
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void processAddBookMenu() {
        System.out.println("-- Добавление новой книги на склад --");
        try {
            System.out.println("Введите название книги:");
            String title = scanner.nextLine();
            System.out.println("Введите автора книги:");
            String author = scanner.nextLine();
            System.out.println("Введите описание книги:");
            String description = scanner.nextLine();
            System.out.println("Введите дату издания книги ГГГГ-ММ-ДД:");
            LocalDate date = LocalDate.parse(scanner.nextLine());
            System.out.println("Введите стоимость книги:");
            double price = Double.parseDouble(scanner.nextLine());

            Book book = new Book(title, author, description, date, price);
            bookController.saveBook(book);
            System.out.println("Книга добавлена");
        } catch (Exception e) {
            System.out.println("Ошибка ввода данных");
        }
    }

    private void processChangeBookStatusToAbsent() {
        System.out.println("-- Списание книги со склада --");
        try {
            System.out.println("Введите id книги для списания:");
            long bookId = Long.parseLong(scanner.nextLine());
            bookController.changeBookStatusToAbsent(bookId);
        } catch (BookNotFoundException e) {
            System.out.println("Возникла ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Возникла непредвиденная ошибка: " + e.getMessage());
        }
    }

    private void processBookDescription() {
        System.out.println("-- Посмотреть описание книги --");
        try {
            System.out.println("Введите id книги для просмотра:");
            long bookId = Long.parseLong(scanner.nextLine());
            bookController.getBookDescription(bookId);
        } catch (BookNotFoundException e) {
            System.out.println("Возникла ошибка: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Возникла непредвиденная ошибка: " + e.getMessage());
        }
    }

    private void processCreateOrderMenu() {
        System.out.println("-- Создание заказа --");
        try {
            System.out.println("Введите ID клиента:");
            long clientId = Long.parseLong(scanner.nextLine());
            System.out.println("Введите ID книги:");
            long bookId = Long.parseLong(scanner.nextLine());

            Client client = new Client("", "", "", "", "");
            client.setId(clientId);

            Book book = new Book("", "", "", LocalDate.now(), 0.0);
            book.setId(bookId);

            Order order = new Order(book, client);
            orderController.saveOrder(order);
            System.out.println("Заказ успешно создан!");
        } catch (BookNotFoundException e) {
            System.out.println("Возникла ошибка при поиске книги: " + e.getMessage());
        } catch (ClientNotFoundException e) {
            System.out.println("Возникла ошибка при поиске клиента: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Возникла непредвиденная ошибка: " + e.getMessage());
        }
    }

    private void processCancelOrderMenu() {
        System.out.println("-- Отмена заказа --");
        try {
            System.out.println("Введите ID заказа для отмены:");
            long orderId = Long.parseLong(scanner.nextLine());
            orderController.cancelOrder(orderId);
            System.out.println("Заказ успешно отменен!");
        } catch (OrderNotFoundException e) {
            System.out.println("Ошибка поиска заказа: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Возникла непредвиденная ошибка: " + e.getMessage());
        }
    }

    private void processChangeOrderStatusMenu() {
        System.out.println("-- Изменение статуса заказа --");
        try {
            System.out.println("Введите ID заказа:");
            long orderId = Long.parseLong(scanner.nextLine());

            System.out.println("Выберите новый статус:");
            System.out.println("1 - Новый");
            System.out.println("2 - Выполнен");
            System.out.println("3 - Отменен");
            String statusChoice = scanner.nextLine();

            OrderStatus status = switch (statusChoice) {
                case "1" -> OrderStatus.NEW;
                case "2" -> OrderStatus.COMPLETED;
                case "3" -> OrderStatus.CANCELED;
                default -> throw new IllegalArgumentException("Неверный выбор статуса");
            };

            orderController.changeOrderStatus(orderId, status);
            System.out.println("Статус заказа успешно изменен!");
        } catch (OrderNotFoundException e) {
            System.out.println("Ошибка поиска заказа: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка при изменении статуса: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Возникла непредвиденная ошибка: " + e.getMessage());
        }
    }

    private void processOrderDetailsMenu() {
        System.out.println("-- Детали заказа --");
        try {
            System.out.println("Введите ID заказа:");
            long orderId = Long.parseLong(scanner.nextLine());
            orderController.getOrderDetails(orderId);
        } catch (OrderNotFoundException e) {
            System.out.println("Ошибка поиска заказа: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void processCreateRequestMenu() {
        System.out.println("-- Создание запроса на книгу --");
        try {
            System.out.println("Введите ID клиента:");
            long clientId = Long.parseLong(scanner.nextLine());
            System.out.println("Введите ID книги:");
            long bookId = Long.parseLong(scanner.nextLine());

            BookRequest request = new BookRequest(bookId, clientId);
            requestController.createRequest(request);
            System.out.println("Запрос на книгу успешно создан!");
        } catch (BookNotFoundException e) {
            System.out.println("Возникла ошибка при поиске книги: " + e.getMessage());
        } catch (ClientNotFoundException e) {
            System.out.println("Возникла ошибка при поиске клиента: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Возникла непредвиденная ошибка при создании запроса на книгу: " + e.getMessage());
        }
    }

    private void processCompletedOrdersByDateMenu() {
        System.out.println("-- Выполненные заказы за период (по дате) --");
        try {
            LocalDate[] period = getPeriodFromUser();
            orderController.getCompletedOrdersByDate(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка получения выполненных заказов по дате: " + e.getMessage());
        }
    }

    private void processCompletedOrdersByPriceMenu() {
        System.out.println("-- Выполненные заказы за период (по цене) --");
        try {
            LocalDate[] period = getPeriodFromUser();
            orderController.getCompletedOrdersByPrice(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка получения выполненных заказов по цене: " + e.getMessage());
        }
    }

    private void processEarnedAmountMenu() {
        System.out.println("-- Сумма заработанных средств за период --");
        try {
            LocalDate[] period = getPeriodFromUser();
            orderController.getEarnedAmountInPeriod(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка получения суммы заработанных средств: " + e.getMessage());
        }
    }

    private void processCompletedOrdersCountMenu() {
        System.out.println("-- Количество выполненных заказов за период --");
        try {
            LocalDate[] period = getPeriodFromUser();
            orderController.getCompletedOrdersCountInPeriod(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка получения количества выполненных заказов за период: " + e.getMessage());
        }
    }

    private void processImportBookFromCsv() {
        try {
            System.out.println("Введите имя файла для импорта:");
            String filename = scanner.nextLine();
            bookController.importBooksFromCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка импорта данных");
        }
    }

    private void processExportBooksToCsv() {
        try {
            System.out.println("Введите имя файла для экспорта:");
            String filename = scanner.nextLine();
            bookController.exportBooksToCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка экспорта данных");
        }
    }

    private void processExportClientsToCsv() {
        try {
            System.out.println("Введите имя файла для экспорта:");
            String filename = scanner.nextLine();
            clientController.exportClientsToCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка экспорта данных");
        }
    }

    private void processImportClientsFromCsv() {
        try {
            System.out.println("Введите имя файла для импорта:");
            String filename = scanner.nextLine();
            clientController.importClientsFromCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка импорта данных");
        }
    }

    private void processExportOrdersToCsv() {
        try {
            System.out.println("Введите имя файла для экспорта:");
            String filename = scanner.nextLine();
            orderController.exportOrdersToCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка экспорта данных");
        }
    }

    private void processImportOrdersFromCsv() {
        try {
            System.out.println("Введите имя файла для импорта:");
            String filename = scanner.nextLine();
            orderController.importOrdersFromCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка импорта данных");
        }
    }

    private void processExportRequestsToCsv() {
        try {
            System.out.println("Введите имя файла для экспорта:");
            String filename = scanner.nextLine();
            requestController.exportRequestsToCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка экспорта данных");
        }
    }

    private void processImportRequestsFromCsv() {
        try {
            System.out.println("Введите имя файла для импорта:");
            String filename = scanner.nextLine();
            requestController.importRequestsFromCsv(filename);
        } catch (Exception e) {
            System.out.println("Ошибка импорта данных");
        }
    }

    private LocalDate[] getPeriodFromUser() {
        System.out.println("Введите начальную дату (ГГГГ-ММ-ДД):");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.println("Введите конечную дату (ГГГГ-ММ-ДД):");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        LocalDate start = LocalDate.from(startDate.atStartOfDay());
        LocalDate end = LocalDate.from(endDate.atTime(23, 59, 59));

        return new LocalDate[]{start, end};
    }
}