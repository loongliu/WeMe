package space.weme.remix.ui.community;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.MediaType;
import space.weme.remix.R;
import space.weme.remix.ui.base.AtyImage;
import space.weme.remix.util.BitmapUtils;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.GridLayout;

/**
 * Created by Liujilong on 2016/1/31.
 * liujilong.me@gmail.com
 */
public class AtyPostReply extends AtyImage {
    private static final String TAG = "AtyPostReply";
    public static final String INTENT_ID = "intent_id";
    public static final String INTENT_CONTENT = "intent_content";
    protected static final MediaType MEDIA_TYPE_JPG = MediaType.parse("image/*");


    private String mPostID;
    TextView mSend;
    EditText mEditor;
    ProgressDialog mProgressDialog;
    SimpleDraweeView mDrawAddImage;
    GridLayout mImageGrids;

    private AtomicInteger mSendImageResponseNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_post_aty_reply);
        mPostID = getIntent().getStringExtra(INTENT_ID);
        String content = getIntent().getStringExtra(INTENT_CONTENT);

        mDrawAddImage = (SimpleDraweeView) findViewById(R.id.add_image);
        mDrawAddImage.setImageURI(Uri.parse("res:/" + R.mipmap.add_image));
        mDrawAddImage.setOnClickListener(mListener);
        mSend = (TextView) findViewById(R.id.send);
        mEditor = (EditText) findViewById(R.id.main_editor);
        mEditor.setText(content);
        mImageGrids = (GridLayout) findViewById(R.id.select_image_view);

        mSend.setOnClickListener(new ReplyListener());
        mChosenPicturePathList = new ArrayList<>();
        mSendImageResponseNum = new AtomicInteger();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE && resultCode == Activity.RESULT_OK){
            List<String> paths=data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
            mChosenPicturePathList.clear();
            mChosenPicturePathList.addAll(paths);
            mImageGrids.removeAllViews();
            for(String path : mChosenPicturePathList){
                SimpleDraweeView image = new SimpleDraweeView(AtyPostReply.this);
                int size = mImageGrids.getCellSize();
                BitmapUtils.showResizedPicture(image,Uri.parse("file://"+path), size,size);
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

    private class ReplyListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if(mEditor.getText().length()==0){
                return;
            }
            mProgressDialog = ProgressDialog.show(AtyPostReply.this, null, getResources().getString(R.string.committing));
            ArrayMap<String,String> map  = new ArrayMap<>();
            map.put("token", StrUtils.token());
            map.put("postid", mPostID);
            map.put("body", mEditor.getText().toString());
            OkHttpUtils.post(StrUtils.COMMENT_TO_POST_URL,map,TAG,new OkHttpUtils.SimpleOkCallBack(){
                @Override
                public void onFailure(IOException e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(AtyPostReply.this, R.string.reply_failed, Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onResponse(String s) {
                    LogUtils.i(TAG,s);
                    JSONObject j = OkHttpUtils.parseJSON(AtyPostReply.this,s);
                    if(j == null){
                        mProgressDialog.dismiss();
                        Toast.makeText(AtyPostReply.this, R.string.reply_failed, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String id = j.optString("id");
                    if(mChosenPicturePathList.size()==0) {
                        setResult(RESULT_OK);
                        finish();
                        return;
                    }
                    ArrayMap<String,String> p = new ArrayMap<>();
                    p.put("token",StrUtils.token());
                    p.put("type","-7");
                    p.put("commentid",id);
                    mSendImageResponseNum.set(0);
                    for(int number = 0; number<mChosenPicturePathList.size(); number++){
                        p.put("number", String.format("%d", number));
                        String path = mChosenPicturePathList.get(number);
                        OkHttpUtils.uploadFile(StrUtils.UPLOAD_AVATAR_URL,p,path,MEDIA_TYPE_JPG,TAG,new OkHttpUtils.SimpleOkCallBack(){
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
    }

    private void uploadImageReturned(){
        int num = mSendImageResponseNum.incrementAndGet();
        if(num == mChosenPicturePathList.size()){
            setResult(RESULT_OK);
            finish();
        }

    }
}
