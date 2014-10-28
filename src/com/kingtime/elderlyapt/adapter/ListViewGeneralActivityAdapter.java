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
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.widget.LruImageCache;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月6日
 */
public class ListViewGeneralActivityAdapter extends BaseAdapter {

	private Context context;
	private List<MyActivity> activities = new ArrayList<MyActivity>();
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	public ListViewGeneralActivityAdapter(Context myContext,
			List<MyActivity> myActivities) {
		System.out.println("start");
		this.context = myContext;
		this.activities = myActivities;
	}

	@Override
	public int getCount() {
		return activities.size();
	}

	@Override
	public Object getItem(int position) {
		return activities.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View view, ViewGroup parent) {
		Log.i("Adapter-position", String.valueOf(position));
		MyActivity activity = activities.get(position);
		Show show = null;
		if (view == null) {
			show = new Show();
			view = LayoutInflater.from(context).inflate(
					R.layout.recommend_cell, parent, false);
			show.picIV = (NetworkImageView) view
					.findViewById(R.id.recommend_image);
			show.titleTV = (TextView) view.findViewById(R.id.recomment_name);
			show.dateTV = (TextView) view.findViewById(R.id.recommend_time);
			show.stateTV = (TextView) view.findViewById(R.id.recommend_state);
			show.nowNumTV = (TextView) view
					.findViewById(R.id.recommend_now_num);
			view.setTag(show);
		} else {
			show = (Show) view.getTag();
		}

		show.titleTV.setText(activity.getPostName());
		show.dateTV.setText(activity.getSustained());
		show.nowNumTV.setText(activity.getNowNum() + "人已参与");
		show.stateTV.setText(activity.getState());
		//暂时有问题，去掉颜色变化
//		System.out.println("stateid::::1---"+activity.getStateId());
//		if (activity.getStateId() == MyActivity.ACTIVITY_END) {
//			System.out.println("stateid::::2---"+activity.getStateId());
//			show.stateTV.setBackgroundResource(R.drawable.bg_end_radius);
//		}
		if (activity.getStateId() == MyActivity.ACTIVITY_REVIEW) {
			show.nowNumTV.setVisibility(View.INVISIBLE);
		}
		System.out.println(activity.getContent() + "," + activity.getSustained() + "," + activity.getStateId());

		show.picIV.setDefaultImageResId(R.drawable.activity_default_big);
		show.picIV.setErrorImageResId(R.drawable.activity_default_big);
		rQueue = Volley.newRequestQueue(context);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
		String imageURL = ApiClient.formImageURL(URLs.REQUEST_COMMON_IMAGE, activity.getMainPic());
		System.out.println(imageURL);
		show.picIV.setImageUrl(imageURL, imageLoader);

		return view;
	}

	class Show {
		NetworkImageView picIV;// 活动图片
		TextView titleTV;// 活动主题
		TextView stateTV;// 活动状态
		TextView dateTV;// 活动日期
		TextView nowNumTV;// 活动当前参与人数
	}

}
