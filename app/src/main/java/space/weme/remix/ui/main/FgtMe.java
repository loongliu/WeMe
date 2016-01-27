package space.weme.remix.ui.main;

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
public class FgtMe extends Fragment {
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
        return rootView;
    }
}
