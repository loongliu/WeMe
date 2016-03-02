package space.weme.remix.ui.intro;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.core.ImagePipeline;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import space.weme.remix.R;
import space.weme.remix.model.User;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.community.DatePickerFragment;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.WSwitch;

/**
 * Created by Liujilong on 16/2/3.
 * liujilong.me@gmail.com
 */
public class AtyEditInfo extends BaseActivity {
    private static final String TAG = "AtyEditInfo";

    public static final String INTENT_EDIT = "intent_edit";
    public static final String INTENT_INFO = "intent_info";
    private boolean mEdit;
    private User mUser;
    //private boolean

    private static final int REQUEST_IMAGE = 0xef;
    private static final int REQUEST_CITY = 0xff;
    private final int REQUEST_CROP = 400;
    private String filePath = Environment.getExternalStorageDirectory().getPath() + "/" + "small.jpg";



    private String mAvatarPath;

    private String education;

    SimpleDraweeView mDrawAvatar;
    EditText etName;
    TextView tvBirth;
    EditText etPhone;
    TextView tvSchool;
    Spinner spEducation;
    EditText etMajor;
    EditText etWeChat;
    EditText etQQ;
    EditText etHome;
    TextView etCommit;
    WSwitch wSwitch;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_editinfo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.aty_editinfo_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        bindViews();

        mEdit = getIntent().getBooleanExtra(INTENT_EDIT,false);
        if(mEdit){
            String info = getIntent().getStringExtra(INTENT_INFO);
            try {
                mUser = User.fromJSON(new JSONObject(info));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            showUserInfo();
        }
    }

