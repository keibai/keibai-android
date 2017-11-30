package io.github.keibai.http;

import android.content.Context;

import com.google.gson.Gson;

import io.github.keibai.models.ModelAbstract;
import okhttp3.CookieJar;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Http {
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private final OkHttpClient http;

    public Http(Context context) {
        CookieJar cookieJar = new HttpCookieJar(context);
        http = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    public void get(String url, HttpCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        http.newCall(request).enqueue(callback);
    }

    public void post(String url, ModelAbstract body, HttpCallback callback) {
        String jsonBody = body == null ? "{}" : new Gson().toJson(body);
        RequestBody requestBody = RequestBody.create(JSON, jsonBody);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        http.newCall(request).enqueue(callback);
    }
}
