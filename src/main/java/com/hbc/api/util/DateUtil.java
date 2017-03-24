package com.hbc.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by cheng on 16/6/29.
 * 日期工具类,通用日期工具方法放到这里
 */
public class DateUtil {

    /**
     * 获得当前月
     *
     * @return 格式 20160706
     */

    public static SimpleDateFormat sdfYYYYMM = new SimpleDateFormat("yyyyMM");

    public static  SimpleDateFormat sdfYYYY_MM_dd = new SimpleDateFormat("yyyy-MM-dd");

    public static  SimpleDateFormat sdfYYYY_MM = new SimpleDateFormat("yyyy-MM");

    public static SimpleDateFormat sdfYYYYMMdd = new SimpleDateFormat("yyyyMMdd");

    public static SimpleDateFormat sdfHHmmss = new SimpleDateFormat("HH:mm:ss");

    public  static SimpleDateFormat sdfYYYY_MM_DD_HH_mm_ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public  static SimpleDateFormat sdfYYYYMMDDHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");

    /**
     * 获得当前月
     * @return yyyyMM
     */
    public static String getCurrentDate() {
        return sdfYYYYMM.format(new Date());
    }

    /**
     * 获取近6个月的月份
     *
     * @return list<String> [yyyyMMdd,yyyyMMdd]
     */
    public static List<String> getPreSixMonth() {
        List<String> list = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        list.add(getCurrentDate());
        for (int i = 0; i < 5; i++) {
            cal.add(Calendar.MONTH, -1);
            String dataMonth = sdfYYYYMM.format(cal.getTime());
            list.add(dataMonth);
        }
        return list;
    }

    /**
     * 获取月分的第一天
     *
     * @param dateMonth yyyyMM
     * @return yyyyMMdd
     */
    public static String getFirstDayInMonth(String dateMonth) {
        return dateMonth+"01";
    }

    /**
     * 获取月份的最后一天
     * @param dataMonth yyyyMM
     * @return yyyyMMdd
     */
    public static  String getEndDayInMonth(String dataMonth){
        try {
            Date date = sdfYYYYMM.parse(dataMonth);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            return sdfYYYYMMdd.format(cal.getTime());

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     * 日期格式转换工具
     * @param dateStr yyyyMMdd
     * @return        yyyy-MM-dd
     */
    public static String dateConvert(String dateStr){
        try {
            Date date = sdfYYYYMMdd.parse(dateStr);
            return  sdfYYYY_MM_dd.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return  null;
    }

    public static Integer timeToSecond(String time){
        String[] my =time.split(":");
        int hour =Integer.parseInt(my[0]);
        int min =Integer.parseInt(my[1]);
        int sec =Integer.parseInt(my[2]);

        int zong =hour*3600+min*60+sec;
        return  zong;
    }

//    public static void main(String[] args) {
////        System.out.print(getEndDayInMonth("201607"));
//        int i = timeToSecond("00:00:04");
//        System.out.print(i);
//    }
}
