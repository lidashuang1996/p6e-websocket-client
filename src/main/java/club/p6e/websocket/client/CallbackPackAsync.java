package club.p6e.websocket.client;

import io.netty.buffer.ByteBuf;

/**
 * @author lidashuang
 * @version 1.0
 */
public class CallbackPackAsync implements Callback {

    private final Callback callback;

    public CallbackPackAsync(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onOpen(Client client) {
        ThreadPool.execute(() -> callback.onOpen(client));
    }

    @Override
    public void onClose(Client client) {
        ThreadPool.execute(() -> callback.onClose(client));
    }

    @Override
    public void onError(Client client, Throwable throwable) {
        ThreadPool.execute(() -> callback.onError(client, throwable));
    }

    @Override
    public void onMessageText(Client client, String message) {
        ThreadPool.execute(() -> callback.onMessageText(client, message));
    }

    @Override
    public void onMessageBinary(Client client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessageBinary(client, message));
    }

    @Override
    public void onMessagePong(Client client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessagePong(client, message));
    }

    @Override
    public void onMessagePing(Client client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessagePing(client, message));
    }

    @Override
    public void onMessageContinuation(Client client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessageContinuation(client, message));
    }
}
