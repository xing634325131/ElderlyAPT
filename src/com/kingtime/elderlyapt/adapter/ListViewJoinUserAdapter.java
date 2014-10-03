package com.kingtime.elderlyapt.adapter;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.api.URLs;
import com.kingtime.elderlyapt.entity.Record;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.widget.LruImageCache;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RatingBar;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月8日
 */
public class ListViewJoinUserAdapter extends BaseAdapter {

	private Context context;
	private List<Record> records;
	/**
	 * 负责记录单选框状态，传递结果
	 */
	private List<Record> deleteRecords;
	/**
	 * 创建者ID
	 */
	private int postUserId;

	// 网络图片获取
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	/**
	 * @param c
	 *            上下文
	 * @param userList
	 *            用户列表
	 */
	public ListViewJoinUserAdapter(Context c, List<Record> recordList) {
		this.context = c;
		this.records = recordList;
		this.deleteRecords = recordList;
		postUserId = 0;
		rQueue = Volley.newRequestQueue(c);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
	}

	public int getPostUserId() {
		return postUserId;
	}

	public void setPostUserId(int postUserId) {
		this.postUserId = postUserId;
	}

	public List<Record> getDeleteRecords() {
		return deleteRecords;
	}

	@Override
	public int getCount() {
		return records.size();
	}

	@Override
	public Object getItem(int position) {
		return records.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Log.i("Adapter-position", String.valueOf(position));
		final Record record = (Record) getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.join_info_cell, parent, false);
			holder.photoImageView = (NetworkImageView) convertView
					.findViewById(R.id.info_photo);
			holder.credibilityBar = (RatingBar) convertView
					.findViewById(R.id.info_credibility);
			holder.extraInfoTV = (TextView) convertView
					.findViewById(R.id.info_other);
			holder.usernameTV = (TextView) convertView
					.findViewById(R.id.info_name);
			holder.dutyNameTV = (TextView) convertView
					.findViewById(R.id.info_duty);
			holder.manageCB = (CheckBox) convertView
					.findViewById(R.id.info_checkbox);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String imageURL = ApiClient.formImageURL(URLs.REQUEST_PHOTO_IMAGE,
				record.getUser().getPhotoName());
		holder.photoImageView.setImageUrl(imageURL, imageLoader);
		holder.usernameTV.setText(record.getUser().getName());
		holder.dutyNameTV.setText(record.getDutyName());
		holder.credibilityBar.setRating(record.getUser().getCredibility());

		if (record.getUser().getUid() == postUserId
				&& record.getUser().getRoleId() != User.COMMON_USER) {
			holder.extraInfoTV.setVisibility(View.VISIBLE);
			holder.extraInfoTV.setText("创建者；" + record.getUser().getRole());
		} else if (record.getUser().getUid() == postUserId) {
			holder.extraInfoTV.setVisibility(View.VISIBLE);
			holder.extraInfoTV.setText("创建者；");
		} else if (record.getUser().getRoleId() != User.COMMON_USER) {
			holder.extraInfoTV.setVisibility(View.VISIBLE);
			holder.extraInfoTV.setText(record.getUser().getRole());
		}

		if (record.getStateId() == Record.RECORD_MANAGEING) {
			holder.manageCB.setVisibility(View.VISIBLE);
		} else {
			holder.manageCB.setVisibility(View.INVISIBLE);
		}
		holder.manageCB.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				int stateId = isChecked?Record.RECORD_CANCLED:Record.RECORD_COMMON;
				deleteRecords.get(position).setStateId(stateId);
			}
		});
		return convertView;
	}

	class ViewHolder {
		NetworkImageView photoImageView;
		TextView usernameTV;
		TextView extraInfoTV;
		RatingBar credibilityBar;
		TextView dutyNameTV;
		CheckBox manageCB;
	}

}
