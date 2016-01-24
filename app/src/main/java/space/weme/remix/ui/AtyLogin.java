package space.weme.remix.ui;

import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import space.weme.remix.APP;
import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.LoadingView;

/**
 * Created by Liujilong on 16/1/22.
 * liujilong.me@gmail.com
 */
public class AtyLogin extends BaseActivity {

    private final static String TAG = "AtyLogin";

    EditText mEtUser, mEtPassword;
    Button mBtnLogin;
    TextView mTvRegister;

    LoadingView mLoadingView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_login);
        bindView();

    }

    private void bindView(){
        mEtUser = (EditText) findViewById(R.id.login_edit_text_user);
        mEtPassword = (EditText) findViewById(R.id.login_edit_text_password);
        mBtnLogin = (Button) findViewById(R.id.login_button_login);
        mTvRegister = (TextView) findViewById(R.id.login_text_view_register);
        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        mLoadingView = new LoadingView(this);
    }

    private void login(){
        String userName = mEtUser.getText().toString();
        String passWord = mEtPassword.getText().toString();
        if(userName.isEmpty()||passWord.isEmpty()){
            Toast.makeText(this,R.string.account_or_password_not_null,Toast.LENGTH_SHORT).show();
            return;
        }
        String passMd5 = StrUtils.md5(passWord);
        Map<String,String> map = new HashMap<>();
        map.put("username", userName);
        map.put("password",passMd5);
        WindowManager windowManager = getWindowManager();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,0,0, PixelFormat.TRANSPARENT);
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.x = 300;
        params.y = 300;
        windowManager.addView(mLoadingView,params);
        OkHttpUtils.post(StrUtils.LOGIN_URL, map, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LogUtils.i(TAG,response.body().string());
                String s = response.body().string();
                JSONObject j;
                try {
                    j = new JSONObject(s);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(AtyLogin.this,R.string.login_fail,Toast.LENGTH_SHORT).show();
                    return;
                }
                String state = j.optString("state","");
                if(state.equals("successful")){
                    String token = j.optString("token","");
                    String id = j.optString("id","");
                    SharedPreferences sp = APP.context().getSharedPreferences(StrUtils.SP_USER,MODE_PRIVATE);
                    sp.edit().putString(StrUtils.SP_USER_TOKEN,token)
                            .putString(StrUtils.SP_USER_ID, id).apply();
                    Toast.makeText(AtyLogin.this,R.string.login_success,Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}
