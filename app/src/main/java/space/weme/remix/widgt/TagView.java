package space.weme.remix.widgt;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

import space.weme.remix.APP;
import space.weme.remix.R;
import space.weme.remix.util.DimensionUtils;

/**
 * Created by Liujilong on 2016/5/17.
 * liujilong.me@gmail.com
 */
public class TagView extends ViewGroup {
    private String[] mTags;
    private int mGapX = DimensionUtils.dp2px(4);
    private int mGapY = mGapX;

    private ArrayList<Integer> newLineIndex = new ArrayList<>();


    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    public void setTags(String[] tags){
        mTags = tags;
        for(int i = 0; i<getChildCount(); i++){
            if(i<tags.length){
                InTag tv = (InTag) getChildAt(i);
                tv.setText(mTags[i]);
            }else{
                removeViewAt(i);
            }
        }
        for(int i = getChildCount(); i<tags.length; i++){
            InTag tagView = new InTag(getContext());
            tagView.setText(mTags[i]);
            addView(tagView);
        }
    }

    public void setGapX(int px){
        mGapX = px;
    }

    public void setGapY(int px){
        mGapY = px;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int height,width = 0;
        int screenWidth = DimensionUtils.getDisplay().widthPixels;
        switch (widthMode){
            case MeasureSpec.UNSPECIFIED:
                width = screenWidth;
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(screenWidth,widthSize);
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
        }

        height = getHeightForWidth(width);
        switch (heightMode){
            case MeasureSpec.AT_MOST:
                height = Math.min(height,heightSize);
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
        }
        setMeasuredDimension(width,height);
    }

    private int getHeightForWidth(int parentWidth){
        newLineIndex.clear();

        int currentX;
        int currentY;
        int widthSpec = MeasureSpec.makeMeasureSpec(parentWidth,MeasureSpec.AT_MOST);// TODO: 2016/5/18 margin
        int heightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);

        // measure first child
        if(getChildCount()==0) return 0; // todo no tag condition

        View child = getChildAt(0);
        child.measure(widthSpec,heightSpec);
        currentX = child.getMeasuredWidth();
        currentY = child.getMeasuredHeight();

        for(int i = 1; i<getChildCount(); i++){
            child = getChildAt(i);
            child.measure(widthSpec,heightSpec);
            int height = child.getMeasuredHeight();
            int width = child.getMeasuredWidth();
            if(width + currentX + mGapX < parentWidth){
                currentX = currentX + width + mGapX;
            }else{
                newLineIndex.add(i);
                currentX = width;
                currentY += (height+mGapY);
            }
        }
        return currentY;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(getChildCount() == 0) return; // TODO: 2016/5/18 no tag condition
        int currentX=-mGapX , currentY=0, nextY = 0;
        int index = 0;
        for(int i = 0; i<getChildCount(); i++){
            View child = getChildAt(i);
            if(index<newLineIndex.size() && i == newLineIndex.get(index)){
                index++;
                child.layout(0,nextY,child.getMeasuredWidth(),nextY+child.getMeasuredHeight());
                currentX = child.getMeasuredWidth();
                currentY = nextY;
                nextY = nextY + child.getMeasuredHeight()+mGapY;
            }else{
                child.layout(mGapX + currentX,currentY,mGapX + currentX+child.getMeasuredWidth(),currentY+child.getMeasuredHeight());
                currentX += (child.getMeasuredWidth() + mGapX);
                nextY = currentY + child.getMeasuredHeight() + mGapY;
            }
        }
    }


    public static class InTag extends TextView{
        final static int[] TAG_BACK_COLORS = new int[]{0xffcac9e2, 0xffcfead8,0xffd3dee3,0xfff7b0a4,0xfffeeebf,0xffb5b6b7, 0xffb6c0de};
        final static int[] TAG_FRONT_COLORS = new int[]{0xff64638d,0xff13602d, 0xff35769c,0xffc4503c,0xffc3a245, 0xff5a6b80, 0xff6478b8};

        static Random r = new Random(System.nanoTime());

        RoundRectShape rect = new RoundRectShape(
                new float[]{30, 30, 30,30, 30,30, 30,30},
                null,
                null);
        ShapeDrawable background = new ShapeDrawable(rect);



        public InTag(Context context) {
            this(context, null);
        }



        public InTag(Context context,  AttributeSet attrs){
            super(context, attrs);
            int index = r.nextInt(TAG_BACK_COLORS.length);
            setTextColor(TAG_FRONT_COLORS[index]);
            background.getPaint().setColor(TAG_BACK_COLORS[index]);
            setGravity(Gravity.CENTER);
            setTextSize(14);
            int dp8 = DimensionUtils.dp2px(8);
            setPadding(dp8, dp8, dp8, dp8);
            setBackgroundDrawable(background);
            setEnabled(true);



            setOnClickListener(clickListener);

            setOnLongClickListener(longClickListener);

            setOnTouchListener(touchListener);
        }

        View.OnClickListener clickListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences sp = APP.context().getSharedPreferences("Info3",Context.MODE_PRIVATE);
                if(sp.getBoolean("shown",false)) return;
                new WDialog.Builder(getContext()).setMessage(R.string.longclick).hideNegative(true).show();
                sp.edit().putBoolean("shown",true).apply();
            }
        };

        View.OnLongClickListener longClickListener = new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                View vv = LayoutInflater.from(getContext()).inflate(R.layout.tag_view_edit_or_delete,null);
                final WDialog dialog = new WDialog.Builder(getContext()).setCustomView(vv).hideButtons(true).show();
                vv.findViewById(R.id.delete_tag).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        new WDialog.Builder(getContext()).setMessage("确定要删除标签：" + getText())
                                .setPositive(R.string.ok, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        // TODO: 2016/5/19 delete
                                    }
                        }).show();
                    }
                });
                vv.findViewById(R.id.edit_tag).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        View vvv = LayoutInflater.from(getContext()).inflate(R.layout.tag_view_edit,null);
                        new WDialog.Builder(getContext()).setCustomView(vvv).show();
                        EditText et = (EditText) vvv.findViewById(R.id.edit_tag_edit_text);
                        et.setText(getText());
                        // TODO: 2016/5/19 new Dialog
                    }
                });
                return true;
            }
        };

         View.OnTouchListener touchListener = new View.OnTouchListener(){
             @Override
             public boolean onTouch(View v, MotionEvent event) {
//                 SharedPreferences sp = APP.context().getSharedPreferences("Info",Context.MODE_PRIVATE);
//                 if(sp.getBoolean("shown",false)) return false;
//                 new WDialog.Builder(getContext()).setTitle(R.string.longclick).setMessage(R.string.longclick).show();
//                 sp.edit().putBoolean("shown",true).apply();
                 return false;
             }

        };
    }
}
