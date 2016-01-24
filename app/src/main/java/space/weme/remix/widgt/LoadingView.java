package space.weme.remix.widgt;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class LoadingView extends LinearLayout {
    ProgressBar mProgressBar;
    TextView mTextView;
    Context mContext;

    public LoadingView(Context context) {
        super(context);
        mContext = context;
        init();
    }
    private void init(){
        mProgressBar = new ProgressBar(mContext,null,android.R.attr.progressBarStyleLarge);
        mTextView = new TextView(mContext);
        mTextView.setText("Text");
        addView(mProgressBar);
        addView(mTextView);
    }
}
