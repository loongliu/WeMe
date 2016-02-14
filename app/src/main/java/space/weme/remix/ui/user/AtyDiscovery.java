package space.weme.remix.ui.user;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.CardView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.BitmapUtils;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.Card;

/**
 * Created by Liujilong on 16/2/14.
 * liujilong.me@gmail.com
 */
public class AtyDiscovery extends BaseActivity {

    private static final String TAG = "AtyDiscovery";

    private static final String TAG_USER = TAG + "_USER";

    private static final int STATE_FIRST = 0x1;
    private static final int STATE_ANIMATING = 0x2;
    private static final int STATE_READY = 0x3;

    private int state = STATE_FIRST;


    private DisplayMetrics displayMetrics;

    private Card mCard;
    private FrameLayout flBackground;

    private float mTranslationY;


    private boolean isLoading = false;

    private float preValue = 0;

    private List<User> userList;
    private int currentIndex = 0;

    ExecutorService exec;
    private Handler mHandler;

    private PopupWindow popupWindow;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_discovery);

        ImageView ivBack = (ImageView) findViewById(R.id.aty_discovery_back);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ImageView ivMore = (ImageView) findViewById(R.id.aty_discovery_more);
        ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivMoreClicked();
            }
        });

        exec = Executors.newSingleThreadExecutor();
        mHandler = new Handler();
        flBackground = (FrameLayout) findViewById(R.id.aty_discovery_background);

        BitmapDrawable b = (BitmapDrawable) getResources().getDrawable(R.mipmap.spade_bk);
        if(b!=null) {
            setBackground(b.getBitmap());
        }

        mCard = Card.fromXML(this, flBackground);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0,0);
        displayMetrics = DimensionUtils.getDisplay();

        params.width = displayMetrics.widthPixels*7/10;
        params.height = params.width*3/2;
        params.gravity= Gravity.CENTER;
        mCard.setLayoutParams(params);
        mCard.setAvatarSize();
        flBackground.addView(mCard);

        mTranslationY = displayMetrics.heightPixels / 2 + 21 * displayMetrics.widthPixels / 40;
        mCard.setTranslationY(mTranslationY);

        CardView cardView = (CardView) findViewById(R.id.aty_discovery_card);
        cardView.setLayoutParams(params);
        cardView.setTranslationY(mTranslationY-DimensionUtils.dp2px(64));

        TextView tvText = (TextView) findViewById(R.id.aty_discovery_text);
        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state!=STATE_ANIMATING) {
                    startAnimation();
                }
            }
        });
        userList = new ArrayList<>();

        flBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopup();
            }
        });

    }

    private void dismissPopup(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }


    private void ivMoreClicked(){
        if(state!=STATE_READY){
            return;
        }
        if(popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
        }else{
            initPopupWindow();
            popupWindow.showAtLocation(flBackground, Gravity.BOTTOM, 0, 0);
        }
    }

    private void initPopupWindow(){
        View content = LayoutInflater.from(this).inflate(R.layout.aty_discovery_option,flBackground,false);
        popupWindow = new PopupWindow(content,displayMetrics.widthPixels,DimensionUtils.dp2px(152));
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        View.OnClickListener popupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = userList.get(currentIndex).ID + "";
                if(v.getId()==R.id.aty_discovery_option_message){
                    Intent i = new Intent(AtyDiscovery.this,AtyMessageReply.class);
                    i.putExtra(AtyMessageReply.INTENT_ID,id);
                    startActivity(i);
                }else if(v.getId() == R.id.aty_discovery_option_follow){
                    followUser(id);
                }
                dismissPopup();
            }
        };
        content.findViewById(R.id.aty_discovery_option_cancel).setOnClickListener(popupListener);
        content.findViewById(R.id.aty_discovery_option_follow).setOnClickListener(popupListener);
        content.findViewById(R.id.aty_discovery_option_message).setOnClickListener(popupListener);
    }

    private void followUser(String id){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("id", id);
        OkHttpUtils.post(StrUtils.FOLLOW_USER, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                Toast.makeText(AtyDiscovery.this, R.string.follow_fail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(AtyDiscovery.this, s);
                if (j == null) {
                    Toast.makeText(AtyDiscovery.this, R.string.follow_fail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AtyDiscovery.this, R.string.follow_success, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setBackground(final Bitmap b){
        LogUtils.i("Time","Label 1 : " + System.currentTimeMillis());
        if(b == null){
            return;
        }
        exec.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.i("Time","Label 2 : " + System.currentTimeMillis());
                Bitmap sized = BitmapUtils.scale(b,40,40*b.getHeight()/b.getWidth());
                LogUtils.i("Time","Label 3 : " + System.currentTimeMillis());
                final int radius = 5;
                final Bitmap blur = BitmapUtils.blur(sized,radius);
                LogUtils.i("Time", "Label 4 : " + System.currentTimeMillis());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i("Time","Label 5 : " + System.currentTimeMillis());
                        flBackground.setBackgroundDrawable(new BitmapDrawable(getResources(),blur));
                    }
                });
            }
        });
    }



    private void startAnimation(){
        currentIndex++;
        if(currentIndex>=userList.size()&& !isLoading){
            fetchUser();
            currentIndex=0;
        }
        ObjectAnimator a1 =  ObjectAnimator.ofFloat(mCard, "TranslationY", mTranslationY, 0)
                .setDuration(500);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(mCard,"RotationX",0,180).setDuration(500);
        a2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCard.setAvatarSize();
                float value = (float) animation.getAnimatedValue();

                if (preValue<90 && value > 90) {
                    mCard.turnOver();
                    if(userList.size()!=0) {
                        mCard.showUser(userList.get(currentIndex));
                    }
                }
                preValue = value;
            }
        });
        a2.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                state=STATE_READY;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                state=STATE_READY;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        if(state==STATE_FIRST){
            state=STATE_ANIMATING;
            AnimatorSet set = new AnimatorSet();
            set.playSequentially(a1, a2);
            set.start();
        }else{
            state=STATE_ANIMATING;
            ObjectAnimator a3 = ObjectAnimator.ofFloat(mCard,"RotationX",180,0).setDuration(500);
            a3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mCard.setAvatarSize();
                    float value = (float) animation.getAnimatedValue();

                    if (preValue>90 && value < 90) {
                        mCard.turnOver();
                    }
                    preValue = value;
                }
            });
            ObjectAnimator a4 =  ObjectAnimator.ofFloat(mCard, "TranslationY", 0, mTranslationY)
                    .setDuration(500);

            AnimatorSet set = new AnimatorSet();
            set.playSequentially(a3, a4, a1, a2);
            set.start();
        }

    }

    private void fetchUser(){
        OkHttpUtils.cancel(TAG_USER);
        isLoading = true;
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_RECOMMAND_USER,param,TAG_USER,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                isLoading = false;
                JSONObject j = OkHttpUtils.parseJSON(AtyDiscovery.this, s);
                if(j == null){
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                for(int i = 0; i <array.length(); i++){
                    userList.add(User.fromJSON(array.optJSONObject(i)));
                }
                if(userList.size()>0){
                    mCard.showUser(userList.get(0));
                }
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    protected void onStop() {
        super.onStop();
        dismissPopup();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OkHttpUtils.cancel(TAG_USER);
        exec.shutdown();
    }
}
