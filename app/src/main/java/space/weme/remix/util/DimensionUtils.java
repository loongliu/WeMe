package space.weme.remix.util;

import android.util.DisplayMetrics;

import space.weme.remix.APP;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public final class DimensionUtils {
    public static int dp2px(int dp){
        DisplayMetrics metrics =  APP.context().getResources().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }
}
