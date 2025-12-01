package ebookstore.console;

import java.util.Scanner;

public class ConsoleMenu {

    private final Scanner scanner;
    private final MenuController menuController;

    public ConsoleMenu(MenuController menuController) {
        this.menuController = menuController;
        scanner = new Scanner(System.in);
    }

    public void start() {
        boolean run = true;

        while (run) {
            PrintMenu.printMainMenu();
            String choice = scanner.nextLine();
            switch (choice) {
                case "1" -> menuController.processBookMenu();
                case "2" -> menuController.processOrderMenu();
                case "3" -> menuController.processRequestMenu();
                case "4" -> menuController.processStatisticMenu();
                case "5" -> menuController.processImportExportMenu();
                case "0" -> {
                    System.out.println("Выход из программы...");
                    run = false;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
        scanner.close();
    }
}