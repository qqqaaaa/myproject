package com.microcyber.cloud.history.tool;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeTurnTools {

	public static Date worldToChina(String utcTime) throws ParseException {

		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		//设置时区UTC
		df.setTimeZone(TimeZone.getTimeZone("UTC"));
		//格式化，转当地时区时间
		Date after = df.parse(utcTime);
		df.applyPattern("yyyy-MM-dd HH:mm:ss");
		//默认时区
		df.setTimeZone(TimeZone.getDefault());
	    return after;
	}
	public static Date futureDate(Date starttime,int a,int b,int c){
		SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(starttime);
		//calendar.add(calendar.DAY_OF_YEAR, -c);
        if(b==1){
			calendar.add(calendar.DAY_OF_YEAR, a);
		}else if(b==2){
			calendar.add(calendar.MONTH, a);
		}else if(b==3){
			calendar.add(calendar.YEAR, a);
		}
		calendar.add(calendar.DAY_OF_YEAR, -c);
		//calendar.add(calendar.DAY_OF_YEAR, 1);//增加一天,负数为减少一天
		//calendar.add(calendar.DAY_OF_MONTH, 1);//增加一天
		//calendar.add(calendar.DATE,1);//增加一天
		//calendar.add(calendar.WEEK_OF_MONTH, 1);//增加一个礼拜
		//calendar.add(calendar.WEEK_OF_YEAR,1);//增加一个礼拜
		//calendar.add(calendar.YEAR, 1);//把日期往后增加一年.整数往后推,负数往前移动
		starttime = calendar.getTime();
		return starttime;

	}
	public static void main(String[] args) throws ParseException {
		String utcTime = "2018-01-31T14:32:19Z";
		TimeTurnTools.worldToChina(utcTime);
	}
}
