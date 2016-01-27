package space.weme.remix.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
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
        SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER,MODE_PRIVATE);
        String token = sp.getString(StrUtils.SP_USER_TOKEN,"");
        if(true){
            loginIn();
        }else{
            main();
        }
        finish();

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
