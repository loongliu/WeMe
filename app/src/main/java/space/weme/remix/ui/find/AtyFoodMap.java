package space.weme.remix.ui.find;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;

/**
 * Created by Liujilong on 2016/2/21.
 * liujilong.me@gmail.com
 */
public class AtyFoodMap extends BaseActivity {
    private static final String TAG = "AtyFoodMap";

    public static final String INTENT_LAT = "intent_lat";
    public static final String INTENT_LON = "intent_lon";


    private MapView mapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_food_map);
        TextView toolbar = (TextView) findViewById(R.id.toolbar);
        toolbar.setText(R.string.food_location);

        double lat = getIntent().getDoubleExtra(INTENT_LAT,0);
        double lon = getIntent().getDoubleExtra(INTENT_LON,0);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        AMap aMap = mapView.getMap();

        LatLng latLng = new LatLng(lat,lon);
        // zoom level is in 4-20
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(latLng, 16);
        aMap.moveCamera(update);

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.mipmap.map_marker);
        options.icon(descriptor);
        aMap.addMarker(options);
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
    }


    @Override
    protected String tag() {
        return TAG;
    }
}
