package space.weme.remix.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.util.ArrayMap;
import android.view.View;

import org.json.JSONObject;

import java.io.File;

import space.weme.remix.R;
import space.weme.remix.widgt.WDialog;

/**
 * Created by Liujilong on 2016/2/26.
 * liujilong.me@gmail.com
 */
public class UpdateUtils {
    private static String v1 = "1";
    private static String v2 = "3";
    private static String v3 = "3";

    public static void checkUpdate(final Context context){
        ArrayMap<String,String> map = new ArrayMap<>();
        map.put("v1",v1);
        map.put("v2",v2);
        map.put("v3",v3);
        LogUtils.d("UpdateUtils", map.toString());
        OkHttpUtils.post(StrUtils.CHECK_UPDATE_URL,map,"UpdateUtils",new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.d("UpdateUtils",s);
                JSONObject j = OkHttpUtils.parseJSON(context, s);
                if(j == null){
                    return;
                }
                String flag = j.optString("updateflag");
                if(!flag.equals("yes")){
                    return;
                }
                String url = j.optString("apkurl");
                JSONObject version = j.optJSONObject("version_newest");
                if(version == null){
                    return;
                }
                String v1 = version.optString("v1");
                String v2 = version.optString("v2");
                String v3 = version.optString("v3");
                final String filePath = Environment.getExternalStorageDirectory()
                        + File.separator + "weme_"+v1+"_"+v2+"_"+v3+".apk";
                showDialog(context,
                        context.getString(R.string.whether_update_or_not),
                        url , filePath);
            }
        });
    }

    private static void showDialog(final Context aty, String message, final String url,final String filePath){
        new WDialog.Builder(aty).setMessage(message).setPositive(R.string.update, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OkHttpUtils.downloadFile( url,  filePath,false, new OkHttpUtils.SimpleOkCallBack(){
                    @Override
                    public void onResponse(String s) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
                        aty.startActivity(intent);
                    }
                });
            }
        }).show();

    }
}
