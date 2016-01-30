package space.weme.remix.ui.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import space.weme.remix.model.Post;
import space.weme.remix.model.Reply;

/**
 * Created by Liujilong on 2016/1/30.
 * liujilong.me@gmail.com
 */
public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context mContext;

    Post mPost;
    List<Reply> mReplyList;

    PostAdapter(Context context){
        mContext = context;
    }

    void setPost(Post post){
        mPost = post;
    }
    void setReplyList(List<Reply> list){
        mReplyList = list;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
