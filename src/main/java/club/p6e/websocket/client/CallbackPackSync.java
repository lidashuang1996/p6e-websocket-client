package club.p6e.websocket.client;

import io.netty.buffer.ByteBuf;

/**
 * @author lidashuang
 * @version 1.0
 */
public class CallbackPackSync implements Callback {

    private final Callback callback;

    public CallbackPackSync(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onOpen(Client client) {
        callback.onOpen(client);
    }

    @Override
    public void onClose(Client client) {
        callback.onClose(client);
    }

    @Override
    public void onError(Client client, Throwable throwable) {
        callback.onError(client, throwable);
    }

    @Override
    public void onMessageText(Client client, String message) {
        callback.onMessageText(client, message);
    }

    @Override
    public void onMessageBinary(Client client, ByteBuf message) {
        callback.onMessageBinary(client, message);
    }

    @Override
    public void onMessagePong(Client client, ByteBuf message) {
        callback.onMessagePong(client, message);
    }

    @Override
    public void onMessagePing(Client client, ByteBuf message) {
        callback.onMessagePing(client, message);
    }

    @Override
    public void onMessageContinuation(Client client, ByteBuf message) {
        callback.onMessageContinuation(client, message);
    }
}
