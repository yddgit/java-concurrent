package com.my.project.java;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class ServUVersionsParserTest {

	private ServUVersionsParser servUVersionsParser;

	@Before
	public void before() {
		this.servUVersionsParser = new ServUVersionsParser();
		servUVersionsParser.setServUVersionsURL("http://www.serv-u.info/aktuelles/versionshinweise_liste.php");
	}

	@Test
	public void testParseByHtmlUnit() {
		String versionsFile = "serv-u-versions-htmlunit.txt";
		servUVersionsParser.setServUVersionsFile(versionsFile);

		long start = System.currentTimeMillis();
		List<String> versions = servUVersionsParser.parseByHtmlUnit();
		long end = System.currentTimeMillis();
		System.out.println("使用HtmlUnit解析耗时(ms): " + (end-start));

		assertEquals(versions.size(), 139);
		servUVersionsParser.writeToFile(versions);
	}

	@Test
	public void testParseByRegex() {
		String versionsFile = "serv-u-versions-regex.txt";
		servUVersionsParser.setServUVersionsFile(versionsFile);

		long start = System.currentTimeMillis();
		List<String> versions = servUVersionsParser.parseByRegex();
		long end = System.currentTimeMillis();
		System.out.println("使用正则表达式解析耗时(ms): " + (end-start));

		assertEquals(versions.size(), 139);
		servUVersionsParser.writeToFile(versions);
	}

	@Test
	public void testDateFormat() throws ParseException {
		Date d = new SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).parse("February 13, 2017");
		assertEquals("2017-02-13", new SimpleDateFormat("yyyy-MM-dd").format(d));
	}
}
