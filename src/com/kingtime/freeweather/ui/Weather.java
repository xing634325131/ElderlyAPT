package com.kingtime.freeweather.ui;

import java.util.ArrayList;
import java.util.List;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.util.FileUtils;
import com.kingtime.elderlyapt.util.StreamTool;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.elderlyapt.widget.LoadingDialog;
import com.kingtime.elderlyapt.widget.OnViewChangeListener;
import com.kingtime.elderlyapt.widget.ScrollLayout;
import com.kingtime.freeweather.adapter.DayWeatherAdapter;
import com.kingtime.freeweather.entity.WeatherInfo;
import com.kingtime.freeweather.helper.SharedPreferencesMgr;
import com.kingtime.freeweather.utils.XMLReader;
import com.kingtime.freeweather.widget.WeatherSelectDialog;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Weather extends Activity implements OnClickListener, OnViewChangeListener {

	private Button backBtn;
	private TextView titleTV;
	private TextView setTV;

	// 滑动界面和顶部模块
	private ScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private TextView infoTV;
	private TextView suggestTV;

	// WeatherInfo 最近天气情况
	private TextView locationTV;
	private TextView tempTV;
	private TextView predictTV;
	private TextView descTV;
	private TextView windTV;
	private TextView windDrectionTV;
	private TextView humidityTV;
	private TextView airQualityTV;
	private TextView raysTV;
	private TextView lastRefreshTV;
	private ImageView refreshIV;
	private ListView dayLV;
	private DayWeatherAdapter weatherAdapter;

	// WeatherSuggest 生活小贴士
	private LinearLayout[] showSuggestLayouts;
	private TextView[] tipDetailsTextViews;
	private static int[] layoutIds = { R.id.suggest_layout_sunglass, R.id.suggest_layout_clothes, R.id.suggest_layout_tour,
			R.id.suggest_layout_sports, R.id.suggest_layout_car, R.id.suggest_layout_makeup, R.id.suggest_layout_cold,
			R.id.suggest_layout_rays, R.id.suggest_layout_comfort };
	private static int[] textviewIds = { R.id.suggest_sunglass, R.id.suggest_clothes, R.id.suggest_tour, R.id.suggest_sports,
			R.id.suggest_car, R.id.suggest_makeup, R.id.suggest_cold, R.id.suggest_rays, R.id.suggest_comfort };
	private int tipLength;

	private List<String> weatherList;
	private WeatherInfo info;

	private LoadingDialog loadingDialog;
	private WeatherSelectDialog weatherSelectDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.header);
		
		loadingDialog = new LoadingDialog(this);
		weatherSelectDialog = new WeatherSelectDialog(this);
		loadingDialog.setLoadText("刷新中...");
		SharedPreferencesMgr.init(getApplicationContext(), "weather");
		weatherList = new ArrayList<String>();
		
		preInit();
		loadData();
	}

	private void preInit() {
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		setTV = (TextView) findViewById(R.id.head_right);
		titleTV.setText("天气");
		setTV.setText("设置");
		setTV.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		setTV.setOnClickListener(this);

	}

	private void initMainLayout() {
		setContentView(R.layout.weather);
		preInit();
		
		mScrollLayout = (ScrollLayout) findViewById(R.id.weather_scrolllayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.weather_layout);
		infoTV = (TextView) findViewById(R.id.weather_since);
		suggestTV = (TextView) findViewById(R.id.weather_tips);

		mViewCount = mScrollLayout.getChildCount();
		// System.out.println("mviewCount-->"+mViewCount);
		mImageViews = new LinearLayout[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);

	}

	private void loadData() {
		final int cityCode = SharedPreferencesMgr.getInt("DEFAULTCITYCODE", 1722);
		System.out.println("CityCode->" + cityCode);

		if(loadingDialog != null){
			loadingDialog.show();
		}
		
		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				String weatherString = FileUtils.read(getApplicationContext(), "weather" + cityCode + ".xml");
				if (!StringUtils.isEmpty(weatherString)) {
					try {
						System.out.println("Start parsing weather info...");
						weatherList = XMLReader.readToStringList(weatherString);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					info = new WeatherInfo(weatherList);

					initMainLayout();
					initWeatherInfo();
					initWeatherSuggest();
					loadWeatherInfoData();
					loadingDialog.dismiss();
				}

				super.handleMessage(msg);
			}

		};

		new Thread() {
			public void run() {
				try {
					byte[] data1 = StreamTool.read(ApiClient.getWeather(cityCode));
					FileUtils.write(getApplicationContext(), "weather" + cityCode + ".xml", new String(data1));
					handler.sendEmptyMessage(0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					Toast.makeText(getApplicationContext(), "Failed to get the weather infomation!", Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			};
		}.start();
	}

	private void initWeatherInfo() {
		locationTV = (TextView) findViewById(R.id.info_location);
		tempTV = (TextView) findViewById(R.id.info_temp);
		predictTV = (TextView) findViewById(R.id.info_predict);
		descTV = (TextView) findViewById(R.id.info_desc);
		windTV = (TextView) findViewById(R.id.info_wind);
		windDrectionTV = (TextView) findViewById(R.id.info_wind_drection);
		humidityTV = (TextView) findViewById(R.id.info_humidity);
		airQualityTV = (TextView) findViewById(R.id.info_airquality);
		raysTV = (TextView) findViewById(R.id.info_rays);
		lastRefreshTV = (TextView)findViewById(R.id.info_refresh_time);
		refreshIV = (ImageView)findViewById(R.id.info_refresh);
		dayLV = (ListView) findViewById(R.id.info_day_list);
	}

	private void loadWeatherInfoData() {
		Log.i("Weather", "Loading data...");
		locationTV.setText(info.getPosition().getCityName());
		int nowTemp = info.getDayDetail().getCommonTemp();
		if(nowTemp == 999){
			tempTV.setText("暂无实况");
		}else{
			tempTV.setText(info.getDayDetail().getCommonTemp() + "℃");
		}
		// predictTV.setText("");
		descTV.setText(info.getDayWeathers().get(0).getWeatherDesc());
		// windTV.setText("");
		windDrectionTV.setText(info.getDayDetail().getWindDirection());
		int humidity = info.getDayDetail().getHumidityPercent();
		if(humidity == 101){
			humidityTV.setText("暂无实况");
		} else{
			humidityTV.setText(info.getDayDetail().getHumidityPercent() + "%");
		}
		airQualityTV.setText(info.getDayDetail().getAirQuality());
		raysTV.setText(info.getDayDetail().getUVIntensity());
		lastRefreshTV.setText("刷新于" + info.getLastRefreshTime());
		
		weatherAdapter = new DayWeatherAdapter(getApplicationContext(), info);
		dayLV.setCacheColorHint(0);
		dayLV.setAdapter(weatherAdapter);
		weatherAdapter.notifyDataSetChanged();
		refreshIV.setOnClickListener(this);
	}

	private void initWeatherSuggest() {
		tipLength = info.getTips().size();
		showSuggestLayouts = new LinearLayout[tipLength];
		tipDetailsTextViews = new TextView[tipLength];
		for (int i = 0; i < tipLength; i++) {
			showSuggestLayouts[i] = (LinearLayout) findViewById(layoutIds[i]);
			tipDetailsTextViews[i] = (TextView) findViewById(textviewIds[i]);

			tipDetailsTextViews[i].setText(info.getTips().get(i).getDetails());
			showSuggestLayouts[i].setOnClickListener(this);
		}
	}

	private int getNum(int id) {// for weatherSuggest
		System.out.println("id :" + id);
		for (int j = 0; j < layoutIds.length; j++) {
			System.out.println("length :" + layoutIds.length + ",index :" + j);
			System.out.println("Compare id :" + layoutIds[j]);
			if (id == layoutIds[j]) {
				return j;
			}
		}
		return -1;
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			infoTV.setTextColor(0xff228B22);
			suggestTV.setTextColor(Color.WHITE);
		} else if (index == 1) {
			suggestTV.setTextColor(0xff228B22);
			infoTV.setTextColor(Color.WHITE);
		}
	}

	@Override
	public void OnViewChange(int view) {
		setCurPoint(view);
	}

	@Override
	public void onClick(View v) {
		int tipNum = getNum(v.getId());
		if (tipNum != -1) {
			Toast.makeText(getApplicationContext(), info.getTips().get(tipNum).getSuggest(), Toast.LENGTH_LONG).show();
			return;
		}

		if (v.getId() == R.id.head_back) {
			finish();
		} else if (v.getId() == R.id.head_right) {
			showSelectDialog();
			//
		} else if(v.getId() == R.id.info_refresh){
			loadData();
		}
			else {
			int pos = (Integer) (v.getTag());
			setCurPoint(pos);
			mScrollLayout.snapToScreen(pos);
		}
	}
	
	private void showSelectDialog(){
		weatherSelectDialog.show();
		weatherSelectDialog.setSureOnClickListener(new android.view.View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SharedPreferencesMgr.setInt("DEFAULTCITYCODE", weatherSelectDialog.defaultCityCode);
				weatherSelectDialog.dismiss();
				loadData();
			}
		});
	}

	@Override
	protected void onDestroy() {
		if(loadingDialog != null){
			loadingDialog.dismiss();
		}
		if(weatherSelectDialog != null){
			weatherSelectDialog.dismiss();
		}
		super.onDestroy();
	}
	
	
}
