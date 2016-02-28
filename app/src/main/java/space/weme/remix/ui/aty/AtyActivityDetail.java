package space.weme.remix.ui.aty;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.MediaType;
import space.weme.remix.R;
import space.weme.remix.model.AtyDetail;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.OkHttpUtils.SimpleOkCallBack;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.WDialog;

import static space.weme.remix.R.id.txt_public_author;

public class AtyActivityDetail extends SwipeActivity {
    private static final String TAG = "AtyDetail";
    public static final String INTENT = "activityid";
    private static final int MAX_PICTURE=2;
    private static final int REQUEST_IMAGE=2;
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private int activityid;
    private ArrayList<String> path;
    private TextView txtTime;
    private TextView txtLocation;
    private TextView txtSchool;
    private TextView txtAuthor;
    private TextView txtDetail;
    private TextView txtRemark;
    private TextView txtSignNumber;
    private Button btnSign;
    private Button btnLove;
    private SimpleDraweeView avatar;
    private TextView tvSlogan;

    private SimpleDraweeView atyAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_detail);

        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        activityid=getIntent().getIntExtra(INTENT,-1);
        LogUtils.d(TAG, "id:" + activityid);
        if (activityid!=-1){
            initView();
            initData();
        }
    }

    void initView() {
        atyAvatar = (SimpleDraweeView) findViewById(R.id.sign_pic);
        txtTime = (TextView) findViewById(R.id.sign_time);
        txtLocation = (TextView) findViewById(R.id.sign_location);
        txtSchool= (TextView) findViewById(R.id.txt_public_school);
        txtAuthor= (TextView) findViewById(txt_public_author);
        txtDetail= (TextView) findViewById(R.id.sign_detail);
        txtRemark= (TextView) findViewById(R.id.sign_remark);
        txtSignNumber= (TextView) findViewById(R.id.sign_number);
        btnSign= (Button) findViewById(R.id.btn_sign);
        btnLove= (Button) findViewById(R.id.btn_love);
        btnSign.setBackgroundResource(R.drawable.bg_login_btn_pressed);
        btnLove.setBackgroundResource(R.drawable.bg_login_btn_pressed);
        avatar = (SimpleDraweeView) findViewById(R.id.image);
        ViewGroup.LayoutParams params = avatar.getLayoutParams();
        params.height = DimensionUtils.getDisplay().widthPixels/2;
        avatar.setLayoutParams(params);
        tvSlogan = (TextView) findViewById(R.id.slogan);
        ((TextView) findViewById(R.id.main_title)).setText(R.string.activity_detail);
    }

    void initData(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("activityid", String.valueOf(activityid));
        OkHttpUtils.post(StrUtils.GET_ACTIVITY_DETAIL_URL, param, TAG, new SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(AtyActivityDetail.this, s);
                if (j == null)
                    return;
                JSONObject result = j.optJSONObject("result");
                final AtyDetail detail = AtyDetail.fromJSON(result);
                RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                roundingParams.setRoundAsCircle(true);
                ((TextView)findViewById(R.id.main_title)).setText(detail.title);
                atyAvatar.getHierarchy().setRoundingParams(roundingParams);
                atyAvatar.setImageURI(Uri.parse(StrUtils.thumForID(detail.authorid + "")));
                txtTime.setText(detail.time);
                txtDetail.setText(detail.detail);
                txtAuthor.setText(detail.author);
                txtSignNumber.setText(detail.signnumber);
                txtRemark.setText(detail.remark);
                txtSchool.setText(detail.school);
                txtLocation.setText(detail.location);
                if (detail.state.equals("no")) {
                    btnSign.setText("我要报名");
                    btnSign.setBackgroundResource(R.drawable.bg_login_btn_pressed);
                } else {
                    btnSign.setText("已报名");
                    btnSign.setBackgroundResource(R.drawable.bg_login_btn_common);
                }
                if (detail.likeflag.equals("0")) {
                    btnLove.setText("关注一下");
                    btnLove.setBackgroundResource(R.drawable.bg_login_btn_pressed);
                } else {
                    btnLove.setText("已关注");
                    btnLove.setBackgroundResource(R.drawable.bg_login_btn_common);
                }
                btnSign.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (detail.state.equals("no")) {
                            if (detail.whetherimage.equals("false"))
                                showDialog("确定参加活动吗？", 1);
                            else {
                                showDialog("请上传您的生活照", 5);
                            }
                        } else {
                            showDialog("是否取消参加该活动吗？", 2);
                        }
                    }
                });
                btnLove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (detail.likeflag.equals("0")) {
                            showDialog("确定关注吗？", 3);
                        } else {
                            showDialog("是否取消关注？", 4);
                        }

                    }
                });
                avatar.setImageURI(Uri.parse(detail.imageurl));
                tvSlogan.setText(detail.advertise);

            }
        });

    }

    protected void showDialog(String msg, final int flag){
        new WDialog.Builder(AtyActivityDetail.this).setMessage(msg)
                .setPositive(R.string.sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switch (flag) {
                            case 1:
                                likeSignAty(StrUtils.SIGN_ACTIVITY);
                                break;
                            case 2:
                                likeSignAty(StrUtils.DEL_SIGN_ACTIVITY);
                                break;
                            case 3:
                                likeSignAty(StrUtils.LIKE_ACTIVITY);
                                break;
                            case 4:
                                likeSignAty(StrUtils.UNLIKE_ACTIVITY);
                                break;
                            case 5:
                                chooseImage();
                                likeSignAty(StrUtils.SIGN_ACTIVITY);
                                break;
                            default:
                                LogUtils.e(TAG, "error" + flag);
                                break;
                        }
                    }
                }).show();

    }

    void likeSignAty(String url){
        ArrayMap<String,String> map=new ArrayMap<>();
        map.put("token", StrUtils.token());
        map.put("activityid", String.valueOf(activityid));
        //signup 路由有bug
        map.put("activity", String.valueOf(activityid));
        OkHttpUtils.post(url, map, TAG, new SimpleOkCallBack() {
            @Override
            public void onResponse(String s) {
                JSONObject j = OkHttpUtils.parseJSON(AtyActivityDetail.this, s);
                if (j != null)
                    initData();
            }

        });
    }

    void chooseImage(){
        Intent intent = new Intent(AtyActivityDetail.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
        // 最大可选择图片数量(多图情况下)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, MAX_PICTURE);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE&&resultCode == android.app.Activity.RESULT_OK) {
            path=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            if (path!=null&&!path.isEmpty()){
                Map<String,String> map=new ArrayMap<>();
                map.put("token", StrUtils.token());
                map.put("type","-10");
                map.put("activityid",String.valueOf(activityid));
                Toast.makeText(AtyActivityDetail.this,"正在上传生活照,请等待",Toast.LENGTH_LONG).show();
                for (int i=0;i<path.size();i++){
                    map.put("number",String.valueOf(i+1));
                    OkHttpUtils.uploadFile(StrUtils.UPLOAD_AVATAR_URL, map, path.get(i), MEDIA_TYPE_PNG, TAG, new SimpleOkCallBack() {
                        @Override
                        public void onResponse(String s) {
                            JSONObject j = OkHttpUtils.parseJSON(AtyActivityDetail.this, s);
                            if (j != null)
                                Toast.makeText(AtyActivityDetail.this, "上传成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
