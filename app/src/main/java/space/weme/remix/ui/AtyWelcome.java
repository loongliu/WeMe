package space.weme.remix.ui;

import android.content.Intent;
import android.os.Bundle;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;

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

        loginIn();
        finish();

    }

    private void loginIn(){
        startActivity(new Intent(AtyWelcome.this,AtyLogin.class));
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
