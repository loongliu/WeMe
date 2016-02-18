package space.weme.remix.model;

import android.support.v4.util.ArrayMap;

import org.json.JSONObject;

/**
 * Created by Liujilong on 16/2/2.
 * liujilong.me@gmail.com
 */
public class UserImage {

    public String image;
    public String thumbnail;
    public String title;
    public String body;
    public String topic;
    public String timestamp;
    public String postid;

    public static UserImage fromJSON(JSONObject j){
        UserImage ui = new UserImage();
        ui.body = j.optString("body");
        ui.image = j.optString("image");
        ui.postid = j.optString("postid");
        ui.thumbnail = j.optString("thumbnail");
        ui.timestamp = j.optString("time");
        ui.title = j.optString("title");
        ui.topic = j.optString("topic");
        return ui;
    }

    public JSONObject toJSON(){
        ArrayMap<String,String> map = new ArrayMap<>();
        map.put("body", body);
        map.put("image",image);
        map.put("postid", postid);
        map.put("thumbnail",thumbnail);
        map.put("time",timestamp);
        map.put("title", title);
        map.put("topic",topic);
        return new JSONObject(map);
    }
}
