package space.weme.remix.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;

/**
 * Created by Liujilong on 2016/1/29.
 * liujilong.me@gmail.com
 */
public class AtyImage extends BaseActivity {
    private static final String TAG = "AtyImage";
    public static final String URL_INTENT = "url";

    public static final String INTENT_JSON = "inteng_json";
    public static final String KEY_INDEX = "key_index";
    public static final String KEY_ARRAY = "key_array";



    List<String> userImageList;
    int currentIndex;

    ViewPager pager;

    ImageAdapter adapter;

    View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
            overridePendingTransition(0, 0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_image);
        String url = getIntent().getStringExtra(URL_INTENT);
        String json = getIntent().getStringExtra(INTENT_JSON);

        userImageList = new ArrayList<>();
        if(json == null){
            userImageList.add(url);
            currentIndex = 0;
        }else{
            try {
                JSONObject j = new JSONObject(json);
                JSONArray array = j.optJSONArray(KEY_ARRAY);
                currentIndex = j.optInt(KEY_INDEX);
                for(int i = 0; i<array.length(); i++){
                    userImageList.add(array.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new ImageAdapter();
        pager.setAdapter(adapter);
        pager.setCurrentItem(currentIndex);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, 0);
    }

    @Override
    protected String tag() {
        return TAG;
    }

    private class ImageAdapter extends PagerAdapter {
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimpleDraweeView view = new SimpleDraweeView(AtyImage.this);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            container.addView(view, params);
            view.setImageURI(Uri.parse(userImageList.get(position)));
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
