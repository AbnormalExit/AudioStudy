package audio.sxshi.com.audiostudy;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    private byte[] mAudioData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final MyAudioRecord record = new MyAudioRecord();
        final MyAudioPlayer player=new MyAudioPlayer();

        record.setOnAudioFrameRecordListener(new MyAudioRecord.OnAudioFrameRecordListener() {
            @Override
            public void onAudioFrameRecord(byte[] audioData) {
                mAudioData=audioData;
                Log.d(TAG, "onAudioFrameRecord: audioData length:" + audioData.length);
            }
        });
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.startRecord();
            }
        });

        findViewById(R.id.btn_stop).setOnClickListener(new
                                                               View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record.stopRecord();
            }
        });

        findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.initAudioPlayer();//初始化播放器
                player.play(mAudioData,0,mAudioData.length);
            }
        });
        findViewById(R.id.btn_stop_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.stop();
            }
        });
    }

}
