package club.p6e.websocket.client;

import io.netty.buffer.ByteBuf;

/**
 * 同步包装
 * @author lidashuang
 * @version 1.0
 */
public class CallbackPackSync implements P6eWebSocketCallback {

    /** 回调对象 */
    private final P6eWebSocketCallback callback;

    /**
     * 构造方法初始化回调对象
     * @param callback 回调对象
     */
    public CallbackPackSync(P6eWebSocketCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onOpen(P6eWebSocketClient client) {
        callback.onOpen(client);
    }

    @Override
    public void onClose(P6eWebSocketClient client) {
        callback.onClose(client);
    }

    @Override
    public void onError(P6eWebSocketClient client, Throwable throwable) {
        callback.onError(client, throwable);
    }

    @Override
    public void onMessageText(P6eWebSocketClient client, ByteBuf message) {
        callback.onMessageText(client, message);
    }

    @Override
    public void onMessageBinary(P6eWebSocketClient client, ByteBuf message) {
        callback.onMessageBinary(client, message);
    }

    @Override
    public void onMessagePong(P6eWebSocketClient client, ByteBuf message) {
        callback.onMessagePong(client, message);
    }

    @Override
    public void onMessagePing(P6eWebSocketClient client, ByteBuf message) {
        callback.onMessagePing(client, message);
    }

    @Override
    public void onMessageContinuation(P6eWebSocketClient client, ByteBuf message) {
        callback.onMessageContinuation(client, message);
    }
}
