package space.weme.remix.ui.user;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.AudioVisualizerView;
import space.weme.remix.widgt.LoadingPrompt;
import space.weme.remix.widgt.TimerView;

/**
 * Created by Liujilong on 2016/6/23.
 * liujilong.me@gmail.com
 */
public class AtyAudioRecord extends BaseActivity {
    public static final String TAG = "AtyAudioRecord";

    private static final int STATE_INIT = 0x001;
    private static final int STATE_RECORDING = 0x002;
    private static final int STATE_FINISH = 0x004;
    private static final int STATE_PLAY = 0x008;


    public static final int REPEAT_INTERVAL = 200;

    private int mRecordState = STATE_INIT;

    private LinearLayout mLeftLayout, mRightLayout;
    private ImageView mLeftButton, mRightButton;
    private TextView mLeftTextView, mRightTextView;
    private AudioVisualizerView mVisualizerView;

    private TextView tvUploadRecord;

    private MediaRecorder mRecorder;
    private MediaPlayer mPlayer;
    private Visualizer mVisualizer;

    private boolean canUpload = false;

    private TimerView mTimerView;

    private String mFileName;

    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_audio_record);
        mHandler = new Handler(getMainLooper());
        mLeftButton = (ImageView) findViewById(R.id.left_button);
        mLeftLayout = (LinearLayout) findViewById(R.id.left_layout);
        mLeftTextView = (TextView) findViewById(R.id.left_text);
        mRightButton = (ImageView) findViewById(R.id.right_button);
        mRightLayout = (LinearLayout) findViewById(R.id.right_layout);
        mRightTextView = (TextView) findViewById(R.id.right_text);

        mVisualizerView = (AudioVisualizerView) findViewById(R.id.visualizer_view);

        mTimerView = (TimerView) findViewById(R.id.aty_audio_timer_view);

        tvUploadRecord = (TextView) findViewById(R.id.upload_audio_record);

        tvUploadRecord.setOnClickListener(uploadListener);

        mLeftButton.setOnClickListener(leftClickListener);
        mRightButton.setOnClickListener(rightClickListener);

        mFileName = Environment.getExternalStorageDirectory()
                + File.separator + "record.m4a";

        //mFileName = g+ File.separator+"record.m4a";
    }

    View.OnClickListener leftClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (mRecordState){
                case STATE_INIT:
                    startAudioRecord();
                    break;
                case STATE_RECORDING:
                    stopAudioRecord();
                    break;
                case STATE_PLAY:
                    Toast.makeText(AtyAudioRecord.this, getResources().getString(R.string.audio_palying), Toast.LENGTH_SHORT).show();
                    break;
                case STATE_FINISH:
                    layoutAnimateReversely();
                    startAudioRecord();
            }
        }
    };
    View.OnClickListener rightClickListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            switch (mRecordState){
                case STATE_FINISH:
                    playRecord();
                    break;
                case STATE_PLAY:
                    stopPlayRecord();
            }
        }
    };

    private void playRecord(){
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mRecordState = STATE_FINISH;
                    mHandler.postDelayed(delayedUpload,2000);
                    mRightTextView.setText(R.string.audio_play_record);
                    mRightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_audio_play));
                    mVisualizer.release();
                    mVisualizerView.clear();
                    mTimerView.stop();
                }
            });
            mPlayer.start();
            mVisualizer = new Visualizer(mPlayer.getAudioSessionId());
            mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener()
            {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] audioData,
                                                  int samplingRate)
                {
                    //LogUtils.d(TAG, "onWaveFormDataCapture" + Arrays.toString(audioData));
                    double amplitude = 0;
                    for (int i = 0; i < audioData.length/2; i++) {
                        double y = (audioData[i*2] | audioData[i*2+1] << 8) / 32768.0;
                        // depending on your endianness:
                        // double y = (audioData[i*2]<<8 | audioData[i*2+1]) / 32768.0
                        amplitude += Math.abs(y);
                    }
                    LogUtils.d(TAG,"before plus amplitude: " + amplitude);
                    amplitude = amplitude / audioData.length / 2;
                    LogUtils.d(TAG,"after plus amplitude: " + amplitude);
                    mVisualizerView.addAmplitude((float) amplitude);
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                             int samplingRate)
                {
                    //LogUtils.d(TAG, "onFftDataCapture" + Arrays.toString(bytes));
                }
            };
            mVisualizer.setDataCaptureListener(captureListener,
                    Visualizer.getMaxCaptureRate() / 2, true, false);
            mVisualizer.setEnabled(true);
            //mHandler.post(updatePlayerVisualizer);
        } catch (IOException e) {
            LogUtils.e(TAG, "prepare() failed");
        }
        mRecordState = STATE_PLAY;
        mTimerView.start();
        mRightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_audio_stop));
        mRightTextView.setText(R.string.audio_stop_playing);
    }

    private void stopPlayRecord(){
        mRecordState = STATE_FINISH;
        mHandler.postDelayed(delayedUpload,2000);
        mPlayer.release();
        mPlayer = null;
        mRightTextView.setText(R.string.audio_play_record);
        mRightButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_audio_play));
        mVisualizer.release();
        mVisualizerView.clear();
        mTimerView.stop();
    }


    private void startAudioRecord(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);


        try {
            mRecorder.prepare();
        } catch (IOException e) {
            LogUtils.e(TAG, "prepare() failed");
        }

        mRecorder.start();
        mRecordState = STATE_RECORDING;
        mHandler.post(updateRecorderVisualizer);
        mLeftButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_audio_stop));
        mLeftTextView.setText(R.string.recording);
        mTimerView.start();
    }

    private void stopAudioRecord(){
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        mRecordState = STATE_FINISH;
        mVisualizerView.clear();
        mTimerView.stop();
        layoutAnimate();
    }

    private void layoutAnimate(){
        int width = DimensionUtils.getDisplay().widthPixels;
        ObjectAnimator.ofFloat(mLeftLayout,"TranslationX",0,-width/4).setDuration(500).start();
        mRightLayout.setVisibility(View.VISIBLE);
        ObjectAnimator.ofFloat(mRightLayout,"TranslationX",0,width/4).setDuration(500).start();

        mLeftButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_audio_re_record));
        mLeftTextView.setText(R.string.audio_re_record);

        mRightTextView.setText(R.string.audio_play_record);
        tvUploadRecord.setVisibility(View.VISIBLE);
        canUpload = true;
    }

    private void layoutAnimateReversely(){
        final int width = DimensionUtils.getDisplay().widthPixels;
        ObjectAnimator.ofFloat(mLeftLayout,"TranslationX",-width/4,0).setDuration(500).start();
        ObjectAnimator animator =
        ObjectAnimator.ofFloat(mRightLayout,"TranslationX",width/4,0).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if ((float) animation.getAnimatedValue() < (width / 16)) {
                    mRightLayout.setVisibility(View.GONE);
                }
            }
        });
        animator.start();
        tvUploadRecord.setVisibility(View.GONE);
    }

    @Override
    protected String tag() {
        return TAG;
    }


    // updates the visualizer every 50 milliseconds
    Runnable updateRecorderVisualizer = new Runnable() {
        @Override
        public void run() {
            if (mRecordState == STATE_RECORDING) // if we are already recording
            {
                // get the current amplitude
                int x = mRecorder.getMaxAmplitude();
                mVisualizerView.addAmplitude(x); // update the VisualizeView
                LogUtils.d(TAG, x + "");
                // update in 40 milliseconds
                mHandler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };

    Runnable delayedUpload = new Runnable() {
        @Override
        public void run() {
            canUpload = true;
        }
    };

    View.OnClickListener uploadListener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            if(mRecordState == STATE_PLAY || !canUpload){
                Toast.makeText(AtyAudioRecord.this, R.string.please_stop_playing_first, Toast.LENGTH_SHORT).show();
            }else if(mRecordState == STATE_FINISH){
                uploadRecord();
            }
        }
    };

    private void uploadRecord(){
        Map<String,String> map=new ArrayMap<>();
        map.put("token", StrUtils.token());
        map.put("type", "-12");
        final LoadingPrompt prompt = new LoadingPrompt(this);
        prompt.show(tvUploadRecord,getString(R.string.upload_record));
        OkHttpUtils.uploadAudio(StrUtils.UPLOAD_AVATAR_URL, map, mFileName, MediaType.parse("audio/mp4"), TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                Toast.makeText(AtyAudioRecord.this, R.string.upload_audio_record_finis, Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(IOException e) {
                Toast.makeText(AtyAudioRecord.this, R.string.upload_audio_record_fail, Toast.LENGTH_SHORT).show();
                prompt.dismiss();
            }
        });
    }
}
