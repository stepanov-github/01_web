import org.apache.hc.core5.net.URLEncodedUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Server {
    //    private final
    private final List<String> validPaths = List.of("/index.html", "/spring.svg", "/spring.png", "/resources.html", "/styles.css", "/app.js", "/links.html", "/forms.html", "/classic.html", "/events.html", "/events.js");
    private ServerSocket serverSocket;
    private Map<String, Map<String, Handler>> handlers = new HashMap<>();

    public Server() {
        try {
            serverSocket = new ServerSocket(9999);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addHandler(String type, String path, Handler handler) {
        if (!handlers.containsKey(type)) {
            Map map = new HashMap<String, Handler>();
            map.put(path, handler);
            handlers.put(type, map);
        } else {
            Map m = handlers.get(type);
            if (m.containsKey(path)) {
                System.out.println("Такой handler уже существует");
            } else {
                m.put(path, handler);
                handlers.put(type, m);
            }
        }


    }


    public void listen() {
        try (
                final var socket = serverSocket.accept();
                final var in = new BufferedInputStream(socket.getInputStream());
                final var out = new BufferedOutputStream(socket.getOutputStream());
        ) {
            final var max = 4096;
            in.mark(max);
            final var message = new byte[max];
            final var read = in.read(message);

            int indexRequestLineEnd = -1;
            for (int i = 0; i < read - 1; i++) {
                if (message[i] == '\r' && message[i + 1] == '\n') {
                    indexRequestLineEnd = i;
                    break;
                }
            }
            if (indexRequestLineEnd == -1) {
                return;
            }
            final var requestLine = new String(Arrays.copyOf(message, indexRequestLineEnd));
            System.out.println(requestLine);
            final var parts = requestLine.split(" ");

            if (parts.length != 3) {
                out.write((
                        "HTTP/1.1 400 Bad Request\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());

                return;
            }
            URI uri = new URI(parts[1]);
            final var pair = URLEncodedUtils.parse(uri, StandardCharsets.UTF_8);

            final var path = uri.getPath();
            if (!validPaths.contains(path)) {
                out.write((
                        "HTTP/1.1 404 Not Found\r\n" +
                                "Content-Length: 0\r\n" +
                                "Connection: close\r\n" +
                                "\r\n"
                ).getBytes());
                out.flush();
                return;
            }

            int indexHeadersEnd = -1;
            for (int i = indexRequestLineEnd; i < read - 3; i++) {
                if (message[i] == '\r' && message[i + 1] == '\n' && message[i + 2] == '\r' && message[i + 3] == '\n') {
                    indexHeadersEnd = i;
                }
            }

            final var headersByte = Arrays.copyOfRange(message, indexRequestLineEnd + 2, indexHeadersEnd);
            final var headersString = new String(headersByte);
            System.out.println(headersString);
            final var headersParts = headersString.split("\r\n");

            Map<String, String> headers = new HashMap<>();

            for (String line : headersParts) {
                System.out.println(line);
                final var header = line.split(": ");
                if (header.length != 2) {
                    continue;
                }
                headers.put(header[0], header[1]);
            }

            final var bodyByte = Arrays.copyOfRange(message, indexHeadersEnd + 4, read);
            final var bodyString = new String(bodyByte);
            System.out.println(bodyString);

            Request request = new Request(parts[0], parts[1], parts[2], bodyByte, headers);
            request.setQueryParams(pair);
            System.out.println(request.toString());
            final var filePath = Path.of(".", "public", path);
            final var mimeType = Files.probeContentType(filePath);

            if (handlers.containsKey(request.getMethod())) {
                if (handlers.get(request.getMethod()).containsKey(request.getPath())) {
                    handlers.get(request.getMethod()).get(request.getPath()).handle(request, out);
                    return;
                }
            }

            final var length = Files.size(filePath);
            out.write((
                    "HTTP/1.1 200 OK\r\n" +
                            "Content-Type: " + mimeType + "\r\n" +
                            "Content-Length: " + length + "\r\n" +
                            "Connection: close\r\n" +
                            "\r\n"
            ).getBytes());
            Files.copy(filePath, out);
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
