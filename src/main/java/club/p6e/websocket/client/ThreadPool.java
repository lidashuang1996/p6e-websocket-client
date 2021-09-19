package club.p6e.websocket.client;

import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 异步处理消息的线程池对象
 * @author lidashuang
 * @version 1.0
 */
public class ThreadPool {

    /** 线程池对象 */
    private static ThreadPoolExecutor EXECUTOR;
    /** 线程池名称 */
    private static final String POOL_NAME = "P6E_WS_CTP";
    /** 注入日志对象 */
    private static final Logger LOGGER = LoggerFactory.getLogger(ThreadPool.class);

    /**
     * 初始化线程池对象
     */
    public static void init() {
        if (EXECUTOR == null) {
            setThreadPoolExecutor(new ThreadPoolExecutor(5, 30, 60L,
                    TimeUnit.SECONDS, new SynchronousQueue<>(), new DefaultThreadFactory(POOL_NAME)));
        } else {
            throw new RuntimeException(ThreadPool.class
                    + " thread pool has been initialized and does not need to be reinitialized.");
        }
    }

    /**
     * 初始化线程池对象
     * @param executor 线程池
     */
    public static void init(ThreadPoolExecutor executor) {
        if (EXECUTOR == null) {
            setThreadPoolExecutor(executor);
        } else {
            throw new RuntimeException(ThreadPool.class
                    + " thread pool has been initialized and does not need to be reinitialized.");
        }
    }

    /**
     * 线程池赋值
     * 加锁是为了避免多线程执行初始化造成覆盖问题
     * @param executor 线程池
     */
    private synchronized static void setThreadPoolExecutor(ThreadPoolExecutor executor) {
        if (EXECUTOR == null) {
            EXECUTOR = executor;
            LOGGER.info("[ P6eWebSocketClient ] ==> thread pool initialization succeeded.");
        } else {
            throw new RuntimeException(ThreadPool.class
                    + " thread pool has been initialized and does not need to be reinitialized.");
        }
    }

    /**
     * 将任务交给线程池执行
     * @param runnable 任务对象
     */
    public static void execute(Runnable runnable) {
        if (EXECUTOR != null && !EXECUTOR.isShutdown()) {
            EXECUTOR.execute(runnable);
        } else {
            throw new RuntimeException(ThreadPool.class + " thread pool is not initialized or closed.");
        }
    }

    /**
     * 关闭线程池
     */
    public static void shutdown() {
        if (EXECUTOR != null) {
            EXECUTOR.shutdown();
            EXECUTOR = null;
        }
        LOGGER.info("[ P6eWebSocketClient ] ==> thread pool closed.");
    }
}
