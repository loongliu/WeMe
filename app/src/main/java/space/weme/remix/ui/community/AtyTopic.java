package space.weme.remix.ui.community;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import space.weme.remix.R;
import space.weme.remix.model.Post;
import space.weme.remix.model.Topic;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/1/28.
 * liujilong.me@gmail.com
 */
public class AtyTopic extends BaseActivity {
    private static final String TAG = "AtyTopic";
    public static final String TOPIC_ID = "topic_id";

    private String mTopicId;
    private Topic mTopic;
    private ArrayList<Post> mPostList;
    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private int curPage = 0;
    private boolean canLoadMore = true;

    private TopicAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_topic);
        mTopicId = getIntent().getStringExtra(TOPIC_ID);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO new post
//                Intent i = new Intent(AtyTopic.this, PostNewActivity.class);
//                i.putExtra("topicid",mTopicId);
//                startActivityForResult(i, REQUEST_POST);
            }
        });
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.aty_topic_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AtyTopic.this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                    loadPage(curPage + 1);
                }
            }
        });
        mAdapter = new TopicAdapter(AtyTopic.this);
        mRecyclerView.setAdapter(mAdapter);
        mPostList = new ArrayList<>();
        mAdapter.setPostList(mPostList);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (isRefreshing) {
                    Log.d(TAG, "ignore manually update!");
                } else {
                    loadAll();
                }
            }
        });
        loadAll();
    }

    private void loadAll(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("topicid", mTopicId);
        OkHttpUtils.post(StrUtils.GET_TOPIC_INFO, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyTopic.this, s);
                if (j == null) {
                    return;
                }
                JSONObject object = j.optJSONObject("result");
                mTopic = Topic.fromJson(object);
                mAdapter.setTopic(mTopic);
                mAdapter.notifyItemChanged(0);
            }
        });
        loadPage(1, true);
    }

    private void beforeLoadPage(boolean replace){
        if(replace){
            isRefreshing = true;
            curPage = 1;
        }else{
            isLoading = true;
            mPostList.add(null);
            mAdapter.notifyItemInserted(mPostList.size() -1);
        }
    }
    private void afterLoadPage(boolean replace){
        if(replace){
            mSwipeRefreshLayout.setRefreshing(false);
            isRefreshing = false;
        }else{
            isLoading = false;
            mPostList.remove(mPostList.size()-1);
            mAdapter.notifyItemRemoved(mPostList.size());
        }
    }

    private void loadPage(int page){
        loadPage(page,false);
    }
    private void loadPage(int page, final boolean replace){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("topicid",mTopicId);
        param.put("page", page + "");
        beforeLoadPage(replace);
        OkHttpUtils.post(StrUtils.GET_POST_LIST,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onFailure(IOException e) {
                afterLoadPage(replace);
            }

            @Override
            public void onResponse(String res) {
                afterLoadPage(replace);
                LogUtils.i(TAG,res);
                JSONObject j = OkHttpUtils.parseJSON(AtyTopic.this,res);
                if(j==null){
                    return;
                }
                if(replace){
                    canLoadMore = true;
                }else{
                    curPage++;
                }
                int previousCount = mPostList.size();
                JSONArray result = j.optJSONArray("result");
                int size = result.length();
                if(replace){
                    mPostList.clear();
                }
                if(size==0){
                    canLoadMore = false;
                    return;
                }
                for(int i = 0; i<result.length(); i++){
                    mPostList.add(Post.fromJSON(result.optJSONObject(i)));
                }
                mAdapter.notifyItemRangeInserted(previousCount, size);
            }
        });
    }


    @Override
    protected String tag() {
        return TAG;
    }



}
