package space.weme.remix.ui.community;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.util.ArrayMap;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import space.weme.remix.R;
import space.weme.remix.model.Post;
import space.weme.remix.model.Topic;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/1/28.
 * liujilong.me@gmail.com
 */
public class AtyTopic extends SwipeActivity {
    private static final String TAG = "AtyTopic";
    public static final String TOPIC_ID = "topic_id";

    private static final int REQUEST_NEW_POST = 0xef;

    private String mTopicId;
    private Topic mTopic;
    private ArrayList<Post> mPostList;
    private boolean isRefreshing = false;
    private boolean isLoading = false;
    private int curPage = 0;
    private boolean canLoadMore = true;

    private TopicAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SimpleDraweeView mImage;
    private TextView mTvSlogan;
    private TextView mTvTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_topic);

        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        AppBarLayout mBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        int width = DimensionUtils.getDisplay().widthPixels;
        CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(width,width/2);
        mBarLayout.setLayoutParams(params);

        mImage = (SimpleDraweeView) findViewById(R.id.aty_topic_title_image);
        mTvSlogan = (TextView) findViewById(R.id.aty_topic_title_slogan);
        mTvTheme = (TextView) findViewById(R.id.aty_topic_theme);

        mTopicId = getIntent().getStringExtra(TOPIC_ID);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(AtyTopic.this, AtyPostNew.class);
                i.putExtra(AtyPostNew.INTENT_ID,mTopicId);
                startActivityForResult(i, REQUEST_NEW_POST);
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
                    loadPage(1,true);
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
                mImage.setImageURI(Uri.parse(mTopic.imageurl));
                mTvSlogan.setText(mTopic.slogan);
                mTvTheme.setText(mTopic.theme);
                mAdapter.setTopic(mTopic);
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
            mAdapter.notifyItemInserted(mPostList.size());
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
                mAdapter.setPostList(mPostList);
                if(replace){
                    mAdapter.notifyDataSetChanged();
                }else {
                    mAdapter.notifyItemRangeInserted(previousCount, size);
                }
            }
        });
    }


    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_NEW_POST && resultCode == RESULT_OK){
            loadAll();
        }
    }
}
