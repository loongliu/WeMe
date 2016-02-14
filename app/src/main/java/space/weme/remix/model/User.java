package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by Liujilong on 2016/1/28.
 * liujilong.me@gmail.com
 */
public class User {
    public String birthday;
    public String degree;
    public String enrollment;
    public String hobby;
    public int ID;
    public String phone;
    public String preference;
    public String qq;
    public String wechat;
    public String username;
    public String name;
    public String school;
    public String department;
    public String gender;
    public String hometown;
    public String lookcount;
    public String weme;
    public String constellation;

    public String avatar;

    public static User fromJSON(JSONObject j){
        User user = new User();
        user.birthday = j.optString("birthday");
        user.degree = j.optString("degree");
        user.department = j.optString("department");
        user.enrollment = j.optString("enrollment");
        user.gender = j.optString("gender");
        user.hobby = j.optString("hobby");
        user.hometown = j.optString("hometown");
        user.ID = j.optInt("id");
        user.lookcount = j.optString("lookcount");
        user.name = j.optString("name");
        user.phone = j.optString("phone");
        user.preference = j.optString("preference");
        user.qq = j.optString("qq");
        user.school = j.optString("school");
        user.username = j.optString("username");
        user.wechat = j.optString("wechat");
        user.weme = j.optString("weme");
        user.constellation = j.optString("constellation");

        user.avatar = j.optString("avatar");
        return user;
    }
}
