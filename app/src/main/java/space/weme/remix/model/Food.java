package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 16/2/15.
 * liujilong.me@gmail.com
 */
public class Food {
    public String ID;
    public String title;
    public String authorId;
    public String author;
    public String location;
    public double longitude;
    public double latitude;
    public String price;
    public String comment;
    public int likeNumber;
    public String url;
    public boolean likeFlag;

    public static Food fromJSON(JSONObject j){
        Food f = new Food();
        f.ID = j.optString("id");
        f.title = j.optString("title");
        f.authorId = j.optString("authorid");
        f.author = j.optString("author");
        f.location = j.optString("location");
        f.longitude = Double.parseDouble(j.optString("longitude"));
        f.latitude = Double.parseDouble(j.optString("latitude"));
        f.price = j.optString("price");
        f.comment = j.optString("comment");
        f.likeNumber = j.optInt("likenumber");
        f.likeFlag = j.optInt("likeflag")==1;
        f.url = j.optString("imageurl");
        return f;
    }
}
