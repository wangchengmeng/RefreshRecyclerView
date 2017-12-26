/**
 * @Project: Framework
 * @Title: DateUtil.java
 * @package com.maogu.htclibrary.util
 * @Description:
 * @author tan.xx
 * @date 2013-4-25 下午5:19:58
 * @Copyright: 2013 www.paidui.cn Inc. All rights reserved.
 * @version V1.0
 */
package com.maogu.htclibrary.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期处理工具类
 *
 * @author tan.xx
 * @version 2013-4-25 下午5:19:58 tan.xx<br>
 *          2013-11-07 下午5:19:58 zou.sq 增加判断市别的方法<br>
 */
public class DateUtil {
    public static final String TAG = "DateUtil";
    public static final String DEFAULT_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATETIME_FORMAT_YYYY_MM_DD_HH_MM = "yyyy-MM-dd HH:mm";
    public static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    public static String dtFormat(Date date, String dateFormat) {
        return getFormat(dateFormat).format(date);
    }

    public static DateFormat getFormat(String format) {
        return new SimpleDateFormat(format, Locale.getDefault());
    }

    /**
     * 获取日期在本月中第几天
     *
     * @param dateStr yyyy-MM-dd格式日期
     * @return int
     */
    public static int getDayOfMonth(String dateStr) {
        SimpleDateFormat df = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
        Date d = null;
        try {
            d = df.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(d);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取系统时间
     *
     * @param dateFormatStr 日期格式字符串 如： yyyy-MM-dd
     * @return 系统时间字符串
     */
    public static String getSysDate(String dateFormatStr) {
        String systemDate = "";
        if (dateFormatStr != null) {
            Calendar date = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat(dateFormatStr, Locale.getDefault());
            systemDate = dateFormat.format(date.getTime()); // 系统时间
        }
        return systemDate;
    }

    /**
     * 获取系统时间，用默认的时间格式
     *
     * @return 系统时间
     */
    public static String getSysDate() {
        return getSysDate(DEFAULT_DATE_FORMAT);
    }

    /**
     * 转换日期字符串格式
     *
     * @param dateStr    日期字符串（格式必须为fromFormat 一致）
     * @param fromFormat 原格式
     * @param toFormat   目标格式
     * @return String
     */
    public static String switchDateStr(String dateStr, String fromFormat, String toFormat) {
        try {
            SimpleDateFormat fromFormatter = new SimpleDateFormat(fromFormat, Locale.getDefault());
            ParsePosition pos = new ParsePosition(0);
            Date strDate = fromFormatter.parse(dateStr, pos);
            SimpleDateFormat toFormatter = new SimpleDateFormat(toFormat, Locale.getDefault());
            return toFormatter.format(strDate);
        } catch (Exception e) {
            com.maogu.htclibrary.util.EvtLog.w(TAG, "switchDateStr is error ");
            return dateStr;
        }
    }

    /**
     * 获取系统当前时间
     *
     * @param pattern 日期格式
     * @return String 时间格式
     */
    public static String getCurrentTime(String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern, Locale.getDefault());
        return df.format(new Date());
    }

    /**
     * 字符串转换到时间格式
     *
     * @param dateStr   需要转换的字符串
     * @param formatStr 需要格式的目标字符串 举例 yyyy-MM-dd
     * @return Date 返回转换后的时间
     * @throws ParseException 转换异常
     */
    public static Date stringToDate(String dateStr, String formatStr) throws ParseException {
        DateFormat sdf = new SimpleDateFormat(formatStr, Locale.getDefault());
        return sdf.parse(dateStr);
    }

    /**
     * 转换日期字符串
     *
     * @param i 0变为00， 9 变为09
     * @return String 返回类型
     */
    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else {
            retStr = "" + i;
        }
        return retStr;
    }

    /**
     * @param time1     时间1
     * @param time2     时间2
     * @param formatStr 格式
     * @return boolean 返回类型
     */
    public static boolean timeCompare(String time1, String time2, String formatStr) {
        Calendar calendar1 = stringToCalendar(time1, formatStr);
        Calendar calendar2 = stringToCalendar(time2, formatStr);
        return calendar1.compareTo(calendar2) >= 0;
    }

