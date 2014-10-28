package com.kingtime.elderlyapt.adapter;

import java.util.List;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.entity.Duty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014年8月11日
 */
public class ListViewShowAddDutyAdapter extends BaseAdapter {

	private Context context;
	private List<Duty> dutyList;

	public ListViewShowAddDutyAdapter(Context c, List<Duty> duties) {
		this.context = c;
		this.dutyList = duties;
	}

	@Override
	public int getCount() {
		return dutyList.size();
	}

	@Override
	public Object getItem(int position) {
		return dutyList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		System.out.println("adapter:start!" + position);
		final Duty duty = (Duty) getItem(position);
		System.out.println("dutyname:" + duty.getDutyName());
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.add_duty_cell, parent, false);
			holder.nameTV = (TextView) convertView
					.findViewById(R.id.add_to_name);
			holder.descTV = (TextView) convertView
					.findViewById(R.id.add_to_desc);
			holder.deleteIV = (ImageView) convertView
					.findViewById(R.id.add_to_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.nameTV.setText(duty.getDutyName());
		//System.out.println(duty.getDutyIntegral());
		String integral = duty.getDutyIntegral() > 0 ? "获得" + duty.getDutyIntegral() : "消耗" + Math.abs(duty.getDutyIntegral());
		holder.descTV.setText(duty.getNeedNum() + "人," + integral + "时间币/人");
		holder.deleteIV.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Toast.makeText(context, "Delete:" + duty.getDutyName(), Toast.LENGTH_LONG).show();
			}
		});
		return convertView;
	}

	class ViewHolder {
		TextView nameTV;
		TextView descTV;
		ImageView deleteIV;
	}

}
