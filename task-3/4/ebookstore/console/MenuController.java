package ebookstore.console;

import ebookstore.controller.ConsoleBookController;
import ebookstore.controller.ConsoleBookRequestController;
import ebookstore.controller.ConsoleClientController;
import ebookstore.controller.ConsoleOrderController;
import ebookstore.model.Book;
import ebookstore.model.BookRequest;
import ebookstore.model.Client;
import ebookstore.model.Order;
import ebookstore.model.enums.OrderStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Scanner;

public class MenuController {

    private final ConsoleBookController bookController;
    private final ConsoleClientController clientController;
    private final ConsoleOrderController orderController;
    private final ConsoleBookRequestController requestController;
    private final Scanner scanner;

    public MenuController(ConsoleBookController bookController,
                          ConsoleClientController clientController,
                          ConsoleOrderController orderController,
                          ConsoleBookRequestController requestController
    ) {
        this.bookController = bookController;
        this.clientController = clientController;
        this.orderController = orderController;
        this.requestController = requestController;
        scanner = new Scanner(System.in);
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

    /*
    Методы книг.
    Надо бы всё это дело разнести по отдельности...
    * **/
    private void processAddBookMenu() {
        String title;
        String author;
        String description;
        LocalDate date;
        double price;
        System.out.println("-- Добавление новой книги на склад --");
        try {
            System.out.println("Введите название книги:");
            title = scanner.nextLine();
            System.out.println("Введите автора книги:");
            author = scanner.nextLine();
            System.out.println("Введите описание книги:");
            description = scanner.nextLine();
            System.out.println("Введите дату издания книги ГГГГ-ММ-ДД:");
            date = LocalDate.parse(scanner.nextLine());
            System.out.println("Введите стоимость книги:");
            price = scanner.nextDouble();

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
            String bookId = scanner.nextLine();
            bookController.changeBookStatusToAbsent(Integer.parseInt(bookId));

        } catch (Exception e) {
            System.out.println("Вы ввели неверный, или несуществующий id книги");
        }
    }

    private void processBookDescription() {
        System.out.println("-- Посмотреть описание книги --");
        try {
            System.out.println("Введите id книги для просмотра:");
            String bookId = scanner.nextLine();
            bookController.getBookDescription(Integer.parseInt(bookId));

        } catch (Exception e) {
            System.out.println("Вы ввели неверный, или несуществующий id книги");
        }
    }

    // методы для заказов
    private void processCreateOrderMenu() {
        System.out.println("-- Создание заказа --");
        try {
            System.out.println("Введите ID клиента:");
            long clientId = Long.parseLong(scanner.nextLine());

            Client client = new Client("", "", "", "", "");
            client.setId(clientId);

            System.out.println("Введите ID книги:");
            long bookId = Long.parseLong(scanner.nextLine());

            Book book = new Book("", "", "", LocalDate.now(), 0.0);
            book.setId(bookId);

            Order order = new Order(book, client);
            orderController.saveOrder(order);
            System.out.println("Заказ успешно создан!");

        } catch (Exception e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        }
    }

    private void processCancelOrderMenu() {
        System.out.println("-- Отмена заказа --");
        try {
            System.out.println("Введите ID заказа для отмены:");
            long orderId = Long.parseLong(scanner.nextLine());
            orderController.cancelOrder(orderId);
            System.out.println("Заказ успешно отменен!");
        } catch (Exception e) {
            System.out.println("Ошибка при отмене заказа: " + e.getMessage());
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

        } catch (Exception e) {
            System.out.println("Ошибка при изменении статуса: " + e.getMessage());
        }
    }

    private void processOrderDetailsMenu() {
        System.out.println("-- Детали заказа --");
        try {
            System.out.println("Введите ID заказа:");
            long orderId = Long.parseLong(scanner.nextLine());
            orderController.getOrderDetails(orderId);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    // методы для запроса
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

        } catch (Exception e) {
            System.out.println("Ошибка при создании запроса: " + e.getMessage());
        }
    }

    // методы для статистики
    private void processCompletedOrdersByDateMenu() {
        System.out.println("-- Выполненные заказы за период (по дате) --");
        try {
            LocalDateTime[] period = getPeriodFromUser();
            orderController.getCompletedOrdersByDate(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void processCompletedOrdersByPriceMenu() {
        System.out.println("-- Выполненные заказы за период (по цене) --");
        try {
            LocalDateTime[] period = getPeriodFromUser();
            orderController.getCompletedOrdersByPrice(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void processEarnedAmountMenu() {
        System.out.println("-- Сумма заработанных средств за период --");
        try {
            LocalDateTime[] period = getPeriodFromUser();
            orderController.getEarnedAmountInPeriod(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private void processCompletedOrdersCountMenu() {
        System.out.println("-- Количество выполненных заказов за период --");
        try {
            LocalDateTime[] period = getPeriodFromUser();
            orderController.getCompletedOrdersCountInPeriod(period[0], period[1]);
        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private LocalDateTime[] getPeriodFromUser() {
        System.out.println("Введите начальную дату (ГГГГ-ММ-ДД):");
        LocalDate startDate = LocalDate.parse(scanner.nextLine());

        System.out.println("Введите конечную дату (ГГГГ-ММ-ДД):");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(23, 59, 59);

        return new LocalDateTime[]{start, end};
    }
}
