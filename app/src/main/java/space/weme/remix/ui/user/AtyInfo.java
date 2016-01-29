package space.weme.remix.ui.user;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/1/29.
 * liujilong.me@gmail.com
 */
public class AtyInfo extends BaseActivity {
    private static final String TAG = "AtyInfo";
    public static final String ID_INTENT = "id";

    private String mId;
    private TextView mTvVisit;
    private TextView mTvConstellation;
    private ImageView mIvGender;

    int birthFlag;
    int followFlag;

    private View[] mPagerViews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getStringExtra(ID_INTENT);
        setContentView(R.layout.aty_info);
        mTvVisit = (TextView) findViewById(R.id.aty_info_visit);
        mTvConstellation = (TextView) findViewById(R.id.aty_info_constellation);
        mIvGender = (ImageView) findViewById(R.id.aty_info_gender);
        SimpleDraweeView mDrawAvatar = (SimpleDraweeView) findViewById(R.id.aty_info_avatar);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.aty_info_tabs);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.aty_info_pager);
        mViewPager.setAdapter(new InfoAdapter());
        mTabLayout.setupWithViewPager(mViewPager);

        mDrawAvatar.setImageURI(Uri.parse(StrUtils.thumForID(mId)));


        View view = findViewById(R.id.aty_info_top);
        int width = DimensionUtils.getDisplay().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width*3/5);
        view.setLayoutParams(params);

        SimpleDraweeView mDrawBackground = (SimpleDraweeView) findViewById(R.id.aty_info_background);
        mDrawBackground.setImageURI(Uri.parse(StrUtils.backgroundForID(mId)));

        fireInfo();

        setUpPagerViews();
    }

    private void fireInfo(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("userid", mId);
        OkHttpUtils.post(StrUtils.GET_VISIT_INFO, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this, s);
                if (j == null) {
                    return;
                }
                JSONObject result = j.optJSONObject("result");
                int today = result.optInt("today");
                int total = result.optInt("total");
                String visitInfo = getResources().getString(R.string.today_visit) + " " + today + " " +
                        getResources().getString(R.string.total_visit) + " " + total + " ";
                mTvVisit.setText(visitInfo);
            }
        });
        param.clear();
        param.put("token", StrUtils.token());
        param.put("id", mId);
        OkHttpUtils.post(StrUtils.GET_PROFILE_BY_ID, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this,s);
                if(j == null){
                    return;
                }
                User user = User.fromJSON(j);
                birthFlag = j.optInt("birthflag");
                followFlag = j.optInt("followflag");
                mTvConstellation.setText(user.constellation);
                mIvGender.setImageResource(user.gender.equals("\u7537")?R.mipmap.boy:R.mipmap.girl);
            }
        });
    }

    private void setUpPagerViews(){
        mPagerViews = new View[3];
        View v1 = LayoutInflater.from(this).inflate(R.layout.aty_info_view1,null,false);
    }

    @Override
    protected String tag() {
        return TAG;
    }

    private class InfoAdapter extends PagerAdapter{
        TextView[] mTvs = new TextView[3];

        public InfoAdapter(){
            for(int i = 0; i<3; i++){
                TextView view = new TextView(AtyInfo.this);
                view.setLayoutParams(new ViewGroup.LayoutParams(100, 100));
                view.setBackgroundColor(Color.RED);
                view.setText(i+"");
                mTvs[i] = view;
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mTvs[position];
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return "Page" + position;
        }
    }


}
