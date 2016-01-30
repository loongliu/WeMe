package space.weme.remix.ui.user;

import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/1/29.
 * liujilong.me@gmail.com
 */
public class AtyInfo extends SwipeActivity {
    private static final String TAG = "AtyInfo";
    public static final String ID_INTENT = "id";

    private String mId;
    private User mUser;

    private TextView mTvVisit;
    private TextView mTvConstellation;
    private ImageView mIvGender;

    int birthFlag;
    int followFlag;

    private View[] mPagerViews;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getStringExtra(ID_INTENT);
        setContentView(R.layout.aty_info);

        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);



        mTvVisit = (TextView) findViewById(R.id.aty_info_visit);
        mTvConstellation = (TextView) findViewById(R.id.aty_info_constellation);
        mIvGender = (ImageView) findViewById(R.id.aty_info_gender);
        SimpleDraweeView mDrawAvatar = (SimpleDraweeView) findViewById(R.id.aty_info_avatar);
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.aty_info_tabs);
        mViewPager = (ViewPager) findViewById(R.id.aty_info_pager);
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
                mUser = user;
                birthFlag = j.optInt("birthflag");
                followFlag = j.optInt("followflag");
                mTvConstellation.setText(user.constellation);
                mIvGender.setImageResource(user.gender.equals("\u7537")?R.mipmap.boy:R.mipmap.girl);
                configView1(user);
            }
        });
    }

    private void setUpPagerViews(){
        mPagerViews = new View[3];
        View v0 = LayoutInflater.from(this).inflate(R.layout.aty_info_view1,mViewPager,false);
        View v1 = LayoutInflater.from(this).inflate(R.layout.aty_info_view2,mViewPager,false);
        View v2 = LayoutInflater.from(this).inflate(R.layout.aty_info_view3,mViewPager,false);
        mPagerViews[0] = v0;
        mPagerViews[1] = v1;
        mPagerViews[2] = v2;
    }

    private void configView1(User user){
        TextView tvName = (TextView) mPagerViews[0].findViewById(R.id.aty_info_name);
        tvName.setText(user.name);
        TextView tvBirth = (TextView) mPagerViews[0].findViewById(R.id.aty_info_birth);
        if(birthFlag == -1){
            tvBirth.setText(R.string.larger_than_you);
        }else if(birthFlag == 0){
            tvBirth.setText(R.string.the_same_birth);
        }else if (birthFlag == 1){
            tvBirth.setText(R.string.smaller_than_you);
        }
        TextView tvSchool = (TextView) mPagerViews[0].findViewById(R.id.aty_info_school);
        tvSchool.setText(user.school);
        TextView tvEducation = (TextView) mPagerViews[0].findViewById(R.id.aty_info_education);
        tvEducation.setText(user.enrollment);
        TextView tvMajor = (TextView) mPagerViews[0].findViewById(R.id.aty_info_major);
        tvMajor.setText(user.department);

        TextView tvHome = (TextView) mPagerViews[0].findViewById(R.id.aty_info_home);
        tvHome.setText(user.hometown);

        TextView tvQQ = (TextView) mPagerViews[0].findViewById(R.id.aty_info_qq);
        tvQQ.setText(user.qq);

        TextView tvWeChat = (TextView) mPagerViews[0].findViewById(R.id.aty_info_we_chat);
        tvWeChat.setText(user.wechat);

        final Button btnFollow = (Button) mPagerViews[0].findViewById(R.id.aty_info_follow_btn);
        if(mId.equals(StrUtils.id())){
            btnFollow.setVisibility(View.GONE);
        }else{
            boolean isHe = user.gender.equals("\u7537");
            switch (followFlag){
                case 1:
                    btnFollow.setText(isHe?R.string.has_follow_he:R.string.has_follow_she);
                    break;
                case 2:
                    btnFollow.setText(isHe?R.string.he_has_follow_you:R.string.she_has_follow_you);
                    break;
                case 3:
                    btnFollow.setText(R.string.has_follow_each_other);
                    break;
                case 4:
                    btnFollow.setText(R.string.add_follow);
            }
            btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(followFlag == 1 || followFlag==3){
                        unfollow();
                    }else{
                        follow();
                    }
                }
            });
        }

    }

    private void unfollow(){
        LogUtils.i(TAG, mUser.name);
    }

    private void follow(){

    }

    @Override
    protected String tag() {
        return TAG;
    }

    private class InfoAdapter extends PagerAdapter{


        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = mPagerViews[position];
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
            String[] titles = getResources().getStringArray(R.array.aty_info_tabs);
            return titles[position];
        }
    }


}
