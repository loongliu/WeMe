package space.weme.remix.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

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
    RecyclerView mRecycler;

    private List<FriendData> friendDataList;
    private FriendAdapter adapter;
    private List<FriendData> searchList;

    boolean isLoading = false;
    boolean canLoadMore = true;
    int curPage = 1;

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

        friendDataList = new ArrayList<>();
        searchList = new ArrayList<>();
        adapter = new FriendAdapter(this);
        adapter.setList(friendDataList);
        mRecycler.setAdapter(adapter);

        mRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = recyclerView.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItem = layoutManager.findFirstVisibleItemPosition();
                if (!isLoading && (totalItemCount - visibleItemCount)
                        <= (firstVisibleItem + 1) && canLoadMore) {
                    // End has been reached
                    Log.i(TAG, "scroll to end  load page " + (curPage + 1));
                    getFollowersAtPage(curPage + 1);
                }
            }
        });



        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {   }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {    }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()==0){
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
                Intent i = new Intent(AtyFriend.this,AtySeeMe.class);
                startActivity(i);
            }
        });

        getFollowersAtPage(1);
    }

    private void getFollowersAtPage(final int page){
        ArrayMap<String, String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("page",String.format("%d", page));
        param.put("direction", "followeds");
        isLoading = true;
        OkHttpUtils.post(StrUtils.GET_FOLLOWERS_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                isLoading = false;
                JSONObject j = OkHttpUtils.parseJSON(AtyFriend.this, s);
                if (j == null) {
                    return;
                }
                curPage = page;
                JSONArray result = j.optJSONArray("result");
                int previousCount = friendDataList.size();
                int count = result.length();
                if(count == 0){
                    canLoadMore = false;
                    return;
                }
                for (int i = 0; i < result.length(); i++) {
                    friendDataList.add(FriendData.fromJSON(result.optJSONObject(i)));
                }
                adapter.notifyItemRangeInserted(previousCount, count);
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
                LogUtils.i(TAG,s);
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
