package space.weme.remix.ui.find;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseFragment;

/**
 * Created by Liujilong on 2016/3/3.
 * liujilong.me@gmail.com
 */
public class FgtPrice extends BaseFragment {

    private static final String TAG = "FgtPrice";

    public static FgtPrice newInstance() {
        Bundle args = new Bundle();
        final FgtPrice fragment = new FgtPrice();
        fragment.setArguments(args);
        return fragment;
    }

    private ListView listView;
    private String[] array;
    private AtyAddFood aty;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fgt_food_price,container,false);
        listView = (ListView) v;
        aty = (AtyAddFood) getActivity();
        array = getResources().getStringArray(R.array.food_card_price);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),R.layout.fgt_price_cell,array){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.fgt_price_cell, parent, false);
                }
                ((TextView)convertView ).setText(array[position]);
                return convertView;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                aty.fgtAddFood.setPrice(array[position]);
                aty.switchToFragment(aty.fgtAddFood);
            }
        });
        return v;
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
