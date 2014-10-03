package com.kingtime.freeweather.ui;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.WeatherApplication;
import com.kingtime.freeweather.adapter.DayWeatherAdapter;
import com.kingtime.freeweather.entity.WeatherInfo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

public class WeatherActivity extends Activity{
	
	private TextView locationTV;
	private TextView tempTV;
	private TextView predictTV;
	private TextView descTV;
	private TextView windTV;
	private TextView windDrectionTV;
	private TextView humidityTV;
	private TextView airQualityTV;
	private TextView raysTV;
	private ListView dayLV;
	
	private WeatherInfo info;
	private WeatherApplication weatherApplication;
	
	private DayWeatherAdapter weatherAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_info);

		initLayout();
		initData();
		loadData();
	}
	
	private void initLayout(){
		locationTV = (TextView)findViewById(R.id.info_location);
		tempTV = (TextView)findViewById(R.id.info_temp);
		predictTV = (TextView)findViewById(R.id.info_predict);
		descTV = (TextView)findViewById(R.id.info_desc);
		windTV = (TextView)findViewById(R.id.info_wind);
		windDrectionTV = (TextView)findViewById(R.id.info_wind_drection);
		humidityTV = (TextView)findViewById(R.id.info_humidity);
		airQualityTV = (TextView)findViewById(R.id.info_airquality);
		raysTV = (TextView)findViewById(R.id.info_rays);
		dayLV = (ListView)findViewById(R.id.info_day_list);
	}
	
	private void initData(){
		weatherApplication = (WeatherApplication)getApplication();
		if (weatherApplication.dataMap.containsKey("localWeatherInfo")) {
			info = (WeatherInfo) weatherApplication.dataMap
					.get("localWeatherInfo");
		} else {
			// TODO
			info = null;
		}
		
		weatherAdapter = new DayWeatherAdapter(getApplicationContext(), info);
	}

	private void loadData(){
		Log.i("WeatherActivity", "Loading data...");
		locationTV.setText(info.getPosition().getCityName());
		tempTV.setText(info.getDayDetail().getCommonTemp() + "¡æ");
		//predictTV.setText("");
		descTV.setText(info.getDayWeathers().get(0).getWeatherDesc());
		//windTV.setText("");
		windDrectionTV.setText(info.getDayDetail().getWindDirection());
		humidityTV.setText(info.getDayDetail().getHumidityPercent()+"%");
		airQualityTV.setText(info.getDayDetail().getAirQuality());
		raysTV.setText(info.getDayDetail().getUVIntensity());
		dayLV.setAdapter(weatherAdapter);
	}

	@Override
	protected void onDestroy() {
		weatherApplication.dataMap.remove("localWeatherInfo");
		super.onDestroy();
	}
	
}
