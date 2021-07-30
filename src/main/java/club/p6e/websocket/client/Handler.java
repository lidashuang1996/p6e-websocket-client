package club.p6e.websocket.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author lidashuang
 * @version 1.0
 */
public class Handler implements ChannelInboundHandler {

    /** 注入日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(Handler.class);

    /** 客户端对象 */
    private Client client;
    /** 回调函数 */
    private final Callback callback;
    /** Web Socket Client Handshake 对象 */
    private final WebSocketClientHandshaker webSocketClientHandshaker;

    /**
     * 构造方法初始化
     * @param config 配置文件对象
     * @param callback 回调函数
     */
    public Handler(Config config, Callback callback) {
        this.callback = callback;
        this.webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(
                config.uri(),
                config.version(),
                null,
                false,
                config.httpHeaders()
        );
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ channelRegistered ] ==> " + ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ channelUnregistered ] ==> " + ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ channelActive ] ==> " + ctx);
        webSocketClientHandshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ channelInactive ] ==> " + ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.debug("( " + client.getId() + " ) [ channelRead ] ==> " + ctx);
        if (msg instanceof FullHttpResponse) {
            // 判断是否为 HTTP 返回
            if (webSocketClientHandshaker.isHandshakeComplete()) {
                final FullHttpResponse response = (FullHttpResponse) msg;
                final String error = "unexpected http response [ "
                        + response.status() + " ] ==> "
                        + response.content().toString(CharsetUtil.UTF_8);
                LOGGER.error(error);
                exceptionCaught(ctx, new IOException(error));
            } else {
                // 结束握手
                webSocketClientHandshaker.finishHandshake(ctx.channel(), (FullHttpResponse) msg);
                client = new Client(ctx.channel());
                callback.onOpen(client);
            }
        } else if (msg instanceof WebSocketFrame){
            if (client == null) {
                final String error = "connection exception, client is null.";
                LOGGER.error(error);
                exceptionCaught(ctx, new IOException(error));
            } else {
                final WebSocketFrame frame = (WebSocketFrame) msg;
                final ByteBuf byteBuf = frame.content();
                if (frame instanceof BinaryWebSocketFrame) {
                    callback.onMessageBinary(client, byteBuf);
                } else if (frame instanceof TextWebSocketFrame) {
                    callback.onMessageText(client, ((TextWebSocketFrame) frame).text());
                } else if (frame instanceof PongWebSocketFrame) {
                    callback.onMessagePong(client, byteBuf);
                } else if (frame instanceof PingWebSocketFrame) {
                    callback.onMessagePing(client, byteBuf);
                } else if (frame instanceof ContinuationWebSocketFrame) {
                    callback.onMessageContinuation(client, byteBuf);
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ channelReadComplete ] ==> " + ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        LOGGER.debug("( " + client.getId() + " ) [ userEventTriggered ] ==> " + ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ channelWritabilityChanged ] ==> " + ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ handlerAdded ] ==> " + ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + client.getId() + " ) [ handlerRemoved ] ==> " + ctx);
        callback.onClose(client);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.debug("( " + client.getId() + " ) [ exceptionCaught ] ==> " + ctx);
        cause.printStackTrace();
        if (client.isOpen()) {
            callback.onError(client, cause);
        }
        ctx.close(); // 关闭当前连接
    }
}
