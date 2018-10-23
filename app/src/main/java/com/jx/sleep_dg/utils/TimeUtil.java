package com.jx.sleep_dg.utils;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by 覃微 on 2018/5/25.
 */

public class TimeUtil {
    /**
     * 指定日期格式 yyyy.M.d
     */
    public static final String DATE_FORMAT_5 = "yyyy.MM.dd";
    /**
     * 指定日期格式 yyyy-MM-dd
     */
    public static final String DATE_FORMAT_4 = "yyyy-MM-dd";
    /**
     * 指定日期格式 yyyy-MM-dd
     */
    public static final String DATE_FORMAT_3 = "yyyy年MM月dd日";
    /**
     * 指定日期格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String DATE_FORMAT_2 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取年 日期
     *
     * @param date
     * @return
     */
    public static String getMonth(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String data = format.format(date);
        String[] newdata = data.split("-");
        return newdata[1];
    }

    /**
     * 获取
     *
     * @param date
     * @return
     */
    public static String getForth(Date date, String getformat) {
        SimpleDateFormat format = new SimpleDateFormat(getformat);
        return format.format(date);
    }

    //返回某个日期下几天的日期
    public static Date getNextDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + i);
        return cal.getTime();
    }

    /**
     * 获取月 日期
     *
     * @param date
     * @return
     */
    public static String getYear(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String data = format.format(date);
        String[] newdata = data.split("-");
        return newdata[0];
    }

    /**
     * 获取
     *
     * @param date
     * @return
     */
    public static String getYearAndMouthAndDat(Date date, String forth) {
        SimpleDateFormat format = new SimpleDateFormat(forth);
        return format.format(date);
    }

    /**
     * 根据指定格式，获取现在时间
     */
    public static String getNowDateFormat(String format) {
        final Date currentTime = new Date();
        final SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(currentTime);
    }

    /**
     * 根据指定日期返回这一周的数据
     */
    public static String getWeekData(String format, String unit) {
        String data = "";
        String week = getWeek(getDateFormat(format, DATE_FORMAT_4));
        LogUtil.e("星期几", getWeek(getDateFormat(format, DATE_FORMAT_4)));
        if ("星期一".equals(week)) {
            data = getDateFormat(format, DATE_FORMAT_4) + unit + getYearAndMouthAndDat(getNextDay(String2Date(format), 6), DATE_FORMAT_4);
        } else if ("星期二".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 1), DATE_FORMAT_4) + unit + getYearAndMouthAndDat(getNextDay(String2Date(format), 5), DATE_FORMAT_4);
        } else if ("星期三".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 2), DATE_FORMAT_4) + unit + getYearAndMouthAndDat(getNextDay(String2Date(format), 4), DATE_FORMAT_4);
        } else if ("星期四".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 3), DATE_FORMAT_4) + unit + getYearAndMouthAndDat(getNextDay(String2Date(format), 3), DATE_FORMAT_4);
        } else if ("星期五".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 4), DATE_FORMAT_4) + unit + getYearAndMouthAndDat(getNextDay(String2Date(format), 2), DATE_FORMAT_4);
        } else if ("星期六".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 5), DATE_FORMAT_4) + unit + getYearAndMouthAndDat(getNextDay(String2Date(format), 1), DATE_FORMAT_4);
        } else if ("星期天".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 6), DATE_FORMAT_4) + unit + getYearAndMouthAndDat(getNextDay(String2Date(format), 0), DATE_FORMAT_4);
        }
        return data;
    }

    /**
     * 根据指定日期返回这一周的数据
     */
    public static String getWeekDataString(String format) {
        String data = "";
        String week = getWeek(getDateFormat(format, DATE_FORMAT_4));
        LogUtil.e("星期几", getWeek(getDateFormat(format, DATE_FORMAT_4)));
        if ("星期一".equals(week)) {
            data = getDateFormat(format, DATE_FORMAT_4) + "&" + getYearAndMouthAndDat(getNextDay(String2Date(format), 6), DATE_FORMAT_4);
        } else if ("星期二".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 1), DATE_FORMAT_4) + "&" + getYearAndMouthAndDat(getNextDay(String2Date(format), 5), DATE_FORMAT_4);
        } else if ("星期三".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 2), DATE_FORMAT_4) + "&" + getYearAndMouthAndDat(getNextDay(String2Date(format), 4), DATE_FORMAT_4);
        } else if ("星期四".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 3), DATE_FORMAT_4) + "&" + getYearAndMouthAndDat(getNextDay(String2Date(format), 3), DATE_FORMAT_4);
        } else if ("星期五".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 4), DATE_FORMAT_4) + "&" + getYearAndMouthAndDat(getNextDay(String2Date(format), 2), DATE_FORMAT_4);
        } else if ("星期六".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 5), DATE_FORMAT_4) + "&" + getYearAndMouthAndDat(getNextDay(String2Date(format), 1), DATE_FORMAT_4);
        } else if ("星期天".equals(week)) {
            data = getYearAndMouthAndDat(getFrontDay(String2Date(format), 6), DATE_FORMAT_4) + "&" + getYearAndMouthAndDat(getNextDay(String2Date(format), 0), DATE_FORMAT_4);
        }
        return data;
    }

    //返回某个日期前几天的日期
    public static Date getFrontDay(Date date, int i) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - i);
        return cal.getTime();
    }

    public static SimpleDateFormat getFormat(String partten) {
        return new SimpleDateFormat(partten);
    }

    /**
     * 把String日期转成制定格式
     */
    public static String getDateFormat(String getDateString, String format) {
        if (!TextUtils.isEmpty(getDateString)) {
            final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

            Date getDate = null;
            try {
                getDate = getFormat(format).parse(getDateString);
            } catch (ParseException e) {
                e.printStackTrace();
                return getDateString;
            }

            return simpleDateFormat.format(getDate);
        }
        return getDateString;
    }

    /**
     * 把2012-02-20 星期一 转化Date 对象
     *
     * @param temp
     * @return
     */
    public static Date String2Date(String temp) {
//        final String str = getDateNoWeek(temp);
        if (temp != null && temp.length() == 10) {
            final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

            try {
                return format.parse(temp);
            } catch (ParseException e) {
                LogUtil.e(e.toString());
                e.printStackTrace();
            }

        }
        return null;
    }

    /**
     * 去掉星期只截取日期
     *
     * @param temp
     * @return
     */
    public static String getDateNoWeek(String temp) {
        if (temp != null && temp.length() > 10) {
            return temp.substring(0, 10);
        }
        return temp;
    }

    /**
     * 根据当前日期获得是星期几
     * time=yyyy-MM-dd
     *
     * @return
     */
    public static String getWeek(String time) {
        String Week = "";
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int wek = c.get(Calendar.DAY_OF_WEEK);

        if (wek == 1) {
            Week += "星期日";
        }
        if (wek == 2) {
            Week += "星期一";
        }
        if (wek == 3) {
            Week += "星期二";
        }
        if (wek == 4) {
            Week += "星期三";
        }
        if (wek == 5) {
            Week += "星期四";
        }
        if (wek == 6) {
            Week += "星期五";
        }
        if (wek == 7) {
            Week += "星期六";
        }
        return Week;
    }

    /**
     * 根据提供的年月日获取该月份的第一天
     *
     * @param year
     * @param monthOfYear
     * @return
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:26:57
     */
    public static Date getSupportBeginDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return firstDate;
    }

    /**
     * 根据提供的年月获取该月份的最后一天
     *
     * @param year
     * @param monthOfYear
     * @return
     * @Description: (这里用一句话描述这个方法的作用)
     * @Author: gyz
     * @Since: 2017-1-9下午2:29:38
     */
    public static Date getSupportEndDayofMonth(int year, int monthOfYear) {
        Calendar cal = Calendar.getInstance();
        // 不加下面2行，就是取当前时间前一个月的第一天及最后一天
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);

        cal.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = cal.getTime();

        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = cal.getTime();
        return lastDate;
    }


    /**
     * 16进制转换成为string类型字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "UTF-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }

    public static String englishData(String data) {
        String newdata = "";
        if ("1".equals(data) || "01".equals(data)) {
            return "January";
        } else if ("2".equals(data) || "02".equals(data)) {
            return "February";
        } else if ("3".equals(data) || "03".equals(data)) {
            return "Marcy";
        } else if ("4".equals(data) || "04".equals(data)) {
            return "April";
        } else if ("5".equals(data) || "05".equals(data)) {
            return "May";
        } else if ("6".equals(data) || "06".equals(data)) {
            return "June";
        } else if ("7".equals(data) || "07".equals(data)) {
            return "July";
        } else if ("8".equals(data) || "08".equals(data)) {
            return "August";
        } else if ("9".equals(data) || "09".equals(data)) {
            return "September";
        } else if ("10".equals(data) || "10".equals(data)) {
            return "October";
        } else if ("11".equals(data) || "11".equals(data)) {
            return "November";
        } else if ("12".equals(data) || "12".equals(data)) {
            return "December";
        }

        return newdata;
    }

    /**
     * 时间戳转时间
     */
    public static String timeStamp(long timeStamp) {
        //long timeStamp = 1495777335060;//直接是时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//这个是你要转成后的时间的格式
        return sdf.format(new Date(timeStamp));
    }

    /**
     * 根据毫秒返回时分秒
     *
     * @param time
     * @return
     */
    public static String getFormatHMS(long time) {
        time = time / 1000;//总秒数
        int s = (int) (time % 60);//秒
        int m = (int) (time / 60);//分
        int h = (int) (time / 3600);//秒
        return String.format("%02d:%02d:%02d", h, m, s);
    }
}
