package space.weme.remix.widgt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Liujilong on 2016/6/25.
 * liujilong.me@gmail.com
 */
public class TimerView extends TextView {

    private long startTime;
    private int timePassed;

    private static final int STATE_IDLE = 0x000;
    private static final int STATE_RUNNING = 0x001;
    private static final int STATE_PAUSE = 0x002;

    private int state = STATE_IDLE;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    public TimerView(Context context) {
        super(context);
    }

    public TimerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void start(){
        if(state != STATE_IDLE && state != STATE_PAUSE) return;

        startTime = System.currentTimeMillis();
        timePassed = 0;
        state = STATE_RUNNING;
        setText(getTimeString(timePassed));
        mHandler.postDelayed(timerRunnable, 1000);
    }

    public void stop(){
        state = STATE_PAUSE;
        startTime = 0;
        timePassed = 0;
    }

    private String getTimeString(int time){
        int hour = time/3600;
        int minute = time%3600/60;
        int second = time%60;
        String s = String.format("%02d:%02d",minute,second);
        if(hour != 0){
            s = String.format("%d:",hour) + s;
        }
        return s;
    }

    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(state == STATE_RUNNING){
                timePassed++;
                setText(getTimeString(timePassed));
                long nextTime = startTime + (timePassed+1) * 1000 - System.currentTimeMillis();
                mHandler.postDelayed(timerRunnable,nextTime);
            }
        }
    };

}
