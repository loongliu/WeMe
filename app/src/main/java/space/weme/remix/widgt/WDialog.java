package space.weme.remix.widgt;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import space.weme.remix.APP;
import space.weme.remix.R;
import space.weme.remix.util.DimensionUtils;

/**
 * Created by Liujilong on 2016/2/17.
 * liujilong.me@gmail.com
 */
public class WDialog extends Dialog {
    private WDialog(Context context) {
        super(context, R.style.Dialog);
    }


    public static class Builder{
        Context context;
        String title;
        String message;
        String positiveText;
        View.OnClickListener positiveListener;
        String negativeText;
        View.OnClickListener negativeListener;
        public Builder(Context context){
            this.context = context;
        }
        public Builder setTitle(String title){
            this.title = title;
            return this;
        }
        public Builder setTitle(int resId){
            this.title = APP.context().getResources().getString(resId);
            return this;
        }
        public Builder setMessage(String message){
            this.message = message;
            return this;
        }
        public Builder setMessage(int resId){
            this.message = APP.context().getResources().getString(resId);
            return this;
        }
        public Builder setPositive(String text, View.OnClickListener listener){
            positiveText = text;
            positiveListener = listener;
            return this;
        }
        public Builder setPositive(int resId, View.OnClickListener listener){
            return setPositive(APP.context().getString(resId),listener);
        }
        public Builder setNegative(String text, View.OnClickListener listener){
            negativeText = text;
            negativeListener = listener;
            return this;
        }
        public Builder setNegative(int resId, View.OnClickListener listener){
            return setNegative(APP.context().getString(resId), listener);
        }

        public WDialog build(){
            final WDialog dialog = new WDialog(context);
            View contextView = LayoutInflater.from(context).inflate(R.layout.wdialg,null,false);
            TextView tvTitle = (TextView) contextView.findViewById(R.id.wdialog_title);
            if(title==null){
                tvTitle.setVisibility(View.GONE);
            }else{
                tvTitle.setText(title);
            }
            TextView tvContext = (TextView) contextView.findViewById(R.id.wdialog_content);
            tvContext.setText(message);
            TextView tvPositive = (TextView) contextView.findViewById(R.id.wdialog_positive);
            if(positiveText==null){
                tvPositive.setText(R.string.ok);
            }else {
                tvPositive.setText(positiveText);
            }
            tvPositive.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (positiveListener != null) {
                        positiveListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            TextView tvNegative = (TextView) contextView.findViewById(R.id.wdialog_negative);
            if(negativeText==null){
                tvNegative.setText(R.string.cancel);
            }else {
                tvNegative.setText(negativeText);
            }
            tvNegative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (negativeListener != null) {
                        negativeListener.onClick(v);
                    }
                    dialog.dismiss();
                }
            });
            dialog.setContentView(contextView);
            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
            wmlp.width = DimensionUtils.getDisplay().widthPixels - DimensionUtils.dp2px(80);

            return dialog;
        }
        public WDialog show(){
            WDialog dialog = build();
            dialog.show();
            return dialog;
        }

    }

}
