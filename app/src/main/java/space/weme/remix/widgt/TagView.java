package space.weme.remix.widgt;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import space.weme.remix.R;
import space.weme.remix.util.DimensionUtils;

/**
 * Created by Liujilong on 2016/5/17.
 * liujilong.me@gmail.com
 */
public class TagView extends ViewGroup {
    private TagAdapter mAdapter;
    private int mGapX = DimensionUtils.dp2px(4);
    private int mGapY = mGapX;



    private ArrayList<Integer> newLineIndex = new ArrayList<>();


    public TagView(Context context) {
        super(context);
    }

    public TagView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }




    public void setAdapter(TagAdapter adapter) {
        if(mAdapter!=null){
            mAdapter.unregisterDataSetObserver(dataSetObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(dataSetObserver);
        adapterInvalidated();
    }

    DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            adapterChanged();
        }

        @Override
        public void onInvalidated() {
            adapterInvalidated();
        }
    };

    // remove all child views and create new views, this can make adapter's click listener work
    private void adapterInvalidated(){
        removeAllViews();
        for(int i = 0; i<mAdapter.getCount(); i++){
            InTag tagView = (InTag) mAdapter.getView(i,null,null);
            addView(tagView);
        }
        if(mAdapter.getCanEdit()){
            EditTag editTag = mAdapter.getAddView();
            addView(editTag);
        }
    }

    // reuse current views
    private void adapterChanged(){
        EditTag editTag = null;
        if(getChildCount()>=1 && getChildAt(getChildCount()-1) instanceof EditTag){
            editTag = (EditTag) getChildAt(getChildCount()-1);
            removeViewAt(getChildCount()-1);
        }
        for(int i = 0; i<getChildCount(); i++){
            if(i<mAdapter.getCount()){
                InTag tv = (InTag) getChildAt(i);
                tv.setText((String) mAdapter.getItem(i));
            }else{
                removeViewAt(i);
            }
        }
        for(int i = getChildCount(); i<mAdapter.getCount(); i++){
            InTag tagView = (InTag) mAdapter.getView(i,null,null);
            addView(tagView);
        }
        if(mAdapter.getCanEdit()){
            if(editTag == null){
                editTag = mAdapter.getAddView();
            }
            addView(editTag);
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
        if(getChildCount() == 0) return;
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



    public static class TagAdapter extends BaseAdapter{
        private boolean mCanEdit = false;

        public void setCanEdit(boolean canEdit){
            mCanEdit = canEdit;
        }

        public boolean getCanEdit(){
            return mCanEdit;
        }
        private View.OnClickListener clickListener;
        private View.OnLongClickListener longClickListener;
        private View.OnClickListener addListener;

        private List<String> mTags = new ArrayList<>();
        private Context mContext;

        public TagAdapter(Context context){
            mContext = context;
        }

        public void setTags(List<String> tags){
            mTags = tags;
            notifyDataSetInvalidated();
        }

        public List<String> getTags(){
            return mTags;
        }

        private void checkTags(){
            if(mTags == null){
                mTags = new ArrayList<>();
            }
        }

        public void addTag(String tag){
            checkTags();
            mTags.add(tag);
            notifyDataSetChanged();
        }

        public void setTagAtPosition(int position, String tag){
            checkTags();
            if(position>=mTags.size()) throw new ArrayIndexOutOfBoundsException(position);
            mTags.set(position, tag);
            notifyDataSetChanged();
        }

        public void removeTagAtPosition(int position){
            checkTags();
            mTags.remove(position);
            notifyDataSetChanged();
        }

        public void setOnItemClickListener(View.OnClickListener clickListener){
            this.clickListener = clickListener;
        }

        public void setOnItemLongClickListener(View.OnLongClickListener longClickListener){
            this.longClickListener = longClickListener;
        }

        public void setOnAddListener(View.OnClickListener listener){
            addListener = listener;
        }

        @Override
        public int getCount() {
            return mTags==null?0:mTags.size();
        }

        @Override
        public Object getItem(int position) {
            return mTags.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public EditTag getAddView(){
            EditTag editTag = new EditTag(mContext);
            if(addListener!=null){
                editTag.setOnClickListener(addListener);
            }
            return editTag;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            InTag tagView = new InTag(mContext);
            tagView.setText(mTags.get(position));
            if(clickListener!=null) tagView.setOnClickListener(clickListener);
            if(longClickListener!=null) tagView.setOnLongClickListener(longClickListener);
            return tagView;
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


        }
    }

    public static class EditTag extends InTag{

        public EditTag(Context context) {
            super(context);
            setTextColor(Color.DKGRAY);
            background.getPaint().setColor(Color.GRAY);
            setHint(R.string.add_tag);
            setTextSize(12);
        }
    }
}
