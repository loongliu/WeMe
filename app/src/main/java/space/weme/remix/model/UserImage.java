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
    public String timestamp;
    public String userId;
    public String userName;
    public String id;

    public static UserImage fromJSON(JSONObject j){
        UserImage ui = new UserImage();
        ui.image = j.optString("image");
        ui.thumbnail = j.optString("thumbnail");
        ui.timestamp = j.optString("time");
        ui.id = j.optString("id");
        ui.userId = j.optString("userid");
        ui.userName = j.optString("userName");
        return ui;
    }

    public JSONObject toJSON(){
        ArrayMap<String,String> map = new ArrayMap<>();
        map.put("image",image);
        map.put("thumbnail",thumbnail);
        map.put("time",timestamp);
        map.put("id",id);
        map.put("userid",userId);
        map.put("userName",userName);
        return new JSONObject(map);
    }
}
