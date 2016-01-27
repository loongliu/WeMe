package space.weme.remix.util;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

    // reserve all the running calls for cancel.
    private ArrayMap<String,Set<Call>> mRunningCalls;

    private static OkHttpUtils mInstance;
    private OkHttpClient mClient;
    private Handler mHandler;

    private OkHttpUtils(){
        mClient = new OkHttpClient();
        mHandler = new Handler(Looper.getMainLooper());
        mRunningCalls = new ArrayMap<>();
    }

    private synchronized static OkHttpUtils getInstance(){
        if(mInstance==null){
            mInstance = new OkHttpUtils();
        }
        return mInstance;
    }

    public static void post(String url, Map<String,String> params, Callback callback){
        post(url, params, null, callback);
    }

    public static void post(String url, Map<String,String> params,String tag, Callback callback){
        JSONObject j = new JSONObject(params);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), j.toString());
        Request.Builder builder = new Request.Builder().url(url).post(body);
        if(tag!=null) builder.tag(tag);
        Request request = builder.build();
        getInstance().firePost(request, callback);
    }

    private void firePost(Request request, final Callback callback){
        Call call = mClient.newCall(request);
        cacheCallForCancel(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (isCancel(call)) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailure(call, e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (isCancel(call)) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            callback.onResponse(call, response);
                        } catch (IOException e) {
                            callback.onFailure(call, e);
                        }
                    }
                });
            }
        });
    }

    private void cacheCallForCancel(Call call){
        String tag = (String) call.request().tag();
        if(tag!=null){
            Set<Call> calls = mRunningCalls.get(tag);
            if(calls == null){
                calls = new HashSet<>();
            }
            calls.add(call);
            mRunningCalls.put(tag,calls);
        }
    }

    private boolean isCancel(Call call){
        String tag = (String) call.request().tag();
        if(tag==null) return false;
        Set<Call> calls = mRunningCalls.get(tag);
        if(calls == null) return true;
        calls.remove(call);
        if(calls.isEmpty()){
            mRunningCalls.remove(tag);
        }
        return false;
    }

    public static void cancel(String tag){
        getInstance().mRunningCalls.remove(tag);
    }

}
