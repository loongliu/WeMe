package space.weme.remix.util;

import android.content.Context;
import android.content.SharedPreferences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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

    public static final String GET_AVATAR = BASE_URL_NGINX + "avatar/";

    public static String thumForID(String id){
        return GET_AVATAR + id + "_thumbnail.jpg";
    }


    /** ################## SharedPreferences ####################### **/

    public static final String SP_USER = "StrUtils_sp_user";
    public static final String SP_USER_TOKEN = SP_USER + "_token";
    public static final String SP_USER_ID = SP_USER + "_id";



    public static String token(){
        SharedPreferences sp = APP.context().getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        return sp.getString(SP_USER_TOKEN,"");
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
}
