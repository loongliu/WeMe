package space.weme.remix.widgt;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;

import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.find.AtyDiscovery;
import space.weme.remix.ui.user.AtyInfo;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/14.
 * liujilong.me@gmail.com
 */
public class Card extends CardView {
    private static final String TAG = "Card";
    private View mFront;
    private View mBack;
    private SimpleDraweeView avatar;
    private TextView tvName;
    private TextView tvBirth;
    private TextView tvSchool;
    private TextView tvDegree;
    private TextView tvLocation;
    private ImageView ivGender;
    private ImageView ivLike;
    private TextView tvLikeAdd;
    private ImageView ivVoice;
    private FrameLayout frameLayout;
    private AtyDiscovery aty;

    private ValueAnimator animator;
    private AnimatorSet likeTextAnimator;

    private AvatarListener avatarListener;

    private VoiceListener voiceListener;
    private MediaPlayer mMediaPlayer;
    private int mediaState = 0;

    private User user;



    private boolean isFront;

    private boolean isLiked;

    public Card(Context context) {
        super(context);
    }

    public Card(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Card(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    public static Card fromXML(AtyDiscovery context, ViewGroup parent){
        Card card = (Card) LayoutInflater.from(context).inflate(R.layout.aty_discovery_card,parent,false);
        if(card.getChildCount()>1){
            card.mFront = card.getChildAt(1);
            card.mBack = card.getChildAt(0);
            card.isFront = false;
            card.mFront .setVisibility(GONE);
            card.configFront();
        }
        card.aty = context;
        return card;
    }

    public void setAvatarSize(){
        if(mFront==null){
            return;
        }
        SimpleDraweeView avatar = (SimpleDraweeView) mFront.findViewById(R.id.card_people_avatar);
        ViewGroup.LayoutParams params = avatar.getLayoutParams();
        params.height = avatar.getWidth();
        avatar.setLayoutParams(params);
    }


    public void turnOver(){
        if(!checkBackAndFront()){
            return;
        }
        if(isFront){
            stopLikeAnimation();
            stopMedia();
        }else{
            startLikeAnimation();
            ViewGroup.LayoutParams params = ivVoice.getLayoutParams();
            params.width = params.height;
            ivVoice.setLayoutParams(params);
        }
        isLiked = false;
        mFront.setVisibility(isFront?GONE:VISIBLE);
        mBack.setVisibility(isFront?VISIBLE:GONE);
        isFront = !isFront;
    }

    private void stopLikeAnimation(){
        animator.cancel();
    }

    private void startLikeAnimation(){
        animator.start();
    }

    private boolean checkBackAndFront(){
        if(mBack==null||mFront==null){
            if(getChildCount()>1) {
                mFront = getChildAt(1);
                mBack = getChildAt(0);
                mFront.setVisibility(GONE);
                isFront = false;
                configFront();
                return true;
            }
            return false;
        }
        return true;
    }

    private void configFront(){
        mFront.setRotationX(180);
        setCameraDistance(50000);
        avatar = (SimpleDraweeView) mFront.findViewById(R.id.card_people_avatar);
        tvName = (TextView) mFront.findViewById(R.id.card_people_name);
        ivGender = (ImageView) mFront.findViewById(R.id.card_people_gender);
        ivLike = (ImageView) mFront.findViewById(R.id.card_people_like);
        tvBirth = (TextView) mFront.findViewById(R.id.card_people_birth);
        tvSchool = (TextView) mFront.findViewById(R.id.card_people_school);
        tvDegree = (TextView) mFront.findViewById(R.id.card_people_education);
        tvLocation = (TextView) mFront.findViewById(R.id.card_people_location_text);
        tvLikeAdd = (TextView) mFront.findViewById(R.id.card_people_like_add);
        ivVoice = (ImageView) mFront.findViewById(R.id.card_people_voice);
        frameLayout = (FrameLayout) mFront.findViewById(R.id.card_people_image_click);
        avatarListener = new AvatarListener();


        animator = ValueAnimator.ofFloat(0.6f,1.4f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ivLike.setScaleX((Float) animation.getAnimatedValue());
                ivLike.setScaleY((Float) animation.getAnimatedValue());
            }
        });
        animator.setDuration(800);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());

        frameLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user == null) {return;}
                if (!isLiked) {
                    ArrayMap<String, String> map = new ArrayMap<>();
                    map.put("token", StrUtils.token());
                    map.put("userid", user.ID + "");
                    final User cached = user;
                    OkHttpUtils.post(StrUtils.LIKE_USER_CARD, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
                        @Override
                        public void onResponse(String s) {
                            LogUtils.i("Card", s);
                            JSONObject j = OkHttpUtils.parseJSON(getContext(), s);
                            if (j == null) {
                                return;
                            }
                            String flag = j.optString("flag");
                            if ("1".equals(flag)&&cached==user) {
                                aty.showLikeEachOther(user);
                            }
                        }
                    });
                }
                isLiked = true;
                likeTextAnimator.start();
            }
        });


        Random random = new Random(System.currentTimeMillis());
        ObjectAnimator a1 = ObjectAnimator.ofFloat(tvLikeAdd,"Rotation",0,random.nextFloat()*360);
        ObjectAnimator a2 = ObjectAnimator.ofFloat(tvLikeAdd,"ScaleX",1f,0.5f);
        ObjectAnimator a3 = ObjectAnimator.ofFloat(tvLikeAdd,"ScaleY",1f,0.5f);
        ObjectAnimator a4 = ObjectAnimator.ofFloat(tvLikeAdd, "Alpha", 1f, 0f);
        ObjectAnimator a5 = ObjectAnimator.ofFloat(tvLikeAdd,"TranslationY",0, DimensionUtils.dp2px(30));
        likeTextAnimator = new AnimatorSet();
        likeTextAnimator.playTogether(a1,a2,a3,a4,a5);
        likeTextAnimator.setDuration(1000);
        likeTextAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                tvLikeAdd.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                tvLikeAdd.setVisibility(GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {            }

            @Override
            public void onAnimationRepeat(Animator animation) {            }
        });
    }


    public void showUser(final User user){
        if(!checkBackAndFront()){
            return;
        }
        this.user = user;
        showAvatar(user);
        avatarListener.setId(user.ID + "");
        avatar.setOnClickListener(avatarListener);
        tvName.setText(user.name);
        ivGender.setImageResource(user.gender.equals("\u7537") ? R.mipmap.boy : R.mipmap.girl);
        tvBirth.setText(user.birthday);
        tvSchool.setText(user.school);
        tvDegree.setText(user.degree);
        if(user.hometown==null || user.hometown.trim().equals("") || user.hometown.equals("null")){
            tvLocation.setText(R.string.hometown_unknown);
        }else {
            tvLocation.setText(user.hometown);
        }
        if(TextUtils.equals(user.voiceUrl,"")){
            ivVoice.setVisibility(GONE);
        }else{
            ivVoice.setVisibility(VISIBLE);
            voiceListener = new VoiceListener();
            voiceListener.setUser(user);
            ivVoice.setOnClickListener(voiceListener);
        }
    }

    private void showAvatar(User user){
        ControllerListener controllerListener = new BaseControllerListener<ImageInfo>() {
            @Override
            public void onFinalImageSet(  String id, ImageInfo imageInfo, Animatable anim) {
                if (imageInfo == null) {
                    return;
                }
                if(imageInfo instanceof CloseableStaticBitmap) {
                    CloseableStaticBitmap b = (CloseableStaticBitmap) imageInfo;
                    Bitmap bitmap = b.getUnderlyingBitmap();
                    aty.setBackground(bitmap);
                }
            }

            @Override
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {     }

            @Override
            public void onFailure(String id, Throwable throwable) {       }
        };

        Uri uri = Uri.parse(user.avatar);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(uri)
        .build();
        avatar.setController(controller);
    }

    class AvatarListener implements View.OnClickListener{
        String id;

        public void setId(String id){
            this.id = id;
        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(aty,AtyInfo.class);
            i.putExtra(AtyInfo.ID_INTENT,id);
            aty.startActivity(i);
        }
    }

    public void stopMedia(){
        if(mediaState == 1 && mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mediaState = 0;
        }
    }

    class VoiceListener implements View.OnClickListener{
        private User user;

        public void setUser(User user){
            this.user = user;
        }
        @Override
        public void onClick(View v) {
            if(mediaState == 0) {
                mMediaPlayer = new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(getContext(), Uri.parse(user.voiceUrl));
                    mMediaPlayer.prepareAsync();
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mMediaPlayer.start();
                        }
                    });
                    mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            mediaState = 0;
                        }
                    });
                    mediaState=1;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                stopMedia();
            }
        }
    }

}
