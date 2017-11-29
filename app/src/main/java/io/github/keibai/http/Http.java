package io.github.keibai.http;

import com.google.gson.Gson;

import io.github.keibai.models.ModelAbstract;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Http<T extends ModelAbstract> {
    private static final OkHttpClient http = new OkHttpClient();
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public void get(String url, HttpCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        http.newCall(request).enqueue(callback);
    }

    public void post(String url, T body, HttpCallback callback) {
        String jsonBody = new Gson().toJson(body);
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        http.newCall(request).enqueue(callback);
    }
}
