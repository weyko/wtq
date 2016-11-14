package chat.common.util.time;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.imlibrary.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import chat.common.util.TextUtils;

public class DateUtils {
    private static final String DATETIME = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE = "yyyy-MM-dd";
    private static final String TIME = "HH:mm:ss";
    private static final long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
    private static final long nh = 1000 * 60 * 60;//一小时的毫秒数
    private static final long nm = 1000 * 60;//一分钟的毫秒数
    /**
     * 倒计时-展示分/秒
     */
    public static final int MINUTE_SECOND = 101;
    /**
     * 倒计时-展示天/时/分/秒
     */
    public static final int DAY_HOUR_MINUTE_SECOND = MINUTE_SECOND + 1;

    /**
     * 比较时间大小
     */
    public static boolean isCloseEnough(long paramLong1, long paramLong2) {
        long l = paramLong1 - paramLong2;
        if (l < 0L)
            l = -l;
        return l < 180L * 1000;
    }


    /**
     * @return String
     * @Title: getStampTime
     * @param: long time
     * @Description: 将时间戳转换为HH:mm
     */
    public static String getStampTime(long time) {
        String mTmepString = "";
        if (time == 0) {
            return mTmepString;
        }
        String format = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        mTmepString = sdf.format(new Date(Long.valueOf(time)));
        return mTmepString;
    }

    public static String getDateFormatWithPattern(Date date, String pattern) {
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        return format.format(date);
    }

    public static Date getDateWithPattern(String time, String pattern) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 倒计时工具
     *
     * @param context
     * @param baseTime  初始时间（GMT时区）
     * @param PreFix    剩余时间提示文本
     * @param limitText 要显示的控件
     */
    public static String setLimitTimerText(Context context,
                                           final String baseTime, final int mTextType, final String PreFix,
                                           final TextView limitText) {
        final StringBuffer limiteTime = new StringBuffer();
        final SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss");
//		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        final Context appContext = context.getApplicationContext();
        stopLimitTimer();
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Date date = null;
                    try {
                        date = dateFormat.parse(baseTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Calendar calendar = Calendar.getInstance();
                    switch (mTextType) {
                        case MINUTE_SECOND:
                            long todaySecond = calendar.getTimeInMillis() / 1000;
                            long limiteSecond = 0;
                            if (date != null) {
                                limiteSecond = date.getTime() / 1000;
                            }
                            long dotSecond = limiteSecond - todaySecond;
                            long minute,
                                    second;
                            if (dotSecond > 0) {
                                long limitSeconds = dotSecond % (24 * 60 * 60);
                                minute = (limitSeconds % (60 * 60)) / 60;
                                second = ((limitSeconds % (60 * 60))) % 60;
                                limiteTime.append(minute > 9 ? minute : "0"
                                        + minute);
                                limiteTime.append(appContext
                                        .getString(R.string.goods_minute));
                                limiteTime.append(second > 9 ? second : "0"
                                        + second);
                                limiteTime.append(appContext
                                        .getString(R.string.goods_second));
                            }
                            break;

                        default:
                            long todaySeconds = calendar.getTimeInMillis() / 1000;
                            long limiteSeconds = 0;
                            if (date != null) {
                                limiteSeconds = date.getTime() / 1000;
                            }
                            long dotSeconds = limiteSeconds - todaySeconds;
                            long day,
                                    hours,
                                    minutes,
                                    seconds;
                            if (dotSeconds > 0) {
                                day = dotSeconds / (24 * 60 * 60);
                                long limitSeconds = dotSeconds % (24 * 60 * 60);
                                hours = limitSeconds / (60 * 60);
                                minutes = (limitSeconds % (60 * 60)) / 60;
                                seconds = ((limitSeconds % (60 * 60))) % 60;
                                limiteTime.append(day > 9 ? day : "0" + day);
                                limiteTime.append(appContext
                                        .getString(R.string.goods_day));
                                limiteTime.append(hours > 9 ? hours : "0" + hours);
                                limiteTime.append(appContext
                                        .getString(R.string.goods_hour));
                                limiteTime.append(minutes > 9 ? minutes : "0"
                                        + minutes);
                                limiteTime.append(appContext
                                        .getString(R.string.goods_minute));
                                limiteTime.append(seconds > 9 ? seconds : "0"
                                        + seconds);
                                limiteTime.append(appContext
                                        .getString(R.string.goods_second));
                            }
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (limiteTime != null && limiteTime.length() > 0) {
                    limitText.setText(PreFix + limiteTime);
                    limiteTime.delete(0, limiteTime.length());
                } else {
                    return;
                }
                Message message = handler.obtainMessage(0);
                sendMessageDelayed(message, 1000);
            }
        };
        Message message = handler.obtainMessage(0);
        handler.sendMessageDelayed(message, 1000);
        return limiteTime.toString();
    }

    public static void stopLimitTimer() {
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
            System.gc();
        }
    }

