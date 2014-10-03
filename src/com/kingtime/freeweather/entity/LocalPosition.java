package com.kingtime.freeweather.entity;

public class LocalPosition {

	private String province;
	private String state;
	private String cityName;
	private int code;

	public LocalPosition() {
		super();
	}

	public LocalPosition(String province, String state, String cityName,
			int code) {
		super();
		this.province = province;
		this.state = state;
		this.cityName = cityName;
		this.code = code;
	}

	public LocalPosition(String province, String state, String cityName) {
		super();
		this.province = province;
		this.state = state;
		this.cityName = cityName;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String toString() {
		return "Province :" + getProvince() + ",State :" + getState()
				+ ",CityName :" + getCityName() + ",CityCode :" + getCode();
	}
}
