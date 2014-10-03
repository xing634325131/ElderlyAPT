package com.kingtime.elderlyapt.adapter;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.api.URLs;
import com.kingtime.elderlyapt.entity.ChatRecord;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.widget.LruImageCache;
import com.kingtime.elderlyapt.widget.MenuDialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewChatMsgAdapter extends BaseAdapter {

	private List<ChatRecord> recordLsit;
	private Context context;
	private List<User> users;
	private RequestQueue rQueue;
	private ImageLoader imageLoader;
	private MenuDialog menuDialog;
	private Handler uiHandler;
	private Context activity;

	// 语音合成
	private SpeechSynthesizer newspeechSynthesizer;

	public ListViewChatMsgAdapter(Context activity, Context context, List<ChatRecord> recordList, List<User> users) {
		this.activity = activity;
		this.recordLsit = recordList;
		this.context = context;
		this.users = users;
		rQueue = Volley.newRequestQueue(context);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
		menuDialog = new MenuDialog(activity);
	}

	@Override
	public int getCount() {
		return recordLsit.size();
	}

	@Override
	public Object getItem(int position) {
		return recordLsit.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 判断消息的类型，是其他人发过来的消息，还是自己的
	 */
	public boolean isFromOthers(int position) {
		ChatRecord record = (ChatRecord) getItem(position);
		AppContext aContext = (AppContext) context;
		User user = aContext.getLoginInfo();
		if (record.getUid() == user.getUid()) {// 自己发出的消息
			return false;
		} else {// 其他人的消息
			return true;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatRecord record = recordLsit.get(position);
		User user = null;
		for (int i = 0; i < users.size(); i++) {
			if (users.get(i).getUid() == record.getUid()) {
				user = users.get(i);
				break;
			}
		}
		boolean isComMsg = isFromOthers(position);

		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = LayoutInflater.from(context).inflate(R.layout.chat_text_left_item, null);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.chat_text_right_item, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.timeTV = (TextView) convertView.findViewById(R.id.chat_time);
			viewHolder.nameTV = (TextView) convertView.findViewById(R.id.chat_name);
			viewHolder.contentTV = (TextView) convertView.findViewById(R.id.chat_content);
			viewHolder.photoNIV = (NetworkImageView) convertView.findViewById(R.id.chat_photo);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.timeTV.setText(record.getChatTime());
		viewHolder.contentTV.setText(record.getContent());
		viewHolder.nameTV.setText(user.getName());

		viewHolder.photoNIV.setDefaultImageResId(R.drawable.default_headicon);
		viewHolder.photoNIV.setErrorImageResId(R.drawable.default_headicon);
		String imageURL = ApiClient.formImageURL(URLs.REQUEST_PHOTO_IMAGE, user.getPhotoName());
		viewHolder.photoNIV.setImageUrl(imageURL, imageLoader);

//		final OnClickListener chatCopyListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(context, "文字已复制到剪切板", Toast.LENGTH_SHORT).show();
//			}
//		};
//
//		final OnClickListener chatPlayListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				Toast.makeText(context, "文字已复制到剪切板", Toast.LENGTH_SHORT).show();
//			}
//		};

		viewHolder.contentTV.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				uiHandler = new Handler(activity.getMainLooper()) {// 界面控制器

					@Override
					public void handleMessage(Message msg) {
						menuDialog.show();
						//menuDialog.setCopyListener(chatCopyListener);
						//menuDialog.setPlayListener(chatPlayListener);
						super.handleMessage(msg);
					}
				};
				uiHandler.sendEmptyMessage(0);
				return false;
			}
		});

		return convertView;
	}

	static class ViewHolder {
		public TextView timeTV;
		public TextView nameTV;
		public TextView contentTV;
		public NetworkImageView photoNIV;
	}

}
