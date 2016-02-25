package space.weme.remix.ui.aty;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequestBuilder;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import space.weme.remix.R;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

public class AtyPublicActivity extends SwipeActivity {

    private static final String TAG = "AtyPublicActivity";
    private static final int REQUEST_IMAGE = 2;

    private SimpleDraweeView actAdd;
    private TextView txtPublic;
    private ArrayList<String> path;

    private EditText editTitle;
    private EditText editTime;
    private EditText editLocation;
    private EditText editNumber;
    private EditText editAdvertise;
    private EditText editDetail;
    private EditText editLabe;
    private ToggleButton toggleButton;
    private boolean checked=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_public_activity);

        initView();

        actAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AtyPublicActivity.this, MultiImageSelectorActivity.class);
                // 是否显示拍摄图片
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                // 最大可选择图片数量(多图情况下)
                //intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, Config.MAX_PICTURE);
                // 选择模式
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);

                startActivityForResult(intent, REQUEST_IMAGE);
            }
        });

        txtPublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(path!=null&&!path.isEmpty()
                        &&getEditText()!=null){
                    new AlertDialog.Builder(AtyPublicActivity.this)
                            .setTitle("提示")
                            .setMessage("确定发布活动吗？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    publicActivity(getEditText());
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            })
                            .show();
                }
                else{
                    new AlertDialog.Builder(AtyPublicActivity.this)
                            .setTitle("提示")
                            .setMessage("请填写必填信息")
                            .setPositiveButton("确认",null).show();
                }

            }
        });

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleButton.setChecked(isChecked);
                checked=isChecked;
                LogUtils.d(TAG, "isChecked " + isChecked);
            }
        });


    }

    void initView(){
        txtPublic= (TextView) findViewById(R.id.txt_activity_public);
        actAdd= (SimpleDraweeView) findViewById(R.id.img_act_add);
        editAdvertise= (EditText) findViewById(R.id.edit_advertise);
        editDetail= (EditText) findViewById(R.id.edit_detail);
        editLocation= (EditText) findViewById(R.id.edit_location);
        editLabe= (EditText) findViewById(R.id.edit_labe);
        editTitle= (EditText) findViewById(R.id.edit_title);
        editTime= (EditText) findViewById(R.id.edit_time);
        editLabe= (EditText) findViewById(R.id.edit_labe);
        editNumber= (EditText) findViewById(R.id.edit_number);
        toggleButton= (ToggleButton) findViewById(R.id.tog_btn);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE&&resultCode == Activity.RESULT_OK){
            path=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            Uri uri=Uri.fromFile(new File(path.get(0)));

           // Uri uri = "file:///mnt/sdcard/MyApp/myfile.jpg";
            //解决图片大于4M不显示问题(由于openGL最大支持4M，有局限只能缩小图片了)
            LogUtils.e(TAG, actAdd.getLayoutParams().width + " " + actAdd.getLayoutParams().height);
            ImageRequestBuilder imageRequestBuilder = ImageRequestBuilder.newBuilderWithSource(uri)
                    .setAutoRotateEnabled(true)
                    .setResizeOptions(new ResizeOptions(actAdd.getLayoutParams().width, actAdd.getLayoutParams().height));

            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(imageRequestBuilder.build())
                    .setOldController(actAdd.getController())
                    .build();

            actAdd.setController(controller);
            LogUtils.d(TAG, "uri " + uri.toString());
            //     actAdd.setImageURI(uri);
        }
    }

    Map<String,String> getEditText(){
        Map<String,String> map=new ArrayMap<>();
        map.put("token", StrUtils.token());
        map.put("title",editTitle.getText().toString());
        map.put("location",editLocation.getText().toString());
        map.put("number",editNumber.getText().toString());
        map.put("time",editTime.getText().toString());
        map.put("advertise",editAdvertise.getText().toString());

        map.put("whetherimage",checked?"true":"false");
        map.put("detail",editDetail.getText().toString());
        map.put("labe",editLabe.getText().toString());
        return map;
    }

    void publicActivity(Map<String,String> map){
        OkHttpUtils.post(StrUtils.PUBLISH_ACTIVITY, map, TAG, new OkHttpUtils.SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(AtyPublicActivity.this, s);
                String state = j.optString("state");
                if (state != null && state.equals("successful")) {
                    new AlertDialog.Builder(AtyPublicActivity.this)
                            .setTitle("提示")
                            .setMessage("已发布活动")
                            .setPositiveButton("确认", null).show();
                } else {
                    new AlertDialog.Builder(AtyPublicActivity.this)
                            .setTitle("提示")
                            .setMessage("活动发布失败")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    onBackPressed();
                                }
                            }).show();
                }
            }
        });
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
