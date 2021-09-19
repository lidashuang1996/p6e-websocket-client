package club.p6e.websocket.client;

import io.netty.buffer.ByteBuf;

/**
 * 回调函数
 * @author lidashuang
 * @version 1.0
 */
public interface Callback {

    /**
     * 连接成功的触发的事件
     * @param client WebSocketClient 对象
     */
    public void onOpen(Client client);

    /**
     * 连接关闭的触发的事件
     * @param client WebSocketClient 对象
     */
    public void onClose(Client client);

    /**
     * 连接错误的触发的事件
     * @param client WebSocketClient 对象
     * @param throwable 错误事件对象
     */
    public void onError(Client client, Throwable throwable);

    /**
     * 触发文本消息的事件
     * @param client WebSocketClient 对象
     * @param message 消息内容
     */
    public void onMessageText(Client client, String message);

    /**
     * 触发二进制消息的事件
     * @param client WebSocketClient 对象
     * @param message 消息内容
     */
    public void onMessageBinary(Client client, ByteBuf message);

    /**
     * 触发 Pong 消息的事件
     * @param client WebSocketClient 对象
     * @param message 消息内容
     */
    public void onMessagePong(Client client, ByteBuf message);

    /**
     * 触发 Ping 消息的事件
     * @param client WebSocketClient 对象
     * @param message 消息内容
     */
    public void onMessagePing(Client client, ByteBuf message);

    /**
     * 触发 Continuation 消息的事件
     * @param client WebSocketClient 对象
     * @param message 消息内容
     */
    public void onMessageContinuation(Client client, ByteBuf message);

}
