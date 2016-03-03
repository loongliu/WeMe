package space.weme.remix.ui.find;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.util.ArrayList;
import java.util.List;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;

/**
 * Created by Liujilong on 2016/3/3.
 * liujilong.me@gmail.com
 */
public class FgtFoodMap extends BaseFragment {
    private static final String TAG = "FgtFoodMap";

    public static FgtFoodMap newInstance() {
        Bundle args = new Bundle();
        final FgtFoodMap fragment = new FgtFoodMap();
        fragment.setArguments(args);
        return fragment;
    }

    EditText etext;
    TextView tvCancel;
    MapView mapView;
    private AMap aMap;
    RecyclerView recyclerView;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fgt_food_map,container,false);
        etext = (EditText) v.findViewById(R.id.food_map_search);
        tvCancel = (TextView) v.findViewById(R.id.search_cancel);
        mapView = (MapView) v.findViewById(R.id.map_view);
        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        etext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                tvCancel.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });
        etext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {    }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {     }

            @Override
            public void afterTextChanged(Editable s) {
                searchText(s.toString());
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etext.clearFocus();
                etext.setText("");
            }
        });
        ViewGroup.LayoutParams params = mapView.getLayoutParams();
        params.height = DimensionUtils.getDisplay().widthPixels/2;
        mapView.setLayoutParams(params);
        return v;
    }

    private void searchText(String text){
        PoiSearch.Query query = new PoiSearch.Query(text, "05", "025");
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(1);//设置查询页码
        PoiSearch poiSearch = new PoiSearch(getActivity(),query);//初始化poiSearch对象
        poiSearch.setOnPoiSearchListener(searchListener);//设置回调数据的监听器
        poiSearch.searchPOIAsyn();//开始搜索
    }

    PoiSearch.OnPoiSearchListener searchListener = new PoiSearch.OnPoiSearchListener() {
        @Override
        public void onPoiSearched(PoiResult poiResult, int i) {
            ArrayList<PoiItem> poiItems = poiResult.getPois();
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {

        }
    };

    @Override
    protected String tag() {
        return TAG;
    }

    @Override
    public void onResume() {
        LogUtils.i("sys", "mf onResume");
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     * map的生命周期方法
     */
    @Override
    public void onDestroy() {
        Log.i("sys", "mf onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<PoiItem> list;
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fgt_food_map_cell,parent,false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            // todo
        }

        class VH extends RecyclerView.ViewHolder{

            public VH(View itemView) {
                super(itemView);
            }
        }
        public int getItemCount() {
            return list==null?0:list.size();
        }
    }

}
