package space.weme.remix.ui.intro;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.main.AtyMain;
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
        startActivity(new Intent(AtyWelcome.this, AtyLogin.class));
    }

    private void main(){
        startActivity(new Intent(AtyWelcome.this, AtyMain.class));
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
