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

    private static final String TAG = "OkHttpUtils";

    // reserve all the running calls for cancel.
    private final ArrayMap<String,Set<Call>> mRunningCalls;

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

    @SuppressWarnings("unused")
    public static void post(String url, Map<String,String> params, OkCallBack callback){
        post(url, params, null, callback);
    }

    public static void post(String url, Map<String,String> params,String tag, OkCallBack callback){
        JSONObject j = new JSONObject(params);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), j.toString());
        Request.Builder builder = new Request.Builder().url(url).post(body);
        if(tag!=null) builder.tag(tag);
        Request request = builder.build();
        getInstance().firePost(request, callback);
    }

    private void firePost(Request request, final OkCallBack callback){
        Call call = mClient.newCall(request);
        cacheCallForCancel(call);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (isCancel(call)) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isCancel(call, true)) return;
                        callback.onFailure(e);
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws IOException {
                if (isCancel(call)) return;
                final String s = response.body().string();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (isCancel(call, true)) return;
                        callback.onResponse(s);
                    }
                });
            }
        });
    }

    private void cacheCallForCancel(Call call){
        String tag = (String) call.request().tag();
        if(tag!=null){
            synchronized (mRunningCalls) {
                LogUtils.d(TAG,"start caching Call: " + printCall(call) + " \ncurrent running calls: "+printRunningCalls());
                Set<Call> calls = mRunningCalls.get(tag);
                if (calls == null) {
                    calls = new HashSet<>();
                }
                calls.add(call);
                mRunningCalls.put(tag, calls);
                LogUtils.d(TAG,"finish caching Call: " + printCall(call) + " \ncurrent running calls: "+printRunningCalls());
            }
        }
    }

    private static String printRunningCalls(){
        return "\n"+getInstance().mRunningCalls.toString();
    }
    private String printCall(Call call){
        return "call: " + call.toString() + " with tag " + call.request().tag();
    }

    private boolean isCancel(Call call){
        return isCancel(call,false);
    }
    private boolean isCancel(Call call, boolean remove){
        String tag = (String) call.request().tag();
        if(tag==null) return false;
        synchronized (mRunningCalls) {
            LogUtils.d(TAG,"start remove Call: " + printCall(call) + " \ncurrent running calls: "+printRunningCalls());
            Set<Call> calls = mRunningCalls.get(tag);
            if (calls == null || !calls.contains(call)) return true;
            if(remove) {
                calls.remove(call);
                if (calls.isEmpty()) {
                    mRunningCalls.remove(tag);
                    LogUtils.d(TAG, "finish remove Call: " + printCall(call) + " \ncurrent running calls: " + printRunningCalls());
                }
            }
        }
        return false;
    }

    public static void cancel(String tag){
        LogUtils.d(TAG,"start remove Calls with tag: " + tag + " \ncurrent running calls: "+printRunningCalls());
        getInstance().mRunningCalls.remove(tag);
        LogUtils.d(TAG, "finish remove Calls with tag: " + tag + " \ncurrent running calls: " + printRunningCalls());
    }

    public interface OkCallBack{
        void onFailure(IOException e);
        void onResponse(String res);
    }
    public static class SimpleOkCallBack implements OkCallBack{
        @Override
        public void onFailure(IOException e) {
        }
        @Override
        public void onResponse(String res) {
        }
    }

}
