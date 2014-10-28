package com.kingtime.elderlyapt.entity;

import android.graphics.Bitmap;

/**
 * @author xp
 * @created 2014年7月29日
 */
public class ServiceCell {

	private Bitmap serviceImage;
	private String serviceDesc;
	
	public ServiceCell(){
		
	}
	
	public ServiceCell(Bitmap image,String desc){
		serviceDesc = desc;
		serviceImage = image;
	}

	public Bitmap getServiceImage() {
		return serviceImage;
	}

	public String getServiceDesc() {
		return serviceDesc;
	}
}