    /**
     * 把字符串转换成Calendar对象
     *
     * @param dateStr   带转换的字符串对象
     * @param formatStr 格式
     * @return Calendar 返回类型
     */
    public static Calendar stringToCalendar(String dateStr, String formatStr) {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        DateFormat dd = new SimpleDateFormat(formatStr, Locale.getDefault());
        Date date;
        try {
            date = dd.parse(dateStr);
            calendar.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }

    public static int getDayOfWeek(String dateStr) {
        Calendar calendar = stringToCalendar(dateStr, DEFAULT_DATE_FORMAT);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 把Calendar对象转成字符串
     *
     * @param calendar  带转换的Calendar对象
     * @param formatStr 格式
     * @return String 返回类型
     */
    public static String calendarToString(Calendar calendar, String formatStr) {
        SimpleDateFormat df = new SimpleDateFormat(formatStr, Locale.getDefault());
        return df.format(calendar.getTime());
    }

    /**
     * 从yyyy-MM-dd HH:mm:ss格式字符串中获取 年月日
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss格式字符串
     * @return String 年月日
     */
    public static String getDateStr(String dateStr) {
        if (!com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(dateStr)) {
            if (dateStr.length() > 10) {
                return dateStr.substring(0, 10);
            }
        }
        return dateStr;
    }

    /**
     * 从yyyy-MM-dd HH:mm:ss格式字符串中获取HH:mm 时间
     *
     * @param dateStr 从yyyy-MM-dd HH:mm:ss格式字符串
     * @return String HH:mm
     */
    public static String getTimeStr(String dateStr) {
        if (!com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(dateStr)) {
            if (dateStr.length() > 15) {
                return dateStr.substring(11, 16);
            }
        }
        return dateStr;
    }

    /**
     * 时间增加相应的分钟
     *
     * @param dateStr    起始时间
     * @param addMinutes 增加分钟
     * @param formatStr  日期格式
     * @return String
     */
    public static String addMinutes(String dateStr, int addMinutes, String formatStr) {
        Calendar calendar = stringToCalendar(dateStr, formatStr);
        calendar.add(Calendar.MINUTE, addMinutes);
        return calendarToString(calendar, formatStr);
    }

    /**
     * 时间增加相应的日期
     *
     * @param dateStr   起始时间
     * @param addDays   增加天数
     * @param formatStr 日期格式
     * @return String
     */
    public static String addDays(String dateStr, int addDays, String formatStr) {
        Calendar calendar = stringToCalendar(dateStr, formatStr);
        calendar.add(Calendar.DATE, addDays);
        return calendarToString(calendar, formatStr);
    }

    /**
     * 获取周几
     *
     * @param dateTime   日期
     * @param weekDayArr 星期描述 String[]
     * @return String
     */
    private static String getWeekDay(String dateTime, String[] weekDayArr) {
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(dateTime) || weekDayArr == null || weekDayArr.length == 0) {
            return "";
        }
        Calendar calendar = DateUtil.stringToCalendar(DateUtil.getDateStr(dateTime), DateUtil.DEFAULT_DATE_FORMAT);
        if (calendar == null) {
            return "";
        }
        String week = "";
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.MONDAY:
                if (weekDayArr.length > 0) {
                    week = weekDayArr[0];
                }
                break;

            case Calendar.TUESDAY:
                if (weekDayArr.length > 1) {
                    week = weekDayArr[1];
                }
                break;
            case Calendar.WEDNESDAY:
                if (weekDayArr.length > 2) {
                    week = weekDayArr[2];
                }
                break;
            case Calendar.THURSDAY:
                if (weekDayArr.length > 3) {
                    week = weekDayArr[3];
                }
                break;
            case Calendar.FRIDAY:
                if (weekDayArr.length > 4) {
                    week = weekDayArr[4];
                }
                break;
            case Calendar.SATURDAY:
                if (weekDayArr.length > 5) {
                    week = weekDayArr[5];
                }
                break;
            case Calendar.SUNDAY:
                if (weekDayArr.length > 6) {
                    week = weekDayArr[6];
                }
                break;
            default:
                week = "";
                break;
        }
        return week;
    }

