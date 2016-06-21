package space.weme.remix.ui.user;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbyInfo;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchFunctionType;
import com.amap.api.services.nearby.NearbySearchResult;
import com.amap.api.services.nearby.UploadInfo;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/2/19.
 * liujilong.me@gmail.com
 */
public class AtyNearBy extends BaseActivity {
    private static final String TAG = "AtyNearBy";
    private MapView mapView;
    private AMap aMap;
    TextView tv;
    NearbySearch search;
    private NearbySearch.NearbyListener mNearbyListener;
    private AMapLocationListener mLocationListener;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;

    private Marker currentMarker;

    private ListView listView;
    private Adapter adapter = new Adapter();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_near_by);
        mapView = (MapView) findViewById(R.id.aty_near_by_map);
        int width = DimensionUtils.getDisplay().widthPixels;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width * 2 / 3);
        mapView.setLayoutParams(params);
        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
//        registerListener();
//        showLastLocation();
        tv = (TextView) findViewById(R.id.text);
        listView = (ListView) findViewById(R.id.aty_near_by_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String strId = ((NearbyInfo)parent.getAdapter().getItem(position)).getUserID();
                showUser(strId);
            }
        });
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationListener = new AMapLocationListener(){
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                LogUtils.i(TAG, "location changed: " + aMapLocation.toString());
                showLocation(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                uploadUserInfo(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                fetchUserInfo(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                mLocationClient.stopLocation();

            }
        };
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setWifiActiveScan(true);
        mLocationOption.setMockEnable(false);
        mLocationOption.setInterval(2000);
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.startLocation();


        search = NearbySearch.getInstance(getApplicationContext());
        mNearbyListener = new NearbySearch.NearbyListener() {
            @Override
            public void onUserInfoCleared(int i) {      }

            @Override
            public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int i) {
                if(nearbySearchResult==null){
                    LogUtils.i(TAG, "nearBy null");
                }else {
                    List<NearbyInfo> list = nearbySearchResult.getNearbyInfoList();
                    LogUtils.i(TAG,"size: "+list.size());
                    BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.map_marker);
                    for(NearbyInfo info : list){
                        LogUtils.i(TAG, "id: " + info.getUserID() + " " + info.getDistance());
                        MarkerOptions markerOption = new MarkerOptions();
                        markerOption.position(new LatLng(info.getPoint().getLatitude(),
                                info.getPoint().getLongitude())).icon(descriptor).title(info.getDistance()+"");
                        Marker marker = aMap.addMarker(markerOption);
                        marker.setObject(info);//这里可以存储用户数据
                    }
                    adapter.list = list;
                    adapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onNearbyInfoUploaded(int i) {
                LogUtils.i(TAG,"onNearbyInfoUploaded");
            }
        };
        search.addNearbyListener(mNearbyListener);
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                currentMarker = marker;
                Object o = marker.getObject();
                if (!(o instanceof NearbyInfo)) return null;
                NearbyInfo info = (NearbyInfo) o;
                View v = LayoutInflater.from(AtyNearBy.this).inflate(R.layout.aty_near_by_info,null);
                SimpleDraweeView view = (SimpleDraweeView) v.findViewById(R.id.aty_near_by_info_avatar);
                view.setImageURI(Uri.parse(StrUtils.thumForID(info.getUserID())));
                String distance = getString(R.string.distance_for_you) + String.format("%.1f km",(info.getDistance())/1000f);
                TextView tv = (TextView) v.findViewById(R.id.aty_near_by_info_distance);
                tv.setText(distance);
                TextView time = (TextView) v.findViewById(R.id.aty_near_by_info_time);
                LogUtils.i(TAG,"system.cu：" + System.currentTimeMillis() + "info.getTimestamp(): "+ info.getTimeStamp());
                time.setText(StrUtils.timeTransfer(System.currentTimeMillis()/1000 - info.getTimeStamp()));
                return v;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LogUtils.i(TAG,"getInfoContents");
                return null;
            }
        });
        aMap.setOnMapClickListener(new AMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(currentMarker!=null){
                    currentMarker.hideInfoWindow();
                }
            }
        });
    }




    private void showLocation(double lat, double lon){
        LatLng latLng = new LatLng(lat,lon);
        // zoom level is in 4-20
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        aMap.moveCamera(update);
    }

    private void uploadUserInfo(double lat, double lon){
        UploadInfo loadInfo = new UploadInfo();
        loadInfo.setCoordType(NearbySearch.AMAP);
        loadInfo.setPoint(new LatLonPoint(lat, lon));
        loadInfo.setUserID(StrUtils.id());
        search.uploadNearbyInfoAsyn(loadInfo);
        LogUtils.i(TAG, "upload user Info");
    }

    private void fetchUserInfo(double lat, double lon) {
        NearbySearch.NearbyQuery query = new NearbySearch.NearbyQuery();
        query.setCenterPoint(new LatLonPoint(lat, lon));
        query.setCoordType(NearbySearch.AMAP);
        query.setRadius(10000);
        query.setTimeRange(10000);
        query.setType(NearbySearchFunctionType.DRIVING_DISTANCE_SEARCH);
        search.addNearbyListener(mNearbyListener);
        search.searchNearbyInfoAsyn(query);
    }





    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(mLocationClient!=null){
            mLocationClient.stopLocation();
        }
    }
    // TODO: 2016/6/21 click to clear info

    @Override
    protected String tag() {
        return TAG;
    }


    class Adapter extends BaseAdapter{
        List<NearbyInfo> list;
        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView = LayoutInflater.from(AtyNearBy.this).inflate(R.layout.aty_near_by_user, parent, false);
            }
            NearbyInfo info = (NearbyInfo) getItem(position);
            String id = info.getUserID();
            SimpleDraweeView avatar = (SimpleDraweeView) convertView.findViewById(R.id.aty_friend_cell_avatar);
            final TextView  name = (TextView) convertView.findViewById(R.id.aty_friend_cell_name);
            final ImageView gender = (ImageView) convertView.findViewById(R.id.aty_friend_cell_gender);
            final TextView school = (TextView) convertView.findViewById(R.id.aty_friend_cell_school);
            avatar.setImageURI(Uri.parse(StrUtils.thumForID(id)));

            TextView tvDistance = (TextView) convertView.findViewById(R.id.aty_near_by_cell_distance);
            tvDistance.setText(String.format("%.1f km", (info.getDistance()) / 1000f));

            ArrayMap<String, String> param = new ArrayMap<>();
            param.put("token", StrUtils.token());
            param.put("id", StrUtils.id());
            OkHttpUtils.post(StrUtils.GET_PROFILE_BY_ID,param,TAG,new OkHttpUtils.SimpleOkCallBack() {
                @Override
                public void onResponse(String s) {
                    //LogUtils.i(TAG, s);
                    JSONObject j = OkHttpUtils.parseJSON(AtyNearBy.this, s);
                    if (j == null) {
                        finish();
                        return;
                    }
                    User user = User.fromJSON(j);
                    name.setText(user.name);
                    boolean isBoy = user.gender.equals(getResources().getString(R.string.boy));
                    gender.setImageResource(isBoy ? R.mipmap.boy : R.mipmap.girl);
                    school.setText(user.school);
                }
            });
            return convertView;
        }
    }

    private void showUser(String id){
        Intent i = new Intent(AtyNearBy.this,AtyInfo.class);
        i.putExtra(AtyInfo.ID_INTENT,id);
        startActivity(i);
    }
}
