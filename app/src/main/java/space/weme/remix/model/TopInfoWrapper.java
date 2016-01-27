package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 2016/1/27.
 * liujilong.me@gmail.com
 */
public class TopInfoWrapper {
    public int id;
    public String url;
    public static TopInfoWrapper fromJSON(JSONObject j){
        TopInfoWrapper info = new TopInfoWrapper();
        info.id = j.optInt("activityid");
        info.url = j.optString("imageurl");
        return info;
    }
}

