package space.weme.remix.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import space.weme.remix.R;
import space.weme.remix.model.TopInfoWrapper;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.PageIndicator;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class FgtCommunity extends BaseFragment {

    private static final String TAG = "FgtCommunity";

    GridLayout mGridLayout;
    ViewPager mVpTop;
    PageIndicator mIndicator;

    public static FgtCommunity newInstance() {
        Bundle args = new Bundle();
        FgtCommunity fragment = new FgtCommunity();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_community,container,false);
        mGridLayout = (GridLayout) rootView.findViewById(R.id.fgt_community_grid_layout);
        mVpTop = (ViewPager) rootView.findViewById(R.id.top_pager_view);
        mIndicator = (PageIndicator) rootView.findViewById(R.id.top_pager_indicator);
        View v = rootView.findViewById(R.id.top_container);
        ViewGroup.LayoutParams params = v.getLayoutParams();
        params.height = DimensionUtils.getDisplay().widthPixels/2;
        v.setLayoutParams(params);
        firePost();
        return rootView;
    }

    private void firePost(){
        Map<String,String> params = new ArrayMap<>(1);
        params.put("token",StrUtils.token());
        OkHttpUtils.post(StrUtils.TOP_BROAD_URL, params, TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                //LogUtils.i(TAG, s);
                JSONObject j;
                try {
                    j = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), R.string.network_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state");
                if (!state.equals("successful")) {
                    Toast.makeText(getActivity(), j.optString("reason"), Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONArray array = j.optJSONArray("result");
                if (array == null) return;
                List<TopInfoWrapper> infoList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    TopInfoWrapper info = TopInfoWrapper.fromJSON(array.optJSONObject(i));
                    infoList.add(info);
                }
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
