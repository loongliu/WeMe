package space.weme.remix.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.support.annotation.ColorInt;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class BitmapUtils {
    public static Bitmap changeColor(Bitmap b, @ColorInt int color){
        Bitmap resultBitmap = Bitmap.createBitmap(b, 0, 0,
                b.getWidth() - 1, b.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(color, 0);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }
}
