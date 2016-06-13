package space.weme.remix.ui.user;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.UserImage;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.LogUtils;

/**
 * Created by Liujilong on 2016/2/17.
 * liujilong.me@gmail.com
 */
public class AtyImagePager extends BaseActivity {
    private static final String TAG = "AtyImagePager";
    public static final String INTENT_CONTENT = "intent_content";
    public static final String INTENT_INDEX = "intent_index";

    List<UserImage> userImageList;
    int currentIndex;

    ViewPager pager;
    View mTop;
    TextView mProgress;
    TextView mTime;

    ImageAdapter adapter;

    private static final int STATE_HIDE = 1;
    //private static final int STATE_ANIMATING = 2;
    private static final int STATE_SHOWN = 3;
    private int state = STATE_SHOWN;

    View.OnClickListener listener;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_image_pager);

        userImageList = new ArrayList<>();
        String json = getIntent().getStringExtra(INTENT_CONTENT);
        currentIndex = getIntent().getIntExtra(INTENT_INDEX,0);
        try {
            JSONArray array = new JSONArray(json);
            for(int i = 0; i<array.length(); i++){
                userImageList.add(UserImage.fromJSON(array.optJSONObject(i)));
            }
        }catch (JSONException e){
            finish();
        }

        pager = (ViewPager) findViewById(R.id.pager);
        mTop = findViewById(R.id.top_bar);
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mProgress = (TextView) findViewById(R.id.progress);
        mTime = (TextView) findViewById(R.id.time);

        adapter = new ImageAdapter();
        pager.setAdapter(adapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                LogUtils.d(TAG, "1");
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtils.d(TAG, "state" + state);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    int item = pager.getCurrentItem();
                    configBar(item);
                }
            }
        });

        configBar(currentIndex);
        pager.setCurrentItem(currentIndex);
    }


    private void configBar(int position){

        String progress = (position+1)+" of "+userImageList.size();
        mProgress.setText(progress);
    }


    @Override
    protected String tag() {
        return TAG;
    }

    private class ImageAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimpleDraweeView view = new SimpleDraweeView(AtyImagePager.this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            view.setImageURI(Uri.parse(userImageList.get(position).image));
            view.setOnClickListener(listener);
            view.getHierarchy().setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return userImageList==null?0:userImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

    }
}
