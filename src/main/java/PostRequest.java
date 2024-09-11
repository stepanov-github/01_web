import okhttp3.Request;
import okhttp3.*;

import java.io.IOException;

public class PostRequest {

    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "test\r\n   Test1\r\n       Test2");
        Request request = new Request.Builder()
                .url("http://localhost:9999/index.html")
                .method("POST", body)
                .addHeader("Content-Type", "text/plain")
                .build();
        Response response = client.newCall(request).execute();
    }
}
