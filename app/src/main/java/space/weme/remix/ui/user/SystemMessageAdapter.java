package space.weme.remix.ui.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.SystemMessage;
import space.weme.remix.ui.community.AtyPost;
import space.weme.remix.util.LogUtils;
import space.weme.remix.util.OkHttpUtils;
import space.weme.remix.util.StrUtils;

/**
 * Created by Liujilong on 2016/4/11.
 * liujilong.me@gmail.com
 */
public class SystemMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<SystemMessage> mList;


    private Context mContext;
    private View.OnClickListener mAvatarListener;
    private View.OnClickListener mListener;

    public SystemMessageAdapter(Context context){
        mContext = context;
        mAvatarListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId = (String) v.getTag();
                Intent i = new Intent(mContext,AtyInfo.class);
                i.putExtra(AtyInfo.ID_INTENT,sendId);
                mContext.startActivity(i);
            }
        };
        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId = (String) v.getTag(R.id.tag_first);
                Intent i = new Intent(mContext, AtyPost.class);
                i.putExtra(AtyPost.POST_INTENT, sendId);
                mContext.startActivity(i);

                // send read info to server
                ArrayMap<String,String> param = new ArrayMap<>();
                param.put("token",StrUtils.token());
                param.put("commentid", (String) v.getTag(R.id.tag_second));
                OkHttpUtils.post(StrUtils.READ_COMMUNITY_NOTIFICATION,param,new OkHttpUtils.SimpleOkCallBack(){
                    @Override
                    public void onResponse(String s) {
                        LogUtils.i("SystemMessageAdapter",s);
                    }
                });

            }
        };
    }


    public void setList(List<SystemMessage> list){
        mList = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.aty_message_cell,parent,false);
        return new MessageAdapter.MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageAdapter.MessageViewHolder messageViewHolder = (MessageAdapter.MessageViewHolder) holder;
        SystemMessage.CommunityMessage m = (SystemMessage.CommunityMessage) mList.get(position);
        messageViewHolder.avatar.setImageURI(Uri.parse(StrUtils.thumForID(m.authorId)));
        messageViewHolder.avatar.setTag(m.authorId);
        messageViewHolder.avatar.setOnClickListener(mAvatarListener);
        messageViewHolder.tvName.setText(m.authorName);
        messageViewHolder.tvSchool.setText(m.authorSchool);
        boolean isBoy = m.authorGender.equals(mContext.getResources().getString(R.string.boy));
        messageViewHolder.ivGender.setImageResource(isBoy ? R.mipmap.boy : R.mipmap.girl);
        messageViewHolder.tvInfo.setText(m.comment);
        messageViewHolder.tvTime.setText(StrUtils.timeTransfer(m.timeStamp));
        messageViewHolder.tvCount.setVisibility(View.VISIBLE);
        messageViewHolder.tvCount.setText(String.valueOf(1));
        messageViewHolder.itemView.setTag(R.id.tag_first, m.postId);
        messageViewHolder.itemView.setTag(R.id.tag_second,m.commentId);
        messageViewHolder.itemView.setOnClickListener(mListener);
    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }
}
