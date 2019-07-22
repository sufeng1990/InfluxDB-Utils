package com.utils.influxdbutils.utils;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

/**
 * 条件实体类
 *
 * @author sufeng
 * @date 2019-07-06
 */
public class TimeUtils {

	public static Date str2Date(String str, String pattern) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(pattern);
		LocalDateTime dateTime = LocalDateTime.parse(str, fmt);
		return toDate(dateTime);
	}

	public static Date toDate(LocalDateTime dateTime) {
		Instant instant = dateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	/**
	 * 时间戳转UTC时间并转换为0时区
	 */
	public static String getUTCTimeStr(Long date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		format.setTimeZone(TimeZone.getTimeZone("GMT"));
		return format.format(date);
	}
}
