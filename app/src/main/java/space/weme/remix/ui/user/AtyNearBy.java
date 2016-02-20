package space.weme.remix.ui.user;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbyInfo;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearchFunctionType;
import com.amap.api.services.nearby.NearbySearchResult;
import com.amap.api.services.nearby.UploadInfo;

import java.util.List;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
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
                    for(NearbyInfo info : list){
                        LogUtils.i(TAG,"id: " + info.getUserID() + " " + info.getDistance());
                    }

                }
            }

            @Override
            public void onNearbyInfoUploaded(int i) {

            }
        };
        search.addNearbyListener(mNearbyListener);
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




    @Override
    protected String tag() {
        return TAG;
    }
}
