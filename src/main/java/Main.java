import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    private static final int THREADS = 64;

    public static void main(String[] args) throws InterruptedException {

        final ExecutorService threadPool = Executors.newFixedThreadPool(THREADS);
        Server server = new Server();

        server.addHandler("GET", "/classic.html", (r, s) -> {
            final var template = Files.readString(r.getPathFile());
            final var mimeType = Files.probeContentType(r.getPathFile());
            final var content = template.replace(
                    "{time}",
                    LocalDateTime.now().toString()
            ).getBytes();
            s.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + content.length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            s.write(content);
            s.flush();
        });


        Runnable logic = null;
        for (int j = 0; j < THREADS; j++) {
            logic = () -> {
                while (true) {
                    server.listen();
                    System.out.printf("%s thread- \n", Thread.currentThread().getName());
                }

            };
            threadPool.submit(logic);
        }
        threadPool.shutdown();
        threadPool.awaitTermination(100, TimeUnit.SECONDS);


    }
}