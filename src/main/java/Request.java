import java.nio.file.Path;
import java.util.Map;

public class Request {

    private String method, path, protocolVersion;
    private String body;
    private Map<String, String> headers;

    public Request(String method, String path, String protocolVersion, String body, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
        this.body = body;
        this.headers = headers;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public Path getPathFile() {
        return Path.of(".", "public", path);
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(method)
                .append(" ")
                .append(path)
                .append(" ")
                .append(protocolVersion)
                .append("\r\n");
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            stringBuilder.append(entry.getKey() + ": " + entry.getValue());
            stringBuilder.append("\n");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        stringBuilder.append("\r\n\r\n");
        stringBuilder.append(body);
        return stringBuilder.toString();
    }


}
