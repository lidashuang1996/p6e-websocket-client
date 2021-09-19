package club.p6e.websocket.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.EventLoopGroup;

import java.util.Iterator;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 程序入口
 * P6e Web Socket Client Application
 * @author lidashuang
 * @version 1.0
 */
public final class P6eWebSocketClientApplication {

    /**
     * 初始化线程池
     */
    public static void initThreadPool() {
        ThreadPool.init();
    }

    /**
     * 初始化线程池
     * @param executor 线程池
     */
    public static void initThreadPool(ThreadPoolExecutor executor) {
        ThreadPool.init(executor);
    }

    /**
     * 关闭线程池
     */
    public static void shutdownThreadPool() {
        ThreadPool.shutdown();
    }

    /**
     * 创建连接器
     */
    public static Connector connector() {
        return new Connector();
    }

    /**
     * 创建连接器
     * @param bootstrap Netty 的 Bootstrap
     */
    public static Connector connector(Bootstrap bootstrap) {
        return new Connector(bootstrap);
    }

    /**
     * 创建连接器
     * @param eventLoopGroup Netty 的 EventLoopGroup
     */
    public static Connector connector(EventLoopGroup eventLoopGroup) {
        return new Connector(eventLoopGroup);
    }

    /**
     * 创建连接器
     * @param bootstrap Netty 的 Bootstrap
     * @param eventLoopGroup Netty 的 EventLoopGroup
     */
    public static Connector connector(Bootstrap bootstrap, EventLoopGroup eventLoopGroup) {
        return new Connector(bootstrap, eventLoopGroup);
    }

    /**
     * 创建连接器
     * @param bootstrap Netty 的 Bootstrap
     * @param eventLoopGroup Netty 的 EventLoopGroup
     * @param channelClass channel 的类型
     */
    public static Connector connector(Bootstrap bootstrap,
                                      EventLoopGroup eventLoopGroup, Class<? extends Channel> channelClass) {
        return new Connector(bootstrap, eventLoopGroup, channelClass);
    }

    /**
     * 关闭所有服务
     */
    public synchronized static void shutdown() {
        // 关闭任务线程池
        shutdownThreadPool();
        // 关闭所有连接器
        final Iterator<Connector> iterator = Connector.getConnectors().iterator();
        while (iterator.hasNext()) {
            final Connector connector = iterator.next();
            connector.shutdown(false);
            iterator.remove();
        }
    }

}
