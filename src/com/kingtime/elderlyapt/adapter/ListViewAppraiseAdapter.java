package com.kingtime.elderlyapt.adapter;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.api.URLs;
import com.kingtime.elderlyapt.entity.Evaluate;
import com.kingtime.elderlyapt.entity.Record;
import com.kingtime.elderlyapt.widget.LruImageCache;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月20日
 */
public class ListViewAppraiseAdapter extends BaseAdapter {

	private Context context;
	private List<Record> recordList;
	private List<Evaluate> evaluates;
	// 网络图片获取
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	public List<Evaluate> getEvaluates() {
		return evaluates;
	}

	public ListViewAppraiseAdapter(Context c, List<Record> records) {
		this.context = c;
		this.recordList = records;
		rQueue = Volley.newRequestQueue(c);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);

		evaluates = new ArrayList<Evaluate>();
		for (Record record : recordList) {
			Evaluate evaluate = new Evaluate();
			evaluate.setUid(record.getUid());
			evaluates.add(evaluate);
		}
	}

	@Override
	public int getCount() {
		return recordList.size();
	}

	@Override
	public Object getItem(int position) {
		return recordList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		Record record = (Record) getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.appraise_cell, parent, false);
			holder.userNameTV = (TextView) convertView.findViewById(R.id.appraise_name);
			holder.credibilityBar = (RatingBar) convertView.findViewById(R.id.appraise_credibility);
			holder.credibilityDescTV = (TextView) convertView.findViewById(R.id.appraise_credibility_desc);
			holder.dutyContentTV = (TextView) convertView.findViewById(R.id.appraise_duty);
			holder.photoNIV = (NetworkImageView) convertView.findViewById(R.id.appraise_photo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String imageURL = ApiClient.formImageURL(URLs.REQUEST_PHOTO_IMAGE, record.getUser().getPhotoName());
		holder.photoNIV.setImageUrl(imageURL, imageLoader);
		holder.userNameTV.setText(record.getUser().getName());
		holder.dutyContentTV.setText(record.getDutyName());
		holder.credibilityBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {

			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				// holder.credibilityDescTV.setText(rating + "分");
				evaluates.get(position).setCredibility(rating);
			}
		});
		// Exist problem,have to hide it
		holder.credibilityDescTV.setVisibility(View.INVISIBLE);
		return convertView;
	}

	class ViewHolder {
		NetworkImageView photoNIV;
		TextView userNameTV;
		TextView dutyContentTV;
		RatingBar credibilityBar;
		TextView credibilityDescTV;
	}
}
