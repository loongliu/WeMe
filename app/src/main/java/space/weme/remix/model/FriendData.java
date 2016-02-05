package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 16/2/5.
 * liujilong.me@gmail.com
 */
public class FriendData {
    public String gender;
    public String id;
    public String name;
    public String school;
    public String timestamp;

    public static FriendData fromJSON(JSONObject j){
        FriendData f = new FriendData();
        f.gender = j.optString("gender");
        f.id = j.optString("id");
        f.name = j.optString("name");
        f.school = j.optString("school");
        f.timestamp = j.optString("timestamp");
        return f;
    }
}
