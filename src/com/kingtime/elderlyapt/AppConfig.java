package com.kingtime.elderlyapt;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import android.content.Context;

/**
 * Ӧ�ó��������ࣺ���ڱ����û������Ϣ������
 * 
 * @author xp
 * @created 2014��4��26��
 */
public class AppConfig {
	private final static String APP_CONFIG = "config";
	private Context mContext;
	private static AppConfig appConfig;

	public static AppConfig getAppConfig(Context context) {
		if (appConfig == null) {
			appConfig = new AppConfig();
			appConfig.mContext = context;
		}
		return appConfig;
	}

	public Properties get() {
		FileInputStream fis = null;
		Properties props = new Properties();
		try {
			// ��ȡapp_configĿ¼�µ�config
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			System.out.println("Path-->>" + dirConf.getPath());
			File conf = new File(dirConf.getPath() + File.separator
					+ APP_CONFIG);
			if (!conf.exists()) {
				System.out.println("Path-->>");
				conf.createNewFile();
			}
			fis = new FileInputStream(dirConf.getPath() + File.separator
					+ APP_CONFIG);
			props.load(fis);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
			}
		}
		return props;
	}

	public String get(String key) {
		Properties props = get();
		return (props != null) ? props.getProperty(key) : null;
	}

	public void set(String key, String value) {
		Properties props = get();
		props.setProperty(key, value);
		setProps(props);
	}

	public void set(Properties ps) {
		Properties props = get();
		props.putAll(ps);
		setProps(props);
	}

	/**
	 * �Ƴ�������Ϣ
	 * 
	 * @param key
	 *            Ҫɾ������������
	 */
	public void remove(String... key) {
		Properties props = get();
		for (String k : key) {
			props.remove(k);
		}
		setProps(props);
	}

	/**
	 * ��������
	 * 
	 * @param props
	 */
	private void setProps(Properties props) {
		FileOutputStream fos = null;
		try {
			// ��ȡapp_configĿ¼�µ�config
			File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
			System.out.println("Path1-->>" + dirConf.getPath());
			File conf = new File(dirConf, APP_CONFIG);
			System.out.println("Path-->>" + conf.getPath());
			fos = new FileOutputStream(conf);
			props.store(fos, null);
			fos.flush();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
