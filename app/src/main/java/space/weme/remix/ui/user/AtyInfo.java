package space.weme.remix.ui.user;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import space.weme.remix.R;
import space.weme.remix.model.TimeLine;
import space.weme.remix.model.User;
import space.weme.remix.model.UserImage;
import space.weme.remix.ui.AtyImage;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.community.AtyPost;
import space.weme.remix.ui.intro.AtyEditInfo;
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
    private static final int REQUEST_IMAGE = 0xef;

    private String mId;
    private User mUser;

    private TextView mTvVisit;
    private TextView mTvConstellation;
    private ImageView mIvGender;
    private View[] mPagerViews;
    private ViewPager mViewPager;
    private SimpleDraweeView mDrawAvatar;
    LinearLayout mWholeLayout;
    SimpleDraweeView mDrawBackground;

    private SwipeRefreshLayout swipe_2;
    private SwipeRefreshLayout swipe_3;

    int birthFlag;
    int followFlag;


    boolean isLoading_2 = false;
    boolean canLoadMore_2 = true;
    int page_2;

    boolean isLoading_3 = false;
    boolean canLoadMore_3 = true;
    int page_3;
    final int GRID_COUNT = 3;

    List<TimeLine> timeLineList;
    TimeLineAdapter mTimeLineAdapter;

    List<UserImage> userImageList;
    UserImageAdapter mUserImageAdapter;

    WindowListener mWindowListener;
    UserImageListener mUserImageListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getStringExtra(ID_INTENT);
        setContentView(R.layout.aty_info);


        mWholeLayout = (LinearLayout) findViewById(R.id.aty_info_layout);


        mTvVisit = (TextView) findViewById(R.id.aty_info_visit);
        mTvConstellation = (TextView) findViewById(R.id.aty_info_constellation);
        mIvGender = (ImageView) findViewById(R.id.aty_info_gender);
        mDrawAvatar = (SimpleDraweeView) findViewById(R.id.aty_info_avatar);
        final TabLayout mTabLayout = (TabLayout) findViewById(R.id.aty_info_tabs);
        mViewPager = (ViewPager) findViewById(R.id.aty_info_pager);
        mViewPager.setAdapter(new InfoAdapter());
        mTabLayout.setupWithViewPager(mViewPager);
        mTabLayout.setSelectedTabIndicatorHeight(0);

        mDrawAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AtyInfo.this, AtyImage.class);
                i.putExtra(AtyImage.URL_INTENT,StrUtils.avatarForID(mId));
                startActivity(i);
                overridePendingTransition(0, 0);
            }
        });


        View view = findViewById(R.id.aty_info_top);
        int width = DimensionUtils.getDisplay().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width*3/5);
        view.setLayoutParams(params);

        mDrawBackground = (SimpleDraweeView) findViewById(R.id.aty_info_background);
        mDrawBackground.setImageURI(Uri.parse(StrUtils.backgroundForID(mId)));
        GenericDraweeHierarchy hierarchy = mDrawBackground.getHierarchy();
        hierarchy.setPlaceholderImage(R.mipmap.info_default);

        mWindowListener = new WindowListener();
        if(StrUtils.id().equals(mId)) {
            mDrawBackground.setOnClickListener(mWindowListener);
        }

        findViewById(R.id.aty_info_more).setOnClickListener(mWindowListener);



        mUserImageListener = new UserImageListener();

        setUpPagerViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fireInfo();
        configView();
        mDrawAvatar.setImageURI(Uri.parse(StrUtils.thumForID(mId)));
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

    private void configView(){
        configView1();

        configView2();

        configView3();
    }

    private void configView1(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("id", mId);
        OkHttpUtils.post(StrUtils.GET_PROFILE_BY_ID, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this, s);
                if (j == null) {
                    return;
                }
                User user = User.fromJSON(j);
                mUser = user;
                birthFlag = j.optInt("birthflag");
                followFlag = j.optInt("followflag");
                mTvConstellation.setText(user.constellation);
                boolean male = getResources().getString(R.string.male).equals(mUser.gender);
                mIvGender.setImageResource(male? R.mipmap.boy : R.mipmap.girl);
                TextView tvName = (TextView) mPagerViews[0].findViewById(R.id.aty_info_name);
                tvName.setText(user.name);
                TextView tvBirth = (TextView) mPagerViews[0].findViewById(R.id.aty_info_birth);
                tvBirth.setText(user.birthday);

                TextView tvSchool = (TextView) mPagerViews[0].findViewById(R.id.aty_info_school);
                tvSchool.setText(user.school);
                TextView tvEducation = (TextView) mPagerViews[0].findViewById(R.id.aty_info_education);
                tvEducation.setText(user.degree);
                TextView tvMajor = (TextView) mPagerViews[0].findViewById(R.id.aty_info_major);
                tvMajor.setText(user.department);

                TextView tvHome = (TextView) mPagerViews[0].findViewById(R.id.aty_info_home);
                tvHome.setText(user.hometown);

                TextView tvQQ = (TextView) mPagerViews[0].findViewById(R.id.aty_info_qq);
                tvQQ.setText(user.qq);

                TextView tvWeChat = (TextView) mPagerViews[0].findViewById(R.id.aty_info_we_chat);
                tvWeChat.setText(user.wechat);

                final Button btnFollow = (Button) mPagerViews[0].findViewById(R.id.aty_info_follow_btn);
                if (mId.equals(StrUtils.id())) {
                    btnFollow.setVisibility(View.GONE);
                } else {
                    btnFollow.setVisibility(View.VISIBLE);
                    boolean isHe = user.gender.equals("\u7537");
                    switch (followFlag) {
                        case 1:
                            btnFollow.setText(isHe ? R.string.has_follow_he : R.string.has_follow_she);
                            break;
                        case 2:
                            btnFollow.setText(isHe ? R.string.he_has_follow_you : R.string.she_has_follow_you);
                            break;
                        case 3:
                            btnFollow.setText(R.string.has_follow_each_other);
                            break;
                        case 0:
                            btnFollow.setText(R.string.add_follow);
                    }
                    btnFollow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (followFlag == 1 || followFlag == 3) {
                                unfollow();
                            } else {
                                follow();
                            }
                        }
                    });
                }
            }
        });

    }

    private void configView2(){
        swipe_2 = (SwipeRefreshLayout) mPagerViews[1];
        RecyclerView recyclerView = (RecyclerView) swipe_2.findViewById(R.id.aty_info_view2_recycler);
        mTimeLineAdapter = new TimeLineAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(AtyInfo.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mTimeLineAdapter);
        timeLineList = new ArrayList<>();
        mTimeLineAdapter.setTimeLineList(timeLineList);
        getTimeLine(1);
        swipe_2.setColorSchemeResources(R.color.colorPrimary);
        swipe_2.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading_2) {
                    getTimeLine(1);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading_2 && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + 2) && canLoadMore_2) {
                    Log.i(TAG, "scroll to end  load page " + (page_2 + 1));
                    getTimeLine(page_2 + 1);
                }
            }
        });
    }

    private void getTimeLine(final int page){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("userid", mId);
        param.put("page", String.format("%d", page));
        //LogUtils.d(TAG, param.toString());
        isLoading_2 = true;
        OkHttpUtils.post(StrUtils.GET_TIME_LINE_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                isLoading_2 = false;
                swipe_2.setRefreshing(false);
            }

            @Override
            public void onResponse(String s) {
                isLoading_2 = false;
                swipe_2.setRefreshing(false);
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this, s);
                if (j == null) {
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if (page == 1) {
                    timeLineList.clear();
                }
                page_2 = page;
                int size = array.length();
                int preCount = timeLineList.size();
                canLoadMore_2 = size != 0;
                for (int i = 0; i < array.length(); i++) {
                    timeLineList.add(TimeLine.fromJSON(array.optJSONObject(i)));
                }
                if (preCount == 0) {
                    mTimeLineAdapter.notifyDataSetChanged();
                } else {
                    mTimeLineAdapter.notifyItemRangeInserted(preCount, size);
                }
            }
        });
    }


    private void configView3(){
        swipe_3 = (SwipeRefreshLayout) mPagerViews[2];
        RecyclerView recyclerView = (RecyclerView) swipe_3.findViewById(R.id.aty_info_view3_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(AtyInfo.this, GRID_COUNT));
        recyclerView.setHasFixedSize(true);
        mUserImageAdapter = new UserImageAdapter();
        userImageList = new ArrayList<>();
        mUserImageAdapter.setUserImageList(userImageList);
        recyclerView.setAdapter(mUserImageAdapter);
        swipe_3.setColorSchemeResources(R.color.colorPrimary);
        swipe_3.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoading_3) {
                    getUserImages(1);
                }
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading_3 && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + 2) && canLoadMore_3) {
                    Log.i(TAG, "scroll to end  load page " + (page_3 + 1));
                    getUserImages(page_3 + 1);
                }
            }
        });
        getUserImages(1);
    }

    private void getUserImages(final int page){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("userid",mId);
        param.put("page", String.format("%d",page));
        isLoading_3 = true;
        OkHttpUtils.post(StrUtils.GET_USER_IMAGES_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                isLoading_3= false;
            }

            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                isLoading_3 = false;
                swipe_3.setRefreshing(false);
                JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this, s);
                if (j == null) {
                    return;
                }
                if (page == 1) {
                    userImageList.clear();
                }
                page_3 = page;
                JSONArray result = j.optJSONArray("result");
                int size = result.length();
                int preCount = userImageList.size();
                canLoadMore_3 = size!=0;
                for (int i = 0; i < result.length(); i++) {
                    userImageList.add(UserImage.fromJSON(result.optJSONObject(i)));
                }
                if (preCount == 0) {
                    mUserImageAdapter.notifyDataSetChanged();
                } else {
                    mUserImageAdapter.notifyItemRangeInserted(preCount, size);
                }
            }
        });
    }

    private void unfollow(){
        // todo dialog theme
        new AlertDialog.Builder(AtyInfo.this)
                .setMessage(getString(R.string.sure_to_unfollow))
                .setPositiveButton(R.string.unfollow, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ArrayMap<String,String> param = new ArrayMap<>();
                        param.put("token",StrUtils.token());
                        param.put("id", mUser.ID+"");
                        OkHttpUtils.post(StrUtils.UNFOLLOW_USER, param, TAG, new OkHttpUtils.SimpleOkCallBack(){
                            @Override
                            public void onFailure(IOException e) {
                                Toast.makeText(AtyInfo.this, R.string.unfollow_fail, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onResponse(String s) {
                                JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this, s);
                                if (j == null) {
                                    Toast.makeText(AtyInfo.this, R.string.unfollow_fail, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(AtyInfo.this, R.string.unfollow_success, Toast.LENGTH_SHORT).show();
                                    configView1();
                                }
                            }
                        });
                    }
                })
                .setNegativeButton(R.string.not_unfollow, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void follow(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("id", mUser.ID+"");
        OkHttpUtils.post(StrUtils.FOLLOW_USER, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                Toast.makeText(AtyInfo.this, R.string.follow_fail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this, s);
                if (j == null) {
                    Toast.makeText(AtyInfo.this, R.string.follow_fail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AtyInfo.this, R.string.follow_success, Toast.LENGTH_SHORT).show();
                    configView1();
                }
            }
        });
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

    private class TimeLineAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<TimeLine> timeLineList;

        public void setTimeLineList(List<TimeLine> timeLineList) {
            this.timeLineList = timeLineList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AtyInfo.this).inflate(R.layout.aty_info_view2_cell,parent,false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final TimeLine timeLine = timeLineList.get(position);
            VH vh = (VH) holder;
            vh.tvTimeStamp.setText(StrUtils.timeTransfer(timeLine.timestamp));
            vh.tvTopic.setText(timeLine.topic);
            vh.mDrawImage.setImageURI(Uri.parse(timeLine.image));
            vh.tvTitle.setText(timeLine.title);
            vh.tvContent.setText(timeLine.body);
            vh.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(AtyInfo.this, AtyPost.class);
                    i.putExtra(AtyPost.POST_INTENT,timeLine.postId);
                    i.putExtra(AtyPost.THEME_INTENT,timeLine.topic);
                    startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return timeLineList==null?0:timeLineList.size();
        }

        private class VH extends RecyclerView.ViewHolder{
            TextView tvTimeStamp;
            TextView tvTopic;
            SimpleDraweeView mDrawImage;
            TextView tvTitle;
            TextView tvContent;
            public VH(View itemView) {
                super(itemView);
                tvTimeStamp = (TextView) itemView.findViewById(R.id.aty_info_view2_cell_timestamp);
                tvTopic = (TextView) itemView.findViewById(R.id.aty_info_view2_cell_topic);
                mDrawImage = (SimpleDraweeView) itemView.findViewById(R.id.aty_info_view2_cell_image);
                tvTitle  = (TextView) itemView.findViewById(R.id.aty_info_view2_cell_title);
                tvContent = (TextView) itemView.findViewById(R.id.aty_info_view2_cell_content);

            }
        }
    }

    private class UserImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<UserImage> userImageList;

        public void setUserImageList(List<UserImage> userImageList) {
            this.userImageList = userImageList;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(AtyInfo.this).inflate(R.layout.aty_info_view3_cell,parent,false);
            int size = DimensionUtils.getDisplay().widthPixels / GRID_COUNT;
            ViewGroup.LayoutParams params = v.getLayoutParams();
            params.height = size;
            params.width = size;
            v.setLayoutParams(params);
            v.setOnClickListener(mUserImageListener);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.draw.setImageURI(Uri.parse(userImageList.get(position).thumbnail));
            vh.itemView.setTag(position);
        }

        @Override
        public int getItemCount() {
            return userImageList==null?0:userImageList.size();
        }

        private class VH extends RecyclerView.ViewHolder{
            SimpleDraweeView draw;
            public VH(View itemView) {
                super(itemView);
                draw = (SimpleDraweeView) itemView.findViewById(R.id.aty_info_view3_cell_image);
            }
        }
    }

    private class WindowListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            View content;
            View.OnClickListener listener;
            final Dialog dialog = new Dialog(AtyInfo.this,R.style.DialogSlideAnim);
            if(v.getId()==R.id.aty_info_background){
                content = LayoutInflater.from(AtyInfo.this).inflate(R.layout.aty_info_option1,mWholeLayout,false);
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(v.getId() == R.id.aty_info_option_change_background){
                            changeBackground();
                        }
                        dialog.dismiss();
                    }
                };
                content.findViewById(R.id.aty_info_option_cancel).setOnClickListener(listener);
                content.findViewById(R.id.aty_info_option_change_background).setOnClickListener(listener);
                dialog.setContentView(content);
            }else if(v.getId()==R.id.aty_info_more){
                if(Integer.parseInt(StrUtils.id())==mUser.ID){
                    content = LayoutInflater.from(AtyInfo.this).inflate(R.layout.aty_info_option3,mWholeLayout,false);
                    listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(v.getId() == R.id.aty_info_option_change_background){
                                changeBackground();
                            }else if(v.getId() == R.id.aty_info_option_edit_info){
                                editMyInfo();
                            }
                            dialog.dismiss();
                        }
                    };
                    content.findViewById(R.id.aty_info_option_cancel).setOnClickListener(listener);
                    content.findViewById(R.id.aty_info_option_change_background).setOnClickListener(listener);
                    content.findViewById(R.id.aty_info_option_edit_info).setOnClickListener(listener);
                    dialog.setContentView(content);
                }else{
                    content = LayoutInflater.from(AtyInfo.this).inflate(R.layout.aty_info_option2,mWholeLayout,false);
                    listener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(v.getId() == R.id.aty_info_option_message){
                                sendMessage();
                            }
                            dialog.dismiss();
                        }
                    };
                    content.findViewById(R.id.aty_info_option_cancel).setOnClickListener(listener);
                    content.findViewById(R.id.aty_info_option_message).setOnClickListener(listener);
                    dialog.setContentView(content);
                }
            }
            WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
            wmlp.gravity = Gravity.BOTTOM | Gravity.START;
            wmlp.x = 0;   //x position
            wmlp.y = 0;   //y position
            wmlp.width = DimensionUtils.getDisplay().widthPixels;
            dialog.show();
        }
    }

    private void changeBackground(){
        Intent intent = new Intent(AtyInfo.this, MultiImageSelectorActivity.class);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void sendMessage(){
        Intent i = new Intent(AtyInfo.this, AtyMessageReply.class);
        i.putExtra(AtyMessageReply.INTENT_ID,mUser.ID+"");
        startActivity(i);
    }



    private void editMyInfo(){
        LogUtils.i(TAG, "edit my info");
        Intent i = new Intent(AtyInfo.this, AtyEditInfo.class);
        i.putExtra(AtyEditInfo.INTENT_EDIT,true);
        i.putExtra(AtyEditInfo.INTENT_INFO,mUser.toJSONString());
        startActivity(i);
    }

    private class UserImageListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            JSONArray array = new JSONArray();
            for(UserImage image : userImageList){
                array.put(image.toJSON());
            }
            int index = (int) v.getTag();
            Intent i = new Intent(AtyInfo.this,AtyImagePager.class);
            i.putExtra(AtyImagePager.INTENT_CONTENT,array.toString());
            i.putExtra(AtyImagePager.INTENT_INDEX,index);
            startActivity(i);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_IMAGE){
            List<String> paths=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            String mAvatarPath = paths.get(0);
            mDrawBackground.setImageURI(Uri.parse("file://" + mAvatarPath));
            ArrayMap<String,String> map = new ArrayMap<>();
            map.put("token", StrUtils.token());
            map.put("type","-1");
            map.put("number","1");
            OkHttpUtils.uploadFile(StrUtils.UPLOAD_AVATAR_URL,map,mAvatarPath,StrUtils.MEDIA_TYPE_IMG,TAG,new OkHttpUtils.SimpleOkCallBack(){
                @Override
                public void onResponse(String s) {
                    LogUtils.d(TAG,s);
                    JSONObject j = OkHttpUtils.parseJSON(AtyInfo.this,s);
                    if(j == null){
                        return;
                    }
                    ImagePipeline imagePipeline = Fresco.getImagePipeline();
                    imagePipeline.evictFromCache(Uri.parse(StrUtils.backgroundForID(mId)));
                }
            });
        }
    }

}
