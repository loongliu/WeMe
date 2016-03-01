package space.weme.remix.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import space.weme.remix.R;
import space.weme.remix.model.Message;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/6.
 * liujilong.me@gmail.com
 */
public class AtyMessageDetail extends SwipeActivity {
    private static final String TAG = "AtyMessageDetail";
    public static final String INTENT_ID = "intent_id";
    public static int REQUEST_CODE = 0xfe;

    private String id;
    private MessageDetailAdapter adapter;

    private List<Message> messageList;

    boolean isLoading = false;
    boolean canLoadMore = true;
    int curPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_message_detail);
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        id = getIntent().getStringExtra(INTENT_ID);
        RecyclerView mRecycler = (RecyclerView) findViewById(R.id.aty_message_detail_recycler);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        messageList = new ArrayList<>();
        adapter = new MessageDetailAdapter(this, id);
        adapter.setMessageList(messageList);
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
                    getMessageDetail(curPage + 1);
                }
            }
        });


        getMessageDetail(1);
    }

    private void setHasRead(String messageId){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("id",messageId);
        param.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.READ_MESSAGE,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.d(TAG,s);
            }
        });
    }

    private void getMessageDetail(final int page){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("page",String.format("%d", page));
        param.put("SendId",id);
        isLoading = true;
        OkHttpUtils.post(StrUtils.GET_MESSAGE_DETAIL,param,TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                isLoading = false;
                JSONObject j = OkHttpUtils.parseJSON(AtyMessageDetail.this, s);
                if(j == null){
                    return;
                }
                JSONArray result = j.optJSONArray("result");
                if(result == null){
                    return;
                }
                curPage = page;
                int previousCount = messageList.size();
                int count = result.length();
                if(count == 0){
                    canLoadMore = false;
                    return;
                }
                for(int i = 0; i<result.length(); i++){
                    Message m = Message.fromJSON(result.optJSONObject(i));
                    messageList.add(m);
                    setHasRead(m.messageid+"");
                }
                adapter.notifyItemRangeInserted(previousCount, count);
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            getMessageDetail(1);
        }
    }
}
