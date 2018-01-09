package audio.sxshi.com.audiostudy;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import audio.sxshi.com.audiostudy.thread.ThreadPoolFactory;

/**
 * Created by sxshi on 2018-1-3.
 * 使用系统API实现一个简单的录音机
 */

public class MyAudioRecord {
    private static final String TAG = "MyAudioRecord";
    private static final int DEFAULT_SOURCE = MediaRecorder.AudioSource.MIC;//音频来源  麦克风
    //采样率，注意，44100Hz是唯一可以保证兼容所有Android手机的采样率。
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;//单通道
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;//数据位宽

    private AudioRecord mAudioRecord;
    private Thread mRecordThread;
    private boolean mIsRecordStarted = false;
    private volatile boolean mIsLoopExit = false;

    private OnAudioFrameRecordListener onAudioFrameRecordListener;

    private RecordRunnable recordRunnable;

    public void setmIsRecordStarted(boolean mIsRecordStarted) {
        this.mIsRecordStarted = mIsRecordStarted;
    }

    public void setOnAudioFrameRecordListener(OnAudioFrameRecordListener onAudioFrameRecordListener) {
        this.onAudioFrameRecordListener = onAudioFrameRecordListener;
    }


    /**
     * 录制音频回调
     */
    public interface OnAudioFrameRecordListener {
        public void onAudioFrameRecord(byte[] audioData);
    }

    /**
     * int size = 采样率 x 位宽 x 采样时间 x 通道数
     * 采样时间一般取 2.5ms~120ms 之间，由厂商或者具体的应用决定，我们其实可以推断，每一帧的采样时间取得越短，
     * 产生的延时就应该会越小，当然，碎片化的数据也就会越多。
     * 不同的厂商的底层实现是不一样的，就是根据上面的计算公式得到一帧的大小，音频缓冲区的大小则必须是一帧大小的2～N倍
     */
    private int bufferSizeInBytes = 0;

    public boolean startRecord() {
        return startRecord(DEFAULT_SOURCE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    /**
     * 开始录音
     *
     * @param defaultSource
     * @param defaultSampleRate
     * @param defaultChannelConfig
     * @param defaultAudioFormat
     * @return
     */
    public boolean startRecord(int defaultSource, int defaultSampleRate, int defaultChannelConfig, int defaultAudioFormat) {
        if (mIsRecordStarted)
            return false;
        bufferSizeInBytes = AudioRecord.getMinBufferSize(defaultSampleRate, defaultChannelConfig, defaultAudioFormat);
        if (bufferSizeInBytes == AudioRecord.ERROR_BAD_VALUE) {
            Log.e(TAG, "startRecord: get bufferSizeInBytes error");
            return false;
        }
        mAudioRecord = new AudioRecord(defaultSource, defaultSampleRate, defaultChannelConfig, defaultAudioFormat, bufferSizeInBytes);

        if (mAudioRecord.getRecordingState() == AudioRecord.STATE_UNINITIALIZED) {
            Log.e(TAG, "startRecord: AudioRecord init fail");
            return false;
        }
        mAudioRecord.startRecording();
        mIsRecordStarted = true;
        mIsLoopExit = false;

        recordRunnable = new RecordRunnable();
        //开启异步线程录制
        ThreadPoolFactory.getRecordPool().execute(recordRunnable);

        Log.d(TAG, "startRecord: start record");
        return true;
    }

    /**
     * 停止录制
     */
    public void stopRecord() {
        if (!mIsRecordStarted) {
            return;
        }
        mIsLoopExit = true;
        ThreadPoolFactory.getRecordPool().removeTask(recordRunnable);
        if (mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
            mAudioRecord.stop();
        }
        mAudioRecord.release();

        mIsRecordStarted = false;
        onAudioFrameRecordListener = null;
        Log.d(TAG, "stopRecord: stop record success!");
    }

    /**
     * 录制线程
     */
    private class RecordRunnable implements Runnable {
        @Override
        public void run() {
            while (!mIsLoopExit) {
                byte[] buffer = new byte[bufferSizeInBytes];
                int read = mAudioRecord.read(buffer, 0, bufferSizeInBytes);
                if (read == AudioRecord.ERROR_INVALID_OPERATION) {
                    Log.e(TAG, "run: ERROR_INVALID_OPERATION");
                } else if (read == AudioRecord.ERROR_BAD_VALUE) {
                    Log.e(TAG, "run: ERROR_BAD_VALUE");
                } else {
                    if (onAudioFrameRecordListener != null) {
                        onAudioFrameRecordListener.onAudioFrameRecord(buffer);
                    }
                    Log.d(TAG, "run: record success " + read + "bytes");
                }

            }
        }
    }

}
