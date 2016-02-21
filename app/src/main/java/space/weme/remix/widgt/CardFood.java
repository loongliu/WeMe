package space.weme.remix.widgt;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.controller.ControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.CloseableStaticBitmap;
import com.facebook.imagepipeline.image.ImageInfo;

import org.json.JSONObject;

import space.weme.remix.R;
import space.weme.remix.model.Food;
import space.weme.remix.ui.find.AtyFoodMap;
import space.weme.remix.ui.find.AtyDiscoveryFood;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/15.
 * liujilong.me@gmail.com
 */
public class CardFood extends CardView {

    private View mFront;
    private View mBack;
    private View mDetail;

    private SimpleDraweeView mFrontPicture;
    private ImageView mFrontLikeImage;
    private TextView mFrontLikeNumber;
    private SimpleDraweeView mFrontAvatar;
    private TextView mFrontUserName;
    private TextView mFrontName;
    private TextView mFrontLocationText;

    private SimpleDraweeView mDetailPicture;
    private TextView mDetailLocation;
    private TextView mDetailPrice;
    private TextView mDetailComment;
    private TextView mDetailName;

    AtyDiscoveryFood aty;


    static final int STATE_BACK = 1;
    static final int STATE_FRONT = 2;
    static final int STATE_DETAIL = 3;
    int state = STATE_BACK;

    LikeListener mLikeListener;
    DetailListener mDetailListener;
    MapListener mMapListener;

    Food currentFood;

    float preValue = 0;




    public CardFood(Context context) {
        super(context);
    }

