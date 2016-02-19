package space.weme.remix.widgt;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;

import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.user.AtyDiscovery;
import space.weme.remix.ui.user.AtyInfo;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/14.
 * liujilong.me@gmail.com
 */
public class Card extends CardView {
    private View mFront;
    private View mBack;
    private SimpleDraweeView avatar;
    private TextView tvName;
    private TextView tvBirth;
    private TextView tvSchool;
    private TextView tvDegree;
    private TextView tvLocation;
    private ImageView ivGender;
    private AtyDiscovery aty;

    private AvatarListener avatarListener;



    private boolean isFront;

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
        mFront.setVisibility(isFront?GONE:VISIBLE);
        mBack.setVisibility(isFront?VISIBLE:GONE);
        isFront = !isFront;
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
        tvBirth = (TextView) mFront.findViewById(R.id.card_people_birth);
        tvSchool = (TextView) mFront.findViewById(R.id.card_people_school);
        tvDegree = (TextView) mFront.findViewById(R.id.card_people_education);
        tvLocation = (TextView) mFront.findViewById(R.id.card_people_location_text);
        avatarListener = new AvatarListener();
    }

    public void showUser(User user){
        if(!checkBackAndFront()){
            return;
        }
        showAvatar(user);
        avatarListener.setId(user.ID+"");
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
            public void onIntermediateImageSet(String id, @Nullable ImageInfo imageInfo) {
            }

            @Override
            public void onFailure(String id, Throwable throwable) {
            }
        };

        Uri uri = Uri.parse(StrUtils.thumForID(user.ID + ""));
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

}
