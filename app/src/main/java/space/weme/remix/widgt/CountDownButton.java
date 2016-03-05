package space.weme.remix.widgt;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Created by Liujilong on 2016/3/5.
 * liujilong.me@gmail.com
 */
public class CountDownButton {

    private CountDownTimer countDownTimer;

    private Button button;

    /**
     *
     * @param button  the Button of counting down
     * @param defaultString string to show
     * @param max max time for countdown
     * @param interval interval (seconds)
     */
    public CountDownButton(final Button button,
                           final String defaultString, int max, int interval) {

        this.button = button;
        // because there is error when counting down ,so we need offset
        countDownTimer = new CountDownTimer(max * 1000, interval * 1000 - 10) {

            @Override
            public void onTick(long time) {
                // offset 15ms
                button.setText(defaultString + "(" + ((time + 15) / 1000)
                        + "秒)");
            }

            @Override
            public void onFinish() {
                button.setEnabled(true);
                button.setText(defaultString);
            }
        };
    }

    /**
     * begin countdown
     */
    public void start() {
        button.setEnabled(false);
        countDownTimer.start();
    }
}
