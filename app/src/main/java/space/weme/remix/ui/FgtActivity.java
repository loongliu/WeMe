package space.weme.remix.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

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
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
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
            throw new RuntimeException("destroyItem has not been realised");
            //super.destroyItem(container, position, object);
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
