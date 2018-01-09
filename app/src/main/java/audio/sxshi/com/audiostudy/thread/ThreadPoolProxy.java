package audio.sxshi.com.audiostudy.thread;

import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by sxshi on 2017-12-29.
 * 线程池代理类
 */
public class ThreadPoolProxy {
    private static final String TAG = "ThreadPoolProxy";
    ThreadPoolExecutor mExecutor;

    public ThreadPoolProxy(ThreadPoolExecutor threadPoolExecutor) {
        super();
        this.mExecutor = threadPoolExecutor;
    }

    /**
     * 执行任务
     *
     * @param task
     */
    public void execute(Runnable task) {
        mExecutor.execute(task);
    }

    /**
     * 提交任务
     *
     * @param task
     */
    public Future<?> submit(Runnable task) {
        return mExecutor.submit(task);
    }

    /**
     * 移除任务
     *
     * @param task
     */
    public void removeTask(Runnable task) {
        mExecutor.remove(task);
    }

}
