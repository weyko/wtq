package net.skjr.wtq.core.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.List;

/**
 * 电话、短信相关
 */
public class PhoneUtils {

    /**
     * 获取电话号码
     * 如果以+86开始，那么移除+86
     * @param context
     * @return
     */
    public static String getPhoneNumber(Context context){
        String num = "";
        try{
            TelephonyManager mTelephonyMgr = (TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE);
            num = mTelephonyMgr.getLine1Number();

            //有的机型无法获取
            if(!TextUtils.isEmpty(num)){
                if(num.startsWith("+86")){
                    num = num.substring(3);
                }
            }
        }catch(Exception ex) {

        }

        return num;
    }


    /**
     * 拨打电话
     * @param context
     * @param phoneNumber
     */
    public static void call(Context context, String phoneNumber){
        if(TextUtils.isEmpty(phoneNumber))
            return;

        String uriStr = "tel:" + phoneNumber;
        Intent dialIntent = new Intent(Intent.ACTION_DIAL, Uri.parse(uriStr));
        context.startActivity(dialIntent);
    }

    /**
     * 拨打电话
     * @param context
     * @param phoneNumbers
     */
    public static void call(final Context context, final List<String> phoneNumbers){
        if(phoneNumbers == null)
            return;
        if(phoneNumbers.size() == 0)
            return;

        if(phoneNumbers.size() == 1){
            call(context, phoneNumbers.get(0));
            return;
        }

        String[] items = phoneNumbers.toArray(new String[phoneNumbers.size()]);
        new AlertDialog.Builder(context).setTitle("拨打电话")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        call(context, phoneNumbers.get(which));
                    }
                }).show();
    }

    /**
     * 发短信
     * @param context
     * @param phoneNumber
     */
    public static void sendSms(Context context, String phoneNumber){
        if(TextUtils.isEmpty(phoneNumber))
            return;

        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent sendIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(sendIntent);
    }
}
