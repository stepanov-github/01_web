import okhttp3.Request;
import okhttp3.*;

import java.io.IOException;

public class GetRequest {
    public static void main(String[] args) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
//                .url("http://localhost:9999/classic.html")
                .url("http://localhost:9999/forms.html?login=afae&password=dthd")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        System.out.println(response);
    }
}
