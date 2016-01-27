package space.weme.remix.ui.main;

import android.content.Context;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import space.weme.remix.model.TopInfoWrapper;

/**
 * Created by Liujilong on 2016/1/27.
 * liujilong.me@gmail.com
 */
public class TopPageAdapter extends PagerAdapter {
    List<TopInfoWrapper> infoList;
    Context context;

    public TopPageAdapter(Context context){
        this.context = context;
    }

    public void setInfoList(List<TopInfoWrapper> infoList) {
        this.infoList = infoList;
    }

    @Override
    public int getCount() {
        return infoList==null?0:infoList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        SimpleDraweeView image = new SimpleDraweeView(context);
        Uri uri = Uri.parse(infoList.get(position).url);
        image.setImageURI(uri);
        container.addView(image);
        return image;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
