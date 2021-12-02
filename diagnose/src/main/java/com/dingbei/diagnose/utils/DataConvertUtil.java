package com.dingbei.diagnose.utils;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Dayo
 * @desc 处理数据工具类
 */

public class DataConvertUtil {

    private static SimpleDateFormat sSimpleDateFormat;

    /**
     * 时间转换 格式 2016-01-21T00:07:29.637642
     *
     * @param time 格式 2016-01-21T00:07:29.637642
     * @return 转码后的字符
     */
    public static String convertTime(String time) {
        TimeZone tz = TimeZone.getDefault();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        long l = 0;
        try {
            l = simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String str = "";

        double sum = ((System.currentTimeMillis() - tz.getRawOffset()) / 1000) - (l / 1000);
        double m = sum / 60 / 60 / 24;
        double n = sum / 60 / 60;
        double b = sum / 60;

        // if(m > 7){
        //     str = convertUTC2GMT(time,"MM-dd");
        // } else
        if (m > 1) {
            // str = (int) m + "天前";
            str = convertUTC2GMT(time, "MM-dd HH:mm");
        } else if (n > 1) {
            str = (int) n + "小时前";
        } else if (b > 1) {
            str = (int) b + "分钟前";
        } else {
            str = "1分钟前";
        }

        return str;
    }

    public static String convertTimeYMD(String time) {
        TimeZone tz = TimeZone.getDefault();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        long l = 0;
        try {
            l = simpleDateFormat.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String str = "";

        double sum = ((System.currentTimeMillis() - tz.getRawOffset()) / 1000) - (l / 1000);
        double m = sum / 60 / 60 / 24;
        double n = sum / 60 / 60;
        double b = sum / 60;

        if (m > 1) {
            str = convertUTC2GMT(time, "yyyy.MM.dd"); //返回年月日
        } else if (n > 1) {
            str = (int) n + "小时前";
        } else if (b > 1) {
            str = (int) b + "分钟前";
        } else {
            str = "1分钟前";
        }

        return str;
    }

    /**
     * UTC时间转为GMT时间格式
     * GMT：格林威治时间
     * UTC：世界时协调时间
     * DST：夏时令
     *
     * @param utcTime UTC时间
     * @param format  format格式 eg:yyyy-MM-dd
     * @return
     */
    public static String convertUTC2GMT(String utcTime, String format) {
        if (TextUtils.isEmpty(utcTime)) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = simpleDateFormat.parse(utcTime);
            return new SimpleDateFormat(format).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String convertChatTime(String utcTime, String format) { //聊天页转换时间 北京时间
        if (TextUtils.isEmpty(utcTime)) {
            return "";
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        //simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = simpleDateFormat.parse(utcTime);
            return new SimpleDateFormat(format).format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());
        return formatter.format(curDate);
    }

    public static String getDate(long date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(date);
        return formatter.format(curDate);
    }

    /**
     * 毫秒转换为时间
     *
     * @param millis
     * @return
     */
    public static String millisToMMSS(long millis) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        return simpleDateFormat.format(millis);
    }

    /**
     * 计算两个UTC时间的差
     * @param endTime
     * @return 时间差 ms
     */
    public static long calculateTimeInterval(String endTime) {
        if (TextUtils.isEmpty(endTime)) {
            return 0;
        }

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date dateEnd = simpleDateFormat.parse(endTime);
            return dateEnd.getTime() - System.currentTimeMillis();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public static long calculateInterval(String startTime, String endTime) {
        long interval = 0;

        if (TextUtils.isEmpty(startTime) || TextUtils.isEmpty(endTime)) {
            return interval;
        }

        if(sSimpleDateFormat == null) {
            //创建静态对象 避免每次调用都有创建销毁对象
            sSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sSimpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        }

        try {
            Date dateStart = sSimpleDateFormat.parse(startTime);
            Date dateEnd = sSimpleDateFormat.parse(endTime);
            interval =  dateEnd.getTime() - dateStart.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return interval;
    }


    /**
     * 秒数转换为倒计时
     * @param sec
     * @return
     */
    public static CountTime secToCountDown(long sec) {
        CountTime countTime = new CountTime();
        long h = sec / 60 / 60;
        long m = sec / 60 % 60;
        long s = sec % 60;
        countTime.hour = h > 9 ? h + "" : "0" + h;
        countTime.minute = m > 9 ? m + "" : "0" + m;
        countTime.second = s > 9 ? s + "" : "0" + s;
        return countTime;
    }

    public static class CountTime {
        public String hour;
        public String minute;
        public String second;
    }

    public static String secondToTime(long second) {
        long m = second / 60;
        long s = second % 60;
        String mStr = m > 9 ? String.valueOf(m) : String.format("0%d", m);
        String sStr = s > 9 ? String.valueOf(s) : String.format("0%d", s);
        return String.format("%s : %s", mStr, sStr);
    }

    /**
     * 获取本周的起始、结束日期
     * @param startOrEnd
     * @return
     */
    public static String getWeekStartOrEnd(boolean startOrEnd) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.DAY_OF_WEEK, startOrEnd ? Calendar.MONDAY : Calendar.SUNDAY);
        return format.format(c.getTime());
    }

    /**
     * 获取本月的起始、结束日期
     * @param startOrEnd
     * @return
     */
    public static String getMonthStartOrEnd(boolean startOrEnd) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH,
                startOrEnd ? 1 : c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return format.format(c.getTime());
    }


    /**
     * 价格转码
     *
     * @param priceStr
     * @return 价格
     */
    public static String convertPrice(String priceStr) {
        double d = 0.0;
        if(TextUtils.isEmpty(priceStr)) {
            return "议价";
        }

        try {
            d = Double.parseDouble(priceStr);
        } catch (Exception e) {
            e.printStackTrace();
            return priceStr;
        }

        if (d <= 0) {
            return "议价";
        }

        String price;
        if (d > 99999999) {
            price = d / 100000000 + " 亿";
        } else if (d > 9999) {
            price = d / 10000 + " 万";
        } else {
            price = "" + ((int) d);
        }
        return price;
    }

    /**
     * 将string价格转换为int
     * @param price
     * @return
     */
    public static int convertPrice2Int(String price) {
        int priceInt = 0;
        try {
            double d = Double.parseDouble(price);
            priceInt = (int) d;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceInt;
    }

    /**
     * 将价钱转换为带小符号的
     * @param price
     * @return
     */
    public static SpannableString convertPriceResize(String price) {
        String deposit = "¥ " + convertPrice(price);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(0.6f);
        SpannableString span = new SpannableString(deposit);
        span.setSpan(sizeSpan, 0, 1, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 富文本 不同颜色
     * @param str
     * @param color
     * @param start
     * @param end
     * @return
     */
    public static SpannableStringBuilder spanColor(String str, int color, int start, int end) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        SpannableStringBuilder span = new SpannableStringBuilder(str);
        span.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 富文本 不同颜色尺寸
     * @param str
     * @param color
     * @param start
     * @param end
     * @param proportion
     * @param start2
     * @param end2
     * @return
     */
    public static SpannableStringBuilder spanColorSize(String str, int color, int start, int end, float proportion, int start2, int end2) {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(color);
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(proportion);
        SpannableStringBuilder span = new SpannableStringBuilder(str);
        span.setSpan(colorSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        span.setSpan(sizeSpan, start2, end2, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 富文本 不同尺寸
     * @param str
     * @param proportion
     * @param start
     * @param end
     * @return
     */
    public static SpannableStringBuilder spanSize(String str, float proportion, int start, int end) {
        RelativeSizeSpan sizeSpan = new RelativeSizeSpan(proportion);
        SpannableStringBuilder span = new SpannableStringBuilder(str);
        span.setSpan(sizeSpan, start, end, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        return span;
    }

    /**
     * 准确计算
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double add(double v1, double v2) {
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    public static double add(String v1, String v2) {
        v1 = TextUtils.isEmpty(v1) ? "0" : v1;
        v2 = TextUtils.isEmpty(v2) ? "0" : v2;
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.add(b2).doubleValue();
    }

    public static double subtract(double v1, double v2) {
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    public static double subtract(String v1, String v2) {
        v1 = TextUtils.isEmpty(v1) ? "0" : v1;
        v2 = TextUtils.isEmpty(v2) ? "0" : v2;
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }

    public static double multiply(double v1, double v2) {
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        //四舍五入,保留两位小数
        return b1.multiply(b2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double multiply(String v1, String v2) {
        v1 = TextUtils.isEmpty(v1) ? "0" : v1;
        v2 = TextUtils.isEmpty(v2) ? "0" : v2;
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        //四舍五入,保留两位小数
        return b1.multiply(b2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double divide(double v1, double v2) {
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        //四舍五入,保留两位小数
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double divide(String v1, String v2) {
        v1 = TextUtils.isEmpty(v1) ? "0" : v1;
        v2 = TextUtils.isEmpty(v2) ? "0" : v2;
        //传入String才能准确计算
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        //四舍五入,保留两位小数
        return b1.divide(b2, 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

}
