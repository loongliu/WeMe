package space.weme.remix.ui.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.Message;
import space.weme.remix.model.SystemMessage;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/2/5.
 * liujilong.me@gmail.com
 */
public class AtyMessage extends BaseActivity {
    private static final String TAG = "AtyMessage";


    TabLayout tabLayout;
    ViewPager viewPager;
    private MessagePagerAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_message);

        tabLayout = (TabLayout) findViewById(R.id.aty_user_message_tab);
        viewPager = (ViewPager) findViewById(R.id.aty_user_message_pager);

        adapter = new MessagePagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


    }
    protected void onResume(){
        super.onResume();
    }



    @Override
    protected String tag() {
        return TAG;
    }


    private class MessagePagerAdapter extends FragmentPagerAdapter {

        public MessagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0) return FgtPrivateMessage.newInstance();
            else return FgtSystemMessage.newInstance();
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==0) return getResources().getString(R.string.private_letter);
            else return getResources().getString(R.string.reply);
        }
    }

    public static class FgtPrivateMessage extends BaseFragment{
        private List<Message> messageList;
        private MessageAdapter mAdapter;

        public static FgtPrivateMessage newInstance(){
            FgtPrivateMessage f = new FgtPrivateMessage();
            Bundle bundle = new Bundle();
            f.setArguments(bundle);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fgt_message, container, false);
            RecyclerView mRecycler = (RecyclerView) v.findViewById(R.id.aty_message_recycler);
            mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecycler.setHasFixedSize(true);
            messageList = new ArrayList<>();
            mAdapter = new MessageAdapter(getActivity());
            mRecycler.setAdapter(mAdapter);

            getMessage();
            return v;
        }
        private void getMessage(){
            ArrayMap<String,String> param = new ArrayMap<>();
            param.put("token", StrUtils.token());
            OkHttpUtils.post(StrUtils.GET_USER_MESSAGE_LIST, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
                @Override
                public void onResponse(String s) {
                    LogUtils.i(TAG, s);
                    JSONObject j = OkHttpUtils.parseJSON(getActivity(), s);
                    if (j == null) {
                        return;
                    }
                    JSONArray result = j.optJSONArray("result");
                    messageList.clear();
                    for (int i = 0; i < result.length(); i++) {
                        messageList.add(Message.fromJSON(result.optJSONObject(i)));
                    }
                    mAdapter.setMessageList(messageList);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

        @Override
        protected String tag() {
            return "FgtPrivateMessage";
        }
    }

    public static class FgtSystemMessage extends BaseFragment{
        private List<SystemMessage> messageList;
        private SystemMessageAdapter mAdapter;


        public static FgtSystemMessage newInstance(){
            FgtSystemMessage f = new FgtSystemMessage();
            Bundle bundle = new Bundle();
            f.setArguments(bundle);
            return f;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fgt_message, container, false);
            RecyclerView mRecycler = (RecyclerView) v.findViewById(R.id.aty_message_recycler);
            mRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
            mRecycler.setHasFixedSize(true);

            messageList = new ArrayList<>();
            mAdapter = new SystemMessageAdapter(getActivity());
            mRecycler.setAdapter(mAdapter);

            return v;
        }

        @Override
        public void onResume() {
            super.onResume();
            getSystemMessage();
        }

        @Override
        protected String tag() {
            return "FgtSystemMessage";
        }

        private void getSystemMessage(){
            ArrayMap<String,String> param = new ArrayMap<>();
            param.put("token", StrUtils.token());
            OkHttpUtils.post(StrUtils.GET_SYSTEM_NOTIFICATION,param,TAG, new OkHttpUtils.SimpleOkCallBack(){
                @Override
                public void onResponse(String s) {
                    LogUtils.i(TAG, s);
                    JSONObject j = OkHttpUtils.parseJSON(getActivity(), s);
                    if (j == null) {
                        return;
                    }
                    JSONArray result = j.optJSONArray("data");
                    messageList.clear();
                    for (int i = 0; i < result.length(); i++) {
                        JSONObject jj = result.optJSONObject(i);
                        if(jj != null && "community".equals(jj.optString("type"))){
                            messageList.add(SystemMessage.CommunityMessage.fromJson(jj.optJSONObject("content")));
                        }
                    }
                    mAdapter.setList(messageList);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }

    }


}
