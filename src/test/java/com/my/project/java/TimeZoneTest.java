package com.my.project.java;

import static org.junit.Assert.*;
import static com.my.project.java.TimeZoneUtil.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Test;

/**
 * Unit test for java time zone.
 * 
 * UTC: Universal Time Coordinated，基于原子钟，为了使UTC与地球自转周期一致，每一年或两年会有一个闰秒。
 * GMT: Greenwich Mean Time，是一个天文概念。
 * 
 * GMT和UTC是的功能与精确度是没有差别的，都与英国伦敦的本地时相同。
 * 
 */
public class TimeZoneTest {

	/**
	 * 所有时区ID
	 */
	@Test
	public void testAllTimeZone() {
		for(String t : TimeZone.getAvailableIDs()) {
			System.out.println(t);
		}
	}

	/**
	 * 指定时区的ID
	 */
	@Test
	public void testAllTimeZone2() {
		for(String t : TimeZone.getAvailableIDs(-8 * 60 * 60 * 1000)) {
			System.out.println(t);
		}
	}

	/**
	 * 根据ID获得TimeZone
	 */
	@Test
	public void testZoneId() {
		System.out.println("-------------------------\nUTC/GTM:");
		showTimeZone(TimeZone.getTimeZone("NON"));
		showTimeZone(TimeZone.getTimeZone(ZoneId.of("Z")));
		// throw java.time.DateTimeException: Invalid ID for ZoneOffset, invalid format: X
		//showTimeZone(TimeZone.getTimeZone(ZoneId.of("X")));
		System.out.println("-------------------------\nLOCAL:");
		showTimeZone(TimeZone.getDefault());
		showTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		showTimeZone(TimeZone.getTimeZone(ZoneId.of("+0800")));
		System.out.println("-------------------------\nPacific:");
		showTimeZone(TimeZone.getTimeZone("GMT-0800"));
		showTimeZone(TimeZone.getTimeZone("US/Pacific"));
		showTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		System.out.println("-------------------------\nEastern:");
		showTimeZone(TimeZone.getTimeZone("GMT-0500"));
		showTimeZone(TimeZone.getTimeZone("US/Eastern"));
		showTimeZone(TimeZone.getTimeZone("America/New_York"));
	}

	/**
	 * 本地时间与GMT/UTC时间转换
	 */
	@Test
	public void testToUTC() {
		// UTC + 时区差 = 本地时间
		// 时区差东为正、西为负，东八区为+0800
		Date local = new Date();
		showTime(local);
		Date utc = new Date(local.getTime() - TimeZone.getDefault().getRawOffset());
		showTime(utc);
	}

	/**
	 * 当前时区(北京时间+8)与UTC时间差
	 */
	@Test
	public void testOffset() {
		showTimeZone(TimeZone.getDefault());
		showTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
		showTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		showTimeZone(TimeZone.getTimeZone("US/Pacific"));
		showTimeZone(TimeZone.getTimeZone("America/New_York"));
		showTimeZone(TimeZone.getTimeZone("US/Eastern"));
	}

	/**
	 * 不同时区的当前时间（考虑夏令时）
	 */
	@Test
	public void testCurrent() {
		showTime(getCurrentTime("Asia/Shanghai"));
		showTime(getCurrentTime("America/Los_Angeles"));
		showTime(getCurrentTime("US/Pacific"));
		showTime(getCurrentTime("America/New_York"));
		showTime(getCurrentTime("US/Eastern"));
	}

	/**
	 * 不同时区的时间-非夏令时期间
	 */
	@Test
	public void testNoDstSavings() {
		Date localTime = fromString("2018-12-20 12:20:24");
		assertEquals(fromString("2018-12-20 12:20:24"), getZoneTime("Asia/Shanghai", localTime));
		assertEquals(fromString("2018-12-19 20:20:24"), getZoneTime("America/Los_Angeles", localTime));
		assertEquals(fromString("2018-12-19 20:20:24"), getZoneTime("US/Pacific", localTime));
		assertEquals(fromString("2018-12-19 23:20:24"), getZoneTime("America/New_York", localTime));
		assertEquals(fromString("2018-12-19 23:20:24"), getZoneTime("US/Eastern", localTime));
	}

	/**
	 * 不同时区的时间-夏令时期间
	 */
	@Test
	public void testDstSavings() {
		Date localTime = fromString("2018-06-20 12:20:24");
		assertEquals(fromString("2018-06-20 12:20:24"), getZoneTime("Asia/Shanghai", localTime));
		assertEquals(fromString("2018-06-19 21:20:24"), getZoneTime("America/Los_Angeles", localTime));
		assertEquals(fromString("2018-06-19 21:20:24"), getZoneTime("US/Pacific", localTime));
		assertEquals(fromString("2018-06-20 00:20:24"), getZoneTime("America/New_York", localTime));
		assertEquals(fromString("2018-06-20 00:20:24"), getZoneTime("US/Eastern", localTime));
	}

	@Test
	public void testSpecificTime() throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		format.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
		Date d = format.parse("2020-06-04 00:00:00.000");
		assertEquals(1591254000000L, d.getTime());
	}

	/**
	 * 显示时间
	 * @param date
	 */
	private void showTime(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		System.out.println(format.format(date));
	}

	/**
	 * 时间转换
	 * @param date 时间字符串
	 * @return Date
	 */
	private Date fromString(String date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			return format.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 显示时区信息
	 * @param zone
	 */
	private void showTimeZone(TimeZone zone) {
		System.out.println("-------------------------");
		System.out.println("TimeZone ID: " + zone.getID());
		System.out.println("TimeZone DisplayName: " + zone.getDisplayName());
		System.out.println("TimeZone与UTC时间差: " + zone.getRawOffset()/1000/60/60 + "h");
		System.out.println("TimeZone当前是否在实行夏令时: " + zone.inDaylightTime(new Date()));
		System.out.println("TimeZone夏令时调节(+): " + zone.getDSTSavings()/1000/60/60 + "h");
	}
}
