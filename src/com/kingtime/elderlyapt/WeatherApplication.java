package com.kingtime.elderlyapt;

import java.util.HashMap;

import android.app.Application;

public class WeatherApplication extends Application {

	public HashMap<String, Object> dataMap;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		dataMap = new HashMap<String, Object>();
		super.onCreate();
	}
	@Override
	public void onLowMemory() {
		System.out.println("Low memory");
		super.onLowMemory();
	}

}

