package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 2016/1/30.
 * liujilong.me@gmail.com
 */
public class Commit {
    public String authorid;
    public String body; // content of this commitRely
    public String destcommentid;
    public String destname;
    public String destuserid;
    public String id; // author's id of this commitReply
    public String name; // author's name of this commitReply
    public String timestamp;
    //public Date time;

    public static Commit fromJSON(JSONObject o){
        Commit reply = new Commit();
        reply.id = o.optString("id");
        reply.name = o.optString("name");
        reply.body = o.optString("body");
        reply.destcommentid = o.optString("destcommentid",null);
        reply.destname = o.optString("destname",null);
        reply.destuserid = o.optString("destuserid",null);


        reply.timestamp = o.optString("timestamp");

//        }
        reply.authorid = o.optString("authorid");
        return reply;
    }
}
