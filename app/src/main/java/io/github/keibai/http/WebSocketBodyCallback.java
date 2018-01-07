package io.github.keibai.http;

import io.github.keibai.models.meta.BodyWS;

/**
 * Modification of okhttp3.WebSocketListener to handle new messages.
 */
public abstract class WebSocketBodyCallback {

    /**
     * Invoked when a text (type {@code 0x1}) message has been received.
     * Text message will be attempted to be converted onto a BodyWS. If unsuccessful, an empty
     * BodyWS will be passed.
     */
    public void onMessage(WebSocketConnection connection, BodyWS body) {}
}
