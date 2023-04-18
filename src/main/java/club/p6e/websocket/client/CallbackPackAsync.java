package club.p6e.websocket.client;

import io.netty.buffer.ByteBuf;

/**
 * 异步包装
 * @author lidashuang
 * @version 1.0
 */
public class CallbackPackAsync implements P6eWebSocketCallback {

    /** 回调对象 */
    private final P6eWebSocketCallback callback;

    /**
     * 构造方法初始化回调对象
     * @param callback 回调对象
     */
    public CallbackPackAsync(P6eWebSocketCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onOpen(P6eWebSocketClient client) {
        ThreadPool.execute(() -> callback.onOpen(client));
    }

    @Override
    public void onClose(P6eWebSocketClient client) {
        ThreadPool.execute(() -> callback.onClose(client));
    }

    @Override
    public void onError(P6eWebSocketClient client, Throwable throwable) {
        ThreadPool.execute(() -> callback.onError(client, throwable));
    }

    @Override
    public void onMessageText(P6eWebSocketClient client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessageText(client, message));
    }

    @Override
    public void onMessageBinary(P6eWebSocketClient client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessageBinary(client, message));
    }

    @Override
    public void onMessagePong(P6eWebSocketClient client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessagePong(client, message));
    }

    @Override
    public void onMessagePing(P6eWebSocketClient client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessagePing(client, message));
    }

    @Override
    public void onMessageContinuation(P6eWebSocketClient client, ByteBuf message) {
        ThreadPool.execute(() -> callback.onMessageContinuation(client, message));
    }
}
