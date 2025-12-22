/**
 * Класс для вывода текущего времени в консоль.
 * Добавил параметр для количества итераций в конструктор, чтобы не делать while(true)
 */
public class SystemTimeThread implements Runnable {

    private final Long delay;
    private volatile boolean running = true;

    /**
     * @param delay - задержка для вывода времени в консоль
     */
    public SystemTimeThread(Long delay) {
        this.delay = delay;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            while (running) {
                System.out.println(java.time.LocalTime.now());
                Thread.sleep(delay * 1000);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}