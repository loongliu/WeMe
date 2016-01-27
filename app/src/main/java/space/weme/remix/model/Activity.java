package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 16/1/27.
 * liujilong.me@gmail.com
 */
public class Activity {
    /**
     *
     * @property (nonatomic, copy)NSString * activityID;
     @property (nonatomic, copy)NSString * time;
     @property (nonatomic, copy)NSString *location;
     @property (nonatomic, copy)NSString *title;
     @property (nonatomic, copy)NSString *capacity;
     @property (nonatomic, assign)BOOL state;
     @property (nonatomic, copy)NSString * signnumber;
     @property (nonatomic, copy)NSString *remark;
     @property (nonatomic, copy)NSString *author;
     @property (nonatomic, copy)NSString *detail;
     @property (nonatomic, copy)NSString *advertise;
     @property (nonatomic, assign)BOOL needsImage;
     @property (nonatomic, assign)BOOL likeFlag;
     @property (nonatomic, copy)NSString *authorID;
     @property (nonatomic, copy)NSString *school;
     @property (nonatomic, strong)NSURL *poster;
     @property (nonatomic, copy) NSString *status;
     *
     */

    public int activityID;
    public String time;
    public String location;
    public String title;
    public String capacity;
    public boolean state;
    public String signNumber;
    public String remark;
    public String author;
    public String detail;
    public String advertise;
    public boolean needsImage;
    public boolean likeFlag;
    public int authorID;
    public String school;
    public String poster;
    public String status;

    public String gender;

    public static Activity fromJSON(JSONObject j){
        Activity a = new Activity();
        a.advertise = j.optString("advertise");
        a.author = j.optString("author");
        a.authorID = j.optInt("authorid");
        a.activityID = j.optInt("id");
        a.gender = j.optString("gender");
        a.location = j.optString("location");
        a.capacity = j.optString("number");
        a.remark = j.optString("remark");
        a.school = j.optString("school");
        a.signNumber = j.optString("signnumber");
        a.state = j.optString("state").equals("yes");
        a.time = j.optString("time");
        a.title = j.optString("title");
        return a;
    }

}
