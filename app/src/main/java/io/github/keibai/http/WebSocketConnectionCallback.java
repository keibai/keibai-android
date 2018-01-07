package io.github.keibai.http;

import android.support.annotation.Nullable;

import io.github.keibai.models.meta.BodyWS;
import okhttp3.Response;

/**
 * Modification of okhttp3.WebSocketListener to handle generic states.
 */
public abstract class WebSocketConnectionCallback {

    /**
     * Invoked when a web socket has been accepted by the remote peer and may begin transmitting
     * messages.
     */
    public void onOpen(WebSocketConnection connection, Response response) {}

    /**
     * Invoked when a text (type {@code 0x1}) message has been received.
     * Receives all messages, regardless of the content, and WebSocketBodyCallback you may have.
     */
    public void onMessage(WebSocketConnection connection, String text) {}

    /**
     * Invoked when a text (type {@code 0x1}) message has been received.
     * Receives all messages, regardless of the content, and WebSocketBodyCallback you may have.
     * Text message will be attempted to be converted onto a BodyWS. If unsuccessful, an empty
     * BodyWS will be passed.
     */
    public void onMessage(WebSocketConnection connection, BodyWS body) {}

    /**
     * Invoked when a text (type {@code 0x1}) message has been received.
     */
    public void onClosing(WebSocketConnection connection, int code, String reason) {}

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    public void onClosed(WebSocketConnection connection, int code, String reason) {}

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing and incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    public void onFailure(WebSocketConnection connection, Throwable t, @Nullable Response response) {}
}