    private static Handler handler = null;

    /**
     * 设置年龄和星座
     *
     * @param context
     * @param time
     * @param mAge
     * @param constellation
     * @param age
     */
    public static void setAge(Context context, long time, TextView mAge,
                              TextView constellation, int age) {
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(time * 1000));

        if (age == -1) {
            if (time != 0) {
                age = DateUtils.getCurrentAge(time * 1000);
            } else {
                age = 0;
            }
        }

        mAge.setText(age + "");
        constellation.setText(DateUtils.getConstellation(context,
                cal2.get(Calendar.MONTH) + 1, cal2.get(Calendar.DAY_OF_MONTH)));
    }

    /**
     * fix by weyko 2015.7.17 处理不支持英文格式化情况
     *
     * @param paramDate
     * @return 返回一个大概的时间，今天，昨天，几月几号，几年几月
     */
    public static String getTimestampString(Context context, Date paramDate) {
        String str;
        long l = paramDate.getTime();
        if (isSameDay(l))
            str = context.getString(R.string.today);
        else if (isYesterday(l))
            str = context.getString(R.string.yesterday);
        else {
            if (isSameYear(l))
                str = new SimpleDateFormat("MM-dd", Locale.CHINA)
                        .format(paramDate);
            else
                str = new SimpleDateFormat("yyyy-MM", Locale.CHINA)
                        .format(paramDate);

        }
        return str;
    }

    public static boolean isSameYear(long paramLong) {
        Date d1 = new Date(paramLong);
        Date d2 = new Date(System.currentTimeMillis());
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);

    }

    /**
     * 判断是否同一天
     *
     * @param d1
     * @param d2
     * @return
     */
    public static boolean isSameDay(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        c1.setTime(d1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(d2);
        int year1 = c1.get(Calendar.YEAR);
        int year2 = c2.get(Calendar.YEAR);
        int month1 = c1.get(Calendar.MONTH);
        int month2 = c2.get(Calendar.MONTH);
        int day1 = c1.get(Calendar.DAY_OF_MONTH);
        int day2 = c1.get(Calendar.DAY_OF_MONTH);
        if (year1 == year2 && month1 == month2 && day1 == day2)
            return true;

        return false;
    }

    /**
     * 根据指定字符串返回long类型
     *
     * @param date
     * @return
     */
    public static long getDateFormat(String date) {
        long time = 0;
        if (date != null && date.length() > 0) {
            try {
                SimpleDateFormat format = new SimpleDateFormat(DATE);
                Date parse = format.parse(date);
                time = parse.getTime();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return time;
    }

    /**
     * 根据指定字符串返回long类型
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static long getDateFormat2DATETIME(String date)
            throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(DATETIME);
        Date parse = format.parse(date);
        return parse.getTime();
    }

    /**
     * 根据指定字符串返回long类型
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public static long getDateFormat3(String date) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date parse = format.parse(date);
        return parse.getTime();
    }

    public static String getDateFormat2Date(String s) {
        String date = "";
        try {
            date = getDateFormat(getDateFormat2DATETIME(s));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 根据毫秒级时间戳，转换 yyyy-MM-dd格式
     *
     * @param milliseconds
     * @return 2012-03-04
     */
    public static String getDateFormat(long milliseconds) {
        Date d = new Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat(DATE);
        return format.format(d);
    }

    /**
     * 根据毫秒级时间戳，转换 yyyy-MM-dd HH:mm:ss格式
     *
     * @param milliseconds
     * @return
     */
    public static String getDateFormatToSeconds(long milliseconds) {
        Date d = new Date(milliseconds);
        SimpleDateFormat format = new SimpleDateFormat(DATETIME);
        return format.format(d);
    }

    /**
     * 获取yyyyMMdd格式日期
     *
     * @param time
     * @return
     */
    public static Date getDate(String time) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static Date getDateToMm(String time) {
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            date = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String formatDate(Context context, long date) {
        @SuppressWarnings("deprecation")
        int format_flags = android.text.format.DateUtils.FORMAT_NO_NOON_MIDNIGHT
                | android.text.format.DateUtils.FORMAT_ABBREV_ALL
                | android.text.format.DateUtils.FORMAT_CAP_AMPM
                | android.text.format.DateUtils.FORMAT_SHOW_DATE
                | android.text.format.DateUtils.FORMAT_SHOW_DATE
                | android.text.format.DateUtils.FORMAT_SHOW_TIME;
        return android.text.format.DateUtils.formatDateTime(context, date,
                format_flags);
    }

    /**
     * 获取已过去的时间数
     */
    public static int orderDate(Date date) {
        int dateSum = 0;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(date);
        System.out.println(dateStr);
        int year = 0;
        int month = 0;
        int day = 0;
        try {
            if (dateStr.length() > 0) {
                year = Integer.valueOf(dateStr.substring(0, 4));
                month = Integer.valueOf(dateStr.substring(5, 7));
                day = Integer.valueOf(dateStr.substring(8, 10));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 1; i < month; i++) {
            switch (i) {
                case 1:
                case 3:
                case 5:
                case 7:
                case 8:
                case 10:
                case 12:
                    dateSum += 31;
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dateSum += 30;
                    break;
                case 2:
                    if (((year % 4 == 0) & (year % 100 != 0)) | (year % 400 == 0))
                        dateSum += 29;
                    else
                        dateSum += 28;
            }
        }

        return dateSum = dateSum + day;
    }

    public static String formatDate1(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (date == null)
            return "";
        return format.format(date);
    }

    public static String formatDate3(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd HH:mm");
        if (date == null)
            return "";
        return format.format(date);
    }

    /**
     * 获取 YYYY-MM-dd 格式日期
     */
    public static String formatDate2(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private static boolean isSameDay(long paramLong) {
        TimeInfo localTimeInfo = getTodayStartAndEndTime();
        return (paramLong > localTimeInfo.getStartTime())
                && (paramLong < localTimeInfo.getEndTime());
    }

    private static boolean isYesterday(long paramLong) {
        TimeInfo localTimeInfo = getYesterdayStartAndEndTime();
        return (paramLong > localTimeInfo.getStartTime())
                && (paramLong < localTimeInfo.getEndTime());
    }

    public static TimeInfo getYesterdayStartAndEndTime() {
        Calendar localCalendar1 = Calendar.getInstance();
        localCalendar1.add(5, -1);
        localCalendar1.set(11, 0);
        localCalendar1.set(12, 0);
        localCalendar1.set(13, 0);
        localCalendar1.set(14, 0);
        Date localDate1 = localCalendar1.getTime();
        long l1 = localDate1.getTime();
        Calendar localCalendar2 = Calendar.getInstance();
        localCalendar2.add(5, -1);
        localCalendar2.set(11, 23);
        localCalendar2.set(12, 59);
        localCalendar2.set(13, 59);
        localCalendar2.set(14, 999);
        Date localDate2 = localCalendar2.getTime();
        long l2 = localDate2.getTime();
        TimeInfo localTimeInfo = new TimeInfo();
        localTimeInfo.setStartTime(l1);
        localTimeInfo.setEndTime(l2);
        return localTimeInfo;
    }

    public static TimeInfo getTodayStartAndEndTime() {
        Calendar localCalendar1 = Calendar.getInstance();
        localCalendar1.set(11, 0);
        localCalendar1.set(12, 0);
        localCalendar1.set(13, 0);
        localCalendar1.set(14, 0);
        Date localDate1 = localCalendar1.getTime();
        long l1 = localDate1.getTime();
        /*SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss S");*/
        Calendar localCalendar2 = Calendar.getInstance();
        localCalendar2.set(11, 23);
        localCalendar2.set(12, 59);
        localCalendar2.set(13, 59);
        localCalendar2.set(14, 999);
        Date localDate2 = localCalendar2.getTime();
        long l2 = localDate2.getTime();
        TimeInfo localTimeInfo = new TimeInfo();
        localTimeInfo.setStartTime(l1);
        localTimeInfo.setEndTime(l2);
        return localTimeInfo;
    }

    /**
     * fix by weyko 2015.7.17 处理不支持英文格式化情况 获取聊天格式时间
     *
     * @param context
     * @param paramDate
     * @return
     */
    public static String getTimesTampString(Context context, Date paramDate) {
        String str = null;
        long l = paramDate.getTime();
        boolean isYesterday = false;
        if (isSameDay(l)) {
            Calendar localCalendar = GregorianCalendar.getInstance();
            localCalendar.setTime(paramDate);
            int i = localCalendar.get(11);
            if (i > 17)
                // str = "晚上 hh:mm";
                str = "HH:mm";
            else if ((i >= 0) && (i <= 6))
                // str = "凌晨 hh:mm"
                str = "HH:mm";
            else if ((i > 11) && (i <= 17))
                // str = "下午 hh:mm";
                str = "HH:mm";
            else
                // str = "上午 hh:mm";
                str = "HH:mm";
        } else if (isYesterday(l)) {
            str = "HH:mm";// fix by weyko 适配语言
            isYesterday = true;
        } else {
            str = context.getString(R.string.date_format);
        }
        return (isYesterday ? context.getString(R.string.yesterday) : "")
                + new SimpleDateFormat(str).format(paramDate);
    }

    //add by h.j.huang
    public static String getTimesTampString(Context context, long mills) {
        boolean isYesterday = false;
        String pattern = "HH:mm";
        if (isSameDay(mills)) {
            pattern = "HH:mm";
        } else if (isYesterday(mills)) {
            isYesterday = true;
            pattern = "HH:mm";
        } else {
            pattern = context.getString(R.string.date_format);
        }
        return (isYesterday ? context.getString(R.string.yesterday) : "")
                + TimeUtil.timeFormatStandardToSimple(mills / 1000, pattern);
    }

    /**
     * According to the time stamp for the current age 根据当前的时间戳返回年龄
     */
    public static int getCurrentAge(long TimeInMillis) {
        int age = 0;
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(new Date(TimeInMillis));
        age = cal.get(Calendar.YEAR) - cal2.get(Calendar.YEAR);
        int currentYear = cal.get(Calendar.YEAR);
        int newyear = cal2.get(Calendar.YEAR);
        age = currentYear - newyear;
        if ((cal.get(Calendar.MONTH) - cal2.get(Calendar.MONTH)) < 0) {
            age -= 1;
        }
        return age;
    }

    /**
     * 计算一个时间跟系统时间相差多少天
     *
     * @return long
     * @Title: getWithThreedaysStatus
     * @param: String time
     * @Description: 返回相差的天数。如果小于一天但大于当前系统时间则返回1，以及该时间日期及时分相同，但分钟数大于当前系统时间分钟数则仍返回1，否则返回0.
     */
    public static long getTimeDifference(String time) {
        long nowTimeLong = new Date().getTime();
        long ckStartTimeLong;
        try {
            ckStartTimeLong = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time)
                    .getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        long diff = ckStartTimeLong - nowTimeLong;
        long day = diff / nd;
        long hour = diff % nd / nh;//计算差多少小时
        long min = diff % nd % nh / nm;//计算差多少分钟
        if (day == 0) {//相同日期
            if (hour == 0) {//相同分钟数
                if (min > 0) {
                    return 1;
                }
            } else if (hour > 0) {
                return 1;
            }
        }
        return day;
    }

    @SuppressWarnings("deprecation")
    public static String getTime(Context mContext, String time) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
            Date date = sdf.parse(time);
            Date nowDay = new Date();

            if (date.getYear() == nowDay.getYear()
                    && date.getMonth() == nowDay.getMonth()
                    && date.getDate() == nowDay.getDate()) {
                return time.substring(11, 16);
            } else if (date.getYear() == nowDay.getYear()
                    && date.getMonth() == nowDay.getMonth()
                    && date.getDate() == nowDay.getDate() - 1) {
                return mContext.getString(R.string.yesterday)
                        + time.substring(11, 16);
            } else {
                if (date.getYear() == nowDay.getYear()) {
                    return time.substring(5, 16);
                } else {
                    return time.substring(0, 16);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return time;
        }
    }


    /**
     * 根据月日获取星座
     *
     * @param month 月
     * @param day   日
     * @return
     */
    public static String getConstellation(Context context, int month, int day) {
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18)) {
            return context.getString(R.string.person_consttelltion_aquarius);
        } else if ((month == 2 && day >= 19) || (month == 3 && day <= 20)) {
            return context.getString(R.string.person_consttelltion_Pisces);
        } else if ((month == 3 && day >= 21) || (month == 4 && day <= 19)) {
            return context.getString(R.string.person_consttelltion_Aries);
        } else if ((month == 4 && day >= 20) || (month == 5 && day <= 20)) {
            return context.getString(R.string.person_consttelltion_Taurus);
        } else if ((month == 5 && day >= 21) || (month == 6 && day <= 21)) {
            return context.getString(R.string.person_consttelltion_Gemini);
        } else if ((month == 6 && day >= 22) || (month == 7 && day <= 22)) {
            return context.getString(R.string.person_consttelltion_cancer);
        } else if ((month == 7 && day >= 23) || (month == 8 && day <= 22)) {
            return context.getString(R.string.person_consttelltion_Leo);
        } else if ((month == 8 && day >= 23) || (month == 9 && day <= 22)) {
            return context.getString(R.string.person_consttelltion_virgo);
        } else if ((month == 9 && day >= 23) || (month == 10 && day <= 23)) {
            return context.getString(R.string.person_consttelltion_libra);
        } else if ((month == 10 && day >= 24) || (month == 11 && day <= 22)) {
            return context.getString(R.string.person_consttelltion_Scorpio);
        } else if ((month == 11 && day >= 23) || (month == 12 && day <= 21)) {
            return context.getString(R.string.person_consttelltion_Sagittarius);
        } else /*
                 * if ((((month != 12) || (day < 22))) && (((month != 1) || (day
				 * > 19))))
				 */ {
            return "魔蝎座";
        }
        // return "";
    }

    /**
     * 根据年月日获取年龄
     *
     * @param year  年
     * @param month 月
     * @param day   日
     * @return
     */
    public static int getCurrentAge(int year, int month, int day) {
        int age = 0;
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.YEAR) == year) {
            if (calendar.get(Calendar.MONTH) == month) {
                if (calendar.get(Calendar.DAY_OF_MONTH) >= day) {
                    age = calendar.get(Calendar.YEAR) - year + 1;
                } else {
                    age = calendar.get(Calendar.YEAR) - year;
                }
            } else if (calendar.get(Calendar.MONTH) > month) {
                age = calendar.get(Calendar.YEAR) - year + 1;
            } else {
                age = calendar.get(Calendar.YEAR) - year;
            }
        } else {
            age = calendar.get(Calendar.YEAR) - year;
        }
        if (age < 0) {
            return 0;
        }
        return age;
    }

    /**
     * 根据日期格式转时间戳 add by liumin
     */
    public static long getTimeStamp(String str) {
        long timeStamp = 0;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm");
        Date date;
        try {
            date = simpleDateFormat.parse(str);
            timeStamp = date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeStamp / 1000;
    }

    /**
     * 根据日期格式转时间戳 add by liumin
     */
    /*public static long getTimeStampOne(String str) {
		long timeStamp = 0;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm");
		Date date;
		try {
			date = simpleDateFormat.parse(str);
			timeStamp = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timeStamp / 1000;
	}*/

    /**
     * 获取两个日期之间的间隔天数 liumin
     *
     * @return 间隔天数
     */
    public static int getGapCount(Date startTime2, Date endTime2) {
        if (startTime2 == null || endTime2 == null) {
            return 0;
        }
        Calendar fromCalendar = Calendar.getInstance();
        fromCalendar.setTime(startTime2);
        fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
        fromCalendar.set(Calendar.MINUTE, 0);
        fromCalendar.set(Calendar.SECOND, 0);
        fromCalendar.set(Calendar.MILLISECOND, 0);

        Calendar toCalendar = Calendar.getInstance();
        toCalendar.setTime(endTime2);
        toCalendar.set(Calendar.HOUR_OF_DAY, 0);
        toCalendar.set(Calendar.MINUTE, 0);
        toCalendar.set(Calendar.SECOND, 0);
        toCalendar.set(Calendar.MILLISECOND, 0);

        return (int) ((toCalendar.getTime().getTime() - fromCalendar.getTime()
                .getTime()) / (1000 * 60 * 60 * 24));
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 设置的需要判断的时间 //格式如2015-11-18
     */
    public static String getWeek(Context context, String pTime) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week = context.getString(R.string.sunday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week = context.getString(R.string.monday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week = context.getString(R.string.tuesday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week = context.getString(R.string.wednesday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week = context.getString(R.string.thursday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week = context.getString(R.string.friday);
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week = context.getString(R.string.saturday);
        }
        return Week;
    }

    /**
     * @return
     */
    public static String convertToLocalTimeZoneData(String GTMData, String formatType) {
        String localTimeZoneData = "";
        if (TextUtils.getString(GTMData).length() > 0) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(formatType);
            dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date date = null;
            try {
                date = dateFormat.parse(GTMData);
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            if (date != null) {
                dateFormat.setTimeZone(TimeZone.getDefault());
                localTimeZoneData = dateFormat.format(date);
            }
        }
        return localTimeZoneData;
    }

    /**
     * @return
     */
    public static String convertToLocalTimeZoneData(Date date,
                                                    String formatType) {
        String localTimeZoneData = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatType);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        if (date != null) {
            dateFormat.setTimeZone(TimeZone.getDefault());
            localTimeZoneData = dateFormat.format(date);
        }
        return localTimeZoneData;
    }


    public static String getTimeStringFormDate(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static long getLongTypeTime(String time) {
        long endTime = 0;
        if (android.text.TextUtils.isEmpty(time)) {
            return endTime;
        }
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = format.parse(time);
            endTime = date.getTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return endTime;
    }

    static class TimeInfo {
        private long startTime;
        private long endTime;
        public long getStartTime() {
            return this.startTime;
        }

        public void setStartTime(long paramLong) {
            this.startTime = paramLong;
        }

        public long getEndTime() {
            return this.endTime;
        }

        public void setEndTime(long paramLong) {
            this.endTime = paramLong;
        }

    }
}


