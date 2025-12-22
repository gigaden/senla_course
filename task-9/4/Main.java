/**
 * В этом классе тестим вывод времени в консоль
 */
public class Main {
    // время работы главного потока
    private static final long mainThreadTime = 10000L;
    // задержка времени вывода
    private static final long delay = 1L;

    public static void main(String[] args) {
        SystemTimeThread timeThread = new SystemTimeThread(delay);
        Thread thread = new Thread(timeThread);

        thread.start();
        try {
            Thread.sleep(mainThreadTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        timeThread.stop();

        thread.interrupt();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}