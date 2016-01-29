package space.weme.remix.ui.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.Post;
import space.weme.remix.ui.AtyImage;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.GridLayout;

/**
 * Created by Liujilong on 2016/1/28.
 * liujilong.me@gmail.com
 */
public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private List<Post> mPostList;

    private final int TYPE_ITEM = 0x2;
    private final int TYPE_PROGRESS = 0x3;

    private View.OnClickListener mAvatarListener, mImageListener;


    public TopicAdapter(Context context){
        mContext = context;
        mAvatarListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = (String) v.getTag();
                // TODO goto user home page
            }
        };
        mImageListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = (String) v.getTag();
                Intent i = new Intent(mContext, AtyImage.class);
                i.putExtra(AtyImage.URL_INTENT, url);
                mContext.startActivity(i);
                ((Activity)mContext).overridePendingTransition(0, 0);
            }
        };
    }

    public void setPostList(List<Post> postList){
        mPostList = postList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        if(viewType == TYPE_ITEM){
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_topic_item,parent,false);
            holder = new ItemViewHolder(v);
        }else if(viewType == TYPE_PROGRESS) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_topic_progress, parent, false);
            holder = new ProgressViewHolder(v);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            ItemViewHolder item = (ItemViewHolder) holder;
            Post post = mPostList.get(position);
            item.avatar.setImageURI(Uri.parse(StrUtils.thumForID(post.userId)));
            item.avatar.setTag(post.userId);
            item.avatar.setOnClickListener(mAvatarListener);
            item.userName.setText(post.name);
            item.university.setText(post.school);
            item.time.setText(post.timestamp);
            item.title.setText(post.title);
            item.content.setText(post.body);
            item.like_number.setText(post.likenumber);
            item.comment_number.setText(post.commentnumber);
            item.grid.removeAllViews();
            for(int i = 0; i<post.thumbnailUrl.size(); i++) {
                String url = post.thumbnailUrl.get(i);
                SimpleDraweeView image = new SimpleDraweeView(mContext);
                item.grid.addView(image);
                image.setImageURI(Uri.parse(url));
                image.setTag(post.imageUrl.get(i));
                image.setOnClickListener(mImageListener);
            }
            item.itemView.setTag(post.postId);
            // todo post click listener;
        }else if(holder instanceof ProgressViewHolder){
            ProgressViewHolder progress = (ProgressViewHolder) holder;
            progress.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(mPostList.get(position)!=null){
            return TYPE_ITEM;
        }else{
            return TYPE_PROGRESS;
        }
    }

    @Override
    public int getItemCount() {
        return mPostList==null?0:mPostList.size();
    }


    private static class ItemViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView avatar;
        TextView userName;
        TextView university;
        TextView time;
        TextView title;
        TextView content;
        TextView like_number;
        TextView comment_number;
        GridLayout grid;
        public ItemViewHolder(View itemView) {
            super(itemView);
            avatar = (SimpleDraweeView) itemView.findViewById(R.id.aty_topic_item_avatar);
            userName = (TextView) itemView.findViewById(R.id.aty_topic_item_name);
            university = (TextView) itemView.findViewById(R.id.aty_topic_item_university);
            time = (TextView) itemView.findViewById(R.id.aty_topic_item_time);
            title = (TextView) itemView.findViewById(R.id.aty_topic_item_title);
            content = (TextView) itemView.findViewById(R.id.aty_topic_item_content);
            like_number = (TextView) itemView.findViewById(R.id.aty_topic_item_like_number);
            comment_number = (TextView) itemView.findViewById(R.id.aty_topic_item_comment_number);
            grid = (GridLayout) itemView.findViewById(R.id.aty_topic_item_grid);
            grid.setNumInRow(4);
        }
    }

    private static class ProgressViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress);
        }
    }


}
