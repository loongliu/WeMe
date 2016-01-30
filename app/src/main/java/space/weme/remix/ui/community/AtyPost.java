package space.weme.remix.ui.community;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import space.weme.remix.R;
import space.weme.remix.model.Post;
import space.weme.remix.model.Reply;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/1/30.
 * liujilong.me@gmail.com
 */
public class AtyPost extends SwipeActivity {
    private static final String TAG = "AtyPost";
    public static final String POST_INTENT = "postId";
    public static final String THEME_INTENT = "theme";

    private String mPostID;


    private boolean isLoading = false;
    private int curPage = 1;
    private boolean canLoadMore = true;

    private List<Reply> mReplyList;
    private Post mPost;

    private PostAdapter mAdapter;


    private RecyclerView mRecyclerView;
    private LinearLayout mChatView;
    private EditText mEditText;
    private TextView mCommitText;
    private ImageView mAddImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostID = getIntent().getStringExtra(POST_INTENT);
        String theme = getIntent().getStringExtra(THEME_INTENT);
        setContentView(R.layout.aty_post);
        Toolbar toolbar = (Toolbar) findViewById(R.id.post_detail_toolbar);
        if(toolbar!=null) {
            toolbar.setTitle(theme);
            toolbar.setTitleTextColor(Color.WHITE);
        }
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        mChatView = (LinearLayout) findViewById(R.id.chat_view_holder);
        mEditText = (EditText) findViewById(R.id.activity_post_editor);
        mCommitText = (TextView) findViewById(R.id.activity_post_commit);
        mAddImage = (ImageView) findViewById(R.id.activity_post_add_image);

        mRecyclerView = (RecyclerView) findViewById(R.id.post_detail_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(AtyPost.this));
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
                        <= (firstVisibleItem + 2) && canLoadMore) {
                    // End has been reached
                    LogUtils.i(TAG, "scroll to end  load page " + (curPage + 1));
                    loadPage(curPage + 1);
                }
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(mChatView.getVisibility()==View.VISIBLE){
                    mChatView.setVisibility(View.INVISIBLE);
                    return true;
                }
                return false;
            }
        });
        mAdapter = new PostAdapter(this);

        mReplyList = new ArrayList<>();
        mAdapter.setReplyList(mReplyList);
        mRecyclerView.setAdapter(mAdapter);
        refreshAll();
    }

    private void refreshAll(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("postid", mPostID);
        OkHttpUtils.post(StrUtils.GET_POST_DETAIL,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyPost.this,s);
                if(j == null){
                    return;
                }
                JSONObject result = j.optJSONObject("result");
                mPost = Post.fromJSON(result);
                mAdapter.setPost(mPost);
                mAdapter.notifyDataSetChanged();
            }
        });
        loadPage(1);
    }

    private void loadPage(int page){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("postid",mPostID);
        param.put("page", page + "");
        OkHttpUtils.post(StrUtils.GET_POST_COMMIT,param,TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyPost.this,s);
                if(j == null){
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if(array==null){
                    return;
                }
                for(int i = 0; i<array.length(); i++){
                    mReplyList.add(Reply.fromJSON(array.optJSONObject(i)));
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }



    @Override
    protected String tag() {
        return TAG;
    }
}
