package space.weme.remix.util;

import android.os.Handler;
import android.os.Looper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Liujilong on 16/1/22.
 * liujilong.me@gmail.com
 */
public final class OkHttpUtils {

    private static OkHttpUtils mInstance;
    private OkHttpClient mClient;
    private Handler mHandler;

    private OkHttpUtils(){
        mClient = new OkHttpClient();
        mHandler = new Handler(Looper.getMainLooper());
    }

    private synchronized static OkHttpUtils getInstance(){
        if(mInstance==null){
            mInstance = new OkHttpUtils();
        }
        return mInstance;
    }

    public static void post(String url, Map<String,String> params, Callback callback){
        JSONObject j = new JSONObject(params);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"),j.toString());
        Request request = new Request.Builder().url(url).post(body).build();
        getInstance().firePost(request, callback);
    }

    private void firePost(Request request, final Callback callback){
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                callback.onFailure(call, e);
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onResponse(call,response);
                        } catch (IOException e) {
                            callback.onFailure(call, e);
                        }
                    }
                });
            }
        });
    }


}
