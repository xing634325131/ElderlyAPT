package com.kingtime.freeweather.entity;

import java.util.ArrayList;
import java.util.List;


public class BaseLocation {

	private String baseName;
	private int baseCode;
	
	public String getBaseName() {
		return baseName;
	}
	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}
	public int getBaseCode() {
		return baseCode;
	}
	public void setBaseCode(int baseCode) {
		this.baseCode = baseCode;
	}
	
	public static List<BaseLocation> toBaseLocations(List<String> baseStrings){
		List<BaseLocation> basesList = new ArrayList<BaseLocation>();
		for (String baseString : baseStrings) {
			String [] params = baseString.split(",");
			BaseLocation base = new BaseLocation();
			base.baseName = params[0];
			base.baseCode = Integer.valueOf(params[1]);
			basesList.add(base);
		}
		return basesList;
	}
	
	public static List<String> baseNames(List<BaseLocation> basesList){
		List<String> names = new ArrayList<String>();
		for (BaseLocation base : basesList) {
			names.add(base.getBaseName());
		}
		return names;
	}
}
