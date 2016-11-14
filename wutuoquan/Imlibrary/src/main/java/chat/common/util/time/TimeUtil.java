package chat.common.util.time;

import android.annotation.SuppressLint;
import android.content.Context;

import com.imlibrary.R;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import static chat.common.util.time.TimeUtil.TimeFormatType.TIME_FOEMAT_M_D;
import static chat.common.util.time.TimeUtil.TimeFormatType.TIME_FOEMAT_NORMAL;
import static chat.common.util.time.TimeUtil.TimeFormatType.TIME_FOEMAT_STANDARD;
import static chat.common.util.time.TimeUtil.TimeFormatType.TIME_FOEMAT_Y_M;
import static chat.common.util.time.TimeUtil.TimeFormatType.TIME_FOEMAT_Y_M_D;
import static chat.common.util.time.TimeUtil.TimeFormatType.TIME_FOEMAT_Y_Y;

public class TimeUtil {
    public static final long UNIT_MS_DAY = 24 * 60 * 60 * 1000;
    /**
     * TimeFormatType 时间格式
     */
    public enum TimeFormatType {
        /** yyyy-MM-dd HH:mm:ss */
        TIME_FOEMAT_STANDARD,
        /** yy-MM-dd HH:mm:ss */
        TIME_FOEMAT_NORMAL,
        /** yyyy-MM-dd */
        TIME_FOEMAT_Y_M_D,
        /** yyyy */
        TIME_FOEMAT_Y_Y,
        /** yy-MM */
        TIME_FOEMAT_Y_M,
        /** MM-dd */
        TIME_FOEMAT_M_D,
        /** HH:mm:ss */
        TIME_FOEMAT_H_M_S,
        /** HH:mm */
        TIME_FOEMAT_H_M,
        /** mm:ss */
        TIME_FOEMAT_M_S,
        /** yyyy/MM/dd HH:mm */
        TIME_FOEMAT_NOT_S,
        /** yyyy/MM/dd */
        TIME_FOEMAT_NOT_TIME,
        /** yyyy/MM/dd HH:mm:ss */
        TIME_FOEMAT,
        /** yyyy/MM */
        Y_M,
        /** dd */
        D,
        /** E */
        E
    }
    public static boolean isEarly(int days, long time) {
        return (currentTimeMillis() - time) > (days * 24 * 3600 * 1000);
    }

    public static int currentTimeSecond() {
        return (int) (System.currentTimeMillis() / 1000);
    }

    public static long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    public static long[] getTsTimes() {
        long[] times = new long[2];

        Calendar calendar = Calendar.getInstance();

        times[0] = calendar.getTimeInMillis() / 1000;

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        times[1] = calendar.getTimeInMillis() / 1000;

        return times;
    }

