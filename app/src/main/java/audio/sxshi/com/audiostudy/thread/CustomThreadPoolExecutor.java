package audio.sxshi.com.audiostudy.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by sxshi on 2017-12-28.
 * 自定义线程池
 * 自定义线程核心数
 * 自定义最大线程数
 */

public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
    private static final String TAG = "CusThreadPoolExecutor";
    public CustomThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    public static CustomThreadPoolExecutor newThreadPoolExecutor() {
//        Log.d(TAG, "*************线程池创建了*************");
        /**
         * 获取CPU数量
         */
        int processors = Runtime.getRuntime().availableProcessors();

        /**
         * 核心线程数量
         */
        int corePoolSize = processors + 1;
        /**
         * 最大线程数量
         */
        int maximumPoolSize = processors * 2 + 1;
        /**
         * 空闲有效时间 超过时间就会自动销毁
         */
        long keepAliveTime = 30;
        /**
         * 队列初始化大小
         */
        int capacity=128;
        BlockingQueue<Runnable> workQueue = new LinkedBlockingDeque<>(capacity);
        return new CustomThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    }
}