    public CardFood(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CardFood(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public static CardFood fromXML(AtyDiscoveryFood context, ViewGroup parent, FrameLayout.LayoutParams params){
        CardFood card = (CardFood) LayoutInflater.from(context).inflate(R.layout.aty_discovery_food_card, parent, false);
        card.setLayoutParams(params);
        card.config();
        card.aty = context;
        return card;
    }


    public void turnToFront(){
        mFront.setVisibility(VISIBLE);
        mBack.setVisibility(INVISIBLE);
        mDetail.setVisibility(INVISIBLE);

        state = STATE_FRONT;
    }

    public void turnToBack(){
        mFront.setVisibility(INVISIBLE);
        mBack.setVisibility(VISIBLE);
        mDetail.setVisibility(INVISIBLE);

        state = STATE_BACK;

        setRotationY(0);
    }

    public void turnToDetail(){
        mFront.setVisibility(INVISIBLE);
        mBack.setVisibility(INVISIBLE);
        mDetail.setVisibility(VISIBLE);

        state = STATE_DETAIL;
    }



    private void config(){
        setCameraDistance(50000);

        if(getChildCount()<2){
            return;
        }
        mBack = getChildAt(0);
        mFront = getChildAt(1);
        mDetail = getChildAt(2);

        mFront.setRotationX(180);
        mFront.setVisibility(GONE);

        mDetail.setVisibility(GONE);
        mDetail.setRotationY(180);
        mDetail.setRotationX(180);

        mFrontPicture = (SimpleDraweeView) mFront.findViewById(R.id.card_food_picture);
        mFrontLikeImage = (ImageView) mFront.findViewById(R.id.card_food_like_image);
        mFrontLikeNumber = (TextView) mFront.findViewById(R.id.card_food_like_number);
        mFrontAvatar = (SimpleDraweeView) mFront.findViewById(R.id.card_food_avatar);
        mFrontUserName = (TextView) mFront.findViewById(R.id.card_food_user_name);
        mFrontName = (TextView) mFront.findViewById(R.id.card_food_name);
        mFrontLocationText = (TextView) mFront.findViewById(R.id.card_food_distance);
        ImageView mFrontLocationImage = (ImageView) mFront.findViewById(R.id.card_food_distance_image);

        mLikeListener = new LikeListener();
        mFrontLikeImage.setOnClickListener(mLikeListener);
        mDetailListener = new DetailListener();
        mFrontLocationImage.setOnClickListener(mDetailListener);

        mDetailPicture = (SimpleDraweeView) mDetail.findViewById(R.id.card_food_detail_picture);
        LinearLayout mDetailLocationLayout = (LinearLayout) mDetail.findViewById(R.id.card_food_detail_location);
        mDetailLocation = (TextView) mDetail.findViewById(R.id.card_food_detail_location_text);
        mDetailPrice = (TextView) mDetail.findViewById(R.id.card_food_detail_price);
        mDetailComment = (TextView) mDetail.findViewById(R.id.card_food_detail_comment);
        mDetailName = (TextView) mDetail.findViewById(R.id.card_food_detail_user);

        mDetail.findViewById(R.id.card_food_detail_backward).setOnClickListener(mDetailListener);

        mMapListener = new MapListener();
        mDetailLocationLayout.setOnClickListener(mMapListener);

    }

    public void resize(){
        ViewGroup.LayoutParams params = mFrontPicture.getLayoutParams();
        params.height = mFrontPicture.getWidth();
        mFrontPicture.setLayoutParams(params);
        RelativeLayout.LayoutParams params1  = (RelativeLayout.LayoutParams) mFrontAvatar.getLayoutParams();
        params1.setMargins(0,params.height- DimensionUtils.dp2px(24),0,0);
        mFrontAvatar.setLayoutParams(params1);
    }

    public void resizeDetail(){
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mDetailPicture.getLayoutParams();
        params.height = mDetail.getHeight()/3;
        params.width = mDetail.getHeight()/3;
        mDetailPicture.setLayoutParams(params);
    }

    public void showFood(Food food){
        currentFood = food;

        mFrontAvatar.setImageURI(Uri.parse(StrUtils.thumForID(food.authorId)));
        showPicture(food);
        mFrontLikeNumber.setText(String.format("%d", food.likeNumber));

        //todo color
        mFrontLikeImage.setImageResource(food.likeFlag ? R.mipmap.like_on : R.mipmap.like_off);
        String name = food.author + aty.getResources().getString(R.string.recommend);
        mFrontUserName.setText(name);
        mFrontName.setText(food.title);
        LatLng foodLL = new LatLng(food.latitude,food.longitude);
        LatLng curLL = aty.getCurrentLatLng();
        if(curLL==null) {
            mFrontLocationText.setText(R.string.distance_unknown);
        }else{
            float meters = AMapUtils.calculateLineDistance(foodLL, curLL);
            String distance = StrUtils.distanceTransfer(meters);
            mFrontLocationText.setText(distance);
        }
        mLikeListener.setFood(food);

        mDetailPicture.setImageURI(Uri.parse(food.url));
        mDetailLocation.setText(food.location);
        String price = aty.getResources().getString(R.string.pre_people)+food.price+" RMB";
        mDetailPrice.setText(price);
        mDetailComment.setText(food.comment);
        String author = aty.getResources().getString(R.string.come_from) + food.author;
        mDetailName.setText(author);

        mMapListener.setFood(food);
    }

    @SuppressWarnings("unchecked")
    private void showPicture(Food food){
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

        Uri uri = Uri.parse(food.url);
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(controllerListener)
                .setUri(uri)
                .build();
        mFrontPicture.setController(controller);
    }

    private class LikeListener implements View.OnClickListener{
       private Food food;

        public void setFood(Food food) {
            this.food = food;
        }

        @Override
        public void onClick(View v) {
            if(food.likeFlag){
                return;
            }
            ArrayMap<String,String> param = new ArrayMap<>();
            param.put("token", StrUtils.token());
            param.put("foodcardid",food.ID);
            OkHttpUtils.post(StrUtils.LIKE_FOOD_URL,param, aty.tag(),new OkHttpUtils.SimpleOkCallBack(){
                @Override
                public void onResponse(String s) {
                    JSONObject j = OkHttpUtils.parseJSON(aty,s);
                    if(j == null || food != currentFood){
                        return;
                    }
                    mFrontLikeNumber.setText(String.format("%d",food.likeNumber+1));
                    // todo
                    mFrontLikeImage.setImageResource(R.mipmap.like_on);
                }
            });
        }
    }

    private class DetailListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            if(state==STATE_FRONT) {
                ObjectAnimator a3 = ObjectAnimator.ofFloat(CardFood.this, "RotationY", 0, 180).setDuration(500);
                a3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();

                        resizeDetail();
                        if (preValue < 90 && value > 90) {
                            turnToDetail();
                        }
                        preValue = value;
                    }
                });
                a3.start();
            }else if(state==STATE_DETAIL){
                ObjectAnimator a3 = ObjectAnimator.ofFloat(CardFood.this, "RotationY", 180, 0).setDuration(500);
                a3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        if (preValue > 90 && value < 90) {
                            turnToFront();
                        }
                        preValue = value;
                    }
                });
                a3.start();
            }
        }
    }

    private class MapListener implements View.OnClickListener{
        Food currentFood;
        void setFood(Food food){
            currentFood = food;
        }
        @Override
        public void onClick(View v) {
            Intent i = new Intent(aty, AtyFoodMap.class);
            i.putExtra(AtyFoodMap.INTENT_LAT,currentFood.latitude);
            i.putExtra(AtyFoodMap.INTENT_LON, currentFood.longitude);
            aty.startActivity(i);
        }
    }

}
