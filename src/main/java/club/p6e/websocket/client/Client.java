package club.p6e.websocket.client;


import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;

/**
 * @author lidashuang
 * @version 1.0
 */
public class Client {

    /** 全局 channel 对象 */
    private final Channel channel;

    /**
     * 构造方法初始化
     * @param channel channel 对象
     */
    public Client(Channel channel) {
        this.channel = channel;
    }

    /**
     * 获取 ID
     * @return ID
     */
    public String getId() {
        return channel.id().toString();
    }

    /**
     * 是否为连接状态
     * @return 状态
     */
    public boolean isOpen() {
        return channel.isOpen();
    }

    /**
     * 获取 channel 对象
     * @return channel 对象
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * 关闭 channel
     */
    public void close() {
        channel.close();
    }

    /**
     * 发送字节码消息
     * @param byteBuf ByteBuf 对象
     */
    public void sendMessageBinary(ByteBuf byteBuf) {
        channel.writeAndFlush(new BinaryWebSocketFrame(byteBuf));
    }

    public void sendMessagePong() {
        channel.writeAndFlush(new PongWebSocketFrame());
    }

    public void sendMessagePing() {
        channel.writeAndFlush(new PingWebSocketFrame());
    }

}
