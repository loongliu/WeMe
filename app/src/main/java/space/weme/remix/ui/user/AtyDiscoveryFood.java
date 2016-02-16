package space.weme.remix.ui.user;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import space.weme.remix.R;
import space.weme.remix.model.Food;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.BitmapUtils;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.CardFood;

/**
 * Created by Liujilong on 16/2/14.
 * liujilong.me@gmail.com
 */
public class AtyDiscoveryFood extends BaseActivity {
    private static final String TAG = "AtyDiscoveryFood";

    private static final int STATE_FIRST = 0x1;
    private static final int STATE_ANIMATING = 0x2;
    private static final int STATE_READY = 0x3;

    private int state = STATE_FIRST;

    private CardFood mCard;
    private FrameLayout flBackground;

    private DisplayMetrics displayMetrics;
    private float mTranslationY;

    ExecutorService exec;
    private Handler mHandler;

    private float preValue;

    private List<Food> foodList;
    private int currentIndex = 0;
    private boolean isLoading = false;

    private PopupWindow popupWindow;

    @Override
    @SuppressWarnings("deprecation")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_discovery_food);

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


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(0,0);
        displayMetrics = DimensionUtils.getDisplay();

        params.width = displayMetrics.widthPixels*7/10;
        params.height = params.width*3/2;
        params.gravity= Gravity.CENTER;
        mCard = CardFood.fromXML(this, flBackground, params);
        flBackground.addView(mCard);

        mTranslationY = displayMetrics.heightPixels / 2 + 21 * displayMetrics.widthPixels / 40;
        CardView cardView = (CardView) findViewById(R.id.aty_discovery_card);
        cardView.setLayoutParams(params);
        cardView.setTranslationY(mTranslationY - DimensionUtils.dp2px(64));

        mCard.setTranslationY(mTranslationY);

        TextView tvText = (TextView) findViewById(R.id.aty_discovery_text);
        tvText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state != STATE_ANIMATING) {
                    startAnimation();
                }
            }
        });

        foodList = new ArrayList<>();

        flBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPopup();
            }
        });

    }


    private void fetchFood(){
        isLoading = true;
        ArrayMap<String,String> map = new ArrayMap<>();
        map.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_RECOMMEND_FOOD,map,TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onFailure(IOException e) {
                isLoading = false;
            }

            @Override
            public void onResponse(String s) {
                isLoading = false;
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyDiscoveryFood.this,s);
                if(j == null){
                    return;
                }
                foodList.clear();
                JSONArray array = j.optJSONArray("result");
                for(int i = 0; i <array.length(); i++) {
                    foodList.add(Food.fromJSON(array.optJSONObject(i)));
                }
                if(foodList.size()>0){
                    mCard.showFood(foodList.get(0));
                }
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void setBackground(final Bitmap b){
        LogUtils.i("Time", "Label 1 : " + System.currentTimeMillis());
        if(b == null){
            return;
        }
        exec.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.i("Time", "Label 2 : " + System.currentTimeMillis());
                Bitmap sized = BitmapUtils.scale(b, 40, 40 * b.getHeight() / b.getWidth());
                LogUtils.i("Time", "Label 3 : " + System.currentTimeMillis());
                final int radius = 5;
                final Bitmap blur = BitmapUtils.blur(sized, radius);
                LogUtils.i("Time", "Label 4 : " + System.currentTimeMillis());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        LogUtils.i("Time", "Label 5 : " + System.currentTimeMillis());
                        flBackground.setBackgroundDrawable(new BitmapDrawable(getResources(), blur));
                    }
                });
            }
        });
    }


    private void startAnimation(){
        currentIndex++;
        if(currentIndex>=foodList.size()&& !isLoading){
            fetchFood();
            currentIndex=0;
        }
        ObjectAnimator a1 =  ObjectAnimator.ofFloat(mCard, "TranslationY", mTranslationY, 0)
                .setDuration(500);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(mCard,"RotationX",0,180).setDuration(500);
        a2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();

                mCard.resize();
                if (preValue < 90 && value > 90) {
                    mCard.turnToFront();
                    if (foodList.size() != 0) {
                        mCard.showFood(foodList.get(currentIndex));
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
                state = STATE_READY;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                state = STATE_READY;
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
                    float value = (float) animation.getAnimatedValue();

                    mCard.resize();
                    if (preValue>90 && value < 90) {
                        mCard.turnToBack();
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


    private void ivMoreClicked(){
        if(popupWindow!=null&&popupWindow.isShowing()){
            popupWindow.dismiss();
        }else{
            initPopupWindow();
            popupWindow.showAtLocation(flBackground, Gravity.BOTTOM, 0, 0);
        }
    }

    private void initPopupWindow(){
        View content = LayoutInflater.from(this).inflate(R.layout.aty_discovery_food_option,flBackground,false);
        popupWindow = new PopupWindow(content,displayMetrics.widthPixels,DimensionUtils.dp2px(102));
        popupWindow.setAnimationStyle(R.style.PopupWindowAnimation);
        View.OnClickListener popupListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId()==R.id.aty_discovery_option_add){
                    LogUtils.i(TAG,"add food card");
                    // todo
                }
                dismissPopup();
            }
        };
        content.findViewById(R.id.aty_discovery_option_cancel).setOnClickListener(popupListener);
        content.findViewById(R.id.aty_discovery_option_add).setOnClickListener(popupListener);
    }

    private void dismissPopup(){
        if(popupWindow!=null && popupWindow.isShowing()){
            popupWindow.dismiss();
        }
    }

    @Override
    public String tag() {
        return TAG;
    }
}
