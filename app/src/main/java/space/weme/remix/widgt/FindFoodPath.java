package space.weme.remix.widgt;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import space.weme.remix.R;
import space.weme.remix.ui.find.AtyDiscovery;
import space.weme.remix.ui.find.AtyDiscoveryFood;
import space.weme.remix.util.DimensionUtils;

/**
 * Created by Liujilong on 2016/2/21.
 * liujilong.me@gmail.com
 */
public class FindFoodPath extends FrameLayout implements View.OnClickListener{
    private Context context;

    private int strokeSize;

    int width;
    int height;

    Path path_food;
    Path path_friend;
    Path pathCircle;
    Paint pathPaint;

    ImageView ivEarth;
    ImageView ivFood;
    LinearLayout llFood;
    ImageView ivFriend;
    LinearLayout llFriend;
    ImageView ivPlane;

    Path full_path_food;
    Path full_path_fried;
    Path full_path_circle;
    PathMeasure full_path_food_measure;
    PathMeasure full_path_friend_measure;
    PathMeasure full_path_circle_measure;

    ValueAnimator animator;


    public FindFoodPath(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public FindFoodPath(Context context) {
        this(context, null);
    }

    public static FindFoodPath fromXML(LayoutInflater inflater, ViewGroup container){
        FindFoodPath path = (FindFoodPath) inflater.inflate(R.layout.fgt_find,container,false);
        inflater.getContext();
        path.configView(path);
        return path;
    }

    private void configView(View v){
        int size = DimensionUtils.getDisplay().widthPixels /6;

        ivEarth = (ImageView) v.findViewById(R.id.earth);
        ViewGroup.LayoutParams params = ivEarth.getLayoutParams();
        params.width = size;
        params.height = size;
        ivEarth.setLayoutParams(params);

        ivFood = (ImageView) v.findViewById(R.id.fgt_find_food);
        params = ivFood.getLayoutParams();
        params.height = size;
        params.width = size;
        ivFood.setLayoutParams(params);

        llFood = (LinearLayout) v.findViewById(R.id.food_layout);

        ivFriend = (ImageView) v.findViewById(R.id.fgt_find_friend);
        params = ivFriend.getLayoutParams();
        params.height = size;
        params.width = size;
        ivFriend.setLayoutParams(params);

        llFriend = (LinearLayout) v.findViewById(R.id.friend_layout);

        ivPlane = (ImageView) v.findViewById(R.id.plane);
        params = ivPlane.getLayoutParams();
        params.width=size/2;
        params.height = size/2;
        ivPlane.setLayoutParams(params);

        llFood.setOnClickListener(this);
        llFriend.setOnClickListener(this);

        startThisAnimation();

    }

    public void startThisAnimation(){
        ObjectAnimator a1 = ObjectAnimator.ofFloat(this, "Angle", 0, 360).setDuration(20000);
        a1.setInterpolator(new LinearInterpolator());
        a1.start();

        animator = ValueAnimator.ofFloat(0, 359).setDuration(20000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                updatePosition(animation);
            }
        });
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();
    }

    public void stopAnimation(){
        clearAnimation();
        animator.cancel();
    }

    public void setAngle(float angle){
        int size = Math.min(width,height);
        path_friend = new Path();
        RectF rect = new RectF(width/2-size/2,height/2-size/4,width/2+size/2,height/2+size/4);
        path_friend.addArc(rect, 0, angle);
        Matrix mMatrix1 = new Matrix();
        mMatrix1.postRotate(45, rect.centerX(), rect.centerY());
        path_friend.transform(mMatrix1);

        path_food = new Path();
        path_food.addArc(rect, 0, angle);
        Matrix mMatrix2 = new Matrix();
        mMatrix2.postRotate(-45, rect.centerX(), rect.centerY());
        path_food.transform(mMatrix2);

        pathCircle = new Path();
        int radius = size * 3/25;
        RectF r = new RectF(width/2-radius, height/2-radius, width/2+radius, height/2+radius);
        pathCircle.addArc(r, 0, angle);

        pathPaint.setStrokeWidth(strokeSize*angle/360);

        invalidate();


    }

    public void updatePosition(ValueAnimator animator){
        if(full_path_food_measure==null){
            configSize(DimensionUtils.getDisplay().widthPixels,DimensionUtils.getDisplay().heightPixels);
        }
        float value = (float) animator.getAnimatedValue();
        //coordinates will be here
        float fPosition[] = {0f, 0f};
        //get coordinates of the middle point
        full_path_food_measure.getPosTan(full_path_food_measure.getLength() * value / 359, fPosition, null);
        llFood.setX(fPosition[0] - llFood.getWidth() / 2);
        llFood.setY(fPosition[1] - llFood.getHeight() / 2);

        full_path_friend_measure.getPosTan(full_path_friend_measure.getLength() * value / 359, fPosition, null);
        llFriend.setX(fPosition[0] - llFriend.getWidth() / 2);
        llFriend.setY(fPosition[1] - llFriend.getHeight() / 2);

        full_path_circle_measure.getPosTan(full_path_circle_measure.getLength() * value / 359, fPosition, null);
        ivPlane.setX(fPosition[0] - ivPlane.getWidth() / 2);
        ivPlane.setY(fPosition[1] - ivPlane.getHeight() / 2);
        ivPlane.setRotation(value);

        ivEarth.setRotation(value);
    }

    private void init(){
        setWillNotDraw(false);
        pathPaint = new Paint();
        pathPaint.setColor(Color.WHITE);
        pathPaint.setStyle(Paint.Style.STROKE);
        pathPaint.setStrokeWidth(10);
        pathPaint.setAntiAlias(true);
        pathPaint.setColor(0x7fffffff);

        strokeSize = DimensionUtils.dp2px(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawPath(path_food, pathPaint);
        canvas.drawPath(path_friend, pathPaint);
        canvas.drawPath(pathCircle, pathPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        configSize(w, h);
    }
    private void configSize(int w, int h){
        width = w;
        height = h;
        setAngle(0);
        int size = Math.min(width,height);
        full_path_fried = new Path();
        RectF rect = new RectF(width/2-size/2,height/2-size/4,width/2+size/2,height/2+size/4);
        full_path_fried.addArc(rect, 0, 360);
        Matrix mMatrix1 = new Matrix();
        mMatrix1.postRotate(45, rect.centerX(), rect.centerY());
        full_path_fried.transform(mMatrix1);

        full_path_food = new Path();
        full_path_food.addArc(rect, 0, 360);
        Matrix mMatrix2 = new Matrix();
        mMatrix2.postRotate(-45, rect.centerX(), rect.centerY());
        full_path_food.transform(mMatrix2);

        full_path_circle = new Path();
        int radius = size*3/25;
        RectF re = new RectF(width/2-radius, height/2-radius, width/2+radius, height/2+radius);
        full_path_circle.addArc(re,0,360);

        full_path_food_measure = new PathMeasure(full_path_food,false);
        full_path_friend_measure = new PathMeasure(full_path_fried, false);
        full_path_circle_measure = new PathMeasure(full_path_circle,false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.food_layout:
                context.startActivity(new Intent(context, AtyDiscoveryFood.class));
                break;
            case R.id.friend_layout:
                context.startActivity(new Intent(context, AtyDiscovery.class));
                break;
        }
    }
}
