package space.weme.remix.ui.main;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.ui.user.AtyDiscovery;
import space.weme.remix.ui.user.AtyFriend;
import space.weme.remix.ui.user.AtyInfo;
import space.weme.remix.ui.user.AtyMessage;
import space.weme.remix.ui.user.AtyUserActivity;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class FgtMe extends BaseFragment {

    private static final String TAG = "FgtMe";

    SimpleDraweeView mDraweeAvatar;
    TextView mTvName;
    TextView mTvCount;

    View.OnClickListener mListener;



    public static FgtMe newInstance() {
        Bundle args = new Bundle();
        FgtMe fragment = new FgtMe();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_me,container,false);
        mDraweeAvatar = (SimpleDraweeView) rootView.findViewById(R.id.fgt_me_avatar);
        mTvName = (TextView) rootView.findViewById(R.id.fgt_me_name);
        mTvCount = (TextView) rootView.findViewById(R.id.fgt_me_count);

        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setRoundAsCircle(true);
        mDraweeAvatar.getHierarchy().setRoundingParams(roundingParams);
        mDraweeAvatar.setImageURI(Uri.parse(StrUtils.thumForID(StrUtils.id() + "")));

        setClickListener(rootView);

        fetchNameInfo();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchUnreadMessage();
    }

    private void fetchUnreadMessage(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_UNREAD_MESSAGE_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(), s);
                if (j == null) {
                    return;
                }
                String number = j.optString("number");
                int count;
                try {
                    count = Integer.parseInt(number);
                } catch (NumberFormatException e) {
                    return;
                }
                if (count <= 0) {
                    return;
                }
                mTvCount.setVisibility(View.VISIBLE);
                if (count < 10) {
                    mTvCount.setText(number);
                    mTvCount.setTextSize(16);
                } else if (count < 100) {
                    mTvCount.setText(number);
                    mTvCount.setTextSize(14);
                } else {
                    mTvCount.setTextSize(12);
                    mTvCount.setText(R.string.more_than_99);
                }
            }
        });
    }

    private void fetchNameInfo(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        OkHttpUtils.post(StrUtils.GET_PERSON_INFO,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(getActivity(), s);
                if(j == null){
                    return;
                }
                User me = User.fromJSON(j);
                mTvName.setText(me.name);
            }
        });

    }



    private void setClickListener(View rootView){
        mListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.fgt_me_me:
                        Intent i = new Intent(getActivity(), AtyInfo.class);
                        i.putExtra(AtyInfo.ID_INTENT,StrUtils.id());
                        getActivity().startActivity(i);
                        break;
                    case R.id.fgt_me_friend:
                        getActivity().startActivity(new Intent(getActivity(), AtyFriend.class));
                        break;
                    case R.id.fgt_me_message:
                        getActivity().startActivity(new Intent(getActivity(), AtyMessage.class));
                        break;
                    case R.id.fgt_me_activity:
                        getActivity().startActivity(new Intent(getActivity(), AtyUserActivity.class));
                        break;
//                    case R.id.fgt_me_location:
//                        LogUtils.i(TAG,"location");
//                        break;
                    case R.id.fgt_me_discovery:
                        getActivity().startActivity(new Intent(getActivity(), AtyDiscovery.class));
                        break;
                    case R.id.fgt_me_food:
                        LogUtils.i(TAG,"food");
                        break;
                    case R.id.fgt_me_setting:
                        logout();
                        break;
                }
            }
        };
        rootView.findViewById(R.id.fgt_me_me).setOnClickListener(mListener);
        rootView.findViewById(R.id.fgt_me_friend).setOnClickListener(mListener);
        rootView.findViewById(R.id.fgt_me_message).setOnClickListener(mListener);
        rootView.findViewById(R.id.fgt_me_activity).setOnClickListener(mListener);
        //rootView.findViewById(R.id.fgt_me_location).setOnClickListener(mListener);
        rootView.findViewById(R.id.fgt_me_discovery).setOnClickListener(mListener);
        rootView.findViewById(R.id.fgt_me_food).setOnClickListener(mListener);
        rootView.findViewById(R.id.fgt_me_setting).setOnClickListener(mListener);

    }

    private void logout(){
        SharedPreferences sp = getActivity().getSharedPreferences(StrUtils.SP_USER, Context.MODE_PRIVATE);
        sp.edit().remove(StrUtils.SP_USER_ID).remove(StrUtils.SP_USER_TOKEN).apply();
        Intent i = new Intent(getActivity(), AtyMain.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(AtyMain.INTENT_LOGOUT,true);
        startActivity(i);
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
