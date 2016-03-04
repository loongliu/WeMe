package space.weme.remix.ui.find;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
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
    Adapter adapter;

    AtyAddFood aty;

    BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.map_marker);



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fgt_food_map,container,false);
        aty = (AtyAddFood) getActivity();

        etext = (EditText) v.findViewById(R.id.food_map_search);
        tvCancel = (TextView) v.findViewById(R.id.search_cancel);
        mapView = (MapView) v.findViewById(R.id.map_view);

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
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()!=0) {
                    searchText(s.toString());
                }
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etext.clearFocus();
                etext.setText("");
                adapter.setList(null);
            }
        });
        ViewGroup.LayoutParams params = mapView.getLayoutParams();
        params.height = DimensionUtils.getDisplay().widthPixels/2;
        mapView.setLayoutParams(params);



        recyclerView = (RecyclerView) v.findViewById(R.id.recycler_view);
        adapter = new Adapter();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

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
            adapter.setList(poiItems);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onPoiItemSearched(PoiItem poiItem, int i) {    }
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


    @Override
    public void onPause() {
        Log.i("sys", "mf onPause");
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.i("sys", "mf onSaveInstanceState");
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        Log.i("sys", "mf onDestroy");
        super.onDestroy();
        mapView.onDestroy();
    }

    class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<PoiItem> list;

        public void setList(List<PoiItem> item){
            list = item;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.fgt_food_map_cell,parent,false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VH vh = (VH) holder;
            vh.tvName.setText(list.get(position).getTitle());
            vh.tvPosition.setText(list.get(position).getSnippet());
        }

        class VH extends RecyclerView.ViewHolder{
            TextView tvName;
            TextView tvChoose;
            TextView tvPosition;
            public VH(View itemView) {
                super(itemView);
                tvName = (TextView) itemView.findViewById(R.id.location_name);
                tvChoose = (TextView) itemView.findViewById(R.id.location_choose);
                tvPosition = (TextView) itemView.findViewById(R.id.location_position);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        LatLonPoint point = list.get(position).getLatLonPoint();
                        LatLng latLng = new LatLng(point.getLatitude(),point.getLongitude());
                        // zoom level is in 4-20
                        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 16);
                        aMap.moveCamera(update);
                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.icon(descriptor);
                        aMap.addMarker(options);
                    }
                });
                tvChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        aty.fgtAddFood.poiItem = list.get(position);
                        aty.switchToFragment(aty.fgtAddFood);
                    }
                });
            }
        }
        public int getItemCount() {
            return list==null?0:list.size();
        }
    }

}
