package com.kingtime.freeweather.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.R.integer;
import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

public class LocationUtils {
	/**
	 * 通过GPS得到城市名
	 * 
	 * @param context
	 *            一個Activity
	 * @return 城市名
	 */
	public static String getCityName(Context context) {
		LocationManager locationManager;
		String contextString = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) context
				.getSystemService(contextString);

		Criteria criteria = new Criteria();
		criteria.setCostAllowed(false); // 设置位置服务免费
		criteria.setAccuracy(Criteria.ACCURACY_COARSE); // 设置水平位置精度
		// getBestProvider 只有允许访问调用活动的位置供应商将被返回
		String provider = locationManager.getBestProvider(criteria, true);

		String cityName = null;

		if (provider == null) {
			return null;
		}
		// 得到坐标相关的信息
		Location location = locationManager.getLastKnownLocation(provider);
		if (location == null) {
			System.out.println("Location is null!");
			return null;
		}

		if (location != null) {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			System.out.println("Position:(" + latitude + "," + longitude + ")");
//			// 更具地理环境来确定编码
//			Geocoder gc = new Geocoder(context);
//			try {
//				// 取得地址相关的一些信息\经度、纬度
//				List<Address> addresses = gc.getFromLocation(latitude,
//						longitude, 1);
//				StringBuilder sb = new StringBuilder();
//				if (addresses.size() > 0) {
//					Address address = addresses.get(0);
//					for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
//						sb.append(address.getAddressLine(i)).append("\n");
//					}
//					cityName = sb.toString();
//				}
//			} catch (IOException e) {
//			}
			
			StringBuilder builder = new StringBuilder();
			try {
				System.out.println("1-->");
			    List<Address> addresses = new Geocoder(context).getFromLocation(latitude, longitude,3);//needs install google service
			    if (addresses.size() > 0) {
					System.out.println("2-->"+addresses.size());
			        Address address = addresses.get(0);
			        // for (Address address : addresses) {
			        for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
						System.out.println("3-->"+i);
			            builder.append(address.getAddressLine(i)).append("\n");
			            // builder.append(address.getLocality()).append("\n");
			            // builder.append(address.getPostalCode()).append("\n");
			            // builder.append(address.getCountryName());
			        }
			        // }
			    }
			}catch (IOException e) {
				e.printStackTrace();
			}
			cityName = builder.toString();
		}
		return cityName;
	}
}