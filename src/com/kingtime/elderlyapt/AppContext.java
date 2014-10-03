package com.kingtime.elderlyapt;

import java.util.Properties;

import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.StringUtils;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ȫ��Ӧ�ó����ࣺ���ڱ���͵���ȫ��Ӧ�����ü�������������
 * 
 * @author xp
 * @created 2014��4��26��
 */
public class AppContext extends Application {
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;

	/**
	 * ��ȡ��ǰ��������
	 * 
	 * @return 0��û������ 1��WIFI���� 2��WAP���� 3��NET����
	 */
	public int getNetworkType() {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	/**
	 * ��ȡ��¼��Ϣ
	 * 
	 * @return
	 */
	public User getLoginInfo() {
		User user = new User();
		user.setUid(StringUtils.toInt(getProperty("uid")));
		user.setPwd(getProperty("pwd"));
		user.setPhotoName(getProperty("photoName"));
		user.setPhone(getProperty("phone"));
		user.setInterest(getProperty("interest"));
		user.setGender(getProperty("gender"));
		user.setEmail(getProperty("email"));
		user.setName(getProperty("name"));
		user.setLocationId(StringUtils.toInt(getProperty("locationId")));
		user.setRoleId(StringUtils.toInt(getProperty("roleId")));
		user.setResPhone(getProperty("resPhone"));
		user.setIntegral(StringUtils.toInt(getProperty("integral")));
		user.setCredibility(StringUtils.toFloat(getProperty("credibility")));
		user.setEvaluateTimes(StringUtils.toInt(getProperty("evaluateTimes")));
		user.setAddress(getProperty("address"));
		return user;
	}

	/**
	 * �����¼��Ϣ
	 */
	public void clearLoginInfo() {
		removeProperty("uid", "roleId", "locationId", "name", "gender",
				"resPhone", "phone", "interest", "email", "pwd", "integral",
				"credibility", "photoName", "evaluateTimes", "address");
	}

	/**
	 * �����¼��Ϣ
	 * 
	 * @param user
	 */
	public void saveLoginInfo(final User user) {
		setProperties(new Properties() {
			{
				setProperty("uid", String.valueOf(user.getUid()));
				setProperty("name", user.getName());
				setProperty("pwd", user.getPwd());
				setProperty("interest", user.getInterest());
				setProperty("phone", user.getPhone());
				setProperty("photoName", user.getPhotoName());
				setProperty("email", user.getEmail());
				setProperty("gender", user.getGender());
				setProperty("locationId", String.valueOf(user.getLocationId()));
				setProperty("roleId", String.valueOf(user.getRoleId()));
				setProperty("resPhone", user.getResPhone());
				setProperty("integral", String.valueOf(user.getIntegral()));
				setProperty("credibility", String.valueOf(user.getCredibility()));
				setProperty("evaluateTimes", String.valueOf(user.getEvaluateTimes()));
				setProperty("address", user.getAddress());
			}
		});
	}

	public void setRememberMe(boolean isRememberMe) {
		System.out.println("isrememberme-->" + isRememberMe);
		setProperty("isRememberMe", isRememberMe ? "true" : "false");
	}

	public boolean getRemberMe() {
		return StringUtils.toBoolean(getProperty("isRememberMe"));
	}

	public boolean containsProperty(String key) {
		Properties props = getProperties();
		return props.containsKey(key);
	}

	public void setProperties(Properties ps) {
		AppConfig.getAppConfig(this).set(ps);
	}

	public Properties getProperties() {
		return AppConfig.getAppConfig(this).get();
	}

	public void setProperty(String key, String value) {
		AppConfig.getAppConfig(this).set(key, value);
	}

	public String getProperty(String key) {
		return AppConfig.getAppConfig(this).get(key);
	}

	public void removeProperty(String... key) {
		AppConfig.getAppConfig(this).remove(key);
	}
}
