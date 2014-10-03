package com.kingtime.elderlyapt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.entity.ServiceCell;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年7月29日
 */
public class GridViewServiceAdapter extends BaseAdapter {

	private LayoutInflater layoutInflater;
	private List<ServiceCell> serviceCells = new ArrayList<ServiceCell>();
	private ServiceCell tempServiceCell;
	
	private static int[] mServiceImages = new int[] { R.drawable.service1,
			R.drawable.service2, R.drawable.service3, R.drawable.service4,
			R.drawable.service5, R.drawable.service6, R.drawable.service7,
			R.drawable.service8, R.drawable.service9 };
	private static String[] mServiceDesc = new String[] { "互助", "娱乐", "家务", "陪护",
		"水电维修", "医疗", "志愿活动", "代办", "短途旅游" };

	public static int getServiceImages(int position){
		return mServiceImages[position];
	}
	
	public static String getServiceDesc(int position){
		return mServiceDesc[position];
	}
	
	public static int getCellLength(){
		if(mServiceDesc.length == mServiceImages.length){
			return mServiceDesc.length;
		}
		return 0;
	}
	
	public GridViewServiceAdapter(Context c,List<ServiceCell> serviceCells){
		this.layoutInflater = LayoutInflater.from(c);
		this.serviceCells = serviceCells;
		tempServiceCell = new ServiceCell();
	}
	
	@Override
	public int getCount() {
		return serviceCells.size();
	}

	@Override
	public Object getItem(int position) {
		return serviceCells.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = null;
		if(layoutInflater != null){
			view = layoutInflater.inflate(R.layout.service_cell, null);
			tempServiceCell = serviceCells.get(position);
			TextView serviceCellTV = (TextView)view.findViewById(R.id.service_cell_desc);
			serviceCellTV.setText(tempServiceCell.getServiceDesc());
			Drawable drawable = new BitmapDrawable(tempServiceCell.getServiceImage());
			drawable.setBounds(0, 0, 80, 80);
			serviceCellTV.setCompoundDrawables(null, drawable, null, null);
		}
		return view;
	}

}
