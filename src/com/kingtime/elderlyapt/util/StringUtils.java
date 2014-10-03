package com.kingtime.elderlyapt.util;

import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * @author xp
 * @created 2014年4月24日
 */
public class StringUtils {

	private final static Pattern emailer = Pattern
			.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	/**
	 * 判断输入字符串是否为空
	 * 
	 * @param input
	 * @return
	 */
	public static boolean isEmpty(String input) {
		if (input == null || "".equals(input))
			return true;

		for (int i = 0; i < input.length(); i++) {
			char c = input.charAt(i);
			if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * @param plainText
	 *            明文
	 * @return 32位密文
	 */
	public static String toMD5(String plainText) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_md5;
	}

	/**
	 * 判断是不是一个合法的电子邮件地址
	 * 
	 * @param email
	 * @return
	 */
	public static boolean isCorrectEmail(String email) {
		if (email == null || email.trim().length() == 0)
			return false;
		return emailer.matcher(email).matches();
	}

	/**
	 * 字符串转整数
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static int toInt(String str, int defValue) {
		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转整数
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static int toInt(Object obj) {
		if (obj == null)
			return 0;
		return toInt(obj.toString(), 0);
	}

	/**
	 * 字符串转浮点型
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public static float toFloat(String str, float defValue) {
		try {
			return Long.parseLong(str);
		} catch (Exception e) {
		}
		return defValue;
	}

	/**
	 * 对象转浮点型
	 * 
	 * @param obj
	 * @return 转换异常返回 0
	 */
	public static float toFloat(Object obj) {
		if (obj == null)
			return 0;
		return toFloat(obj.toString(), 0);
	}

	/**
	 * 字符串转布尔值
	 * 
	 * @param b
	 * @return 转换异常返回 false
	 */
	public static boolean toBoolean(String key) {
		try {
			return Boolean.parseBoolean(key);
		} catch (Exception e) {
		}
		return false;
	}

	/**
	 * 以yyyy-MM-dd HH:mm:ss格式获取当前日期
	 * 
	 * @return 日期字符串
	 */
	public static String getFormatNowDate() {
		Date now = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
				Locale.ENGLISH);
		return df.format(now).toString();
	}

	/**
	 * 将字符串转位日期类型
	 * 
	 * @param sdate
	 * @return
	 */
	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	/**
	 * 通用字节流转换函数，返回操作结果
	 * 
	 * @param 字节流
	 * @return 操作结果
	 */
	public static String parse(InputStream inputStream) {
		byte[] data = null;
		try {
			data = StreamTool.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String(data);
	}
}
