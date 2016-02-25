package space.weme.remix.ui.user;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.json.JSONObject;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.main.AtyMain;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/2/21.
 * liujilong.me@gmail.com
 */
public class AtySetting extends BaseActivity {
    private static final String TAG = "AtySetting";
    Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_setting);
        TextView toolbar = (TextView) findViewById(R.id.toolbar);
        toolbar.setText(R.string.setting);
        findViewById(R.id.aty_setting_cache_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePipeline pipeline = Fresco.getImagePipeline();
                pipeline.clearDiskCaches();
                Toast.makeText(AtySetting.this, R.string.cache_removed, Toast.LENGTH_SHORT).show();
            }
        });
        findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
        aSwitch = (Switch) findViewById(R.id.aty_setting_switch);
        SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER,MODE_PRIVATE);
        boolean checked = sp.getBoolean(StrUtils.SP_USER_CAN_FOUND,true);
        aSwitch.setChecked(checked);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                ArrayMap<String,String> map = new ArrayMap<>();
                map.put("token", StrUtils.token());
                map.put("cardflag",isChecked?"1":"0");
                OkHttpUtils.post(StrUtils.EDIT_CARD_SETTING,map,TAG, new OkHttpUtils.SimpleOkCallBack(){
                    @Override
                    public void onResponse(String s) {
                        LogUtils.i(TAG, s);
                        JSONObject j = OkHttpUtils.parseJSON(AtySetting.this, s);
                        if(j == null){
                            Toast.makeText(AtySetting.this,R.string.edit_fail,Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int string = isChecked ? R.string.shown_in_find : R.string.not_shown_in_found;
                        Toast.makeText(AtySetting.this,string,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void logout(){
        SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, Context.MODE_PRIVATE);
        sp.edit().remove(StrUtils.SP_USER_ID).remove(StrUtils.SP_USER_TOKEN).apply();
        Intent i = new Intent(this, AtyMain.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra(AtyMain.INTENT_LOGOUT, true);
        startActivity(i);
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
