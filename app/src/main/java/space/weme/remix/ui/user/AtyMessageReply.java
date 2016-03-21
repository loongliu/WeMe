package space.weme.remix.ui.user;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import space.weme.remix.R;
import space.weme.remix.ui.base.AtyImage;
import space.weme.remix.util.BitmapUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.GridLayout;

/**
 * Created by Liujilong on 16/2/6.
 * liujilong.me@gmail.com
 */
public class AtyMessageReply extends AtyImage {
    private static final String TAG = "AtyMessageReply";
    public static final String INTENT_ID = "intent_id";
    public static final String INTENT_REPLY = "intent_reply";
    private String sendID;

    TextView mSend;
    EditText mEditor;
    ProgressDialog mProgressDialog;
    SimpleDraweeView mDrawAddImage;
    GridLayout mImageGrids;

    private AtomicInteger mSendImageResponseNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_message_reply);
        sendID = getIntent().getStringExtra(INTENT_ID);
        boolean reply = getIntent().getBooleanExtra(INTENT_REPLY,false);

        TextView tvTitle = (TextView) findViewById(R.id.aty_message_reply_title);
        if(reply){
            tvTitle.setText(R.string.reply_message);
        }else{
            tvTitle.setText(R.string.message);
        }

        mDrawAddImage = (SimpleDraweeView) findViewById(R.id.add_image);
        mDrawAddImage.setImageURI(Uri.parse("res:/" + R.mipmap.add_image));
        mDrawAddImage.setOnClickListener(mListener);
        mSend = (TextView) findViewById(R.id.send);
        mEditor = (EditText) findViewById(R.id.main_editor);
        mImageGrids = (GridLayout) findViewById(R.id.select_image_view);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditor.getText().length()==0){
                    return;
                }
                reply();
            }
        });
        mChosenPicturePathList = new ArrayList<>();
        mSendImageResponseNum = new AtomicInteger();
    }

    private void reply(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token", StrUtils.token());
        param.put("text",mEditor.getText().toString());
        param.put("RecId", sendID);
        mProgressDialog = ProgressDialog.show(AtyMessageReply.this, null, getResources().getString(R.string.committing));
        OkHttpUtils.post(StrUtils.SEND_MESSAGE,param,TAG, new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onFailure(IOException e) {
                mProgressDialog.dismiss();
            }

            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(AtyMessageReply.this, s);
                if(j == null){
                    mProgressDialog.dismiss();
                    return;
                }
                if(mChosenPicturePathList.size()==0){
                    setResult(RESULT_OK);
                    finish();
                    return;
                }
                String id = j.optString("id");
                ArrayMap<String,String> p = new ArrayMap<>();
                p.put("token",StrUtils.token());
                p.put("type","-2");
                p.put("messageid",id);
                p.put("number",mChosenPicturePathList.size()+"");
                mSendImageResponseNum.set(0);
                for(int number = 0; number<mChosenPicturePathList.size(); number++){
                    p.put("number", String.format("%d", number));
                    String path = mChosenPicturePathList.get(number);
                    OkHttpUtils.uploadFile(StrUtils.UPLOAD_AVATAR_URL,p,path,StrUtils.MEDIA_TYPE_IMG,TAG,new OkHttpUtils.SimpleOkCallBack(){
                        @Override
                        public void onFailure(IOException e) {
                            uploadImageReturned();
                        }

                        @Override
                        public void onResponse(String s) {
                            uploadImageReturned();
                        }
                    });
                }
            }
        });
    }

    private void uploadImageReturned(){
        int num = mSendImageResponseNum.incrementAndGet();
        if(num == mChosenPicturePathList.size()){
            setResult(RESULT_OK);
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK){
            List<String> paths=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            mChosenPicturePathList.clear();
            mChosenPicturePathList.addAll(paths);
            mImageGrids.removeAllViews();
            for(String path : mChosenPicturePathList){
                SimpleDraweeView image = new SimpleDraweeView(AtyMessageReply.this);
                int size = mImageGrids.getCellSize();
                BitmapUtils.showResizedPicture(image,Uri.parse("file://"+path), size, size);
                mImageGrids.addView(image);
                image.setOnClickListener(mListener);
            }
            if(mImageGrids.getChildCount()<9){
                mImageGrids.addView(mDrawAddImage);
            }
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
