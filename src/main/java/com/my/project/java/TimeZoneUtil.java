package com.my.project.java;

import java.util.Date;
import java.util.TimeZone;

public class TimeZoneUtil {

	/**
	 * 转换两个时区的时间
	 * <p>
	 * UTC + 时区偏移量 + 夏令时修正量 = 本地时间
	 * </p>
	 * <p>
	 * UTC + offset1 + dstSavings1 = localTime1 (1)<br>
	 * UTC + offset2 + dstSavings2 = localTime2 (2)<br>
	 * <br>
	 * (2) - (1) 得到:
	 * <br>
	 * localTime2 = localTime1 + offset2 - offset1 + dstSavings2 - dstSavings1
	 * </p>
	 * @param zoneId1 timeZone of localtime1
	 * @param localTime1 localtime1
	 * @param zoneId2 timeZone of localtime2
	 * @return localtime2
	 */
	public static Date getLocalTime(String zoneId1, Date localTime1, String zoneId2) {
		if(zoneId1 == null || "".equals(zoneId1.trim())) { return null; }
		if(zoneId2 == null || "".equals(zoneId2.trim())) { return null; }
		if(localTime1 == null) { return null; }
		TimeZone timeZone1 = TimeZone.getTimeZone(zoneId1);
		TimeZone timeZone2 = TimeZone.getTimeZone(zoneId2);
		long offset1 = timeZone1.getRawOffset(); // 时区偏移量ms
		long offset2 = timeZone2.getRawOffset(); // 时区偏移量ms
		long dstSavings1 = timeZone1.getDSTSavings(); // 夏令时修正量ms
		long dstSavings2 = timeZone2.getDSTSavings(); // 夏令时修正量ms

		long tmp = localTime1.getTime();
		// 先修正夏令时为源时区标准时
		if(timeZone1.inDaylightTime(localTime1)) {
			tmp = tmp - dstSavings1;
		}
		// 调整源时区标准时为UTC时间
		tmp = tmp - offset1;
		// 将UTC时间调整为目标时区标准时
		tmp = tmp + offset2;
		// 修正为目标时区夏令时
		if(timeZone2.inDaylightTime(new Date(tmp))) {
			tmp = tmp + dstSavings2;
		}
		return new Date(tmp);
	}

	/**
	 * 根据指定时区时间计算本地对应的时间
	 * @param zoneId 时区
	 * @param zoneDate 时间
	 * @return 本地时间
	 */
	public static Date getLocalTime(String zoneId, Date zoneDate) {
		return getLocalTime(zoneId, zoneDate, TimeZone.getDefault().getID());
	}

	/**
	 * 根据本地时间计算指定时区对应的时间
	 * @param zoneId 时区
	 * @param localDate 本地时间
	 * @return zoneId对应时区的时间
	 */
	public static Date getZoneTime(String zoneId, Date localDate) {
		return getLocalTime(TimeZone.getDefault().getID(), localDate, zoneId);
	}

	/**
	 * 计算指定时区的当前时间
	 * @param zoneId 时间
	 * @return zoneId对应时区的当前时间
	 */
	public static Date getCurrentTime(String zoneId) {
		return getLocalTime(TimeZone.getDefault().getID(), new Date(), zoneId);
	}

}
