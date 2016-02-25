package space.weme.remix.ui.aty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import space.weme.remix.R;
import space.weme.remix.model.Activity;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

public class AtySearchActivity extends SwipeActivity {

    private static final String TAG="AtySearchActivity";
    private List<Activity> list=new ArrayList<>();
    private EditText editSearch;
    private TextView txtCancel;
    private RecyclerView recyclerSearch;
    private searchAdapter adapter=new searchAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_search_activity);
        recyclerSearch= (RecyclerView) findViewById(R.id.aty_search_activity);
        recyclerSearch.setLayoutManager(new LinearLayoutManager(AtySearchActivity.this));
        txtCancel= (TextView) findViewById(R.id.txt_search_cancel);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        editSearch= (EditText) findViewById(R.id.edit_search);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str=s.toString();
                if (str!=null&&!str.equals(""))
                   getSearch(str);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    void getSearch(String text){
        Map<String,String> map=new android.support.v4.util.ArrayMap<>();
        map.put("token", StrUtils.token());
        map.put("text", text);
        OkHttpUtils.post(StrUtils.SEARCH_ACTIVITY, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(AtySearchActivity.this, s);
                String state = j.optString("state");
                if (state.equals("successful")) {

                    JSONArray array = j.optJSONArray("result");
                    if (array == null) return;
                    list.clear();
                    for (int i = 0; i < array.length(); i++) {
                        Activity act = Activity.fromJSON(array.optJSONObject(i));
                        list.add(act);
                    }
                    recyclerSearch.setAdapter(adapter);
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();

                }
            }
        });

    }

    class searchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<Activity> mList;

        public void setList(List<Activity> list){mList=list;}

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            RecyclerView.ViewHolder vh;
            View v= LayoutInflater.from(AtySearchActivity.this).inflate(R.layout.fgt_activity_item,parent,false);
            vh=new VH(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            Activity activity = mList.get(position);
            VH item = (VH) holder;
            item.mTvTitle.setText(activity.title);
            item.mAvatar.setImageURI(Uri.parse(StrUtils.thumForID(activity.authorID + "")));
            String count = activity.signNumber+"/"+activity.capacity;
            item.mTvCount.setText(count);
            item.mTvTime.setText(activity.time);
            item.mTvLocation.setText(activity.location);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }

        class VH extends RecyclerView.ViewHolder{
            SimpleDraweeView mAvatar;
            TextView mTvTitle;
            TextView mTvCount;
            TextView mTvTime;
            TextView mTvLocation;
            public VH(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent detail=new Intent(AtySearchActivity.this,AtyActivityDetail.class);
                        detail.putExtra("activityid",mList.get(getAdapterPosition()).activityID);
                        LogUtils.e(TAG, "id:" + mList.get(getAdapterPosition()).activityID);
                        startActivity(detail);
                    }
                });
                mAvatar = (SimpleDraweeView) itemView.findViewById(R.id.fgt_activity_item_image);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setRoundAsCircle(true);
                mAvatar.getHierarchy().setRoundingParams(roundingParams);
                mTvTitle = (TextView) itemView.findViewById(R.id.fgt_activity_item_title);
                mTvCount = (TextView) itemView.findViewById(R.id.fgt_activity_item_count);
                mTvTime = (TextView) itemView.findViewById(R.id.fgt_activity_item_time);
                mTvLocation = (TextView) itemView.findViewById(R.id.fgt_activity_item_location);
            }
        }
    }
    protected String tag() {
        return TAG;
    }
}
