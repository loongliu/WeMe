package space.weme.remix.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.Activity;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.PageIndicator;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class FgtActivity extends BaseFragment {

    private static final String TAG = "FgtActivity";

    // views
    private SwipeRefreshLayout mSwipeLayout;
    private RecyclerView mRecyclerView;

    private TopPageAdapter mTopAdapter;
    private Adapter mRvAdapter;

    // data
    int page = 1;

    public static FgtActivity newInstance() {
        Bundle args = new Bundle();
        FgtActivity fragment = new FgtActivity();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_activity,container,false);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fgt_activity_swipe_layout);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.fgt_activity_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        firePost();
        return rootView;
    }

    private void firePost(){
        ArrayMap<String,String> params = new ArrayMap<>(3);
        params.put("token",StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_TOP_ACTIVITY_URL, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j;
                try {
                    j = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state");
                if (!state.equals("successful")) {
                    Toast.makeText(getActivity(), j.optString("reason"), Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if (array == null) return;
                List<TopInfoWrapper> infoList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    TopInfoWrapper info = TopInfoWrapper.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mTopAdapter = new TopPageAdapter(getActivity(), infoList);
            }
        });
        params.put("page",page+"");
        OkHttpUtils.post(StrUtils.GET_ACTIVITY_INFO_URL, params, TAG, new OkHttpUtils.SimpleOkCallBack() {

            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j;
                try {
                    j = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state");
                if(!state.equals("successful")){
                    Toast.makeText(getActivity(), j.optString("reason"), Toast.LENGTH_SHORT).show();
                    return;
                }
                int returnedPage = j.optInt("pages");
                if(page < returnedPage){
                    page = returnedPage;
                }
                JSONArray array = j.optJSONArray("result");
                if(array == null) return;
                List<Activity> activityList = new ArrayList<>();
                for(int i = 0; i<array.length(); i++){
                    Activity activity = Activity.fromJSON(array.optJSONObject(i));
                    activityList.add(activity);
                }
                mRvAdapter = new Adapter(activityList);
                mRecyclerView.setAdapter(mRvAdapter);
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        static final int TYPE_TOP = 1;
        static final int TYPE_ACTIVITY = 2;
        List<Activity> mActivityList;

        public Adapter(List<Activity> list){
            mActivityList = list;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            if(viewType==TYPE_TOP){
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.top_pager, parent, false);
                ViewGroup.LayoutParams params = v.getLayoutParams();
                params.height = DimensionUtils.getDisplay().widthPixels/2;
                v.setLayoutParams(params);
                vh = new TopViewHolder(v);
            }else{
                View v = LayoutInflater.from(getActivity()).inflate(R.layout.fgt_activity_item,parent,false);
                vh = new ItemViewHolder(v);
            }
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof TopViewHolder){
                TopViewHolder top = (TopViewHolder) holder;
                top.mViewPager.setAdapter(mTopAdapter);
                top.mIndicator.setViewPager(top.mViewPager);
            }else{
                Activity activity = mActivityList.get(position-1);
                ItemViewHolder item = (ItemViewHolder) holder;
                item.mTvTitle.setText(activity.time);
                item.mAvatar.setImageURI(Uri.parse(activity.));
            }
        }

        @Override
        public int getItemCount() {
            return mActivityList==null?1:1+mActivityList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return position==0?TYPE_TOP:TYPE_ACTIVITY;
        }

        class TopViewHolder extends RecyclerView.ViewHolder{
            ViewPager mViewPager;
            PageIndicator mIndicator;
            public TopViewHolder(View itemView) {
                super(itemView);
                mViewPager = (ViewPager) itemView.findViewById(R.id.top_pager_view);
                mIndicator = (PageIndicator) itemView.findViewById(R.id.top_pager_indicator);
            }
        }
        class ItemViewHolder extends RecyclerView.ViewHolder{
            SimpleDraweeView mAvatar;
            TextView mTvTitle;
            TextView mTvCount;
            TextView mTvTime;
            TextView mTvLocation;

            public ItemViewHolder(View itemView) {
                super(itemView);
                mAvatar = (SimpleDraweeView) itemView.findViewById(R.id.fgt_activity_item_image);
                mTvTitle = (TextView) itemView.findViewById(R.id.fgt_activity_item_title);
                mTvCount = (TextView) itemView.findViewById(R.id.fgt_activity_item_count);
                mTvTime = (TextView) itemView.findViewById(R.id.fgt_activity_item_time);
                mTvLocation = (TextView) itemView.findViewById(R.id.fgt_activity_item_location);
            }
        }
    }

    static class TopPageAdapter extends PagerAdapter {
        List<TopInfoWrapper> infoList;
        Context context;

        public TopPageAdapter(Context context, List<TopInfoWrapper> list){
            this.context = context;
            infoList = list;
        }

        @Override
        public int getCount() {
            return infoList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            SimpleDraweeView image = new SimpleDraweeView(context);
            Uri uri = Uri.parse(infoList.get(position).url);
            image.setImageURI(uri);
            container.addView(image);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    static class TopInfoWrapper {
        int id;
        String url;
        static TopInfoWrapper fromJSON(JSONObject j){
            TopInfoWrapper info = new TopInfoWrapper();
            info.id = j.optInt("activityid");
            info.url = j.optString("imageurl");
            return info;
        }
    }
}