    private void bindViews(){
        mDrawAvatar = (SimpleDraweeView) findViewById(R.id.aty_editinfo_avatar);
        etName = (EditText) findViewById(R.id.aty_editinfo_edittext_name);
        wSwitch = (WSwitch) findViewById(R.id.aty_editinfo_switch_gender);
        tvBirth = (TextView) findViewById(R.id.aty_editinfo_birth);
        etPhone = (EditText) findViewById(R.id.aty_editinfo_phone);
        tvSchool = (TextView) findViewById(R.id.aty_editinfo_school);
        spEducation = (Spinner) findViewById(R.id.aty_editinfo_education);
        etMajor = (EditText) findViewById(R.id.aty_editinfo_major);
        etWeChat = (EditText) findViewById(R.id.aty_editinfo_wechat);
        etQQ = (EditText) findViewById(R.id.aty_editinfo_qq);
        etHome = (EditText) findViewById(R.id.aty_editinfo_home);
        etCommit = (TextView) findViewById(R.id.aty_editinfo_commit);


        mDrawAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AtyEditInfo.this, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });
        tvBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerFragment datePicker = new DatePickerFragment();
                datePicker.setDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        tvBirth.setText(String.format("%4d-%02d-%02d", year, monthOfYear + 1, dayOfMonth));
                    }
                });
                datePicker.show(getFragmentManager(), "DatePicker");
            }
        });

        tvSchool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(AtyEditInfo.this, AtySearchCity.class);
                startActivityForResult(i, REQUEST_CITY);
            }
        });

        final String[] items = new String[]{
                getString(R.string.please_choose_education),
                getString(R.string.benke),
                getString(R.string.master),
                getString(R.string.doctor)};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spEducation.setAdapter(adapter);
        spEducation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    education = null;
                    return;
                }
                education = items[position];
                LogUtils.i(TAG, education);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                education = null;
            }
        });

        // todo choose home

        etCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
    }

    private void showUserInfo(){
        mDrawAvatar.setImageURI(Uri.parse(StrUtils.thumForID(mUser.ID + "")));
        etName.setText(mUser.name);
        boolean male = getResources().getString(R.string.male).equals(mUser.gender);
        wSwitch.setOn(male);
        tvBirth.setText(mUser.birthday);
        etPhone.setText(mUser.phone);
        tvSchool.setText(mUser.school);
        switch (mUser.degree) {
            case "本科":
                spEducation.setSelection(1);
                break;
            case "硕士":
                spEducation.setSelection(2);
                break;
            case "博士":
                spEducation.setSelection(3);
                break;
        }
        etMajor.setText(mUser.department);
        etWeChat.setText(mUser.wechat);
        etQQ.setText(mUser.qq);
        etHome.setText(mUser.hometown);
    }

    private void commit(){
        //LogUtils.d(TAG, swGender.isChecked()+"");
        if(etName.getText().toString().length()==0){
            makeToast(R.string.name_not_empty);
            return;
        }
        if(tvBirth.getText().toString().length()==0){
            makeToast(R.string.birth_not_empty);
            return;
        }
        if(etPhone.getText().toString().length()==0){
            makeToast(R.string.phone_not_empty);
            return;
        }
        if(tvSchool.getText().toString().length()==0){
            makeToast(R.string.school_not_empty);
            return;
        }
        if(education==null){
            makeToast(R.string.choose_degree);
            return;
        }

        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("name", etName.getText().toString());
        param.put("birthday", tvBirth.getText().toString());
        param.put("degree", education);
        if(etMajor.getText().length()!=0){
            param.put("department", etMajor.getText().toString());
        }
        param.put("gender", wSwitch.isOn() ? getResources().getString(R.string.male) : getResources().getString(R.string.female));
        if(etHome.getText().length()!=0){
            param.put("hometown",etHome.getText().toString());
        }
        param.put("phone", etPhone.getText().toString());
        if(etQQ.getText().length()!=0){
            param.put("qq",etQQ.getText().toString());
        }
        param.put("school", tvSchool.getText().toString());
        if(etWeChat.getText().length()!=0){
            param.put("wechat", etWeChat.getText().toString());
        }
        mProgressDialog = ProgressDialog.show(AtyEditInfo.this, null, getResources().getString(R.string.committing));
        OkHttpUtils.post(StrUtils.EDIT_PROFILE_URL, param, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(AtyEditInfo.this, s);
                if (j == null) {
                    return;
                }
                if (mAvatarPath == null) {
                    uploadImageReturned();
                } else {
                    uploadAvatar();
                }
            }
        });
    }

    private void uploadAvatar(){
        ArrayMap<String,String> p = new ArrayMap<>();
        p.put("token",StrUtils.token());
        p.put("type","0");
        p.put("number","0");
        OkHttpUtils.uploadFile(StrUtils.UPLOAD_AVATAR_URL, p, filePath, StrUtils.MEDIA_TYPE_IMG, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onFailure(IOException e) {
                uploadImageReturned();
            }

            @Override
            public void onResponse(String s) {
                uploadImageReturned();
                ImagePipeline imagePipeline = Fresco.getImagePipeline();
                imagePipeline.evictFromCache(Uri.parse(StrUtils.thumForID(mUser.ID + "")));
                imagePipeline.evictFromCache(Uri.parse(StrUtils.avatarForID(mUser.ID+"")));
            }
        });
    }
    private void uploadImageReturned(){
        if(mEdit){
            finish();
        }else {
            Intent i = new Intent(AtyEditInfo.this, AtyLogin.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.putExtra(AtyLogin.INTENT_CLEAR, true);
            startActivity(i);
        }
    }

    private void makeToast(int string_id){
        Toast.makeText(this, string_id,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK){
            return;
        }
        if(requestCode == REQUEST_IMAGE){
            List<String> paths=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            mAvatarPath = paths.get(0);
            performCrop(mAvatarPath);
            //mDrawAvatar.setImageURI(Uri.parse("file://"+mAvatarPath));
        }else if(requestCode == REQUEST_CITY){
            String name = data.getStringExtra(AtySearchCity.INTENT_UNIVERSITY);
            tvSchool.setText(name);
        }else if (requestCode == REQUEST_CROP){
            //Bundle extras = data.getExtras();
            //avatarBitmap = extras.getParcelable("data");

                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setRoundAsCircle(true);
                mDrawAvatar.getHierarchy().setRoundingParams(roundingParams);
                mDrawAvatar.setImageURI(Uri.parse("file://" + filePath));
        }
    }

    private void performCrop(String picUri) {
        try {
            //Start Crop Activity
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            File f = new File(picUri);
            Uri contentUri = Uri.fromFile(f);

            cropIntent.setDataAndType(contentUri, "image/*");
            // set crop properties
            cropIntent.putExtra("crop", "true");
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);

            // retrieve data on return
            //cropIntent.putExtra("return-data", true);
            // start the activity - we handle returning in onActivityResult
            cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.parse("file://" + filePath));
            cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());


            startActivityForResult(cropIntent, REQUEST_CROP);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
