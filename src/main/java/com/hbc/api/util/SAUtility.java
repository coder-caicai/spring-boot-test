package com.hbc.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/*  
 * package com.hbc.api.util.SAUtility
 * @author zhuwenhua
 *         ‎2016年‎11月‎7日 上午 10:08
 */
public class SAUtility {
	/*
	 * @author zhuwenhua ‎ 
	 *         
	 * @fuction 判断是否为空值null、" "或者""
	 */
	public static boolean isBlank(Object obj) {
		if (obj == null || "".equals(obj.toString().trim())) {
			return true;
		}
		return false;
	}

	/*
	 * @author zhuwenhua ‎ 
	 *         
	 * @fuction 判断是否为空值null、" "或者""
	 */
	public static boolean isNotBlank(Object obj) {
		return !isBlank(obj);
	}

	/*
	 * @author zhuwenhua ‎ 
	 *         2016年‎9月‎4日 上午 09:48
	 * @fuction 两日期相减得到相差的天数
	 */
	public static long opration_time(Date endtime, Date bagintime) {
		long day = (endtime.getTime() - bagintime.getTime())
				/ (1000 * 3600 * 24);
		return day;
	}

	/*
	 * @author zhuwenhua ‎ 
	 *         
	 * @fuction 转换日期的格式
	 */
	public static Date strToDate(String dateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}
	/*
	 *返回当前时间减指定天数的日期
	 * 
	 * @param day
	 * @return 
	 * @author zhuwenhua
	 *	       
	 */
	public static Date getDate(int day){
		Date beginDate = new Date();
		Calendar date = Calendar.getInstance();
		date.setTime(beginDate);
		date.set(Calendar.DATE, date.get(Calendar.DATE) - day);
		Date endDate = date.getTime();
	    return endDate;
	}

}
