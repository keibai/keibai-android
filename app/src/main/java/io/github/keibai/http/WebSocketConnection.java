package io.github.keibai.http;

import io.github.keibai.models.meta.BodyWS;

public class WebSocketConnection {

    public final okhttp3.WebSocket webSocket;

    WebSocketConnection(okhttp3.WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public boolean send(BodyWS body) {
        return this.webSocket.send(body.toString());
    }

    public boolean close() {
        return this.webSocket.close(0, "");
    }
}
