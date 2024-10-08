import org.apache.hc.core5.http.NameValuePair;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class Request {

    private String method, path, protocolVersion;
    private byte[] body;
    private Map<String, String> headers;
    private List<NameValuePair> queryParams;

    public Request(String method, String path, String protocolVersion, byte[] body, Map<String, String> headers) {
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

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public List<NameValuePair> getQueryParams() {
        return queryParams;
    }

    public String getQueryParam(String name) {
        for (NameValuePair pair:queryParams) {
            if (pair.getName().equals(name)) {
                return pair.getValue();
            }
        }
        return null;
    }

    public void setQueryParams(List<NameValuePair> queryParams) {
        this.queryParams = queryParams;
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
