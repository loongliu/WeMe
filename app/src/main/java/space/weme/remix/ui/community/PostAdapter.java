package space.weme.remix.ui.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import org.json.JSONObject;

import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.Commit;
import space.weme.remix.model.Post;
import space.weme.remix.model.Reply;
import space.weme.remix.ui.AtyImage;
import space.weme.remix.ui.user.AtyInfo;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.GridLayout;

/**
 * Created by Liujilong on 2016/1/30.
 * liujilong.me@gmail.com
 */
public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;
    AtyPost aty;

    private static final String TAG = "PostAdapter";

    Post mPost;
    List<Reply> mReplyList;

    View.OnClickListener mListener;

    private static final int VIEW_TITLE = 1;
    private static final int VIEW_ITEM = 2;
    private static final int VIEW_PROGRESS = 3;

    private int imageID = StrUtils.generateViewId();
    private int avatarId = StrUtils.generateViewId();


    PostAdapter(Context context){
        mContext = context;
        aty = (AtyPost) context;
        mListener = new PostListener();
    }

    void setPost(Post post){
        mPost = post;
    }
    void setReplyList(List<Reply> list){
        mReplyList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder;
        if(viewType == VIEW_TITLE){
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_post_title,parent,false);
            holder = new PostViewHolder(v);
        }else if(viewType == VIEW_ITEM){
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_post_reply,parent,false);
            holder = new ItemViewHolder(v);
        }else{
            View v = LayoutInflater.from(mContext).inflate(R.layout.aty_post_progress,parent,false);
            holder = new ProgressViewHolder(v);
        }
        return holder;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof PostViewHolder){
            if(mPost==null) { return; }
            PostViewHolder viewHolder = (PostViewHolder) holder;
            viewHolder.avatarDraw.setImageURI(Uri.parse(StrUtils.thumForID(mPost.userId)));
            viewHolder.avatarDraw.setTag(mPost.userId);
            viewHolder.avatarDraw.setOnClickListener(mListener);
            viewHolder.tvName.setText(mPost.name);
            viewHolder.tvUniversity.setText(mPost.school);
            viewHolder.tvTime.setText(mPost.timestamp);
            viewHolder.tvTitle.setText(mPost.title);
            viewHolder.tvContent.setText(mPost.body);
            viewHolder.imagesGridLayout.removeAllViews();
            for(int i = 0; i<mPost.thumbnailUrl.size(); i++) {
                String thumbUrl = mPost.thumbnailUrl.get(i);
                SimpleDraweeView image = new SimpleDraweeView(mContext);
                viewHolder.imagesGridLayout.addView(image);
                image.setImageURI(Uri.parse(thumbUrl));
                image.setTag(mPost.imageUrl.get(i));
                image.setId(imageID);
                image.setOnClickListener(mListener);
            }
            viewHolder.tvLikeNumber.setText(mPost.likenumber);
            viewHolder.tvCommit.setText(mPost.commentnumber);
            if(mPost.flag.equals("0")){
                viewHolder.ivLike.setImageResource(R.mipmap.like_off);
                viewHolder.likeLayout.setOnClickListener(mListener);
            }else{
                viewHolder.ivLike.setImageResource(R.mipmap.like_on);
            }
            viewHolder.commitLayout.setOnClickListener(mListener);
            viewHolder.llLikePeoples.removeAllViews();
            for(int id : mPost.likeusers){
                SimpleDraweeView avatar = (SimpleDraweeView) LayoutInflater.from(mContext).
                        inflate(R.layout.aty_post_avatar,viewHolder.llLikePeoples,false);
                viewHolder.llLikePeoples.addView(avatar);
                avatar.setImageURI(Uri.parse(StrUtils.thumForID(Integer.toString(id))));
                avatar.setTag(String.format("%d", id));
                avatar.setId(avatarId);
                avatar.setOnClickListener(mListener);
                // todo show more
            }
        }else if(holder instanceof ItemViewHolder){
            final Reply reply = mReplyList.get(position-1);
            if(reply == null){return;}
            final ItemViewHolder item = (ItemViewHolder) holder;
            item.avatarDraw.setImageURI(Uri.parse(StrUtils.thumForID(reply.userid)));
            item.avatarDraw.setTag(reply.userid);
            item.avatarDraw.setOnClickListener(mListener);
            item.tvName.setText(reply.name);
            item.tvUniversity.setText(reply.school);
            item.tvTime.setText(reply.timestamp);
            item.tvContent.setText(reply.body);
            item.tvLike.setText(String.format("%d", reply.likenumber));
            item.tvCommit.setText(String.format("%d", reply.commentnumber));
            if(reply.flag.equals("0")){
                item.ivLike.setImageResource(R.mipmap.like_off);
                item.llLike.setTag(position);
                item.llLike.setOnClickListener(mListener);
            }else{
                item.ivLike.setImageResource(R.mipmap.like_on);
            }
            item.llCommit.setTag(reply);
            item.llCommit.setOnClickListener(mListener);
            item.llReplyList.removeAllViews();
            item.llReplyList.setVisibility(reply.reply.size() == 0 ? View.GONE : View.VISIBLE);
            for(Commit commitReply : reply.reply){
                TextView tv = new TextView(mContext);
                SpannableStringBuilder builder = new SpannableStringBuilder();
                builder.append(commitReply.name);
                int color = mContext.getResources().getColor(R.color.colorPrimary);
                builder.setSpan(new ForegroundColorSpan(color), 0, builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                if(commitReply.destcommentid!=null){
                    builder.append(mContext.getString(R.string.reply));
                    int len = builder.length();
                    builder.append(commitReply.destname);
                    builder.setSpan(new ForegroundColorSpan(color),len,builder.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                }
                builder.append(":").append(commitReply.body);
                tv.setText(builder);
                item.llReplyList.addView(tv);
            }
            item.imagesGridLayout.removeAllViews();
            if(reply.image==null || reply.image.size()==0){
                item.imagesGridLayout.setVisibility(View.GONE);
            }else {
                item.imagesGridLayout.setVisibility(View.VISIBLE);
                for(int i = 0; i<reply.thumbnail.size(); i++){
                    SimpleDraweeView drawView = new SimpleDraweeView(mContext);
                    drawView.setImageURI(Uri.parse(reply.thumbnail.get(i)));
                    drawView.setTag(reply.image.get(i));
                    drawView.setId(imageID);
                    drawView.setOnClickListener(mListener);
                    item.imagesGridLayout.addView(drawView);
                }
            }
        }else {
            ProgressViewHolder progress = (ProgressViewHolder)holder;
            progress.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return VIEW_TITLE;
        }else if(mReplyList.get(position-1)!=null){
            return VIEW_ITEM;
        }else{
            return VIEW_PROGRESS;
        }
    }

    @Override
    public int getItemCount() {
        return 1+(mReplyList==null?0:mReplyList.size());
    }








    class PostViewHolder extends RecyclerView.ViewHolder {
        SimpleDraweeView avatarDraw;
        TextView tvName;
        TextView tvUniversity;
        TextView tvTime;
        TextView tvTitle;
        TextView tvContent;
        GridLayout imagesGridLayout; // post images

        TextView tvLikeNumber; // show like number
        ImageView ivLike;
        // for like clickListener
        LinearLayout likeLayout;

        TextView tvCommit; // show commit number
        LinearLayout commitLayout; // for commit listener

        LinearLayout llLikePeoples; // liked people

        public PostViewHolder(View itemView) {
            super(itemView);
            avatarDraw = (SimpleDraweeView) itemView.findViewById(R.id.aty_post_title_avatar);
            tvName = (TextView) itemView.findViewById(R.id.aty_post_title_user);
            tvUniversity = (TextView) itemView.findViewById(R.id.aty_post_title_university);
            tvTime = (TextView) itemView.findViewById(R.id.aty_post_title_time);
            tvTitle = (TextView) itemView.findViewById(R.id.aty_post_title_title);
            tvContent = (TextView) itemView.findViewById(R.id.aty_post_title_content);
            imagesGridLayout = (GridLayout) itemView.findViewById(R.id.aty_post_title_image);
            imagesGridLayout.setNumInRow(3);
            tvLikeNumber = (TextView) itemView.findViewById(R.id.aty_post_title_like_number);
            ivLike = (ImageView) itemView.findViewById(R.id.aty_post_title_like_image);
            tvCommit = (TextView) itemView.findViewById(R.id.aty_post_title_reply_number);
            commitLayout = (LinearLayout) itemView.findViewById(R.id.aty_post_title_reply_layout);
            likeLayout = (LinearLayout) itemView.findViewById(R.id.aty_post_title_like_layout);
            llLikePeoples = (LinearLayout) itemView.findViewById(R.id.aty_post_title_like_people);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView avatarDraw;
        TextView tvName;
        TextView tvUniversity;
        TextView tvTime;
        TextView tvContent;
        LinearLayout llReplyList;
        GridLayout imagesGridLayout;
        TextView tvLike;
        TextView tvCommit;
        LinearLayout llLike;
        LinearLayout llCommit;
        ImageView ivLike;

        public ItemViewHolder(View itemView) {
            super(itemView);
            avatarDraw = (SimpleDraweeView) itemView.findViewById(R.id.aty_post_reply_avatar);
            tvName = (TextView) itemView.findViewById(R.id.aty_post_reply_name);
            tvUniversity = (TextView) itemView.findViewById(R.id.aty_post_reply_university);
            tvTime = (TextView) itemView.findViewById(R.id.aty_post_reply_time);
            tvContent = (TextView) itemView.findViewById(R.id.aty_post_reply_content);
            llReplyList = (LinearLayout) itemView.findViewById(R.id.aty_post_reply_reply_list);
            imagesGridLayout = (GridLayout) itemView.findViewById(R.id.aty_post_reply_images);
            imagesGridLayout.setNumInRow(3);
            tvLike = (TextView) itemView.findViewById(R.id.aty_post_reply_like_number);
            llLike = (LinearLayout) itemView.findViewById(R.id.aty_post_reply_like_layout);
            ivLike = (ImageView) itemView.findViewById(R.id.aty_post_reply_like_image);
            tvCommit = (TextView) itemView.findViewById(R.id.aty_post_reply_comment_number);
            llCommit = (LinearLayout) itemView.findViewById(R.id.aty_post_reply_commit_layout);
        }
    }
    private static class ProgressViewHolder extends RecyclerView.ViewHolder{
        public ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = (ProgressBar) v.findViewById(R.id.progress);
        }
    }

    private class PostListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.aty_post_title_avatar
                    || v.getId() == R.id.aty_post_reply_avatar
                    || v.getId() == avatarId){
                String userID = (String) v.getTag();
                Intent i = new Intent(mContext,AtyInfo.class);
                i.putExtra(AtyInfo.ID_INTENT,userID);
                mContext.startActivity(i);
            }else if(v.getId()==imageID){
                String url = (String) v.getTag();
                Intent i = new Intent(mContext, AtyImage.class);
                i.putExtra(AtyImage.URL_INTENT, url);
                mContext.startActivity(i);
                ((Activity)mContext).overridePendingTransition(0, 0);
            }else if(v.getId() == R.id.aty_post_title_like_layout){
                likePost();
            }else if(v.getId() == R.id.aty_post_title_reply_layout){
                aty.commitPost();
            }else if(v.getId() == R.id.aty_post_reply_like_layout){
                likeCommit(v);
            }else if(v.getId() == R.id.aty_post_reply_commit_layout){
                Reply reply = (Reply) v.getTag();
                aty.commitReply(reply);
            }
        }
    }

    private void likePost(){
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("postid", mPost.postId);
        OkHttpUtils.post(StrUtils.LIKE_POST_URL,param,TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG, s);
                JSONObject j = OkHttpUtils.parseJSON(mContext, s);
                if(j == null){
                    return;
                }
                mPost.likenumber = (Integer.parseInt(mPost.likenumber)+1)+"";
                mPost.likeusers.add(Integer.parseInt(StrUtils.id()));
                mPost.flag = "1";
                notifyItemChanged(0);
            }
        });
    }

    private void likeCommit(final View v){
        final int position = (int) v.getTag();
        final Reply reply = mReplyList.get(position-1);
        ArrayMap<String,String> param = new ArrayMap<>();
        param.put("token",StrUtils.token());
        param.put("commentid",reply.id);
        OkHttpUtils.post(StrUtils.LIKE_COMMET_URL,param, TAG,new OkHttpUtils.SimpleOkCallBack(){
            @Override
            public void onResponse(String s) {
                LogUtils.i(TAG,s);
                JSONObject j = OkHttpUtils.parseJSON(mContext, s);
                if(j == null){
                    return;
                }
                reply.flag = "1";
                reply.likenumber++;
                notifyItemChanged(position);
            }
        });
    }
}
