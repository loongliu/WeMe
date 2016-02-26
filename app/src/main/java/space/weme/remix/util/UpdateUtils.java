package space.weme.remix.util;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.view.View;

import org.json.JSONObject;

import space.weme.remix.R;
import space.weme.remix.widgt.WDialog;

/**
 * Created by Liujilong on 2016/2/26.
 * liujilong.me@gmail.com
 */
public class UpdateUtils {
    private static String v1 = "1";
    private static String v2 = "1";
    private static String v3 = "0";

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
                showDialog(context,
                        context.getString(R.string.new_version_found),
                        context.getString(R.string.whether_update_or_not),
                        url );
            }
        });
    }

    private static void showDialog(final Context aty, String title,String message, final String url){
        new WDialog.Builder(aty).setTitle(title).setMessage(message).setPositive(R.string.update, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final String filePath = Environment.getExternalStorageDirectory()
//                        + File.separator + "weme.apk";
//                OkHttpUtils.downloadFile( url,  filePath, new OkHttpUtils.SimpleOkCallBack(){
//                    @Override
//                    public void onResponse(String s) {
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setDataAndType(Uri.fromFile(new File(filePath)), "application/vnd.android.package-archive");
//                        aty.startActivity(intent);
//                    }
//                });
            }
        }).show();

    }
}
