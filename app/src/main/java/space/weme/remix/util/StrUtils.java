package space.weme.remix.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Liujilong on 16/1/22.
 * liujilong.me@gmail.com
 */
public final class StrUtils {
    private StrUtils(){}


    /** ####################### URLs ############################### **/
    private static final String BASE_URL = "http://218.244.147.240:8080/";

    public static final String LOGIN_URL = BASE_URL + "login";

    /** ################## SharedPreferences ####################### **/

    public static final String SP_USER = "StrUtils_sp_user";
    public static final String SP_USER_TOKEN = SP_USER + "_token";
    public static final String SP_USER_ID = SP_USER + "_id";



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
