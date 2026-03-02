package ebookstore;

import ebookstore.configuration.ConsoleConfig;
import ebookstore.console.ConsoleMenu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Класс регистрирует бины, инициализирует их и запускает консольное приложение
 */
@Component
public class EBookStoreAppConsole {

    private static final Logger log = LoggerFactory.getLogger(EBookStoreAppConsole.class);

    /*
     * ТуДу:
     *
     * - задокументировать больше
     * - переделать логику обработки и выброса исключений в консольном варианте
     * - вернуться к первым тз, вкурить чего хотят от запросов и переделать нормально
     * */
    public static void main(String[] args) {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ConsoleConfig.class)) {
            ConsoleMenu consoleMenu = context.getBean(ConsoleMenu.class);
            consoleMenu.start();
        } catch (Exception e) {
            log.error("Ошибка запуска: {}", e.getMessage(), e);
        }
    }
}