    public static String getFormatDatetime(int year, int month, int day) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        return formatter.format(new GregorianCalendar(year, month, day).getTime());
    }

    public static Date getDateFromFormatString(String formatDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return sdf.parse(formatDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getNowDatetime() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return (formatter.format(new Date()));
    }

    public static int getNow() {
        return (int) ((new Date()).getTime() / 1000);
    }

    public static String getNowDateTime(String format) {
        Date date = new Date();

        SimpleDateFormat df = new SimpleDateFormat(format, Locale.getDefault());
        return df.format(date);
    }

    public static String getDateString(long milliseconds) {
        return getDateTimeString(milliseconds, "yyyyMMdd");
    }

    public static String getTimeString(long milliseconds) {
        return getDateTimeString(milliseconds, "HHmmss");
    }
    public static String getBeijingNowTime(String format) {
        TimeZone timezone = TimeZone.getTimeZone("Asia/Shanghai");

        Date date = new Date(currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        formatter.setTimeZone(timezone);

        return formatter.format(date);
    }

    public static String getDateTimeString(long milliseconds, String format) {
        Date date = new Date(milliseconds);
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.getDefault());
        return formatter.format(date);
    }


    public static String getFavoriteCollectTime(long milliseconds) {
        String showDataString = "";
        Date today = new Date();
        Date date = new Date(milliseconds);
        Date firstDateThisYear = new Date(today.getYear(), 0, 0);
        if (!date.before(firstDateThisYear)) {
            SimpleDateFormat dateformatter = new SimpleDateFormat("MM-dd", Locale.getDefault());
            showDataString = dateformatter.format(date);
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            showDataString = dateformatter.format(date);
        }
        return showDataString;
    }

    public static String getTimeShowString(long milliseconds, boolean abbreviate) {
        String dataString = "";
        String timeStringBy24 = "";

        Date currentTime = new Date(milliseconds);
        Date today = new Date();
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        Date todaybegin = todayStart.getTime();
        Date yesterdaybegin = new Date(todaybegin.getTime() - 3600 * 24 * 1000);
        Date preyesterday = new Date(yesterdaybegin.getTime() - 3600 * 24 * 1000);

        if (!currentTime.before(todaybegin)) {
            dataString = "今天";
        } else if (!currentTime.before(yesterdaybegin)) {
            dataString = "昨天";
        } else if (!currentTime.before(preyesterday)) {
            dataString = "前天";
        } else if (isSameWeekDates(currentTime, today)) {
            dataString = getWeekOfDate(currentTime);
        } else {
            SimpleDateFormat dateformatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            dataString = dateformatter.format(currentTime);
        }

        SimpleDateFormat timeformatter24 = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeStringBy24 = timeformatter24.format(currentTime);

        if (abbreviate) {
            if (!currentTime.before(todaybegin)) {
                return getTodayTimeBucket(currentTime);
            } else {
                return dataString;
            }
        } else {
            return dataString + " " + timeStringBy24;
        }
    }

    /**
     * 根据不同时间段，显示不同时间
     *
     * @param date
     * @return
     */
    public static String getTodayTimeBucket(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        SimpleDateFormat timeformatter0to11 = new SimpleDateFormat("KK:mm", Locale.getDefault());
        SimpleDateFormat timeformatter1to12 = new SimpleDateFormat("hh:mm", Locale.getDefault());
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 0 && hour < 5) {
            return "凌晨 " + timeformatter0to11.format(date);
        } else if (hour >= 5 && hour < 12) {
            return "上午 " + timeformatter0to11.format(date);
        } else if (hour >= 12 && hour < 18) {
            return "下午 " + timeformatter1to12.format(date);
        } else if (hour >= 18 && hour < 24) {
            return "晚上 " + timeformatter1to12.format(date);
        }
        return "";
    }

    /**
     * 根据日期获得星期
     *
     * @param date
     * @return
     */
    public static String getWeekOfDate(Date date) {
        String[] weekDaysName = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        // String[] weekDaysCode = { "0", "1", "2", "3", "4", "5", "6" };
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int intWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        return weekDaysName[intWeek];
    }

    public static boolean isSameDay(long time1, long time2) {
        return isSameDay(new Date(time1), new Date(time2));
    }

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);

        boolean sameDay = cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        return sameDay;
    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameWeekDates(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        int subYear = cal1.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        if (0 == subYear) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (1 == subYear && 11 == cal2.get(Calendar.MONTH)) {
            // 如果12月的最后一周横跨来年第一周的话则最后一周即算做来年的第一周
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        } else if (-1 == subYear && 11 == cal1.get(Calendar.MONTH)) {
            if (cal1.get(Calendar.WEEK_OF_YEAR) == cal2.get(Calendar.WEEK_OF_YEAR))
                return true;
        }
        return false;
    }

    public static long getSecondsByMilliseconds(long milliseconds) {
        long seconds = new BigDecimal((float) ((float) milliseconds / (float) 1000)).setScale(0,
                BigDecimal.ROUND_HALF_UP).intValue();
        // if (seconds == 0) {
        // seconds = 1;
        // }
        return seconds;
    }

    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else retStr = "" + i;
        return retStr;
    }

    public static String getElapseTimeForShow(int milliseconds) {
        StringBuilder sb = new StringBuilder();
        int seconds = milliseconds / 1000;
        if (seconds < 1)
            seconds = 1;
        int hour = seconds / (60 * 60);
        if (hour != 0) {
            sb.append(hour).append("小时");
        }
        int minute = (seconds - 60 * 60 * hour) / 60;
        if (minute != 0) {
            sb.append(minute).append("分");
        }
        int second = (seconds - 60 * 60 * hour - 60 * minute);
        if (second != 0) {
            sb.append(second).append("秒");
        }
        return sb.toString();
    }

    /**
     * 获取时间格式
     *
     * @param formatType
     * @return
     */
    private static String getTimeFormat(TimeFormatType formatType) {
        String formatStr = "";
        switch (formatType) {
            case TIME_FOEMAT_STANDARD:
                formatStr = "yyyy-MM-dd HH:mm:ss";
                break;
            case TIME_FOEMAT_NORMAL:
                formatStr = "yy-MM-dd HH:mm:ss";
                break;
            case TIME_FOEMAT_Y_M_D:
                formatStr = "yyyy-MM-dd";
                break;
            case TIME_FOEMAT_Y_Y :
                formatStr = "yyyy";
                break;
            case TIME_FOEMAT_Y_M:
                formatStr = "yy-MM";
                break;
            case TIME_FOEMAT_M_D:
                formatStr = "MM-dd";
                break;
            case TIME_FOEMAT_H_M_S:
                formatStr = "HH:mm:ss";
                break;
            case TIME_FOEMAT_H_M:
                formatStr = "HH:mm";
                break;
            case TIME_FOEMAT_M_S:
                formatStr = "mm:ss";
                break;
            case TIME_FOEMAT_NOT_S:
                formatStr = "yyyy/MM/dd HH:mm";
                break;
            case TIME_FOEMAT_NOT_TIME:
                formatStr = "yyyy/MM/dd";
                break;
            case TIME_FOEMAT:
                formatStr = "yyyy/MM/dd HH:mm:ss";
                break;
            case Y_M:
                formatStr = "yyyy/MM";
                break;
            case D:
                formatStr = "dd";
                break;
            case E:
                formatStr = "E";
                break;
        }
        return formatStr;
    }

    /**
     * 时间格式转换
     *
     * @param time
     *            标准时间格式yyyy-MM-dd HH:mm:ss
     * @param formatType
     *            时间格式类型
     * @return 返回指定的时间格式
     */
    public static String timeFormatStandardToSimple(String time,
                                                     TimeFormatType formatType) {
        SimpleDateFormat f = new SimpleDateFormat(
                getTimeFormat(TimeFormatType.TIME_FOEMAT_STANDARD));
        SimpleDateFormat formater = new SimpleDateFormat(
                getTimeFormat(formatType));
        Date date = null;
        try {
            date = f.parse(time);
            return formater.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     *  时间格式转换
     * @Title: timeFormatStandardToSimple
     * @param: Date mDate,
     * 		  TimeFormatType formatType
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @return String
     */
    public static String timeFormatStandardToSimple(Date mDate, TimeFormatType formatType)
    {
        if (null == mDate) {
            return "";
        }
        SimpleDateFormat formater = new SimpleDateFormat(
                getTimeFormat(formatType));
        return formater.format(mDate);
    }

    /**
     * 时间戳转换指定格式的时间 需要HH:mm格式，需要将timestamp/1000
     *
     * @param timestamp
     *            单位秒
     * @param formatType
     *            时间格式类型
     * @return 返回指定的时间格式
     */
    public static String timeFormatStandardToSimple(long timestamp,
                                                    TimeFormatType formatType) {
        SimpleDateFormat f = new SimpleDateFormat(getTimeFormat(formatType));

        String dateString = f.format(new Date(timestamp * 1000));
        return dateString;
    }

    public static String timeFormatStandardToSimple(long timestamp,
                                                    String pattern) {
        SimpleDateFormat f = new SimpleDateFormat(pattern);
        String dateString = f.format(new Date(timestamp * 1000));
        return dateString;
    }

    /**
     * 时间戳转换指定格式的时间
     *
     * @param timestamp
     *            单位秒
     * @param formatType
     *            时间格式类型
     * @return 返回指定的时间格式
     */
    public static String timeFormatStandardToSimple(Context context,
                                                    long timestamp, TimeFormatType formatType) {
        SimpleDateFormat f = new SimpleDateFormat(getTimeFormat(formatType));

        String dateString = f.format(new Date(timestamp * 1000));
        if (dateString.indexOf("一") != -1) {
            dateString = context.getString(R.string.w1);
        } else if (dateString.indexOf("二") != -1) {
            dateString = context.getString(R.string.w2);
        } else if (dateString.indexOf("三") != -1) {
            dateString = context.getString(R.string.w3);
        } else if (dateString.indexOf("四") != -1) {
            dateString = context.getString(R.string.w4);
        } else if (dateString.indexOf("五") != -1) {
            dateString = context.getString(R.string.w5);
        } else if (dateString.indexOf("六") != -1) {
            dateString = context.getString(R.string.w6);
        } else if (dateString.indexOf("日") != -1) {
            dateString = context.getString(R.string.w7);
        }

        return dateString;
    }

    /**
     * 获取当前时间
     *
     * @param formatType
     *            时间格式类型
     * @return 返回指定的时间格式
     */
    public static String getCurrentTime(TimeFormatType formatType) {
        SimpleDateFormat f = new SimpleDateFormat(getTimeFormat(formatType));
        return f.format(new Date(System.currentTimeMillis()));
    }

    /**
     * @param time
     *            标准时间格式yyyy-MM-dd HH:mm:ss
     * @return 与当前时间差的一个描述(比如:刚刚，几秒前，几分钟前，...)
     */
    public static String getLastUpdateTimeDesc(String time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                getTimeFormat(TimeFormatType.TIME_FOEMAT_STANDARD));
        try {
            String desc = "";
            Date d = simpleDateFormat.parse(time);
            Date n = new Date();
            long delay = n.getTime() - d.getTime();
            long secondsOfHour = 60 * 60;
            long secondsOfDay = secondsOfHour * 24;
            long secondsOfTwoDay = secondsOfDay * 2;
            long secondsOfThreeDay = secondsOfDay * 3;
            // 相差的秒数
            long delaySeconds = delay / 1000;
            if (delaySeconds < 10) {
                desc = "刚刚";
            } else if (delaySeconds <= 60) {
                desc = delaySeconds + "秒前";
            } else if (delaySeconds < secondsOfHour) {
                desc = (delaySeconds / 60) + "分前";
            } else if (delaySeconds < secondsOfDay) {
                desc = (delaySeconds / 60 / 60) + "小时前";
            } else if (delaySeconds < secondsOfTwoDay) {
                desc = "一天前";

            } else if (delaySeconds < secondsOfThreeDay) {
                desc = "两天前";

            }
            return desc;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * @param time
     *            标准时间格式yyyy/MM/dd 或 MM/dd/yyyy
     * @return 与当前时间差的一个描述(今天，昨天，yyyy/MM/dd 或 MM/dd/yyyy )
     *
     *         by ami 2014-09-16
     */
    public static String getDateDesc(String time, Context context) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                getTimeFormat(TimeFormatType.TIME_FOEMAT_NOT_TIME));
        String desc = time;
        try {
            Date d = simpleDateFormat.parse(time);
            Date n = new Date();
            long delay = n.getTime() - d.getTime();
            long secondsOfHour = 60 * 60;
            long secondsOfDay = secondsOfHour * 24;
            long secondsOfTwoDay = secondsOfDay * 2;
            // 相差的秒数
            long delaySeconds = delay / 1000;
            if (delaySeconds >= 0 && delaySeconds <= secondsOfDay) {
                desc = "今天";
            } else if (delaySeconds < secondsOfTwoDay) {
                desc = "昨天";
            }
            return desc;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return desc;
    }

    /**
     * 获取月份有多少天数
     *
     * @param year
     * @param month
     *            Java月份从0开始算
     * @return int
     */
    public static int getActualMaximum(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, month);// Java月份从0开始算
        return cal.getActualMaximum(Calendar.DATE);
    }

    /**
     * 获取当前日期
     *
     * @return int[]
     */
    public static int[] getCurrentDate() {
        int[] array = new int[3];
        // 获取系统时间
        Calendar calendar = Calendar.getInstance();
        array[0] = calendar.get(Calendar.YEAR);
        array[1] = calendar.get(Calendar.MONTH);
        array[2] = calendar.get(Calendar.DAY_OF_MONTH);
        return array;
    }

    /**
     * 得到几天前的时间
     *
     * @param d
     * @param day
     * @param format
     * @return String
     */
    public static String getDateBefore(Date d, int day, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format); // 格式化日期
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        String beforeDate = sdf.format(now.getTime());
        return beforeDate;
    }

    /**
     * 得到几天后的时间
     *
     * @param d
     * @param day
     * @param format
     * @return String
     */
    public static String getDateAfter(Date d, int day, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format); // 格式化日期
        Calendar now = Calendar.getInstance();
        now.setTime(d);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
        String afterDate = sdf.format(now.getTime());
        return afterDate;
    }

    /**
     * 将日期转换为Long类型
     *
     * @param dateString
     * @param type
     *            1代表开始或者其它代表结束 时间数组
     * @return Long
     */
    public static Long conversionDateToLong(int[] dateString, int type) {
        String string = "";
        if (type == 1) {
            string = dateString[0] + "/" + dateString[1] + "/" + dateString[2]
                    + " 00:00:00";
        } else {
            string = dateString[0] + "/" + dateString[1] + "/" + dateString[2]
                    + " 23:59:59";
        }

        SimpleDateFormat sdf = new SimpleDateFormat(
                getTimeFormat(TimeFormatType.TIME_FOEMAT));
        Date date = null;
        try {
            date = sdf.parse(string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(date != null){
            return date.getTime() / 1000;
        }
        return null ;
    }

    /**
     * 两个时间对比
     *
     * @param DATE1
     * @param DATE2
     * @return
     */
    public static int compare_date(String DATE1, String DATE2) {

        DateFormat df = new SimpleDateFormat(
                getTimeFormat(TimeFormatType.TIME_FOEMAT_NOT_TIME));
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    /**
     * 两个时间的月份是否一致
     *
     * @param Date1
     * @param Date2
     * @return
     */
    public static boolean compareDateMonth(String Date1, String Date2){
        boolean result = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();
        try {
            date1.setTime(sdf.parse(Date1));
            date2.setTime(sdf.parse(Date2));
            if (date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH)){
                result = true;
                return result;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 获取时间的月份
     * @param Date
     * @return
     * @throws ParseException
     */
    public static int getMonthDate(String Date){
        int month = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");//格式化为年月
        Calendar date1 = Calendar.getInstance();
        try {
            date1.setTime(sdf.parse(Date));
            month = date1.get(Calendar.MONTH);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return month + 1;
    }

    /**
     * 与当前系统时间对比
     *
     * @param DATE1
     * @param mFormatType
     * @return -1 1 0
     */
    @SuppressLint("SimpleDateFormat")
    public static int compareDate(String DATE1, TimeFormatType mFormatType) {
        DateFormat df = new SimpleDateFormat(
                getTimeFormat(mFormatType));
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = new Date();
            // System.out.println("dt1.getTime()="+dt1.getTime() +
            // "dt2.getTime()=" + dt2.getTime());
            if (dt1.getTime() > dt2.getTime()) {
                return -1;

            } else if (dt1.getTime() < dt2.getTime()) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    @SuppressLint("SimpleDateFormat")
    public static long conversionDateToLong(String dateStr, TimeFormatType type) {
        SimpleDateFormat sdf = new SimpleDateFormat(getTimeFormat(type));
        Date date = null;
        try {
            date = sdf.parse(dateStr.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return date.getTime();
        } else {
            return 0;
        }
    }

    public static long conversionServiceTimeToLong(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        Date date = null;
        try {
            date = sdf.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date != null) {
            return date.getTime();
        } else {
            return 0;
        }
    }
    /**
     * 时间戳转换日期格式
     *
     * @param timestamp
     *            单位毫秒
     * @return
     */
    public static String getCurrentMillTime(long timestamp, String format) {
        SimpleDateFormat f = new SimpleDateFormat(format);
        return f.format(new Date(timestamp));

    }
}
