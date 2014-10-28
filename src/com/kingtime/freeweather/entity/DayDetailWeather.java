package com.kingtime.freeweather.entity;

public class DayDetailWeather {

	private int commonTemp;
	private String windDirection;
	private int humidityPercent;
	private String airQuality;
	private String uVIntensity;

	public DayDetailWeather() {
		super();
	}

	public DayDetailWeather(int commonTemp, String windDirection,
			int humidityPercent, String airQuality, String uVIntensity) {
		super();
		this.commonTemp = commonTemp;
		this.windDirection = windDirection;
		this.humidityPercent = humidityPercent;
		this.airQuality = airQuality;
		this.uVIntensity = uVIntensity;
	}

	public int getCommonTemp() {
		return commonTemp;
	}

	public void setCommonTemp(int commonTemp) {
		this.commonTemp = commonTemp;
	}

	public String getWindDirection() {
		return windDirection;
	}

	public void setWindDirection(String windDirection) {
		this.windDirection = windDirection;
	}

	public int getHumidityPercent() {
		return humidityPercent;
	}

	public void setHumidityPercent(int humidityPercent) {
		this.humidityPercent = humidityPercent;
	}

	public String getAirQuality() {
		return airQuality;
	}

	public void setAirQuality(String airQuality) {
		this.airQuality = airQuality;
	}

	public String getUVIntensity() {
		return uVIntensity;
	}

	public void setUVIntensity(String uVIntensity) {
		this.uVIntensity = uVIntensity;
	}

	public String toString() {
		return "Temperature :" + getCommonTemp() + ",WindDirection :"
				+ getWindDirection() + ",HumidityPercent :"
				+ getHumidityPercent() + ",AireQuality :" + getAirQuality()
				+ ",UVIntensity :" + getUVIntensity();
	}
}
