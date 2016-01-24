package space.weme.remix;

import android.app.Application;
import android.content.Context;

/**
 * Created by Liujilong on 16/1/20.
 * liujilong.me@gmail.com
 */
public class APP extends Application {
    private static APP mSingleton;
    public static Context context(){
        return mSingleton.getApplicationContext();
    }

    public void onCreate(){
        super.onCreate();
        mSingleton = this;
    }
}
