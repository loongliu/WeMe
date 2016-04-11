package space.weme.remix.ui.user;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.util.List;

import space.weme.remix.R;
import space.weme.remix.model.Message;
import space.weme.remix.util.StrUtils;


/**
 * Created by Liujilong on 16/2/6.
 * liujilong.me@gmail.com
 */
public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<Message> messageList;
    private View.OnClickListener mListener;
    private View.OnClickListener mAvatarListener;

    public MessageAdapter(Context context){
        mContext = context;
        mListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId = (String) v.getTag();
                Intent i = new Intent(mContext, AtyMessageDetail.class);
                i.putExtra(AtyMessageDetail.INTENT_ID,sendId);
                mContext.startActivity(i);
            }
        };
        mAvatarListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId = (String) v.getTag();
                Intent i = new Intent(mContext,AtyInfo.class);
                i.putExtra(AtyInfo.ID_INTENT,sendId);
                mContext.startActivity(i);
            }
        };
    }

    public void setMessageList(List<Message> m){
        messageList = m;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.aty_message_cell,parent,false);
        return new MessageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MessageViewHolder messageViewHolder = (MessageViewHolder) holder;
        Message m = messageList.get(position);
        messageViewHolder.avatar.setImageURI(Uri.parse(StrUtils.thumForID(m.sendId)));
        messageViewHolder.avatar.setTag(m.sendId);
        messageViewHolder.avatar.setOnClickListener(mAvatarListener);
        messageViewHolder.tvName.setText(m.name);
        messageViewHolder.tvSchool.setText(m.school);
        boolean isBoy = m.gender.equals(mContext.getResources().getString(R.string.boy));
        messageViewHolder.ivGender.setImageResource(isBoy ? R.mipmap.boy : R.mipmap.girl);
        messageViewHolder.tvInfo.setText(m.text);
        messageViewHolder.tvTime.setText(StrUtils.timeTransfer(m.lasttime));
        if(m.unreadnum==0){
            messageViewHolder.tvCount.setVisibility(View.INVISIBLE);
        }else{
            messageViewHolder.tvCount.setVisibility(View.VISIBLE);
            messageViewHolder.tvCount.setText(m.unreadnum+"");
        }
        messageViewHolder.itemView.setTag(m.sendId);
        messageViewHolder.itemView.setOnClickListener(mListener);
    }

    @Override
    public int getItemCount() {
        return messageList==null?0:messageList.size();
    }
    public static class MessageViewHolder extends RecyclerView.ViewHolder{
        SimpleDraweeView avatar;
        TextView tvName;
        TextView tvSchool;
        ImageView ivGender;
        TextView tvInfo;
        TextView tvTime;
        TextView tvCount;
        public MessageViewHolder(View itemView) {
            super(itemView);
            avatar = (SimpleDraweeView) itemView.findViewById(R.id.aty_message_cell_avatar);
            tvName = (TextView) itemView.findViewById(R.id.aty_message_cell_name);
            tvSchool = (TextView) itemView.findViewById(R.id.aty_message_cell_school);
            ivGender = (ImageView) itemView.findViewById(R.id.aty_message_cell_gender);
            tvInfo = (TextView) itemView.findViewById(R.id.aty_message_cell_info);
            tvTime = (TextView) itemView.findViewById(R.id.aty_message_cell_time);
            tvCount = (TextView) itemView.findViewById(R.id.aty_message_cell_count);
        }
    }

}
