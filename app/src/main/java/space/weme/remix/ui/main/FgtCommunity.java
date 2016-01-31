package space.weme.remix.ui.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import space.weme.remix.R;
import space.weme.remix.model.Topic;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.ui.community.AtyPost;
import space.weme.remix.ui.community.AtyTopic;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.PageIndicator;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class FgtCommunity extends BaseFragment {
    private static final String TAG = "FgtCommunity";

    SwipeRefreshLayout mSwipeLayout;
    GridLayout mGridLayout;
    ViewPager mVpTop;
    PageIndicator mIndicator;
    TopAdapter mTopAdapter;

    View.OnClickListener mClickListener;

    boolean isRefreshing = false;

    public static FgtCommunity newInstance() {
        Bundle args = new Bundle();
        FgtCommunity fragment = new FgtCommunity();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_community,container,false);
        mGridLayout = (GridLayout) rootView.findViewById(R.id.fgt_community_grid_layout);
        mVpTop = (ViewPager) rootView.findViewById(R.id.top_pager_view);
        mIndicator = (PageIndicator) rootView.findViewById(R.id.top_pager_indicator);
        mSwipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.fgt_community_swipe);
        mSwipeLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!isRefreshing){
                    fireTopics();
                }
            }
        });
        View v = rootView.findViewById(R.id.top_container);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = DimensionUtils.getDisplay().widthPixels/2;
        v.setLayoutParams(params);

        mTopAdapter = new TopAdapter(getActivity());
        mTopAdapter.setListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int id = (int) v.getTag();
                Intent i = new Intent(getActivity(), AtyPost.class);
                i.putExtra(AtyPost.POST_INTENT,id+"");
                i.putExtra(AtyPost.THEME_INTENT,"");
                startActivity(i);
            }
        });
        mVpTop.setAdapter(mTopAdapter);
        firePost();

        mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), AtyTopic.class);
                i.putExtra(AtyTopic.TOPIC_ID,(String)v.getTag());
                startActivity(i);
            }
        };

        return rootView;
    }


    private void firePost(){
        Map<String,String> params = new ArrayMap<>(1);
        params.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.TOP_BROAD_URL, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(), s);
                if (j == null) {
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if (array == null) return;
                List<TopInfo> infoList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    TopInfo info = TopInfo.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
                mTopAdapter.setInfoList(infoList);
                mTopAdapter.notifyDataSetChanged();
                mIndicator.setViewPager(mVpTop);
            }
        });

        fireTopics();


    }

    private void fireTopics(){
        Map<String,String> params = new ArrayMap<>(1);
        params.put("token", StrUtils.token());
        isRefreshing = true;
        OkHttpUtils.post(StrUtils.GET_TOPIC_LIST, params, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                isRefreshing = false;
                mSwipeLayout.setRefreshing(false);
            }

            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                isRefreshing = false;
                mSwipeLayout.setRefreshing(false);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j==null) {
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if (array == null) return;
                List<Topic> topicList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    topicList.add(Topic.fromJson(array.optJSONObject(i)));
                }
                addGridViews(mGridLayout, topicList);
            }
        });
    }

    private void addGridViews(GridLayout grid, List<Topic> lists){
        for(int i = 0; i<lists.size();i++){
            Topic topic = lists.get(i);
            RelativeLayout convertView = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(
                    R.layout.fgt_community_topic_cell, grid, false);
            TextView tvTheme = (TextView) convertView.findViewById(R.id.community_topic_cell_theme);
            TextView tvNote = (TextView) convertView.findViewById(R.id.community_topic_cell_note);
            TextView tvNumber = (TextView) convertView.findViewById(R.id.number);
            SimpleDraweeView ivImage = (SimpleDraweeView) convertView.findViewById(R.id.community_topic_cell_image);
            tvTheme.setText(topic.theme);
            tvNote.setText(topic.note);
            String text = topic.number+"";
            tvNumber.setText(text);
            ivImage.setImageURI(Uri.parse(topic.imageurl));
            GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i/3),GridLayout.spec(i % 3));
            params.width=(grid.getWidth()/grid.getColumnCount()) -params.rightMargin - params.leftMargin;
            RelativeLayout.LayoutParams ivParams = (RelativeLayout.LayoutParams) ivImage.getLayoutParams();
            ivParams.width = params.width;
            ivParams.height = ivParams.width/2;
            ivImage.setLayoutParams(ivParams);
            convertView.setTag(topic.id);
            convertView.setOnClickListener(mClickListener);
            grid.addView(convertView,params);
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }

    private static class TopInfo{
        public int id;
        public String url;
        public static TopInfo fromJSON(JSONObject j){
            TopInfo info = new TopInfo();
            info.id = j.optInt("postid");
            info.url = j.optString("imageurl");
            return info;
        }
    }

    private static class TopAdapter extends PagerAdapter {
        List<TopInfo> infoList;
        Context context;
        View.OnClickListener mListener;

        public TopAdapter(Context context){
            this.context = context;
        }

        public void setInfoList(List<TopInfo> infoList) {
            this.infoList = infoList;
        }
        public void setListener(View.OnClickListener listener){
            mListener = listener;
        }

        @Override
        public int getCount() {
            return infoList==null?0:infoList.size();
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
            image.setTag(infoList.get(position).id);
            image.setOnClickListener(mListener);
            container.addView(image);
            return image;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
