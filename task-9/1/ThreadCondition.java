/**
 * Класс показывает разные состояния потока
 */
public class ThreadCondition {

    private static final Object LOCK = new Object();

    public static void main(String[] args) throws Exception {

        Thread worker = new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getState());

                synchronized (LOCK) {
                    Thread.sleep(500);

                    LOCK.wait();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println(worker.getState());

        synchronized (LOCK) {
            worker.start();
            Thread.sleep(100);
            System.out.println(worker.getState());
        }

        Thread.sleep(200);
        System.out.println(worker.getState());

        Thread.sleep(600);
        System.out.println(worker.getState());

        synchronized (LOCK) {
            LOCK.notify();
        }

        worker.join();
        System.out.println(worker.getState());
    }
}