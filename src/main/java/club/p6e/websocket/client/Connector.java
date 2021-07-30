package club.p6e.websocket.client;


import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author lidashuang
 * @version 1.0
 */
public class Connector {

    /** 缓存所有的连接器 */
    private static final List<Connector> CACHE = new CopyOnWriteArrayList<>();

    /** 日志注入对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(Connector.class);

    /** Netty 的 Bootstrap */
    private final Bootstrap bootstrap;

    /** Netty 的 EventLoopGroup */
    private final EventLoopGroup eventLoopGroup;

    /**
     * 获取所有的连接器
     * @return 连接器集合
     */
    public static List<Connector> getConnectors() {
        return CACHE;
    }

    /**
     * 构造方法初始化
     */
    public Connector() {
        this(new Bootstrap(), new NioEventLoopGroup(), NioSocketChannel.class);
    }

    /**
     * 构造方法初始化
     * @param bootstrap Netty 的 Bootstrap
     */
    public Connector(Bootstrap bootstrap) {
        this(bootstrap, new NioEventLoopGroup(), NioSocketChannel.class);
    }

    /**
     * 构造方法初始化
     * @param eventLoopGroup Netty 的 EventLoopGroup
     */
    public Connector(EventLoopGroup eventLoopGroup) {
        this(new Bootstrap(), eventLoopGroup, NioSocketChannel.class);
    }

    /**
     * 构造方法初始化
     * @param bootstrap Netty 的 Bootstrap
     * @param eventLoopGroup Netty 的 EventLoopGroup
     */
    public Connector(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
        this(bootstrap, eventLoopGroup, NioSocketChannel.class);
    }

    /**
     * 构造方法初始化
     * @param bootstrap Netty 的 Bootstrap
     * @param eventLoopGroup Netty 的 EventLoopGroup
     * @param channelClass channel 的类型
     */
    public Connector(Bootstrap bootstrap, EventLoopGroup eventLoopGroup, Class<? extends Channel> channelClass) {
        this.bootstrap = bootstrap;
        this.eventLoopGroup = eventLoopGroup;
        this.bootstrap
                .option(ChannelOption.TCP_NODELAY,true)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.AUTO_READ, true);
        this.bootstrap.group(eventLoopGroup);
        this.bootstrap.channel(channelClass);

        CACHE.add(this);

        LOGGER.info("[ p6e web socket ] ==> connector created successfully.");
    }

    /**
     * 设置 Netty 的 Bootstrap 的 Option 属性
     * @param option Bootstrap 的 Option 属性
     * @param value Option 属性值
     * @param <T> Option 属性值泛型
     */
    public <T> void setBootstrapOption(ChannelOption<T> option, T value) {
        this.bootstrap.option(option, value);
    }

    /**
     * 根据配置文件连接
     * @param config 配置文件对象
     */
    public void connect(Config config, Callback callback, boolean isAsync) {
        try {
            this.bootstrap.handler(new ChannelInitializer<>() {
                @Override
                protected void initChannel(Channel channel) {
                    channel.pipeline().addLast(new HttpClientCodec());
                    channel.pipeline().addLast(new Handler(
                            config, isAsync ? new CallbackPackAsync(callback) : new CallbackPackSync(callback)));
                }
            });
            this.bootstrap.connect(config.getHost(), config.getPort()).sync();
            LOGGER.info("[ p6e web socket ] ==> connector connect " +
                    "( host: " + config.getHost() + " , port: " + config.getPort() + " ) successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭连接器的连接
     */
    public void shutdown() {
        this.eventLoopGroup.shutdownGracefully();
        LOGGER.info("[ p6e web socket ] ==> connector closed.");
    }
}
