package space.weme.remix.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

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
        mRecycler.setAdapter(adapter);
        setHasRead();
        getMessageDetail(1);
    }

    private void setHasRead(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("id",id);
        param.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.READ_MESSAGE,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.d(TAG,s);
            }
        });
    }

    private void getMessageDetail(int page){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("page",String.format("%d", page));
        param.put("SendId",id);   
        OkHttpUtils.post(StrUtils.GET_MESSAGE_DETAIL,param,TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyMessageDetail.this, s);
                if(j == null){
                    return;
                }
                JSONArray result = j.optJSONArray("result");
                if(result == null){
                    return;
                }
                messageList.clear();
                for(int i = 0; i<result.length(); i++){
                    messageList.add(Message.fromJSON(result.optJSONObject(i)));
                }
                adapter.setMessageList(messageList);
                adapter.notifyDataSetChanged();
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
