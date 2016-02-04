package space.weme.remix.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Liujilong on 16/2/4.
 * liujilong.me@gmail.com
 */
public class University {
    public String province;
    public String name;
    public static List<List<University>> fromJSON(String input){
        try {
            JSONArray array = new JSONArray(input);
            List<List<University>> result = new ArrayList<>();
            for(int i = 0; i<array.length(); i++){
                JSONObject j = array.optJSONObject(i);
                String province = j.optString("province");
                JSONArray schools = j.optJSONArray("university");
                List<University> unviersityList = new ArrayList<>();
                for(int num = 0; num<schools.length(); num++){
                    String name = schools.getJSONObject(num).optString("name");
                    University u = new University();
                    u.province = province;
                    u.name = name;
                    unviersityList.add(u);
                }
                result.add(unviersityList);
            }
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
