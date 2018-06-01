package com.my.project.java;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlListItem;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * 从Serv-U官网上解析其自发布以来的所有历史版本号<br>
 * 
 * 参考站点: 
 * <ul>
 * <li>https://de.wikipedia.org/wiki/Serv-U_FTP-Server</li>
 * <li>http://www.serv-u.info</li>
 * <li>http://www.serv-u.com/releasenotes</li>
 * </ul>
 * 
 * @author yang
 *
 */
public class ServUVersionsParser {

	private static final Logger logger = Logger.getLogger(ServUVersionsParser.class.getName());
	/** 存放Serv-U版本解析结果的默认文件名 */
	private static final String DEFAULT_SERV_U_VERSIONS_FILE = "serv-u-versions.txt";
	/** Serv-U官网版本列表页面默认URL地址(截止2018-03-12) */
	private static final String DEFAULT_SERV_U_VERSIONS_URL = "http://www.serv-u.info/aktuelles/versionshinweise_liste.php";

	/** 存放Serv-U版本解析结果的文件名 */
	private String servUVersionsFile;
	/** Serv-U官网版本列表页面URL地址 */
	private String servUVersionsURL;

	/**
	 * 方法一: 使用html-unit获取所有版本号的li元素并获取元素文本
	 * @return 版本列表, 格式: Version,Date
	 */
	public List<String> parseByHtmlUnit() {

		checkConfig();

		List<String> versions = new ArrayList<String>();

		try (final WebClient webClient = new WebClient()) {

			final HtmlPage page = webClient.getPage(this.servUVersionsURL);
	        // 获取class="liste-hauptfeld"的li元素
	        final List<?> list = page.getByXPath("//li[@class='liste-hauptfeld']");

	        for(Object listItem : list) {

	        	HtmlListItem li = (HtmlListItem) listItem;

	        	// 取得版本号文本内容(在a标签里)
	        	DomNode anchor = (DomNode) li.getFirstChild();
	        	String version = anchor.getTextContent();
	        	if(version != null) {
	        		version = version.trim().toLowerCase(); // 如有字母统一转换为小写
	        	}
	        	// 取得版本日期并格式化为yyyy-MM-dd
	        	String versionDate = "";
	        	DomNode dateText = anchor.getNextSibling();
	        	if(dateText != null) {
	            	versionDate = changeDateFormat(dateText.getTextContent());
	        	}

	        	versions.add(version + "," + versionDate);
	        }

		} catch (FailingHttpStatusCodeException | IOException e) {
			logger.log(Level.SEVERE, "获取并解析网页内容时发生异常", e);
		}

		return versions;

	}

	/**
	 * 方法二: 使用正则表达式获取所有版本号和版本日期信息
	 * @return 版本列表, 格式: Version,Date
	 */
	public List<String> parseByRegex() {

		checkConfig();

		List<String> versions = new ArrayList<String>();

		InputStream input = null;
		OutputStream output = null;

		try {

			URL url = new URL(this.servUVersionsURL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
			input = url.openStream();

			output = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = input.read(buffer)) != -1) {
				output.write(buffer, 0, len);
			}
			String html = output.toString();

			Pattern p = Pattern.compile(
				new StringBuffer("\\s*<li\\s+class\\s*=\\s*\"\\s*liste\\-hauptfeld\\s*\"\\s*>\\s*")
					.append("<a\\s+class\\s*=\\s*\"\\s*link\\-hauptfeld\\s*\"\\s+")
					.append("href\\s*=\\s*'\\s*\\?version=\\d+(\\.([\\d]|[a-zA-Z])+)+\\s*'\\s*>\\s*")
					.append("(\\d+(\\.([\\d]|[a-zA-Z])+)+)") // 版本号
					.append("\\s*</a>")
					.append("(([a-zA-Z]|[\\d]|\\,|[\\s])*)") // 版本日期
					.append("</li>").toString());
			Matcher m = p.matcher(html);
			while(m.find()) {
				// 取得版本号文本内容
				String version = m.group(3);
				if(version != null) {
	        		version = version.trim().toLowerCase(); // 如有字母统一转换为小写
	        	}
				// 取得版本日期并格式化为yyyy-MM-dd
				String versionDate = m.group(6);
				versionDate = changeDateFormat(versionDate);
				versions.add(version + "," + versionDate);
			}

		} catch (IOException e) {
			logger.log(Level.SEVERE, "获取并解析网页内容时发生异常", e);
		} finally {
			try {
				if(output != null) {
					output.close();
				}
				if(input != null) {
					input.close();
				}
			} catch (IOException e) {
				logger.log(Level.SEVERE, "获取并解析网页内容时发生异常", e);
			}
		}

		return versions;
	}

	/**
	 * 将解析出来的版本信息写入文件
	 * @param versions 版本列表
	 */
	public void writeToFile(List<String> versions) {
		try (FileOutputStream output = new FileOutputStream(this.servUVersionsFile)) {
        	output.write("Version,Date\n".getBytes());
			for(String v : versions) {
				output.write((v + "\n").getBytes());
			}
			output.flush();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "将版本信息写入文件时发生异常", e);
		}
		logger.info("已将Serv-U的版本信息写入文件" + this.servUVersionsFile);
	}

	/**
	 * 检查配置
	 */
	private void checkConfig() {
		// 如果没有设置文件名则使用默认文件名
		if(StringUtils.isBlank(this.servUVersionsFile)) {
			this.servUVersionsFile = DEFAULT_SERV_U_VERSIONS_FILE;
		}

		// 如果没有设置URL则使用默认URL
		if(StringUtils.isBlank(this.servUVersionsURL)) {
			this.servUVersionsURL = DEFAULT_SERV_U_VERSIONS_URL;
		}
	}

	/**
	 * 修改版本日期的格式为yyyy-MM-dd
	 * @param dateText 版本日期原始字符串
	 * @return yyyy-MM-dd格式的版本日期
	 */
	private String changeDateFormat(String dateText) {
    	if(StringUtils.isNotBlank(dateText)) {
    		String dateTextNew = dateText.replace("vom", "").trim(); // 去掉版本日期前面的vom
			try {
				Date date = DateUtils.parseDate(dateTextNew, Locale.ENGLISH, "MMM dd, yyyy");
				dateTextNew = DateFormatUtils.format(date, "yyyy-MM-dd");
			} catch (ParseException e) {
				logger.log(Level.SEVERE, "格式化版本日期时发生异常", e);
			}
			return dateTextNew;
    	} else {
    		return "";
    	}
	}

	/**
	 * @return the servUVersionsFile
	 */
	public String getServUVersionsFile() {
		return servUVersionsFile;
	}

	/**
	 * @param servUVersionsFile the servUVersionsFile to set
	 */
	public void setServUVersionsFile(String servUVersionsFile) {
		this.servUVersionsFile = servUVersionsFile;
	}

	/**
	 * @return the servUVersionsURL
	 */
	public String getServUVersionsURL() {
		return servUVersionsURL;
	}

	/**
	 * @param servUVersionsURL the servUVersionsURL to set
	 */
	public void setServUVersionsURL(String servUVersionsURL) {
		this.servUVersionsURL = servUVersionsURL;
	}

}
