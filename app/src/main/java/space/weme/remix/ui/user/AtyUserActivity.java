package space.weme.remix.ui.user;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import space.weme.remix.R;
import space.weme.remix.ui.base.BaseActivity;

/**
 * Created by Liujilong on 16/2/7.
 * liujilong.me@gmail.com
 */
public class AtyUserActivity extends BaseActivity {
    private static final String TAG = "AtyUserActivity";

    TabLayout tabLayout;
    ViewPager viewPager;
    ActivityPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_user_activity);
        tabLayout = (TabLayout) findViewById(R.id.aty_user_activity_tab);
        viewPager = (ViewPager) findViewById(R.id.aty_user_activity_pager);

        adapter = new ActivityPagerAdapter(getFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    }

    @Override
    protected String tag() {
        return TAG;
    }

    private class ActivityPagerAdapter extends FragmentPagerAdapter{

        public ActivityPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return FgtUserActivity.newInstance(position);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String[] titles = getResources().getStringArray(R.array.aty_user_activity_titles);
            return titles[position];
        }
    }
}
