package space.weme.remix.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Liujilong on 2016/1/30.
 * liujilong.me@gmail.com
 */
public class Reply {
    public String body;
    public int commentnumber;
    // whether you liked this reply
    public String flag;
    public String gender;
    public String id;
    public ArrayList<String> image;
    public int likenumber;
    public String name;
    public ArrayList<Commit> reply;
    public String school;
    public ArrayList<String> thumbnail;
    public String timestamp;
    public String userid;


    //public Date time;

    public static Reply fromJSON(JSONObject o) {
        Reply reply = new Reply();
        reply.timestamp = o.optString("timestamp");
//        SimpleDateFormat format = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
//        try {
//            reply.time = format.parse(reply.timestamp);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }

        reply.id = o.optString("id");
        reply.body = o.optString("body");
        JSONArray thum_array = o.optJSONArray("thumbnail");
        reply.thumbnail = new ArrayList<>();
        for(int i = 0; i<thum_array.length(); i++){
            reply.thumbnail.add(thum_array.optString(i));
        }
        reply.school = o.optString("school");
        reply.likenumber = o.optInt("likenumber");
        reply.flag = o.optString("flag");
        reply.name = o.optString("name");
        reply.userid = o.optString("userid");
        JSONArray reply_array = o.optJSONArray("reply");
        reply.reply = new ArrayList<>();
        for(int i = 0; i< reply_array.length(); i++){
            reply.reply.add(Commit.fromJSON(reply_array.optJSONObject(i)));
        }
        JSONArray image_array = o.optJSONArray("image");
        reply.image = new ArrayList<>();
        for(int i = 0; i< image_array.length(); i++){
            reply.image.add(image_array.optString(i));
        }
        reply.gender = o.optString("gender");
        reply.commentnumber = o.optInt("commentnumber");

        return reply;
    }
}
