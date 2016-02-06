package space.weme.remix.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liujilong on 16/2/6.
 * liujilong.me@gmail.com
 */
public class Message {
    public String sendId;
    public List<String> images;
    public int messageid;
    public String readstate;
    public String video;
    public String time;
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
        m.text = j.optString("text", "");
        m.unreadnum = j.optInt("unreadnum");
        m.messageid = j.optInt("messageid");
        m.readstate = j.optString("readstate");
        m.video = j.optString("video");
        m.time = j.optString("time");
        JSONArray array = j.optJSONArray("image");
        if(array!=null){
            m.images = new ArrayList<>();
            for(int i = 0; i<array.length(); i++){
                m.images.add(array.optString(i));
            }
        }
        return m;
    }
}
