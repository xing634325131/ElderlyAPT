package com.kingtime.freeweather.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * SharedPreferences管理类
 * */
public class SharedPreferencesMgr {

	private static Context context;
	private static SharedPreferences sPrefs;

	private SharedPreferencesMgr(Context context, String fileName) {
		this.context = context;
		// 初始化一个SharedPreferences对象，可以读写。
		sPrefs = context.getSharedPreferences(fileName, context.MODE_PRIVATE);
		System.out.println("Create");
	}

	public static void init(Context context, String fileName) {
		new SharedPreferencesMgr(context, fileName);
	}

	public static String fileName;

	/**
	 * 从Preferences缓存得到一个整形
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static int getInt(String key, int defaultValue) {
		return sPrefs.getInt(key, defaultValue);
	}

	/**
	 * 保存一个整形到Preferences中
	 * 
	 * @param key
	 * @param value
	 */
	public static void setInt(String key, int value) {
		sPrefs.edit().putInt(key, value).commit();
	}

	public static boolean getBoolean(String key, boolean defaultValue) {
		return sPrefs.getBoolean(key, defaultValue);
	}

	public static void setBoolean(String key, boolean value) {
		sPrefs.edit().putBoolean(key, value).commit();
	}

	public static String getString(String key, String defaultValue) {
		if (sPrefs == null)
			return null;
		return sPrefs.getString(key, defaultValue);
	}

	public static void setString(String key, String value) {
		if (sPrefs == null)
			return;
		sPrefs.edit().putString(key, value).commit();
	}
}
