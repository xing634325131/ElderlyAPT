package com.kingtime.freeweather.ui;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.WeatherApplication;
import com.kingtime.freeweather.entity.WeatherInfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SecondActivity extends Activity {

	private WeatherApplication weatherApplication;
	private WeatherInfo weatherInfo;
	private LinearLayout[] showSuggestLayouts;
	private TextView[] tipDetailsTextViews;
	private static int[] layoutIds = { R.id.suggest_layout_sunglass,
			R.id.suggest_layout_clothes, R.id.suggest_layout_tour,
			R.id.suggest_layout_sports, R.id.suggest_layout_car,
			R.id.suggest_layout_makeup, R.id.suggest_layout_cold,
			R.id.suggest_layout_rays, R.id.suggest_layout_comfort };
	private static int[] textviewIds = { R.id.suggest_sunglass,
			R.id.suggest_clothes, R.id.suggest_tour, R.id.suggest_sports,
			R.id.suggest_car, R.id.suggest_makeup, R.id.suggest_cold,
			R.id.suggest_rays, R.id.suggest_comfort };
	private int tipLength;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather_suggest);

		weatherApplication = (WeatherApplication) getApplication();
		if (weatherApplication.dataMap.containsKey("localWeatherInfo")) {
			weatherInfo = (WeatherInfo) weatherApplication.dataMap
					.get("localWeatherInfo");
		} else {
			// TODO
			weatherInfo = null;
		}

		initLayout();
	}

	private void initLayout() {
		tipLength = weatherInfo.getTips().size();
		showSuggestLayouts = new LinearLayout[tipLength];
		tipDetailsTextViews = new TextView[tipLength];
		for (int i = 0; i < tipLength; i++) {
			showSuggestLayouts[i] = (LinearLayout) findViewById(layoutIds[i]);
			tipDetailsTextViews[i] = (TextView) findViewById(textviewIds[i]);

			tipDetailsTextViews[i].setText(weatherInfo.getTips().get(i)
					.getDetails());
			showSuggestLayouts[i].setOnClickListener(listener);
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int tipNum = getNum(v.getId());
			Toast.makeText(getApplicationContext(),
					weatherInfo.getTips().get(tipNum).getSuggest(),
					Toast.LENGTH_LONG).show();
		}
	};

	private int getNum(int id) {
		System.out.println("id :"+id);
		for (int j = 0; j < layoutIds.length; j++) {
			System.out.println("length :"+layoutIds.length+",index :"+j);
			System.out.println("Compare id :"+layoutIds[j]);
			if (id == layoutIds[j]) {
				return j;
			}
		}
		return -1;
	}

	@Override
	protected void onDestroy() {
		weatherApplication.dataMap.remove("localWeatherInfo");
		super.onDestroy();
	}

}
