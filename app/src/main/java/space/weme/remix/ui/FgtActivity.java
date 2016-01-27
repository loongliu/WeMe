package space.weme.remix.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import space.weme.remix.R;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class FgtActivity extends Fragment {
    public static FgtActivity newInstance() {
        
        Bundle args = new Bundle();
        
        FgtActivity fragment = new FgtActivity();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fgt_activity,container,false);
        return rootView;
    }
}
