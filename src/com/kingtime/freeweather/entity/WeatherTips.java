package com.kingtime.freeweather.entity;

public class WeatherTips {

	private String tipsName;
	private String details;
	private String suggest;

	public WeatherTips() {
		super();
	}

	public WeatherTips(String tipsName, String details, String suggest) {
		this.tipsName = tipsName;
		this.details = details;
		this.suggest = suggest;
	}

	public String getTipsName() {
		return tipsName;
	}

	public void setTipsName(String tipsName) {
		this.tipsName = tipsName;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getSuggest() {
		return suggest;
	}

	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}

	public String toString() {
		return "Tips name:" + getTipsName() + ",Tips details:" + getDetails()
				+ ",Tips suggest:" + getSuggest();
	}
}
