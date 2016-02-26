package space.weme.remix.ui.main;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import space.weme.remix.R;
import space.weme.remix.ui.aty.AtyPublicActivity;
import space.weme.remix.ui.aty.AtySearchActivity;
import space.weme.remix.ui.base.BaseActivity;
import space.weme.remix.ui.intro.AtyLogin;
import space.weme.remix.util.UpdateUtils;
import space.weme.remix.widgt.TabItem;

/**
 * Created by Liujilong on 16/1/24.
 * liujilong.me@gmail.com
 */
public class AtyMain extends BaseActivity implements Toolbar.OnMenuItemClickListener {

    private static final String TAG = "AtyMain";
    public static final String INTENT_LOGOUT = "intent_lougout";
    public static final String INTENT_UPDATE = "intent_update";

    private static final int PAGE_COUNT = 4;

    private int[] mTitleTexts = new int[]{
            R.string.activity,
            R.string.community,
            R.string.find,
            R.string.me
    };

    private TabItem[] tabItems;
    private ViewPager mPager;
    private TextView mTvTitle;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_main);
        if (getIntent().getBooleanExtra(INTENT_LOGOUT, false))
        {
            Intent i = new Intent(AtyMain.this,AtyLogin.class);
            startActivity(i);
            finish();
            return;
        }
        if(getIntent().getBooleanExtra(INTENT_UPDATE,false)){
            UpdateUtils.checkUpdate(AtyMain.this);
        }
        bindViews();
    }

    private void bindViews(){
        tabItems = new TabItem[4];
        tabItems[0] = (TabItem) findViewById(R.id.main_item_activity);
        tabItems[1] = (TabItem) findViewById(R.id.main_item_community);
        tabItems[2] = (TabItem) findViewById(R.id.main_item_find);
        tabItems[3] = (TabItem) findViewById(R.id.main_item_me);
        tabItems[0].setEnable(true);

        toolbar= (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(this);

        mPager = (ViewPager) findViewById(R.id.main_pager);
        mTvTitle = (TextView) findViewById(R.id.main_title);
        mTvTitle.setText(R.string.activity);

        Adapter mAdapter = new Adapter(getFragmentManager());
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
            @Override
            public void onPageSelected(int position) {
                mTvTitle.setText(mTitleTexts[position]);
                for(int i = 0;i<PAGE_COUNT; i++){
                    tabItems[i].setEnable(i==position);
                }
                if (position==0)
                    toolbar.inflateMenu(R.menu.menu_main);
                else {
                    toolbar.getMenu().clear();
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {}
        });
        View.OnClickListener mTabItemClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int p = (int) v.getTag();
                mPager.setCurrentItem(p);
            }
        };
        for(int i = 0; i<PAGE_COUNT; i++){
            tabItems[i].setTag(i);
            tabItems[i].setOnClickListener(mTabItemClickListener);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_search:
                Intent search=new Intent(AtyMain.this, AtySearchActivity.class);
                startActivity(search);
                return true;
            case R.id.action_public:
                Intent publicActivity=new Intent(AtyMain.this, AtyPublicActivity.class);
                startActivity(publicActivity);
                return true;
            case R.id.action_2code:
                return true;
            default:
                break;
        }
        return false;
    }

    public class Adapter extends FragmentPagerAdapter {

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position){
                case 0:
                    return FgtActivity.newInstance();
                case 1:
                    return FgtCommunity.newInstance();
                case 2:
                    return FgtFind.newInstance();
                case 3:
                    return FgtMe.newInstance();
                default:
                    throw new RuntimeException("position can not be larger than 3");
            }
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
