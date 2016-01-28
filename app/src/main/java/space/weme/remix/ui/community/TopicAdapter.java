package space.weme.remix.ui.community;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import space.weme.remix.model.Post;
import space.weme.remix.model.Topic;

/**
 * Created by Liujilong on 2016/1/28.
 * liujilong.me@gmail.com
 */
public class TopicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private Context mContext;
    private Topic mTopic;
    private List<Post> mPostList;

    private final int TYPE_TITLE = 0x1;
    private final int TYPE_ITEM = 0x2;
    private final int TYPE_PROGRESS = 0x3;


    public TopicAdapter(Context context){
        mContext = context;
    }

    public void setTopic(Topic topic){
        mTopic = topic;
    }

    public void setPostList(List<Post> postList){
        mPostList = postList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        if(position==0){
            return TYPE_TITLE;
        }else if(mPostList.get(position-1)!=null){
            return TYPE_ITEM;
        }else{
            return TYPE_PROGRESS;
        }
    }

    @Override
    public int getItemCount() {
        return 1+(mPostList==null?0:mPostList.size());
    }



}
