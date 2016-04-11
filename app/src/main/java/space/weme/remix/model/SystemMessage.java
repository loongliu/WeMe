package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 2016/4/11.
 * liujilong.me@gmail.com
 */
public class SystemMessage {

    public static class CommunityMessage extends SystemMessage{
        public String authorGender;
        public String authorId;
        public String authorName;
        public String authorSchool;
        public String comment;
        public String commentId;
        public String postId;
        public String timeStamp;
        public static SystemMessage.CommunityMessage fromJson(JSONObject j){
            SystemMessage.CommunityMessage message = new SystemMessage.CommunityMessage();
            JSONObject author = j.optJSONObject("author");
            message.authorGender = author.optString("gender");
            message.authorId = author.optString("id");
            message.authorName = author.optString("name");
            message.authorSchool = author.optString("school");
            message.comment = j.optString("comment");
            message.commentId = String.valueOf(j.optInt("commentid"));
            message.postId = String.valueOf(j.optInt("postid"));
            message.timeStamp = j.optString("timestamp");
            return message;
        }
    }

}
