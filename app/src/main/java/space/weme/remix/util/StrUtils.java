package space.weme.remix.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicInteger;

import space.weme.remix.APP;

/**
 * Created by Liujilong on 16/1/22.
 * liujilong.me@gmail.com
 */
public final class StrUtils {

    private StrUtils(){}



    /** ####################### URLs ############################### **/
    private static final String BASE_URL = "http://218.244.147.240:8080/";

    private static final String BASE_URL_NGINX = "http://218.244.147.240/";

    public static final String LOGIN_URL = BASE_URL + "login";

    public static final String GET_TOP_ACTIVITY_URL = BASE_URL + "activitytopofficial";

    public static final String GET_ACTIVITY_INFO_URL = BASE_URL + "getactivityinformation";

    public static final String TOP_BROAD_URL = BASE_URL + "topofficial";

    public static final String GET_PERSON_INFO = BASE_URL + "getprofile";

    public static final String GET_UNREAD_MESSAGE_URL = BASE_URL + "getmessageunreadnumber";

    public static final String GET_TOPIC_LIST = BASE_URL + "gettopiclist";

    public static final String GET_TOPIC_INFO = BASE_URL + "gettopicslogan";

    public static final String GET_POST_LIST = BASE_URL + "getpostlist";

    public static final String GET_VISIT_INFO = BASE_URL + "visitinfo";

    public static final String GET_PROFILE_BY_ID = BASE_URL + "getprofilebyid";

    public static final String GET_POST_DETAIL = BASE_URL + "getpostdetail";

    public static final String GET_POST_COMMIT = BASE_URL + "getpostcomment";



    public static final String GET_AVATAR = BASE_URL_NGINX + "avatar/";

    public static final String GET_BACKGROUND = BASE_URL_NGINX + "background/";

    public static String thumForID(String id){
        return GET_AVATAR + id + "_thumbnail.jpg";
    }

    public static String backgroundForID(String id){return GET_BACKGROUND + id;}


    /** ################## SharedPreferences ####################### **/

    public static final String SP_USER = "StrUtils_sp_user";
    public static final String SP_USER_TOKEN = SP_USER + "_token";
    public static final String SP_USER_ID = SP_USER + "_id";



    public static String token(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_TOKEN,"");
    }

    public static String id(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_ID,"");
    }

    public static String md5(String input){
        try{
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] inputBytes = input.getBytes();
            byte[] outputBytes = messageDigest.digest(inputBytes);
            return bytesToHex(outputBytes);
        }catch (NoSuchAlgorithmException e){
            return "";
        }
    }
    private static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in setId(int)
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (;;) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
