package space.weme.remix.ui.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.Activity;
import space.weme.remix.ui.aty.AtyActivityDetail;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/7.
 * liujilong.me@gmail.com
 */
public class FgtUserActivity extends BaseFragment {
    private static final String TAG = "FgtUserActivity";

    private int pagerPage;
    private List<Activity> activityList;
    ActivityAdapter adapter;

    String[] urls = new String[]{StrUtils.GET_PUBLISH_ACTIVITY,
                                StrUtils.GET_LIKE_ACTIVITY,
                                StrUtils.GET_REGISTER_ACTIVITY};

    public static FgtUserActivity newInstance(int page){
        FgtUserActivity f = new FgtUserActivity();
        Bundle bundle = new Bundle();
        bundle.putInt("page",page);
        f.setArguments(bundle);
        return f;
    }

    // todo load more and refresh

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.fgt_user_activity, container, false);
        pagerPage = getArguments().getInt("page");
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.fgt_user_activity_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        activityList = new ArrayList<>();
        adapter = new ActivityAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        fetchActivities(1);
        return v;
    }

    private void fetchActivities(int page){
        ArrayMap<String, String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("page", page + "");
        OkHttpUtils.post(urls[pagerPage], param, TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(),s);
                if(j == null){
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                for(int i = 0; i<array.length(); i++){
                    activityList.add(Activity.fromJSON(array.optJSONObject(i)));
                }
                adapter.setActivities(activityList);
                adapter.notifyDataSetChanged();
            }
        });
    }


    @Override
    protected String tag() {
        return TAG;
    }

    private  class ActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<Activity> activities;
        Context mContext;


        public ActivityAdapter(Context context){
            mContext = context;
        }

        public void setActivities(List<Activity> activities) {
            this.activities = activities;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.fgt_user_activity_cell,parent,false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH item = (VH) holder;
            Activity activity = activities.get(position);
            item.mTvTitle.setText(activity.title);
            item.mAvatar.setImageURI(Uri.parse(StrUtils.thumForID(activity.authorID + "")));
            String count = activity.signNumber+"/"+activity.capacity;
            item.mTvCount.setText(count);
            item.mTvTime.setText(activity.time);
            item.mTvLocation.setText(activity.location);
            item.mTvStatus.setText(activity.status);
            item.itemView.setTag(activity);
            item.itemView.setOnClickListener(listener);
        }

        @Override
        public int getItemCount() {
            return activities==null?0:activities.size();
        }
         class VH extends RecyclerView.ViewHolder{
            SimpleDraweeView mAvatar;
            TextView mTvTitle;
            TextView mTvCount;
            TextView mTvTime;
            TextView mTvLocation;
            TextView mTvStatus;
            public VH(View itemView) {
                super(itemView);
                mAvatar = (SimpleDraweeView) itemView.findViewById(R.id.fgt_activity_item_image);
                mTvTitle = (TextView) itemView.findViewById(R.id.fgt_activity_item_title);
                mTvCount = (TextView) itemView.findViewById(R.id.fgt_activity_item_count);
                mTvTime = (TextView) itemView.findViewById(R.id.fgt_activity_item_time);
                mTvLocation = (TextView) itemView.findViewById(R.id.fgt_activity_item_location);
                mTvStatus = (TextView) itemView.findViewById(R.id.fgt_activity_item_status);
            }
        }
    }

    View.OnClickListener listener = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            Activity activity = (Activity) v.getTag();
            Intent i = new Intent(getActivity(), AtyActivityDetail.class);
            i.putExtra(AtyActivityDetail.INTENT,activity.activityID);
            getActivity().startActivity(i);
        }
    };

}
