package io.github.keibai.http;

import android.support.annotation.Nullable;

import io.github.keibai.models.meta.BodyWS;
import okhttp3.*;
import okhttp3.WebSocket;
import okio.ByteString;

public class WebSocketCallback<T> extends WebSocketListener {

    public void onOpen(okhttp3.WebSocket webSocket, Response response) {
        onOpen(new WebSocketConnection(webSocket));
    }

    public void onOpen(WebSocketConnection connection) {}

    public void onMessage(okhttp3.WebSocket webSocket, String text) {
        onMessage(new WebSocketConnection(webSocket), text);
    }

    public void onMessage(WebSocket webSocket, ByteString bytes) {
        onMessage(new WebSocketConnection(webSocket), bytes.toString());
    }

    public void onMessage(WebSocketConnection connection, String text) {
        onMessage(connection, BodyWS.fromString(text));
    }

    public void onMessage(WebSocketConnection connection, BodyWS body) {}

    public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
        onClosing(new WebSocketConnection(webSocket), code, reason);
    }

    public void onClosing(WebSocketConnection connection, int code, String reason) {}

    public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
        onClosed(new WebSocketConnection(webSocket), code, reason);
    }

    public void onClosed(WebSocketConnection connection, int code, String reason) {}

    public void onFailure(WebSocket webSocket, Throwable t, @Nullable Response response) {
        onFailure(new WebSocketConnection(webSocket), t);
    }

    public void onFailure(WebSocketConnection connection, Throwable t) {}
}
