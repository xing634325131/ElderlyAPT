package com.kingtime.elderlyapt.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.entity.Duty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class ListViewDutyJoinAdapter extends BaseAdapter {

	private Context context;
	private List<Duty> dutyList;
	private Map<String, String> selectedDutyId;

	public Map<String, String> getSelectedDutyId() {
		return selectedDutyId;
	}

	public ListViewDutyJoinAdapter(Context c, List<Duty> duties) {
		this.context = c;
		this.dutyList = duties;
		selectedDutyId = new HashMap<String, String>();
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
		final Duty duty = (Duty) getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.duty_join_cell, parent, false);
			holder.nameTV = (TextView) convertView
					.findViewById(R.id.duty_join_name);
			holder.integralTV = (TextView) convertView
					.findViewById(R.id.duty_join_integral);
			holder.stateTV = (TextView) convertView
					.findViewById(R.id.duty_join_state);
			holder.selectedCB = (CheckBox) convertView
					.findViewById(R.id.duty_join_select);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.nameTV.setText(duty.getDutyName());
		if (duty.getNeedNum() == duty.getNowNum()) {
			holder.selectedCB.setVisibility(View.INVISIBLE);
			holder.stateTV.setVisibility(View.VISIBLE);
		}
		holder.integralTV.setText("参与将获得  " + duty.getDutyIntegral() + "  时间币");
		holder.selectedCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						System.out.println(isChecked ? "Selected:" : "Canceled Select:" + duty.getDutyId());
						if (isChecked == true) {
							selectedDutyId.put(duty.getDutyName(), String.valueOf(duty.getDutyId()));
						} else {
							selectedDutyId.remove(duty.getDutyName());
						}
					}
				});
		return convertView;
	}

	class ViewHolder {
		TextView nameTV;
		TextView integralTV;
		TextView stateTV;
		CheckBox selectedCB;
	}
}
