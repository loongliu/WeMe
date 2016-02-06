package space.weme.remix.ui.user;

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
 * Created by Liujilong on 16/2/5.
 * liujilong.me@gmail.com
 */
public class AtyMessage extends SwipeActivity {
    private static final String TAG = "AtyMessage";

    private List<Message> messageList;
    private MessageAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_message);

        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        RecyclerView mRecycler = (RecyclerView) findViewById(R.id.aty_message_recycler);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setHasFixedSize(true);
        messageList = new ArrayList<>();
        mAdapter = new MessageAdapter(this);
        mRecycler.setAdapter(mAdapter);
        getMessage();
    }

    private void getMessage(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_USER_MESSAGE_LIST, param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyMessage.this, s);
                if(j == null){
                    return;
                }
                JSONArray result = j.optJSONArray("result");
                for(int i = 0; i<result.length(); i++){
                    messageList.add(Message.fromJSON(result.optJSONObject(i)));
                }
                mAdapter.setMessageList(messageList);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }



}
