package audio.sxshi.com.audiostudy.thread;


/**
 * @author sxshi
 */
public class ThreadPoolFactory {
    private static final String TAG = "ThreadPoolFactory";
    static ThreadPoolProxy mRecordlPool;
    static ThreadPoolProxy mPlayPool;

    /**
     * 得到一个录制线程队列
     *
     * @return
     */
    public static ThreadPoolProxy getRecordPool() {
        if (mRecordlPool == null) {
            synchronized (ThreadPoolFactory.class) {
                if (mRecordlPool == null) {
                    mRecordlPool = new ThreadPoolProxy(CustomThreadPoolExecutor.newThreadPoolExecutor());
                }
            }
        }
        return mRecordlPool;
    }

    /**
     * 得到一个播放线程池
     * @return
     */
    public static ThreadPoolProxy getPlayPool() {
        if (mPlayPool == null) {
            synchronized (ThreadPoolFactory.class) {
                if (mPlayPool == null) {
                    mPlayPool = new ThreadPoolProxy(CustomThreadPoolExecutor.newThreadPoolExecutor());
                }
            }
        }
        return mPlayPool;
    }
}
