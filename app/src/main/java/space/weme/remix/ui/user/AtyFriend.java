package space.weme.remix.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import space.weme.remix.R;
import space.weme.remix.model.FriendData;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/5.
 * liujilong.me@gmail.com
 */
public class AtyFriend extends SwipeActivity {
    private static final String TAG = "AtyFriend";

    EditText etSearch;
    FrameLayout tvSeeMe;
    // todo load more and refresh
    int pageFlag=1;
    boolean isLoading=false;
    boolean isRefreshing=false;
    boolean canLoadMore=true;

    RecyclerView mRecycler;
    SwipeRefreshLayout mRefresh;

    private List<FriendData> friendDataList;
    private FriendAdapter adapter;
    private List<FriendData> searchList;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_friend);

        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        etSearch = (EditText) findViewById(R.id.aty_friend_search);
        tvSeeMe = (FrameLayout) findViewById(R.id.aty_friend_see_me);
        mRecycler = (RecyclerView) findViewById(R.id.aty_friend_recycler);

        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);

        mRefresh= (SwipeRefreshLayout) findViewById(R.id.aty_friend_refresh);

        friendDataList = new ArrayList<>();
        searchList = new ArrayList<>();
        adapter = new FriendAdapter(this);
        mRecycler.setAdapter(adapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    adapter.setList(friendDataList);
                    adapter.notifyDataSetChanged();
                    return;
                }
                String se = s.toString();
                search(se);
            }
        });

        tvSeeMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AtyFriend.this, AtySeeMe.class);
                startActivity(i);
            }
        });

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (totalItemCount - firstVisibleItem)
                        <= (visibleItemCount + 2) && canLoadMore) {
                    isLoading=true;
                    LogUtils.d(TAG, pageFlag + "");
                    getFollowersAtPage(pageFlag);
                }
            }
        });

        mRefresh.setColorSchemeResources(R.color.colorPrimary);
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRefreshing){
                    LogUtils.d(TAG,"is refreshing");
                }
                else {
                    refresh();
                }
            }
        });
        refresh();
    }

    void refresh(){
        isRefreshing=true;
        pageFlag = 1;
        canLoadMore = true;
        friendDataList.clear();
        getFollowersAtPage(pageFlag);
    }

    private void getFollowersAtPage(final int page){
        ArrayMap<String, String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("page",String.format("%d", page));
        param.put("direction", "followeds");
        OkHttpUtils.post(StrUtils.GET_FOLLOWERS_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {

                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyFriend.this, s);
                if (j == null) {
                    return;
                }
                JSONArray result = j.optJSONArray("result");
                if (result.length()==0){
                    canLoadMore=false;
                    return;
                }
               else{
                    pageFlag++;
                }
                for (int i = 0; i < result.length(); i++) {
                    friendDataList.add(FriendData.fromJSON(result.optJSONObject(i)));
                }
                adapter.setList(friendDataList);
                adapter.notifyDataSetChanged();
                isLoading=false;
                isRefreshing=false;
                mRefresh.setRefreshing(false);
            }
        });
    }

    private void search(String s){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("text",s);
        OkHttpUtils.post(StrUtils.SEARCH_USER_URL,param,TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
               // LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyFriend.this,s);
                if(j == null){
                    return;
                }
                searchList.clear();
                JSONArray result = j.optJSONArray("result");
                for (int i = 0; i < result.length(); i++) {
                    searchList.add(FriendData.fromJSON(result.optJSONObject(i)));
                }
                adapter.setList(searchList);
                adapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }


}
