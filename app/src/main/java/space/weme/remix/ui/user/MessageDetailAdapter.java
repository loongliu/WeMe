package space.weme.remix.ui.user;

import android.app.Activity;
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
import space.weme.remix.ui.AtyImage;
import space.weme.remix.util.StrUtils;
import space.weme.remix.widgt.GridLayout;

/**
 * Created by Liujilong on 16/2/6.
 * liujilong.me@gmail.com
 */
public class MessageDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Message> messageList;
    private View.OnClickListener mAvatarListener;
    private View.OnClickListener mReplyListener;
    private View.OnClickListener mImageListener;
    private String sendId;

    public MessageDetailAdapter(Context context, String id){
        mContext = context;
        sendId = id;
        mAvatarListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId = (String) v.getTag();
                Intent i = new Intent(mContext,AtyInfo.class);
                i.putExtra(AtyInfo.ID_INTENT,sendId);
                mContext.startActivity(i);
            }
        };
        mReplyListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendId = (String) v.getTag();
                Intent i = new Intent(mContext,AtyMessageReply.class);
                i.putExtra(AtyMessageReply.INTENT_ID, sendId);
                ((Activity)mContext).startActivityForResult(i,AtyMessageDetail.REQUEST_CODE);
            }
        };
        mImageListener = new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String url = (String) v.getTag();
                Intent i = new Intent(mContext, AtyImage.class);
                i.putExtra(AtyImage.URL_INTENT,url);
                mContext.startActivity(i);
                ((android.app.Activity)mContext).overridePendingTransition(0,0);
            }
        };
    }

    public void setMessageList(List<Message> messageList) {
        this.messageList = messageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.aty_message_detail_cell,parent,false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VH vh = (VH) holder;
        Message m = messageList.get(position);
        vh.avatar.setImageURI(Uri.parse(StrUtils.thumForID(m.sendId)));
        vh.avatar.setTag(m.sendId);
        vh.avatar.setOnClickListener(mAvatarListener);
        vh.tvName.setText(m.name);
        vh.tvText.setText(m.text);
        vh.tvSchool.setText(m.school);
        if(m.time!=null) {
            vh.tvTime.setText(StrUtils.timeTransfer(m.time));
        }
        if(!sendId.equals(m.sendId)){
            vh.ivReply.setVisibility(View.GONE);
        }else{
            vh.ivReply.setVisibility(View.VISIBLE);
            vh.ivReply.setTag(m.sendId);
            vh.ivReply.setOnClickListener(mReplyListener);
        }
        vh.glImages.setImageLists(m.images,mImageListener);
    }

    @Override
    public int getItemCount() {
        return messageList == null ? 0 : messageList.size();
    }

    class VH extends RecyclerView.ViewHolder{
        SimpleDraweeView avatar;
        TextView tvName;
        TextView tvText;
        TextView tvSchool;
        TextView tvTime;
        ImageView ivReply;
        GridLayout glImages;
        public VH(View view) {
            super(view);
            avatar = (SimpleDraweeView) view.findViewById(R.id.aty_message_detail_cell_avatar);
            tvName = (TextView) view.findViewById(R.id.aty_message_detail_cell_name);
            tvText = (TextView) view.findViewById(R.id.aty_message_detail_cell_text);
            tvSchool = (TextView) view.findViewById(R.id.aty_message_detail_cell_school);
            tvTime = (TextView) view.findViewById(R.id.aty_message_detail_cell_time);
            ivReply = (ImageView) view.findViewById(R.id.aty_message_detail_cell_reply);
            glImages = (GridLayout) view.findViewById(R.id.aty_message_detail_cell_images);
            glImages.setNumInRow(3);
        }
    }
}
