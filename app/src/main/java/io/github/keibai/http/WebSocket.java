package io.github.keibai.http;

import android.content.Context;

import okhttp3.CookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class WebSocket {

    private final OkHttpClient client;

    public WebSocket(Context context) {
        CookieJar cookieJar = new HttpCookieJar(context);
        client = new OkHttpClient.Builder().cookieJar(cookieJar).build();
    }

    public WebSocketConnection connect(String url, WebSocketConnectionCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        return this.connect(request, callback);
    }

    public WebSocketConnection connect(Request request, WebSocketConnectionCallback callback) {
        return new WebSocketConnection(client, request, callback);
    }
}
