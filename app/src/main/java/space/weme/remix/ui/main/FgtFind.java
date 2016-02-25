package space.weme.remix.ui.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.widgt.FindFoodPath;

/**
 * Created by Liujilong on 2016/2/21.
 * liujilong.me@gmail.com
 */
public class FgtFind extends BaseFragment {
    private static final String TAG = "FgtFind";
    private FindFoodPath path;

    public static FgtFind newInstance() {
        Bundle args = new Bundle();
        FgtFind fragment = new FgtFind();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //LogUtils.d(TAG,"onCreateView: savedInstanceState " + (savedInstanceState==null?"null":"not null"));
        path = FindFoodPath.fromXML(inflater,container);
        return path;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        path.stopAnimation();
    }


    @Override
    protected String tag() {
        return TAG;
    }
}
