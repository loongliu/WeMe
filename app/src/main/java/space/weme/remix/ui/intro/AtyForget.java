package space.weme.remix.ui.intro;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.regex.Pattern;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.CountDownButton;

/**
 * Created by Liujilong on 2016/3/5.
 * liujilong.me@gmail.com
 */
public class AtyForget extends BaseActivity {
    private static final String TAG = "AtyForget";

    Pattern phone = Pattern.compile(StrUtils.PHONE_PATTERN);

    EditText etPhone;
    EditText etCode;
    EditText etPass;
    EditText etPass2;
    Button btnCode;
    TextView tvReset;
    TextView tvError;

    CountDownButton mCountDown;
    ProgressDialog progressDialog;

    TextWatcher mTextWatcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_forget);

        etPhone = (EditText) findViewById(R.id.phone);
        etPass = (EditText) findViewById(R.id.login_password);
        etPass2 = (EditText) findViewById(R.id.login_copy_password);
        tvReset = (TextView) findViewById(R.id.reset_button);
        tvError = (TextView) findViewById(R.id.aty_reset_error);
        etCode = (EditText) findViewById(R.id.verification_code);
        btnCode = (Button) findViewById(R.id.gain_verification_code);

        mCountDown = new CountDownButton(btnCode,btnCode.getText().toString(),60,1);
        btnCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCode();
                mCountDown.start();
            }
        });

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                checkText();
            }
        };
        etPhone.addTextChangedListener(mTextWatcher);
        etPass.addTextChangedListener(mTextWatcher);
        etPass2.addTextChangedListener(mTextWatcher);
        etCode.addTextChangedListener(mTextWatcher);
        tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reset();
            }
        });
    }

    private void sendCode(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("phone",etPhone.getText().toString());
        param.put("type", "2");
        OkHttpUtils.post(StrUtils.SEND_CODE, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.d(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyForget.this, s);
                if (j != null) {
                    Toast.makeText(AtyForget.this, R.string.send_code_complete, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void checkText(){
        if(!phone.matcher(etPhone.getText()).matches()){
            tvReset.setEnabled(false);
            tvError.setText(R.string.please_input_phone);
            btnCode.setEnabled(false);
            return;
        }else{
            btnCode.setEnabled(true);
        }
        if(etCode.getText().length()==0){
            tvReset.setEnabled(false);
            tvError.setText(R.string.code_length);
            return;
        }
        if(etPass.getText().length()<6){
            tvReset.setEnabled(false);
            tvError.setText(R.string.password_long_6);
            return;
        }
        if(!etPass.getText().toString().equals(etPass2.getText().toString())){
            tvReset.setEnabled(false);
            tvError.setText(R.string.password_not_equal);
            return;
        }
        tvReset.setEnabled(true);
        tvError.setText("");
    }
    private void reset(){
        String name = etPhone.getText().toString();
        String passMD5 = StrUtils.md5(etPass.getText().toString());
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("phone",name);
        param.put("password", passMD5);
        param.put("code",etCode.getText().toString());
        progressDialog = ProgressDialog.show(AtyForget.this,null,"正在重置密码");
        OkHttpUtils.post(StrUtils.RESET_PASSWORD,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                final JSONObject j = OkHttpUtils.parseJSON(AtyForget.this, s);
                if(j == null){
                    progressDialog.dismiss();
                    return;
                }
                final String id = j.optString("id");
                final String token = j.optString("token");
                SharedPreferences sp = getSharedPreferences(StrUtils.SP_USER, MODE_PRIVATE);
                sp.edit().putString(StrUtils.SP_USER_ID, id)
                        .putString(StrUtils.SP_USER_TOKEN, token).apply();
                progressDialog.setMessage("重置密码成功，正在登陆中");
                Handler handler = new Handler(getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(AtyForget.this, AtyLogin.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.putExtra(AtyLogin.INTENT_CLEAR, true);
                        startActivity(i);
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
