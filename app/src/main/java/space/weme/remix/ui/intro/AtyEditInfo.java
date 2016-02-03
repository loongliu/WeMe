package space.weme.remix.ui.intro;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.community.DatePickerFragment;

/**
 * Created by Liujilong on 16/2/3.
 * liujilong.me@gmail.com
 */
public class AtyEditInfo extends BaseActivity {
    private static final String TAG = "AtyEditInfo";

    private static final int REQUEST_IMAGE = 0xef;

    private String mAvatarPath;
    private int year, month, day;

    SimpleDraweeView mDrawAvatar;
    EditText etName;
    Switch swGender;
    TextView tvBirth;
    EditText etPhone;
    TextView tvSchool;
    TextView tvEducation;
    EditText etMajor;
    EditText etWeChat;
    EditText etQQ;
    EditText etHome;
    TextView etCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_editinfo);
        bindViews();
    }

    private void bindViews(){
        mDrawAvatar = (SimpleDraweeView) findViewById(R.id.aty_editinfo_avatar);
        etName = (EditText) findViewById(R.id.aty_editinfo_edittext_name);
        swGender = (Switch) findViewById(R.id.aty_editinfo_switch_gender);
        tvBirth = (TextView) findViewById(R.id.aty_editinfo_birth);
        etPhone = (EditText) findViewById(R.id.aty_editinfo_phone);
        tvSchool = (TextView) findViewById(R.id.aty_editinfo_school);
        tvEducation = (TextView) findViewById(R.id.aty_editinfo_education);
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
                // 最大可选择图片数量
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                // 选择模式
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
                        AtyEditInfo.this.year = year;
                        AtyEditInfo.this.month = monthOfYear+1;
                        AtyEditInfo.this.day = dayOfMonth;
                        tvBirth.setText(String.format("%4d/%02d/%02d",year,monthOfYear+1,dayOfMonth));
                    }
                });
                datePicker.show(getFragmentManager(),"DatePicker");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_IMAGE && resultCode == RESULT_OK){
            List<String> paths=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            mAvatarPath = paths.get(0);
            mDrawAvatar.setImageURI(Uri.parse("file://"+mAvatarPath));
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
