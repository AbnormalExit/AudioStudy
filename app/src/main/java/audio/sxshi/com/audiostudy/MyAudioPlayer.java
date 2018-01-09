package audio.sxshi.com.audiostudy;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodec;
import android.util.Log;

/**
 * Created by sxshi on 2018-1-3.
 * 使用系统API实现一个简单的录音播放器
 */

public class MyAudioPlayer {
    private static final String TAG = "MyAudioPlayer";
    /**
     * 这个参数代表着当前应用使用的哪一种音频管理策略，
     * 当系统有多个进程需要播放音频时，这个管理策略会决定最终的展现效果,
     * 该参数的可选的值以常量的形式定义在 AudioManager 类中，主要包括：
     * STREAM_VOCIE_CALL：电话声音
     * STREAM_SYSTEM：系统声音
     * STREAM_RING：铃声
     * STREAM_MUSCI：音乐声
     * STREAM_ALARM：警告声
     * STREAM_NOTIFICATION：通知声
     */
    private static final int DEFAULT_STREAM_TYPE = AudioManager.STREAM_MUSIC;
    /**
     * 采样率，注意，44100Hz是唯一可以保证兼容所有Android手机的采样率。
     */
    private static final int DEFAULT_SAMPLE_RATE = 44100;
    /**
     * 单通道
     */
    private static final int DEFAULT_CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_STEREO;
    /**
     * 数据位宽
     */
    private static final int DEFAULT_AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    /**
     * AudioTrack 提供了两种播放模式，一种是 static 方式，一种是 streaming 方式，
     * 前者需要一次性将所有的数据都写入播放缓冲区，简单高效，通常用于播放铃声、系统提醒的音频片段;
     * 后者则是按照一定的时间间隔不间断地写入音频数据，理论上它可用于任何音频播放的场景。
     */
    private static final int DEFAULT_PLAY_MODE = AudioTrack.MODE_STREAM;

    private boolean mIsPlayPre = false;

    private AudioTrack mAudioTrack;
    /**
     * int size = 采样率 x 位宽 x 采样时间 x 通道数
     * 采样时间一般取 2.5ms~120ms 之间，由厂商或者具体的应用决定，我们其实可以推断，每一帧的采样时间取得越短，
     * 产生的延时就应该会越小，当然，碎片化的数据也就会越多。
     * 不同的厂商的底层实现是不一样的，就是根据上面的计算公式得到一帧的大小，音频缓冲区的大小则必须是一帧大小的2～N倍
     */
    private int minBufferSize = 0;

    public boolean initAudioPlayer() {
        return initAudioPlayer(DEFAULT_STREAM_TYPE, DEFAULT_SAMPLE_RATE, DEFAULT_CHANNEL_CONFIG, DEFAULT_AUDIO_FORMAT);
    }

    /**
     * 初始化播放器
     *
     * @param defaultStreamType
     * @param defaultSampleRate
     * @param defaultChannelConfig
     * @param defaultAudioFormat
     * @return
     */
    public boolean initAudioPlayer(int defaultStreamType, int defaultSampleRate, int defaultChannelConfig, int defaultAudioFormat) {
        if (mIsPlayPre) {
            Log.e(TAG, "playAudio: isPlaying");
            return false;
        }

        minBufferSize = AudioTrack.getMinBufferSize(defaultSampleRate, defaultChannelConfig, defaultAudioFormat);
        if (minBufferSize == AudioTrack.ERROR_BAD_VALUE) {
            Log.e(TAG, "playAudio: getMinBufferSize error");
            return false;
        }
        Log.d(TAG , "getMinBufferSize = "+minBufferSize+" bytes !");
        mAudioTrack = new AudioTrack(defaultStreamType, defaultSampleRate, defaultChannelConfig, defaultAudioFormat, minBufferSize, DEFAULT_PLAY_MODE);
        if (mAudioTrack.getPlayState() == AudioTrack.STATE_UNINITIALIZED) {
            Log.e(TAG, "playAudio: STATE_UNINITIALIZED");
            return false;
        }
        mIsPlayPre = true;

        return true;
    }

    /**
     * 开始播放音乐
     * @param audioData
     * @param offsetBytes
     * @param sizeInBytes
     * @return
     */
    public boolean play(byte[] audioData, int offsetBytes, int sizeInBytes) {
        if (!mIsPlayPre) {
            Log.e(TAG, "play: please init player");
            return false;
        }

        if (mAudioTrack.write(audioData, offsetBytes, sizeInBytes) != sizeInBytes) {
            Log.e(TAG, "play: Could not write all the samples to audio devices");
        }
        mAudioTrack.play();
        Log.d(TAG, "play: start success");
        return true;
    }

    /**
     * 停止播放
     */
    public void stop(){
        if (!mIsPlayPre){
            return;
        }
        if (mAudioTrack.getPlayState()==AudioTrack.PLAYSTATE_PLAYING){
            mAudioTrack.stop();
        }
        mAudioTrack.release();
        mIsPlayPre=false;
        Log.d(TAG, "stop: stop success");
    }
}
