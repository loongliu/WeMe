package space.weme.remix.ui;

import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import space.weme.remix.R;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class FgtCommunity extends Fragment {
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
        return rootView;
    }
}
