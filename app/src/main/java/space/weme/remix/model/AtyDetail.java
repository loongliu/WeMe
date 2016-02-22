package space.weme.remix.model;

import org.json.JSONObject;

/**
 * Created by just on 2016/2/12.
 */
public class AtyDetail {
    public String id;
    public String authorid;
    public String school;
    public String gender;
    public String title;
    public String time;
    public String location;
    public String number;
    public String author;
    public String signnumber;
    public String remark;
    public String state;
    public String detail;
    public String advertise;
    public String whetherimage;
    public String likeflag;
    public String imageurl;

    public static AtyDetail fromJSON(JSONObject j){
        AtyDetail detail=new AtyDetail();
        detail.id=j.optString("id");
        detail.authorid=j.optString("authorid");
        detail.school=j.optString("school");
        detail.gender=j.optString("gender");
        detail.title=j.optString("title");
        detail.time=j.optString("time");
        detail.location=j.optString("location");
        detail.number=j.optString("number");
        detail.author=j.optString("author");
        detail.signnumber=j.optString("signnumber");
        detail.remark=j.optString("remark");
        detail.state=j.optString("state");
        detail.detail=j.optString("detail");
        detail.advertise=j.optString("advertise");
        detail.whetherimage=j.optString("whetherimage");
        detail.likeflag=j.optString("likeflag");
        detail.imageurl=j.optString("imageurl");
        return detail;
    }

}
