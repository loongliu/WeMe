package space.weme.remix.widgt;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liujilong on 2016/6/23.
 * liujilong.me@gmail.com
 */
public class AudioVisualizerView extends View {
    private static final int LINE_WIDTH = 5; // width of visualizer lines
    private List<Float> amplitudes; // amplitudes for line lengths
    private int width; // width of this View
    private int height; // height of this View
    private Paint linePaint; // specifies line drawing characteristics

    private float currentMax = 0;
    // constructor
    public AudioVisualizerView(Context context, AttributeSet attrs) {
        super(context, attrs); // call superclass constructor
        linePaint = new Paint(); // create Paint for lines
        linePaint.setColor(Color.RED); // set color to green
        linePaint.setStyle(Paint.Style.FILL);
    }

    // called when the dimensions of the View change
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        width = w; // new width of this View
        height = h; // new height of this View
        amplitudes = new ArrayList<>(width / LINE_WIDTH);
    }

    // clear all amplitudes to prepare for a new visualization
    public void clear() {
        amplitudes.clear();
        currentMax = 0;
        invalidate();
    }

    // add the given amplitude to the amplitudes ArrayList
    public void addAmplitude(float amplitude) {
        if(amplitude>currentMax) currentMax = amplitude;
        amplitudes.add(amplitude); // add newest to the amplitudes ArrayList

        // if the power lines completely fill the VisualizerView
        if (amplitudes.size() * LINE_WIDTH >= width) {
            amplitudes.remove(0); // remove oldest power value
        }
        invalidate();
    }

    // draw the visualizer with scaled lines representing the amplitudes
    @Override
    public void onDraw(Canvas canvas) {
        int middle = height / 2; // get the middle of the View
        int size = amplitudes.size();
        int length = size*LINE_WIDTH;
        float curX = width/2-length/2; // start curX at zero

        // for each item in the amplitudes ArrayList
        for (float power : amplitudes) {
            float scaledHeight = power / currentMax * height; // scale the power
            curX += LINE_WIDTH; // increase X by LINE_WIDTH

            canvas.drawRect(curX, middle - scaledHeight / 2, curX + LINE_WIDTH, middle
                    + scaledHeight / 2, linePaint);
        }
    }

}
