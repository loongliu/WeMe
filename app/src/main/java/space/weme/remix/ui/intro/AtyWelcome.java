package space.weme.remix.ui.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.view.SimpleDraweeView;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.main.AtyMain;
import space.weme.remix.util.BitmapUtils;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 16/1/20.
 * liujilong.me@gmail.com
 */
public class AtyWelcome extends BaseActivity {
    private static final String TAG = "AtyWelcome";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_welcome);

        Handler handler = new Handler();
        final SimpleDraweeView iv = (SimpleDraweeView) findViewById(R.id.background);
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME) // "res"
                .path(String.valueOf(R.mipmap.splash_background))
                .build();
        int width = DimensionUtils.getDisplay().widthPixels;
        int height = DimensionUtils.getDisplay().heightPixels;
        BitmapUtils.showResizedPicture(iv,uri,width,height);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                String token = sp.getString(StrUtils.SP_USER_TOKEN, "");
                if (token.equals("")) {
                    loginIn();
                    overridePendingTransition(0,0);
                }else{
                    main();
                    overridePendingTransition(0,0);
                }
                finish();
            }
        },1000);
    }

    private void loginIn(){
        Intent i = new Intent(AtyWelcome.this,AtyLogin.class);
        i.putExtra(AtyLogin.INTENT_UPDATE,true);
        startActivity(i);
    }

    private void main(){
        Intent i = new Intent(AtyWelcome.this, AtyMain.class);
        i.putExtra(AtyMain.INTENT_UPDATE,true);
        startActivity(i);
    }



    @Override
    protected String tag() {
        return TAG;
    }
}
