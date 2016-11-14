package chat.common.util.sys;

import android.content.Context;
import android.telephony.TelephonyManager;

import java.util.UUID;

import chat.base.IMClient;

/**
 * Description:
 * Created  by: weyko on 2016/6/2.
 */
public class UUIDUtil {
    private static UUIDUtil instance;
    private TelephonyManager telephonyManager;
    public UUIDUtil() {
    }
    public static UUIDUtil getInstance(){
        if(instance==null){
            synchronized (UUIDUtil.class){
                if(instance==null){
                    instance=new UUIDUtil();
                }
            }
        }
        return  instance;
    }
    public static String getUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    public static String getUUID(String var0) {
        int var1;
        if((var1 = var0.lastIndexOf(46)) == -1) {
            return "";
        } else {
            for(int var2 = var1 + 1; var2 < var0.length(); ++var2) {
                char var3;
                if(((var3 = var0.charAt(var2)) < 97 || var3 > 122) && (var3 < 65 || var3 > 90) && (var3 < 48 || var3 > 57)) {
                    return "";
                }
            }

            return var0.substring(var1 + 1, var0.length());
        }
    }
    public String getDataId(){
        return UUID.randomUUID().toString();
    }
    public String getDeviceId(){
        String deviceId="";
        if(telephonyManager==null){
            telephonyManager= (TelephonyManager) IMClient.getInstance().getContext().getSystemService(Context.TELEPHONY_SERVICE);
        }
        try{
            deviceId=telephonyManager.getDeviceId();
        }catch (Exception e){
            e.printStackTrace();
        }
        return deviceId;
    }
}
