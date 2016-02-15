package space.weme.remix.widgt;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import space.weme.remix.ui.user.AtyDiscoveryFood;
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
    private ImageView mFrontLocationImage;

    AtyDiscoveryFood aty;

    boolean isFront = false;

    LikeListener mLikeListener;

    Food currentFood;




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


    public void turnOver(){
        mFront.setVisibility(isFront ? GONE : VISIBLE);
        mBack.setVisibility(isFront ? VISIBLE : GONE);
        isFront = !isFront;
    }


    private void config(){
        setCameraDistance(50000);

        if(getChildCount()<2){
            return;
        }
        mBack = getChildAt(0);
        mFront = getChildAt(1);

        mFront.setRotationX(180);
        mFront.setVisibility(GONE);

        mFrontPicture = (SimpleDraweeView) mFront.findViewById(R.id.card_food_picture);
        mFrontLikeImage = (ImageView) mFront.findViewById(R.id.card_food_like_image);
        mFrontLikeNumber = (TextView) mFront.findViewById(R.id.card_food_like_number);
        mFrontAvatar = (SimpleDraweeView) mFront.findViewById(R.id.card_food_avatar);
        mFrontUserName = (TextView) mFront.findViewById(R.id.card_food_user_name);
        mFrontName = (TextView) mFront.findViewById(R.id.card_food_name);
        mFrontLocationText = (TextView) mFront.findViewById(R.id.card_food_distance);
        mFrontLocationImage = (ImageView) mFront.findViewById(R.id.card_food_distance_image);

        mLikeListener = new LikeListener();
        mFrontLikeImage.setOnClickListener(mLikeListener);


    }

    public void resize(){
        ViewGroup.LayoutParams params = mFrontPicture.getLayoutParams();
        params.height = mFrontPicture.getWidth();
        mFrontPicture.setLayoutParams(params);
        RelativeLayout.LayoutParams params1  = (RelativeLayout.LayoutParams) mFrontAvatar.getLayoutParams();
        params1.setMargins(0,params.height- DimensionUtils.dp2px(24),0,0);
        mFrontAvatar.setLayoutParams(params1);
    }

    public void showFood(Food food){
        currentFood = food;

        mFrontAvatar.setImageURI(Uri.parse(StrUtils.thumForID(food.authorId)));
        showPicture(food);
        mFrontLikeNumber.setText(food.likeNumber + "");

        //todo color
        mFrontLikeImage.setImageResource(food.likeFlag ? R.mipmap.like_on : R.mipmap.like_off);
        String name = food.author + aty.getResources().getString(R.string.recommend);
        mFrontUserName.setText(name);
        mFrontName.setText(food.title);
        mFrontLocationText.setText("unknown"); // todo
        mLikeListener.setFood(food);
    }

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
                    mFrontLikeNumber.setText((food.likeNumber+1)+"");
                    // todo
                    mFrontLikeImage.setImageResource(R.mipmap.like_on);
                }
            });
        }
    }

}
