package com.kingtime.freeweather.helper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.R.integer;
import android.content.Context;

import com.kingtime.elderlyapt.R;

public class ConfigHelper {
	
	public static int getDefaultCityCode(Context context){
		String cityCode = loadProperty(context).getProperty("DEFAULTCITYCODE");
		System.out.println("DefaultCityCode-->"+cityCode);
		return Integer.valueOf(cityCode);
	}
	
	private static Properties loadProperty(Context c){
		InputStream defaultCityCodeInputStream = c.getResources().openRawResource(R.raw.freeweather);
		Properties weatherProperties = new Properties();
		try {
			weatherProperties.load(defaultCityCodeInputStream);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return weatherProperties;
	}
}
