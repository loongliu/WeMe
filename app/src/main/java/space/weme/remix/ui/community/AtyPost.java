package space.weme.remix.ui.community;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
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

    private static final int REPLY_CODE = 10;

    private String mPostID;


    private boolean isLoading = false;
    private int curPage = 1;
    private boolean canLoadMore = true;

    private List<Reply> mReplyList;
    private Post mPost;

    private PostAdapter mAdapter;

    ProgressDialog mProgressDialog;

    private LinearLayout mChatView;
    private EditText mEditText;
    private TextView mCommitText;
    private ImageView mAddImage;

    private TextView tvDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPostID = getIntent().getStringExtra(POST_INTENT);
        String theme = getIntent().getStringExtra(THEME_INTENT);
        setContentView(R.layout.aty_post);
        TextView toolbar = (TextView) findViewById(R.id.post_detail_toolbar);
        toolbar.setText(theme);
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        tvDelete = (TextView) findViewById(R.id.post_detail_delete);

        mChatView = (LinearLayout) findViewById(R.id.chat_view_holder);
        mEditText = (EditText) findViewById(R.id.activity_post_editor);
        mCommitText = (TextView) findViewById(R.id.activity_post_commit);
        mAddImage = (ImageView) findViewById(R.id.activity_post_add_image);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.post_detail_recycler_view);
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
                    LogUtils.i(TAG, "scroll to end  load page " + (curPage + 1));
                    loadPage(curPage + 1);
                }
            }
        });

        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mChatView.getVisibility() == View.VISIBLE) {
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
        mReplyList.clear();
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("postid", mPostID);
        OkHttpUtils.post(StrUtils.GET_POST_DETAIL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyPost.this, s);
                if (j == null) {
                    return;
                }
                JSONObject result = j.optJSONObject("result");
                mPost = Post.fromJSON(result);
                if(TextUtils.equals(mPost.userId,StrUtils.id())){
                    tvDelete.setVisibility(View.VISIBLE);
                    tvDelete.setOnClickListener(deleteListener);
                }else{
                    tvDelete.setVisibility(View.GONE);
                }
                mAdapter.setPost(mPost);
                mAdapter.notifyDataSetChanged();
            }
        });
        loadPage(1);
        mChatView.setVisibility(View.INVISIBLE);
        canLoadMore = true;
    }

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final ProgressDialog progressDialog = new ProgressDialog(AtyPost.this);
            progressDialog.show();
            ArrayMap<String,String> map = new ArrayMap<>();
            map.put("token",StrUtils.token());
            map.put("postid",mPostID);
            OkHttpUtils.post(StrUtils.DELETE_POST_URL, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
                @Override
                public void onResponse(String s) {
                    progressDialog.dismiss();
                    JSONObject j = OkHttpUtils.parseJSON(AtyPost.this,s);
                    if(j == null) return;
                    finish();
                }

                @Override
                public void onFailure(IOException e) {
                    progressDialog.dismiss();
                }
            });
        }
    };

    private void loadPage(final int page){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("postid",mPostID);
        param.put("page", page + "");
        beforeLoadPage(page);
        curPage = page;
        OkHttpUtils.post(StrUtils.GET_POST_COMMIT, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                afterLoadPage(page);
            }

            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                afterLoadPage(page);
                JSONObject j = OkHttpUtils.parseJSON(AtyPost.this, s);
                if (j == null) {
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if (array == null) {
                    return;
                }
                int size = array.length();
                if(size==0){
                    canLoadMore = false;
                    return;
                }
                int previousCount = mReplyList.size();
                for (int i = 0; i < array.length(); i++) {
                    mReplyList.add(Reply.fromJSON(array.optJSONObject(i)));
                }
                if(page==1){
                    mAdapter.notifyDataSetChanged();
                }else {
                    mAdapter.notifyItemRangeInserted(previousCount, array.length());
                }
            }
        });
    }
    private void beforeLoadPage(int page){
        isLoading = true;
        if(page!=1){
            mReplyList.add(null);
            mAdapter.notifyItemInserted(mReplyList.size()+1);
        }
    }
    private void afterLoadPage(int page){
        isLoading = false;
        if(page!=1){
            mReplyList.remove(mReplyList.size() - 1);
            mAdapter.notifyItemRemoved(mReplyList.size() + 1);
        }
    }


    @Override
    protected String tag() {
        return TAG;
    }

    void commitPost(){
        mChatView.setVisibility(View.VISIBLE);
        mAddImage.setVisibility(View.VISIBLE);
        mEditText.setText("");
        mEditText.setHint("");
        mAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AtyPost.this, AtyPostReply.class);
                i.putExtra(AtyPostReply.INTENT_ID, mPostID);
                i.putExtra(AtyPostReply.INTENT_CONTENT, mEditText.getText().toString());
                startActivityForResult(i, REPLY_CODE);
            }
        });
        mCommitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().length() == 0){
                    return;
                }
                ArrayMap<String,String> param = new ArrayMap<>();
                param.put("token",StrUtils.token());
                param.put("body",mEditText.getText().toString());
                param.put("postid",mPostID);
                mProgressDialog = ProgressDialog.show(AtyPost.this, null, getResources().getString(R.string.committing));
                OkHttpUtils.post(StrUtils.COMMENT_TO_POST_URL,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
                    @Override
                    public void onFailure(IOException e) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s) {
                        mProgressDialog.dismiss();
                        LogUtils.i(TAG,s);
                        JSONObject j = OkHttpUtils.parseJSON(AtyPost.this,s);
                        if(j == null){
                            return;
                        }
                        clearChatView();
                        refreshAll();
                    }
                });
            }
        });
    }
    void commitReply(final Reply reply){
        mChatView.setVisibility(View.VISIBLE);
        mAddImage.setVisibility(View.GONE);
        mEditText.setHint(getString(R.string.commit) + reply.name + ":");
        mEditText.setText("");
        mCommitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditText.getText().length()==0){
                    return;
                }
                ArrayMap<String,String> map = new ArrayMap<>();
                map.put("token",StrUtils.token());
                map.put("body",mEditText.getText().toString());
                map.put("destcommentid", reply.id);
                mProgressDialog = ProgressDialog.show(AtyPost.this, null, getResources().getString(R.string.committing));
                OkHttpUtils.post(StrUtils.COMMENT_TO_COMMENT_URL,map,TAG,new OkHttpUtils.SimpleOkCallBack(){
                    @Override
                    public void onFailure(IOException e) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s) {
                        mProgressDialog.dismiss();
                        LogUtils.i(TAG,s);
                        JSONObject j = OkHttpUtils.parseJSON(AtyPost.this,s);
                        if(j == null){
                            return;
                        }
                        clearChatView();
                        refreshAll();
                    }
                });
            }
        });
    }

    private void clearChatView(){
        mChatView.setVisibility(View.GONE);
        mEditText.setText("");
        mCommitText.setOnClickListener(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REPLY_CODE&&resultCode == Activity.RESULT_OK){
            refreshAll();
        }
    }
}
