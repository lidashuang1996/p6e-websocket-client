package club.p6e.websocket.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 处理器
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
     * @param config 配置文件
     * @param callback 回调函数
     */
    public Handler(Config config, Callback callback) {
        LOGGER.debug("P6eWebSocketClient handshake request http uri ==> " + config.uri());
        LOGGER.debug("P6eWebSocketClient handshake request http version ==> " + config.version());
        LOGGER.debug("P6eWebSocketClient handshake request http headers ==> \n\n" + config.httpHeaders() + "\n");
        this.callback = callback;
        this.webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(
                config.uri(),
                config.version(),
                null,
                false,
                config.httpHeaders()
        );
    }

    /**
     * 获取客户端对象
     * @param ctx ChannelHandlerContext 对象
     * @return WebSocketClient 对象
     */
    private Client getClient(ChannelHandlerContext ctx) {
        if (client == null) {
            createClient(ctx);
        }
        return client;
    }

    /**
     * 创建客户端对象
     * @param ctx ChannelHandlerContext 对象
     */
    private synchronized void createClient(ChannelHandlerContext ctx) {
        if (client == null) {
            client = new Client(ctx.channel());
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ channelRegistered ] ==> " + ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ channelUnregistered ] ==> " + ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ channelActive ] ==> " + ctx);
        // 初始化 WebSocketClientHandshake
        webSocketClientHandshaker.handshake(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ channelInactive ] ==> " + ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ channelRead ] ==> " + ctx);
        // 判断是否为 HttpResponse 对象
        if (msg instanceof HttpResponse) {
            // 如果为 HttpResponse 对象需要转换为 FullHttpResponse 对象进行处理
            final HttpResponse httpResponse = (HttpResponse) msg;
            msg = new DefaultFullHttpResponse(httpResponse.protocolVersion(), httpResponse.status(),
                    Unpooled.buffer(0), httpResponse.headers(), new DefaultHttpHeaders());
        }
        // 判断是否为 FullHttpResponse 对象
        if (msg instanceof FullHttpResponse) {
            final FullHttpResponse fullHttpResponse = (FullHttpResponse) msg;
            LOGGER.debug("P6eWebSocketClient handshake response HTTP ==> \n\n" + fullHttpResponse + "\n");
            try {
                // 判断是是否握手成功
                if (webSocketClientHandshaker.isHandshakeComplete()) {
                    final String error = "P6eWebSocketClient unexpected http response [ "
                            + fullHttpResponse.status() + " ] ==> "
                            + fullHttpResponse.content().toString(CharsetUtil.UTF_8);
                    LOGGER.error(error);
                    // 调用关闭
                    this.exceptionCaught(ctx, new IOException(error));
                } else {
                    // 握手成功，结束握手
                    webSocketClientHandshaker.finishHandshake(ctx.channel(), fullHttpResponse);
                    client = new Client(ctx.channel());
                    callback.onOpen(client);
                }
            } finally {
                // 释放缓存
                fullHttpResponse.release();
            }
        } else if (msg instanceof WebSocketFrame){
            // 判断是否为 WebSocketFrame 对象
            final WebSocketFrame frame = (WebSocketFrame) msg;
            if (client == null) {
                final String error = "P6eWebSocketClient connection exception, client is null.";
                LOGGER.error(error);
                exceptionCaught(ctx, new IOException(error));
            } else {
                final ByteBuf byteBuf = frame.content();
                if (frame instanceof BinaryWebSocketFrame) {
                    // byteBuf 资源需要在使用完成后手动回收
                    callback.onMessageBinary(client, byteBuf);
                } else if (frame instanceof TextWebSocketFrame) {
                    try {
                        callback.onMessageText(client, ((TextWebSocketFrame) frame).text());
                    } finally {
                        // 文本就回收掉
                        frame.release();
                    }
                } else if (frame instanceof PongWebSocketFrame) {
                    // byteBuf 资源需要在使用完成后手动回收
                    callback.onMessagePong(client, byteBuf);
                } else if (frame instanceof PingWebSocketFrame) {
                    // byteBuf 资源需要在使用完成后手动回收
                    callback.onMessagePing(client, byteBuf);
                } else if (frame instanceof ContinuationWebSocketFrame) {
                    // byteBuf 资源需要在使用完成后手动回收
                    callback.onMessageContinuation(client, byteBuf);
                }
            }
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ channelReadComplete ] ==> " + ctx);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ userEventTriggered ] ==> " + ctx);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ channelWritabilityChanged ] ==> " + ctx);
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ handlerAdded ] ==> " + ctx);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ handlerRemoved ] ==> " + ctx);
        callback.onClose(client);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LOGGER.debug("( " + getClient(ctx).getId() + " ) [ exceptionCaught ] ==> " + ctx);
        if (client.isOpen()) {
            callback.onError(client, cause);
        }
        ctx.close(); // 关闭当前连接
    }
}
