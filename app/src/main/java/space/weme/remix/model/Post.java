package space.weme.remix.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Liujilong on 2016/1/28.
 * liujilong.me@gmail.com
 */
public class Post {
    public String postId;
    public String userId;
    public String name;
    public String school;
    public String gender;
    public String timestamp;
    public String title;
    public String body;
    public String likenumber;
    public String commentnumber;
    public ArrayList<String> imageUrl;
    public ArrayList<String> thumbnailUrl;


    public ArrayList<Integer> likeusers;

    // whether current user has liked this post
    public String flag;

    private Post(){}

    public static Post fromJSON(JSONObject j){
        Post post = new Post();
        post.body = j.optString("body");
        post.commentnumber = j.optString("commentnumber");
        post.gender = j.optString("gender");
        post.likenumber = j.optString("likenumber");
        post.name = j.optString("name");
        post.postId = j.optString("postid");
        post.school = j.optString("school");
        post.timestamp = j.optString("timestamp");
        post.title = j.optString("title");
        post.userId = j.optString("userid");
        post.imageUrl = new ArrayList<>();
        JSONArray array = j.optJSONArray("imageurl");
        for(int i = 0; i<array.length(); i++){
            post.imageUrl.add(array.optString(i));
        }
        array = j.optJSONArray("thumbnail");
        post.thumbnailUrl = new ArrayList<>();
        for(int i = 0; i<array.length(); i++){
            post.thumbnailUrl.add(array.optString(i));
        }

        post.flag = j.optString("flag");
        array = j.optJSONArray("likeusers");
        if(array!=null) {
            post.likeusers = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                post.likeusers.add(array.optInt(i));
            }
        }
        return post;
    }
}
