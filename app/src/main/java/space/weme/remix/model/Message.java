package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 16/2/6.
 * liujilong.me@gmail.com
 */
public class Message {
    public String sendId;
    public String gender;
    public String lasttime;
    public String name;
    public String school;
    public String text;
    public int unreadnum;

    public static Message fromJSON(JSONObject j){
        Message m = new Message();
        m.sendId = j.optString("SendId","");
        m.gender = j.optString("gender","");
        m.lasttime = j.optString("lasttime","");
        m.name = j.optString("name","");
        m.school = j.optString("school","");
        m.text = j.optString("text","");
        m.unreadnum = j.optInt("unreadnum");
        return m;
    }
}
