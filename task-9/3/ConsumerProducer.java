import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Класс создаёт два потока. Один пишет в общую память, другой оттуда читает
 */
public class ConsumerProducer {

    private static final int memoryCapacity = 7;
    private static LinkedList<Integer> memory = new LinkedList<>();
    private static final Object monitor = new Object();
    private static final Random rnd = new Random();

    public static void main(String[] args) {

        Runnable producer = () -> {
            while (true) {
                synchronized (monitor) {
                    while (memory.size() == memoryCapacity) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    memory.add(rnd.nextInt());
                    monitor.notifyAll();
                }
            }
        };

        Runnable consumer = () -> {
            while (true) {
                synchronized (monitor) {
                    while (memory.isEmpty()) {
                        try {
                            monitor.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Integer num = memory.pop();
                    System.out.println(num);
                    monitor.notifyAll();
                }
            }
        };

        try (ExecutorService executor = Executors.newFixedThreadPool(2)) {
            executor.submit(consumer);
            executor.submit(producer);
        }
    }
}
