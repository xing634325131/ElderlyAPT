package com.kingtime.freeweather.entity;

public class DayWeather {

	private String dayName;
	private int highTemp;
	private int lowTemp;
	private String weatherDesc;
	private String windDesc;
	private String[] weatherPics;

	public DayWeather() {
		super();
	}

	public DayWeather(String dayName, int highTemp, int lowTemp,
			String weatherDesc, String windDesc) {
		super();
		this.dayName = dayName;
		this.highTemp = highTemp;
		this.lowTemp = lowTemp;
		this.weatherDesc = weatherDesc;
		this.windDesc = windDesc;
	}

	public String getDayName() {
		return dayName;
	}

	public void setDayName(String dayName) {
		this.dayName = dayName;
	}

	public int getHighTemp() {
		return highTemp;
	}

	public void setHighTemp(int highTemp) {
		this.highTemp = highTemp;
	}

	public int getLowTemp() {
		return lowTemp;
	}

	public void setLowTemp(int lowTemp) {
		this.lowTemp = lowTemp;
	}

	public String getWeatherDesc() {
		return weatherDesc;
	}

	public void setWeatherDesc(String weatherDesc) {
		this.weatherDesc = weatherDesc;
	}

	public String getWindDesc() {
		return windDesc;
	}

	public void setWindDesc(String windDesc) {
		this.windDesc = windDesc;
	}

	public String[] getWeatherPics() {
		return weatherPics;
	}

	public void setWeatherPics(String pic1, String pic2) {
		String[] weatherPics = { pic1, pic2 };
		this.weatherPics = weatherPics;
	}

	public String toString() {
		return "Date :" + getDayName() + ",Temperature range :" + getLowTemp()
				+ "-" + getHighTemp() + "℃,Details :" + getWeatherDesc()
				+ ",Wind :" + getWindDesc() + ",Pic :(" + getWeatherPics()[0]
				+ "," + getWeatherPics()[1] + ").";
	}
}
