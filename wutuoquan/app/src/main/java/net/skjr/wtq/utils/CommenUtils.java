package net.skjr.wtq.utils;

import android.text.TextUtils;

import java.text.DecimalFormat;

/**
 * 创建者     huangbo
 * 创建时间   2016/10/24 11:35
 * 描述	      ${TODO}
 * <p>
 * 更新者     $Author$
 * 更新时间   $Date$
 * 更新描述   ${TODO}
 */
public class CommenUtils {

    /**
     * 获取带星号的手机
     */
    public static String getSecurityPhone(String phone) {
        if(TextUtils.isEmpty(phone))
            return "";

        String start = phone.substring(0,3);

        String end = "";
        if(phone.length()>7){
            end = phone.substring(7);
        }

        String result = start + "****" + end;

        return result;
    }

    /**
     * 格式化double
     * @param s
     * @param n
     * @return
     */

    public static String decimalFormat(Object s, int n) {
        String partDigital = "";
        for (int i = 0; i < n; i++) {
            partDigital += "0";
        }
        if (partDigital.length() > 0) {
            partDigital = "." + partDigital;
        }

        if (s == null) {
            return "0" + partDigital;
        } else {
            try {
                Double d = Double.valueOf(String.valueOf(s));
                String partNumber = "0";
                return new DecimalFormat(partNumber + partDigital).format(d);
            } catch (Exception e) {
                return "-";
            }
        }
    }


}
