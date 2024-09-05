import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int THREADS = 64;

    public static void main(String[] args) throws InterruptedException {

        final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
        Server server = new Server();
        server.start();

        Runnable logic = null;
        for (int j = 0; j < THREADS; j++) {
            logic = () -> {
                while (true) {
                    server.parse();
                    System.out.printf("%s thread- \n", Thread.currentThread().getName());                }

            };
            threadPool.submit(logic);
        }
        threadPool.shutdown();
        threadPool.awaitTermination(100, TimeUnit.SECONDS);


    }
}