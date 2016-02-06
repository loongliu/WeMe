package space.weme.remix.ui.user;

import android.os.Bundle;

import me.imid.swipebacklayout.lib.SwipeBackLayout;
import space.weme.remix.R;
import space.weme.remix.ui.base.SwipeActivity;
import space.weme.remix.util.DimensionUtils;

/**
 * Created by Liujilong on 16/2/6.
 * liujilong.me@gmail.com
 */
public class AtyMessageDetail extends SwipeActivity {
    private static final String TAG = "AtyMessageDetail";
    public static final String INTENT_ID = "intent_id";

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_message_detail);
        SwipeBackLayout mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeSize(DimensionUtils.getDisplay().widthPixels / 2);
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        id = getIntent().getStringExtra(INTENT_ID);
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
