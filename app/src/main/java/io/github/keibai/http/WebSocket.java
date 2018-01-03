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

    public WebSocketConnection connect(String url, WebSocketCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okhttp3.WebSocket webSocket = client.newWebSocket(request, callback);
        return new WebSocketConnection(webSocket);
    }
}
