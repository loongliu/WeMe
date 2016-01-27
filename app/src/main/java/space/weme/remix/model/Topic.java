package space.weme.remix.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;

/**
 * Created by Liujilong on 2016/1/27.
 * liujilong.me@gmail.com
 */
public class Topic implements Parcelable {
    public String id;
    public String imageurl;
    public String note;
    public int number;
    public String theme;
    public String slogan;

    private Topic(){}

    public static Topic fromJson(JSONObject o){
        Topic topic = new Topic();
        topic.id = o.optString("id","");
        topic.imageurl = o.optString("imageurl","");
        topic.note = o.optString("note","");
        topic.number = o.optInt("number",0);
        topic.theme = o.optString("theme","");
        topic.slogan = o.optString("slogan","");
        return topic;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeString(id);
        out.writeString(imageurl);
        out.writeString(note);
        out.writeInt(number);
        out.writeString(theme);
        out.writeString(slogan);
    }

    public static final Parcelable.Creator<Topic> CREATOR
            = new Parcelable.Creator<Topic>() {
        public Topic createFromParcel(Parcel in) {
            return new Topic(in);
        }

        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };

    private Topic(Parcel in) {
        id = in.readString();
        imageurl = in.readString();
        note = in.readString();
        number = in.readInt();
        theme = in.readString();
        slogan = in.readString();
    }
}
