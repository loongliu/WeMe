package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 16/2/2.
 * liujilong.me@gmail.com
 */
public class TimeLine {

    public String title;
    public String body;
    public String timestamp;
    public String topic;
    public String image;
    public String postId;

    public static TimeLine fromJSON(JSONObject j){
        TimeLine t = new TimeLine();
        t.body = j.optString("body");
        t.image = j.optString("image");
        t.postId = j.optString("postid");
        t.timestamp = j.optString("time");
        t.title = j.optString("title");
        t.topic = j.optString("topic");
        return t;
    }
}
