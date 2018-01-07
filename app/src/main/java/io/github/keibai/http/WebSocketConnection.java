package io.github.keibai.http;

import android.support.annotation.Nullable;

import java.util.concurrent.ConcurrentHashMap;

import io.github.keibai.models.meta.BodyWS;
import okhttp3.*;
import okio.ByteString;

public class WebSocketConnection {

    public final okhttp3.WebSocket webSocket;

    private final WebSocketConnectionCallback callback;
    private final ConcurrentHashMap<String, WebSocketBodyCallback> types;
    private final ConcurrentHashMap<String, WebSocketBodyCallback> nonces;

    WebSocketConnection(OkHttpClient client, Request request, WebSocketConnectionCallback callback) {
        this.webSocket = client.newWebSocket(request, new WebSocketCallbackRouter());
        this.callback = callback;
        this.types = new ConcurrentHashMap<>();
        this.nonces = new ConcurrentHashMap<>();
    }

    /**
     * Sends generic text.
     * @param text
     * @return
     */
    public boolean send(String text) {
        return this.webSocket.send(text);
    }

    /**
     * Sends a BodyWS as text.
     * @param body
     * @return
     */
    public boolean send(BodyWS body) {
        return this.webSocket.send(body.toString());
    }

    /**
     * Sends a BodyWS as text. Expects a (and only) response (identified by the same nonce).
     * The unique nonce will serve as an identifier for the callback. Readding a body with the same
     * nonce will overwrite the callback to be called.
     * @param body
     * @param callback
     * @return 1 if correctly enqueued, 0 otherwise.
     */
    public boolean send(BodyWS body, WebSocketBodyCallback callback) {
        nonces.put(body.nonce, callback);
        return send(body);
    }

    /**
     * Listens indefinitely to messages with a certain BodyWS type.
     * Recalling the function with with the same bodyType will overwrite the callback to be called
     * when the type matches.
     * @param bodyType
     * @param callback
     */
    public void on(String bodyType, WebSocketBodyCallback callback) {
        types.put(bodyType, callback);
    }

    /**
     * Closes the WebSocket Connection successfully, code 0.
     * This method is a shortcut of this.webSocket.close(0, "");
     * @return
     */
    public boolean close() {
        return this.webSocket.close(0, "");
    }

    /**
     * Handle new BodyWS messages by their type and nonce.
     * Listeners to one of these 2 parameters will be notified if properties match.
     * @param bodyWS
     */
    private void onMessage(BodyWS bodyWS) {
        // Callbacks (or acknowledgements) have to be called only once.
        WebSocketBodyCallback nonceCallback = nonces.get(bodyWS.nonce);
        if (nonceCallback != null) {
            nonceCallback.onMessage(this, bodyWS);
            nonces.remove(nonceCallback);
        }

        // Types can be listened forever.
        WebSocketBodyCallback typesCallback = types.get(bodyWS.type);
        if (typesCallback != null) {
            typesCallback.onMessage(this, bodyWS);
        }
    }

    private class WebSocketCallbackRouter extends WebSocketListener {
        public void onOpen(okhttp3.WebSocket webSocket, Response response) {
            callback.onOpen(WebSocketConnection.this, response);
        }

        public void onMessage(okhttp3.WebSocket webSocket, String text) {
            callback.onMessage(WebSocketConnection.this, text);
            WebSocketConnection.this.onMessage(BodyWS.fromString(text));
        }

        public void onMessage(okhttp3.WebSocket webSocket, ByteString bytes) {
            callback.onMessage(WebSocketConnection.this, bytes.toString());
            WebSocketConnection.this.onMessage(BodyWS.fromString(bytes.toString()));
        }

        public void onClosing(okhttp3.WebSocket webSocket, int code, String reason) {
            callback.onClosing(WebSocketConnection.this, code, reason);
        }

        public void onClosed(okhttp3.WebSocket webSocket, int code, String reason) {
            callback.onClosed(WebSocketConnection.this, code, reason);
        }

        public void onFailure(okhttp3.WebSocket webSocket, Throwable t, @Nullable Response response) {
            callback.onFailure(WebSocketConnection.this, t, response);
        }
    }
}