    /**
     * @param firstTime  第一个时间
     * @param secondTime 第二个时间
     * @param formatStr  格式，建议"yyyy-MM-dd HH:mm:ss"
     * @return 返回两个日期时间日差
     */
    public static int getSubDays(String firstTime, String secondTime, String formatStr) {
        DateFormat dateFormat = new SimpleDateFormat(formatStr, Locale.getDefault());
        try {
            Date date1 = dateFormat.parse(firstTime);
            Date date2 = dateFormat.parse(secondTime);
            long time1 = date1.getTime();
            long time2 = date2.getTime();
            long subTime = time1 - time2;
            return (int) Math.ceil(subTime / 3600d / 1000d / 24d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 判断当前时间是否在两个时间范围内
     *
     * @param beginTime   开始时间
     * @param endTime     结束时间
     * @param currentTime 当前时间
     * @return boolean 是否在这个时间段
     */
    public static boolean isBelongTimespan(String beginTime, String endTime, String currentTime) {
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(beginTime) || com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(endTime)
                || com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(currentTime)) {
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {
            Date begin = df.parse(beginTime);
            Date end = df.parse(endTime);
            Date current = df.parse(currentTime);
            if (current.after(begin) && current.before(end)) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    /**
     * 判断当前时间是否在两个时间范围内
     *
     * @param beginTime 开始时间 endTime 结束时间
     * @return boolean 是否在这个时间段
     */
    public static boolean isBelongTimespan(String beginTime, String endTime) {
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(beginTime) || com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(endTime)) {
            return false;
        }
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = df.format(new Date());
        return isBelongTimespan(beginTime, endTime, currentTime);
    }

    /**
     * 判断两个日期是不是同一天
     *
     * @param serverTime 服务器时间
     * @param msgTime    实际时间
     * @return boolean true是同一天，false则不是同一天
     */
    public static boolean isSameDay(String serverTime, String msgTime) {
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(serverTime) || com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(msgTime)) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
        Date serverDate;
        Date msgDate;
        try {
            serverDate = format.parse(serverTime);
            msgDate = format.parse(msgTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }

        if (null != serverDate && null != msgDate) {
            if (serverDate.getYear() == msgDate.getYear() && serverDate.getMonth() == msgDate.getMonth()
                    && serverDate.getDate() == msgDate.getDate()) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断两个时间是不是同一年
     *
     * @param serverTime 服务器时间
     * @param msgTime    实际时间
     * @return boolean true是同一年，false则不是同一年
     */
    public static boolean isSameYear(String serverTime, String msgTime) {
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(serverTime) || com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(msgTime)) {
            return false;
        }
        SimpleDateFormat format = new SimpleDateFormat(DEFAULT_DATE_FORMAT, Locale.getDefault());
        Date serverDate;
        Date msgDate;
        try {
            serverDate = format.parse(serverTime);
            msgDate = format.parse(msgTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        if (null != serverDate && null != msgDate) {
            if (serverDate.getYear() == msgDate.getYear()) {
                return true;
            }
        }
        return false;
    }

    public static String formatTime(String time, String formatType) {
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(time)) {
            return "";
        }
        SimpleDateFormat format = new SimpleDateFormat(DATETIME_FORMAT_YYYY_MM_DD_HH_MM, Locale.getDefault());
        Date msgDate;
        try {
            msgDate = format.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(formatType)) {
            return format.format(msgDate);
        }
        format = new SimpleDateFormat(formatType, Locale.getDefault());
        return format.format(msgDate);
    }

    public static boolean isMoreThan3days(String startTime, String endTime) {
        if (com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(startTime) || com.maogu.htclibrary.util.StringUtil.isNullOrEmpty(endTime)) {
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATETIME_FORMAT, Locale.getDefault());
        Date start;
        Date end;
        try {
            start = sdf.parse(startTime);
            end = sdf.parse(endTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        long cha = end.getTime() - start.getTime();
        long result = 3 * 24 * 60 * 60 * 1000;
        return cha >= result;
    }

    /**
     * @param time 时间戳
     * @return 格式化时间
     */
    public static String formatTime(long time) {
        if (0 == time) {
            return "";
        }
        SimpleDateFormat format1 = new SimpleDateFormat("MM.dd HH:mm", Locale.getDefault());
        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTimeInMillis(time);

        Calendar calendar2 = Calendar.getInstance();

        int year1 = calendar1.get(Calendar.YEAR);
        int year2 = calendar2.get(Calendar.YEAR);

        long diff = (calendar2.getTimeInMillis() - calendar1.getTimeInMillis()) / 1000; // 转换秒
        if (diff < 60) {
            return "刚刚";
        } else if (diff < 60 * 60) {
            return (diff / 60) + 1 + "分钟前";
        } else if (diff < 60 * 60 * 24) {
            return diff / (60 * 60) + "小时前";
        } else if (diff < 60 * 60 * 24 * 2) {
            return "昨天";
        } else if (diff < 60 * 60 * 24 * 3) {
            return "前天";
        } else if (diff < 60 * 60 * 24 * 10) {
            return diff / (60 * 60 * 24) + "天前";
        } else {
            return format.format(new Date(time));
        }
    }
}
