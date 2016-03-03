package space.weme.remix.ui.find;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import space.weme.remix.R;
import space.weme.remix.ui.base.BaseFragment;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/3/3.
 * liujilong.me@gmail.com
 */
public class FgtAddFood extends BaseFragment {
    private static final String TAG = "FgtAddFood";

    AtyAddFood aty;

    SimpleDraweeView pictureDrawee;
    EditText etTitle;
    EditText etComment;
    LinearLayout llLocation;
    LinearLayout llPrice;
    TextView tvLocation;
    TextView tvPrice;

    private String price;
    boolean pictureChosen = false;


    public static FgtAddFood newInstance() {
        Bundle args = new Bundle();
        final FgtAddFood fragment = new FgtAddFood();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fgt_add_food,container,false);
        pictureDrawee = (SimpleDraweeView) v.findViewById(R.id.fgt_add_food_picture);
        etTitle = (EditText) v.findViewById(R.id.edit_text_food_name);
        etComment = (EditText) v.findViewById(R.id.edit_text_comment);
        llLocation = (LinearLayout) v.findViewById(R.id.fgt_add_food_location);
        llPrice = (LinearLayout) v.findViewById(R.id.fgt_add_food_price);
        tvLocation = (TextView) v.findViewById(R.id.fgt_add_text_location);
        tvPrice = (TextView) v.findViewById(R.id.fgt_add_text_price);

        aty = (AtyAddFood) getActivity();

        pictureDrawee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(aty, MultiImageSelectorActivity.class);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, true);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 1);
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
                aty.startActivityForResult(intent, AtyAddFood.REQUEST_IMAGE);
            }
        });

        llPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aty.switchToFragment(aty.fgtPrice);
            }
        });

        llLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aty.switchToFragment(aty.fgtMap);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(price != null) {
            tvPrice.setText(price);
        }
        if(pictureChosen){
            pictureDrawee.setImageURI(Uri.parse("file://" + StrUtils.cropFilePath));
        }

    }

    void setPrice(String price){
        this.price = price;
    }

    @Override
    protected String tag() {
        return TAG;
    }
}
