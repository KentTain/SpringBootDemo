package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public final class DateExtensions {
	public static final SimpleDateFormat milliSecondFmt1 = new SimpleDateFormat("yyyyMMddHHmmssSSS");
	public static final SimpleDateFormat milliSecondFmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
	public static final SimpleDateFormat FMT_yMd1 = new SimpleDateFormat("yyyy-MM-dd");
	public static final SimpleDateFormat FMT_yMdHms1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final SimpleDateFormat FMT_yMd2 = new SimpleDateFormat("yyyy/MM/dd");
	public static final SimpleDateFormat FMT_yMdHms2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	public static final SimpleDateFormat FMT_yMd3 = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat FMT_yMdHms3 = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final SimpleDateFormat FMT_yMdHm1 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public static final SimpleDateFormat FMT_yMdHm2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
	public static final SimpleDateFormat FMT_yMdHm3 = new SimpleDateFormat("yyyyMMddHHmm");
	public static final SimpleDateFormat FMT_yM = new SimpleDateFormat("yyyy/MM");
	public static final SimpleDateFormat FMT_y = new SimpleDateFormat("yyyy");
	public static final SimpleDateFormat FMT_yMd4 = new SimpleDateFormat("yyyy/M/dd");
	public static final SimpleDateFormat FMT_yMd5 = new SimpleDateFormat("yyyy/M/d");
	public static final SimpleDateFormat[] FMTS = new SimpleDateFormat[] { FMT_yMd1, FMT_yMdHms1, FMT_yMd2, FMT_yMdHms2,
			FMT_yMd3, FMT_yMdHms3, FMT_yMdHm1, FMT_yMdHm2, FMT_yMdHm3, FMT_yM, FMT_y, FMT_yMd4, FMT_yMd5 };
	// 默认使用系统当前时区
	public static final ZoneId ZONE = ZoneId.systemDefault();

	/**
	 * 时期是否相等
	 * 
	 * @param date1 日期1
	 * @param date2 日期2
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isEquals(Date date1, Date date2) {
		return date1.equals(date2);
	}

	/** 是否为闰年
	 * @param date
	 * @return boolean
	 */
	public static boolean isLeapYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);// 设置时间
		
		int year =calendar.get(Calendar.YEAR);
        return year % 4 == 0 && (year % 100 != 0 || year % 400 == 0);
    }
	
	/**
	 * 将Date转换成LocalDate
	 * 
	 * @param date
	 * @return LocalDate
	 * @author tianc
	 */
	public static LocalDate toLocalDate(Date date) {
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE);
		return localDateTime.toLocalDate();
	}

	/**
	 * 将Date转换成LocalTime
	 * 
	 * @param date
	 * @return LocalTime
	 * @author tianc
	 */
	public static LocalTime toLocalTime(Date date) {
		Instant instant = date.toInstant();
		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZONE);
		return localDateTime.toLocalTime();
	}

	/**
	 * 将毫秒时间戳转换成Date
	 * 
	 * @param time 毫秒时间
	 * @return Date
	 * @author tianc
	 */
	public static Date timeMilliToDate(String time) {
		return Date.from(Instant.ofEpochMilli(Long.parseLong(time)));
	}

	/**
	 * 在日期上增加相应年份
	 * 
	 * @param years 日期
	 * @param years 要增加的年份数 (整数)
	 * @return Date
	 * @author tianc
	 */
	public static Date addYear(Date date, int years) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, years);// 把日期往后增加years年.正数往后推,负数往前移动
		return cal.getTime();
	}

	/**
	 * 在日期上增加相应月份
	 * 
	 * @param date   月份
	 * @param months 要增加的月份数 (整数)
	 * @return Date
	 * @author tianc
	 */
	public static Date addMonth(Date date, int months) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, months);// 把日期往后增加months月.正数往后推,负数往前移动
		return cal.getTime();
	}

	/**
	 * 在日期上增加相应天数
	 * 
	 * @param date 日期
	 * @param days 要增加的天数 (整数)
	 * @return Date
	 * @author tianc
	 */
	public static Date addDay(Date date, int days) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days);// 把日期往后增加days天.正数往后推,负数往前移动
		return cal.getTime();
	}

	/**
	 * 在当前给定时间 如果是 23:59:59就加一秒</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static final Date addOneSecond(Date date) {
		Date ret = null;
		// 格式化为时/分/秒样式
		String dateStr = getDateString(date, "yyyyMMddHHmmss");
		if (dateStr.endsWith("235959")) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.SECOND, 1);// 加1秒
			ret = cal.getTime();
		}
		return ret;
	}

	/**
	 * 统计两个日期相差小时数 <br/>
	 * 例:3600秒是一小时,那么如果相差3500秒,本方法返回0,如果相差3700秒,本方法返回1,即舍去余数
	 * 
	 * @param date1 第一个日期
	 * @param date2 第二个日期
	 * @return int
	 * @author tianc
	 */
	public static int countHours(Date date1, Date date2) {
		long l = date2.getTime() - date1.getTime();
		return (int) (l / 1000) / 3600;
	}

	/**
	 * 统计两个日期相差天数 <br/>
	 * 例:相差3700秒,那么3700/3600 = 1 返回 例:24小时是一天,如果相差23小时,本方法返回0,如果相差25小时,本方法返回1,,即舍去余数
	 * 
	 * @param date1 第一个日期
	 * @param date2 第二个日期
	 * @return int
	 * @author tianc
	 */
	public static int countDays(Date date1, Date date2) {
		int hours = countHours(date1, date2);
		return (int) hours / 24;
	}

	/**
	 * 得到两个日期间的间隔天数 </br>
	 * 
	 * @param firstDay String格式 "2015-06-08"
	 * @param lastDay  String格式 "2015-06-07"
	 * @author tianc
	 */
	public static final Long countDays(String firstDay, String lastDay) {
		if (firstDay == null || "".equals(firstDay)) {
			return 0L;
		}
		if (lastDay == null || "".equals(firstDay)) {
			return 0L;
		}
		long days = 0;
		try {
			Date beginDate = FMT_yMd1.parse(firstDay);
			Date endDate = FMT_yMd1.parse(lastDay);
			days = (endDate.getTime() - beginDate.getTime()) / (24 * 60 * 60 * 1000);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return days;
	}

	/**
	 * 把String转换成Date</br>
	 * 1.先用指定的specifiedFmt去转换 2.指定的specifiedFmt转换失败,则用默认的时间格式化样式数组去转换
	 * 
	 * @param sourceStr    将要被转换成Date类型的String
	 * @param specifiedFmt 指定格式化样式的String
	 * @return Date
	 * @author tianc
	 */
	public static final Date getDate(String sourceStr, String specifiedFmt) {
		Date ret = null;
		boolean hasException = true;
		if (specifiedFmt != null && !"".equals(specifiedFmt)) {
			try {
				SimpleDateFormat sdf1 = new SimpleDateFormat(specifiedFmt);
				ret = sdf1.parse(sourceStr);
			} catch (ParseException e) {
				hasException = true;
			}
		}
		if (!hasException)// 没有异常表示正常
			return ret;
		ret = getDate(sourceStr);// 有异常就偿试着用默认的格式化
		return ret;
	}

	/**
	 * 把String转换成Date</br>
	 * 用默认的时间格式化样式数组去转换
	 * 
	 * @param sourceStr 将要被转换成Date类型的String
	 * @return Date
	 * @author tianc
	 */
	public static final Date getDate(String sourceStr) {
		sourceStr = sourceStr.replace("-", "/");
		Date d = null;
		boolean hasException = true;
		for (SimpleDateFormat sdf : FMTS) {
			if (sdf.toPattern().length() != sourceStr.length())
				continue;
			if (hasException) {// 如果有异常,那么继续格式化转换日期
				try {
					d = sdf.parse(sourceStr);
					break;
				} catch (ParseException e) {
					hasException = true;
				}
			} else {// 如果没异常了,那么马上退出循环
				break;
			}
		}
		return d;
	}

	/**
	 * 以指定样式格式化字符串成日期
	 * 
	 * @param date         将要被转换成String类型的Date
	 * @param specifiedFmt 指定格式化样式的String
	 * @return String
	 * @author tianc
	 */
	public static final String getDateString(Date date, String specifiedFmt) {
		SimpleDateFormat formatter = new SimpleDateFormat(specifiedFmt);
		String ret = formatter.format(date);
		return ret;
	}

	/**
	 * 以指定样式格式化字符串成日期
	 * 
	 * @param date      将要被转换成String类型的Date
	 * @param formatter 指定格式化样式的格式化类
	 * @return String
	 * @author tianc
	 */
	public static final String getDateString(Date date, SimpleDateFormat formatter) {
		String ret = formatter.format(date);
		return ret;
	}

	/**
	 * 以指定样式格式化字符串成日期
	 * 
	 * @param date      将要被转换成String类型的Date
	 * @param formatter 指定格式化样式的格式化类
	 * @return String
	 * @author tianc
	 */
	public static final String getNowDateString(String specifiedFmt) {
		SimpleDateFormat formatter = new SimpleDateFormat(specifiedFmt);
		String ret = formatter.format(new Date());
		return ret;
	}

	/**
	 * 以指定样式格式化字符串成日期
	 * 
	 * @param date      将要被转换成String类型的Date
	 * @param formatter 指定格式化样式的格式化类
	 * @return String
	 * @author tianc
	 */
	public static final String getNowDateString(SimpleDateFormat formatter) {
		String ret = formatter.format(new Date());
		return ret;
	}

	/**
	 * 以指定样式格式化字符串成日期时间
	 * 
	 * @param date      将要被转换成String类型的DateTime
	 * @param formatter 指定格式化样式的格式化类
	 * @return String
	 * @author tianc
	 */
	public static final String getNowDateTimeString(String specifiedFmt) {
		LocalDateTime current = LocalDateTime.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern(specifiedFmt);
		String ret = current.format(format);
		return ret;
	}

	/**
	 * 获得指定日期上个月第一天的日期</br>
	 * 
	 * @param date Date日期类型
	 * @return String
	 * @author tianc
	 */
	public static final String getLastMonthFirstDay(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = "";
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);// 设置时间
		calendar.add(Calendar.MONTH, -1);// 加/减一个月,正数往后推,负数往前移动
		calendar.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		str = sdf.format(calendar.getTime());
		return str;
	}

	/**
	 * 得到指定日期的月初日期,返回字符串</br>
	 * 
	 * @param today 字符串型日期
	 * @return String
	 * @author tianc
	 */
	public static final String getFirstDayString(String today) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = "";
		try {
			Date date = sdf.parse(today);
			Calendar lastDate = Calendar.getInstance();
			lastDate.setTime(date);
			lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
			str = sdf.format(lastDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 得到指定日期的月末日期，返回字符串</br>
	 * 
	 * @param today 字符串型日期
	 * @return String
	 * @author tianc
	 */
	public static final String getLastDayString(String today) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String str = "";
		try {
			Date date = sdf.parse(today);
			Calendar lastDate = Calendar.getInstance();
			lastDate.setTime(date);
			lastDate.set(Calendar.DATE, 1);// 设为当前月的1号
			lastDate.add(Calendar.MONTH, 1);// 加一个月，变为下月的1号
			lastDate.add(Calendar.DATE, -1);// 减去一天，变为当月最后一天
			str = sdf.format(lastDate.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return str;
	}

	/**
	 * 获取本月第几周的周六</br>
	 * 
	 * @param firstDayOfMonth   Date日期类型
	 * @param nthWeekendInMonth 第几周
	 * @return Date
	 * @author tianc
	 */
	public static Date getNthWeekendOfMonth(Date firstDayOfMonth, int nthWeekendInMonth) {
		int iCount = 0;
		while (iCount < nthWeekendInMonth) {
			if (!isBusinessDay(firstDayOfMonth)) {
				iCount++;
			}
			firstDayOfMonth = addDay(firstDayOfMonth, 1);
		}

		return addDay(firstDayOfMonth, -1);
	}

	/**
	 * 获取本月最后一周的周六</br>
	 * 
	 * @param firstDayOfMonth Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getTheLastWeekendOfMonth(Date firstDayOfMonth) {
		Date result = new Date();

		Date lastDayOfMonth = getTheLastDayOfMonth(firstDayOfMonth);
		for (int i = 1; i <= 7; i++) {
			if (!isBusinessDay(lastDayOfMonth)) {
				result = lastDayOfMonth;
				break;
			}

			lastDayOfMonth = addDay(lastDayOfMonth, -1);
		}

		return result;
	}

	/**
	 * 获取本月第几周的周一</br>
	 * 
	 * @param firstDayOfMonth   Date日期类型
	 * @param nthWeekdayInMonth 第几周
	 * @return Date
	 * @author tianc
	 */
	public static Date getNthWeekdayOfMonth(Date firstDayOfMonth, int nthWeekdayInMonth) {
		int iCount = 0;
		while (iCount < nthWeekdayInMonth) {
			if (isBusinessDay(firstDayOfMonth)) {
				iCount++;
			}
			firstDayOfMonth = addDay(firstDayOfMonth, 1);
		}

		return addDay(firstDayOfMonth, -1);
	}

	/**
	 * 获取本月最后一周的周一</br>
	 * 
	 * @param firstDayOfMonth Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getTheLastWeekdayOfMonth(Date firstDayOfMonth) {
		Date result = new Date();

		Date lastDayOfMonth = getTheLastDayOfMonth(firstDayOfMonth);
		for (int i = 1; i <= 7; i++) {
			if (isBusinessDay(lastDayOfMonth)) {
				result = lastDayOfMonth;
				break;
			}

			lastDayOfMonth = addDay(lastDayOfMonth, -1);
		}

		return result;
	}

	/**
	 * 获取星期几</br>
	 * 
	 * @param date Date日期类型
	 * @return int
	 * @author tianc
	 */
	public static int getDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取本月第几周的第几个工作日</br>
	 * 
	 * @param firstDayOfMonth Date日期类型
	 * @param nthWeekInMonth  第几周
	 * @param weekday         第几个工作日
	 * @return Date
	 * @author tianc
	 */
	public static Date getNthWeekOfMonth(Date firstDayOfMonth, int nthWeekInMonth, int weekday) {
		int iCount = 0;
		while (iCount < nthWeekInMonth) {
			if (getDayOfWeek(firstDayOfMonth) == weekday) {
				iCount++;
			}
			firstDayOfMonth = addDay(firstDayOfMonth, 1);
		}

		return addDay(firstDayOfMonth, -1);
	}

	/**
	 * 获取本月最后一周的第几个工作日</br>
	 * 
	 * @param firstDayOfMonth Date日期类型
	 * @param weekday         第几个工作日
	 * @return Date
	 * @author tianc
	 */
	public static Date getTheLastWeekOfMonth(Date firstDayOfMonth, int weekday) {
		Date result = new Date();
		Date lastDayOfMonth = getTheLastDayOfMonth(firstDayOfMonth);

		for (int i = 1; i <= 7; i++) {
			if (getDayOfWeek(lastDayOfMonth) == weekday) {
				result = lastDayOfMonth;
				break;
			}

			lastDayOfMonth = addDay(lastDayOfMonth, -1);
		}

		return result;
	}

	/**
	 * 获取本月的第几天</br>
	 * 
	 * @param firstDayOfMonth Date日期类型
	 * @param nthDayInMonth   第几天
	 * @return Date
	 * @author tianc
	 */
	public static Date getNthDayOfMonth(Date firstDayOfMonth, int nthDayInMonth) {
		Date result = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(firstDayOfMonth);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
		calendar.add(Calendar.DATE, -1);
		int lastDay = calendar.get(Calendar.DAY_OF_MONTH);

		if (nthDayInMonth <= lastDay) {
			result = calendar.getTime();
		}

		return result;
	}

	/**
	 * 获取本周的第1天</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getTheFirstDayOfWeek(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		return addDay(date, weekday * -1);
	}

	/**
	 * 获取本月的第一天</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getTheFirstDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1);
		return calendar.getTime();
	}

	/**
	 * 获取本月的最后一天</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getTheLastDayOfMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, 1);
		calendar.add(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 获取本年的第一天</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getTheFirstDayOfYear(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), 1, 1);
		return calendar.getTime();
	}

	/**
	 * 获取本天的零点时刻</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getZeroHourOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);
		return calendar.getTime();
	}

	/**
	 * 获得指定日期上个月最后一天的日期</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static final Date getLastMonthLastDayString(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);// 加/减一个月,正数往后推,负数往前移动
		cal.setTime(cal.getTime());
		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);// 取得该月份最后一天
		cal.set(Calendar.DAY_OF_MONTH, lastDay);// 设置成该月份的最后一天
		return cal.getTime();
	}

	/**
	 * 获取下一天的零点时刻</br>
	 * 
	 * @param date Date日期类型
	 * @return Date
	 * @author tianc
	 */
	public static Date getZeroHourOfTheNextDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE) + 1, 0, 0,
				0);
		return calendar.getTime();
	}

	/**
	 * 是否为哪几周</br>
	 * 
	 * @param startDate           Date日期类型
	 * @param weeklyCycleWeekdays 哪几周
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isWeeklyCycleWeekdays(Date startDate, List<Integer> weeklyCycleWeekdays) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
		return weeklyCycleWeekdays.contains(dayOfWeek);
	}

	/**
	 * 是否为工作日</br>
	 * 
	 * @param startDate Date日期类型
	 * @return boolean
	 * @author tianc
	 */
	public static boolean isBusinessDay(Date startDate) {
		Instant instant = startDate.toInstant();
		ZoneId zoneId = ZoneId.systemDefault();
		LocalDate localDate = instant.atZone(zoneId).toLocalDate();
		return localDate.getDayOfWeek() != DayOfWeek.SATURDAY || localDate.getDayOfWeek() != DayOfWeek.SUNDAY;
	}

	/**
	 * 获取基于0001/1/1至今的纳秒数（与Net时间函数的Ticks相对应）</br>
	 * 
	 * @param date Date日期类型
	 * @return long
	 * @author tianc
	 */
	public static long getTicks(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date);
		long milli = calendar.getTimeInMillis();

		long ticks = (milli * 10000) + 621355968000000000L;
		return ticks;
	}

	/**
	 * 基于系统服务器时区的LocalDateTime</br>
	 * 
	 * @param date Date日期类型
	 * @return LocalDateTime
	 * @author tianc
	 */
	public static LocalDateTime dateToLocalDateTime(Date date) {
		ZoneId defaultZoneId = ZoneId.systemDefault();
		Instant instant = date.toInstant();

		return instant.atZone(defaultZoneId).toLocalDateTime();
	}

	/**
	 * 获取已经过去的时间的描述（与本地时间为基准）</br>
	 * 
	 * @param date Date日期类型
	 * @return boolean
	 * @author tianc
	 */
	public static String toRelativeDateString(Date date) {
		ZoneId defaultZoneId = ZoneId.systemDefault();
		LocalDate localNow = LocalDate.now();
		Date now = Date.from(localNow.atStartOfDay(defaultZoneId).toInstant());
		return getRelativeDateValue(date, now);
	}

	/**
	 * 获取已经过去的时间的描述（与UTC时间为基准）</br>
	 * 例如：on 2010，2 days ago，yesterday，15 hours ago，a few minutes ago
	 * 
	 * @param date Date日期类型
	 * @return boolean
	 * @author tianc
	 */
	public static String toRelativeDateStringUtc(Date date) {
		ZoneId defaultZoneId = ZoneId.of("UTC");
		LocalDate localNow = LocalDate.now();
		Date now = Date.from(localNow.atStartOfDay(defaultZoneId).toInstant());
		return getRelativeDateValue(date, now);
	}

	private static String getRelativeDateValue(Date date, Date comparedTo) {
		LocalDateTime startDate = dateToLocalDateTime(date);
		LocalDateTime endDate = dateToLocalDateTime(comparedTo);

		long diffInDays = ChronoUnit.DAYS.between(startDate, endDate);
		long diffInHours = ChronoUnit.HOURS.between(startDate, endDate);
		long diffInMinutes = ChronoUnit.MINUTES.between(startDate, endDate);

		if (diffInDays >= 365) {
			SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy");
			return "on " + format.format(date);
		}
		if (diffInDays >= 7) {
			SimpleDateFormat format = new SimpleDateFormat("MMMM d");
			return "on " + format.format(date);
		} else if (diffInDays > 1)
			return String.format("{0:N0} days ago", diffInDays);
		else if (diffInDays == 1)
			return "yesterday";
		else if (diffInHours >= 2)
			return String.format("{0:N0} hours ago", diffInHours);
		else if (diffInMinutes >= 60)
			return "more than an hour ago";
		else if (diffInMinutes >= 5)
			return String.format("{0:N0} minutes ago", diffInMinutes);
		if (diffInMinutes >= 1)
			return "a few minutes ago";
		else
			return "less than a minute ago";
	}

	/**
	 * 把UTC时间转换成北京时间(UTC+08:00)</br>
	 * 
	 * @param date Date日期类型
	 * @return boolean
	 * @author tianc
	 */
	public static Date ToLocalDate(Date date) {
		ZoneId defaultZoneId = ZoneId.of("Asia/Chita");
		Instant instant = date.toInstant();
		LocalDateTime localNow = instant.atZone(defaultZoneId).toLocalDateTime();

		Date now = Date.from(localNow.atZone(defaultZoneId).toInstant());
		return now;
	}

	/**
	 * 先转换为北京时间(UTC+08:00)，再ToString</br>
	 * 
	 * @param date   Date日期类型
	 * @param format Date日期格式
	 * @return boolean
	 * @author tianc
	 */
	public static String ToLocalDateStr(Date date, String format) {
		ZoneId defaultZoneId = ZoneId.of("Asia/Chita");
		Instant instant = date.toInstant();
		LocalDateTime localNow = instant.atZone(defaultZoneId).toLocalDateTime();

		SimpleDateFormat f = new SimpleDateFormat(format);
		return f.format(localNow);
	}

}
