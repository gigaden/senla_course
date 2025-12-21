import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * В классе создаётся два потока, которые попеременно выводят своё имя
 */
public class ThreadName {
    private static final Object monitor = new Object();
    private static boolean isFirstThreadTurn = true;
    private static volatile boolean running = true;

    public static void main(String[] args) throws InterruptedException {
        Runnable task1 = () -> {
            try {
                while (running) {
                    synchronized (monitor) {
                        while (!isFirstThreadTurn && running) {
                            monitor.wait();
                        }

                        if (!running) break;

                        System.out.println(Thread.currentThread().getName());
                        isFirstThreadTurn = false;
                        monitor.notifyAll();
                    }

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName());
        };

        Runnable task2 = () -> {
            try {
                while (running) {
                    synchronized (monitor) {
                        while (isFirstThreadTurn && running) {
                            monitor.wait();
                        }

                        if (!running) break;

                        System.out.println(Thread.currentThread().getName());
                        isFirstThreadTurn = true;
                        monitor.notifyAll();
                    }

                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println(Thread.currentThread().getName());
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        executor.submit(task1);
        executor.submit(task2);

        Thread.sleep(5000);

        running = false;

        synchronized (monitor) {
            monitor.notifyAll();
        }

        executor.shutdown();
    }
}