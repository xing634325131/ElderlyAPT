package com.kingtime.freeweather.adapter;

import com.kingtime.elderlyapt.R;
import com.kingtime.freeweather.entity.DayWeather;
import com.kingtime.freeweather.entity.WeatherInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DayWeatherAdapter extends BaseAdapter{
	
	private Context context;
	private WeatherInfo info;
	
	public DayWeatherAdapter(Context context, WeatherInfo info) {
		super();
		System.out.println("Hello1");
		this.context = context;
		this.info = info;
	}

	@Override
	public int getCount() {
		return info.getDayWeathers().size();
	}

	@Override
	public Object getItem(int position) {
		return info.getDayWeathers().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("SetView-DayWeatherAdapter");
		DayWeather dayWeather = (DayWeather)getItem(position);
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.day_weather_cell, parent, false);
			holder.dayNameTextView = (TextView)convertView.findViewById(R.id.day_name);
			holder.windTextView = (TextView)convertView.findViewById(R.id.day_wind);
			//holder.pic1ImageView = (ImageView)convertView.findViewById(R.id.day_pic1);
			//holder.pic2ImageView = (ImageView)convertView.findViewById(R.id.day_pic2);
			holder.detailsTextView = (TextView)convertView.findViewById(R.id.day_detail);
			holder.lowTempTextView = (TextView)convertView.findViewById(R.id.day_low_temp);
			holder.highTempTextView = (TextView)convertView.findViewById(R.id.day_high_temp);
			convertView.setTag(holder);
		} else{
			holder = (ViewHolder)convertView.getTag();
		}
		holder.dayNameTextView.setText(dayWeather.getDayName());
		holder.lowTempTextView.setText(dayWeather.getLowTemp() + "℃");
		holder.highTempTextView.setText(dayWeather.getHighTemp() + "℃");
		holder.detailsTextView.setText(dayWeather.getWeatherDesc());
		//holder.detailsTextView.setSelected(true);
		holder.windTextView.setText(dayWeather.getWindDesc());
		return convertView;
	}

	private class ViewHolder{
		TextView dayNameTextView;
		TextView windTextView;
		//ImageView pic1ImageView;
		//ImageView pic2ImageView;
		TextView detailsTextView;
		TextView lowTempTextView;
		TextView highTempTextView;
	}
}
