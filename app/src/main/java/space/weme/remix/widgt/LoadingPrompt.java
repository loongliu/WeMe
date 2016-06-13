package space.weme.remix.widgt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import space.weme.remix.R;

/**
 * Created by Liujilong on 2016/5/24.
 * liujilong.me@gmail.com
 */
public class LoadingPrompt {

    private Context mContext;
    View mTotalView;
    TextView mTextView;
    final PopupWindow popup;



    public LoadingPrompt(Context context){
        mContext = context;
        // TODO: 2016/5/9 add Enter and Exit animation for PopupWindow
        LayoutInflater inflater = LayoutInflater.from(context);
        mTotalView = inflater.inflate(R.layout.loading_popup,null);
        mTextView = (TextView) mTotalView.findViewById(R.id.pop_up_text);

        popup = new PopupWindow(context);
        popup.setContentView(mTotalView);
        popup.setFocusable(false);
        DisplayMetrics metric = context.getResources().getDisplayMetrics();
        int width = metric.widthPixels;
        int height = metric.heightPixels;
        popup.setWidth(width);
        popup.setHeight(height);
        popup.setBackgroundDrawable(new BitmapDrawable(context.getResources(), (Bitmap) null));
    }

    public void show(View parent,String text){
        mTextView.setText(text);
        if(!popup.isShowing()){
            popup.showAtLocation(parent, Gravity.TOP,0,0);
        }
    }

    public void dismiss(){
        popup.dismiss();
    }

    public boolean isShowing(){
        return popup.isShowing();
    }

}