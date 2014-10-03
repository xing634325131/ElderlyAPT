package com.kingtime.elderlyapt.adapter;

import java.util.List;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.entity.Duty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月8日
 */
public class ListViewDutyAdapter extends BaseAdapter {

	private Context context;
	private List<Duty> dutyList;

	// private String activityName;
	//
	// public String getActivityName() {
	// return activityName;
	// }
	//
	// public void setActivityName(String activityName) {
	// this.activityName = activityName;
	// }

	public ListViewDutyAdapter(Context c, List<Duty> duties) {
		this.context = c;
		this.dutyList = duties;
		// activityName = "活动名称";
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
		Duty duty = (Duty) getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.duty_cell, parent, false);
			holder.titleTV = (TextView) convertView
					.findViewById(R.id.duty_name);
			holder.contentTV = (TextView) convertView
					.findViewById(R.id.duty_content);
			holder.needNumTV = (TextView) convertView
					.findViewById(R.id.duty_need_num);
			holder.nowNumTV = (TextView) convertView
					.findViewById(R.id.duty_now_num);
			holder.integralTV = (TextView) convertView
					.findViewById(R.id.duty_integral);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.titleTV.setText(duty.getDutyName());
		holder.contentTV.setText(duty.getDutyContent());
		holder.integralTV.setText("参与将获得  " + duty.getDutyIntegral() + "  时间币");
		holder.needNumTV.setText(String.valueOf(duty.getNeedNum()));
		holder.nowNumTV.setText(String.valueOf(duty.getNowNum()));

		return convertView;
	}

	class ViewHolder {
		TextView titleTV;
		TextView contentTV;
		TextView nowNumTV;
		TextView needNumTV;
		TextView integralTV;
	}
}
