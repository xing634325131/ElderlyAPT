package com.kingtime.freeweather.entity;

import java.util.ArrayList;
import java.util.List;

import com.kingtime.elderlyapt.util.StringUtils;

public class WeatherInfo {

	private static final int STANDARD_SIZE = 0x2a/* 42 */;

	private LocalPosition position;
	private String lastRefreshTime;
	private DayDetailWeather dayDetail;
	private List<WeatherTips> tips;
	private List<DayWeather> dayWeathers;

	public WeatherInfo(List<String> weatherDetailList) {
		if (weatherDetailList.size() != STANDARD_SIZE) {
			return;
		}

		position = new LocalPosition();
		dayDetail = new DayDetailWeather();
		tips = new ArrayList<WeatherTips>();
		dayWeathers = new ArrayList<DayWeather>();

		// 0-2 Line
		String[] positionStrings = weatherDetailList.get(0).split(" ");
		position.setProvince(positionStrings[0]);
		position.setState(positionStrings[1]);
		position.setCityName(weatherDetailList.get(1));
		position.setCode(Integer.valueOf(weatherDetailList.get(2)));
		System.out.println(position.toString());

		// 3 Line
		lastRefreshTime = weatherDetailList.get(3);

		// 4-5 Line
		String[] dayDetailStrings1 = weatherDetailList.get(4).split("；");
		String[] dayDetailStrings2 = weatherDetailList.get(5).split("；");
		dayDetail.setCommonTemp(StringUtils
				.parseTempToNum(splitWithCommon(dayDetailStrings1[0])));
		if(dayDetailStrings1.length < 2){
			dayDetail.setWindDirection("暂无实况");
			dayDetail.setHumidityPercent(101);
			dayDetail.setAirQuality("暂无实况");
			dayDetail.setUVIntensity("暂无实况");
		} else{
			dayDetail.setWindDirection(splitWithCommon(dayDetailStrings1[1]));
			dayDetail.setHumidityPercent(StringUtils
				.parsePercentToNum(splitWithCommon(dayDetailStrings1[2])));
			dayDetail.setAirQuality(splitWithCommon(dayDetailStrings2[0]));
			dayDetail.setUVIntensity(splitWithCommon(dayDetailStrings2[1]));
		}
		System.out.println(dayDetail.toString());

		// 6 Line
		String[] tipStrings = weatherDetailList.get(6).split("：");
		int tipsLength = tipStrings.length;
		// System.out.println("tips split length:" + tipStrings.length);
		// System.out.println("tips length:"+tipsLength);
		for (int i = 1; i < tipsLength; i++) {
			WeatherTips weatherTips = new WeatherTips();

			String[] tipInfo1 = tipStrings[i - 1].split("。");
			weatherTips.setTipsName(StringUtils
					.replaceBlank(tipInfo1[tipInfo1.length - 1]));
			if(weatherTips.getTipsName().equals("空气污染指数")){
				continue;
			}
			String[] tipInfo2 = tipStrings[i].split("。");
			weatherTips.setDetails(tipInfo2[0]);
			weatherTips.setSuggest(tipInfo2.length > 2 ? tipInfo2[1] : null);
			System.out.println(weatherTips.toString());

			tips.add(weatherTips);
		}

		// 7-41 Line(35line=5line/day*7day)
		for (int j = 0; j < 7; j++) {
			DayWeather dayWeather = new DayWeather();
			String[] dayinfo1 = weatherDetailList.get(7 + j * 5).split(" ");
			dayWeather.setDayName(dayinfo1[0]);
			dayWeather.setWeatherDesc(dayinfo1[1]);
			String[] dayinfo2 = weatherDetailList.get(7 + j * 5 + 1).split("/");
			dayWeather.setLowTemp(StringUtils.parseTempToNum(dayinfo2[0]));
			dayWeather.setHighTemp(StringUtils.parseTempToNum(dayinfo2[1]));
			dayWeather.setWindDesc(weatherDetailList.get(7 + 5 * j + 2));
			dayWeather.setWeatherPics(weatherDetailList.get(7 + j * 5 + 3),
					weatherDetailList.get(7 + j * 5 + 4));
			System.out.println(dayWeather.toString());
			dayWeathers.add(dayWeather);
		}
	}

	private String splitWithCommon(String input) {
		String[] outputs = input.split("：");
		return outputs[outputs.length - 1];
	}

	public LocalPosition getPosition() {
		return position;
	}

	public String getLastRefreshTime() {
		return lastRefreshTime;
	}

	public DayDetailWeather getDayDetail() {
		return dayDetail;
	}

	public List<WeatherTips> getTips() {
		return tips;
	}

	public List<DayWeather> getDayWeathers() {
		return dayWeathers;
	}

}
