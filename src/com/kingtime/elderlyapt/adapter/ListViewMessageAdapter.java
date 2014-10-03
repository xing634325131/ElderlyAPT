package com.kingtime.elderlyapt.adapter;

import java.util.List;

import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.entity.MessagePush;
import com.kingtime.elderlyapt.util.BaiduSpeech;
import com.kingtime.elderlyapt.widget.LoadingDialog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ListViewMessageAdapter extends BaseAdapter implements SpeechSynthesizerListener {

	private Context context;
	private List<MessagePush> messagePushs;
	
	// 加载对话框
	private LoadingDialog loadingDialog;

	// 语音合成
	private SpeechSynthesizer newspeechSynthesizer;
	private Handler uiHandler;
	private static final int SHOW_DIALOG = 0;
	private static final int HIDE_DIALOG = 1;
	private static final int EXIST_ERROR = 2;

	public ListViewMessageAdapter(Context c, List<MessagePush> pushs) {
		this.context = c;
		this.messagePushs = pushs;
		loadingDialog = new LoadingDialog(context);
	}

	@Override
	public int getCount() {
		return messagePushs.size();
	}

	@Override
	public Object getItem(int position) {
		return messagePushs.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final MessagePush messagePush = (MessagePush) getItem(position);
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(R.layout.message_cell, parent, false);
			holder = new ViewHolder();
			holder.contentTV = (TextView) convertView.findViewById(R.id.msg_cell_content);
			holder.infoIV = (ImageView) convertView.findViewById(R.id.msg_cell_info);
			holder.newInfoIV = (ImageView) convertView.findViewById(R.id.msg_cell_new);
			holder.timeTV = (TextView) convertView.findViewById(R.id.msg_cell_time);
			holder.voiceTV = (ImageView) convertView.findViewById(R.id.msg_play_out);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.contentTV.setText(messagePush.getPushContent());
		holder.extraInfo = messagePush.getRemark();

		switch (messagePush.getPushCategoryId()) {
		case MessagePush.CATEGORY_JOIN:
		case MessagePush.CATEGORY_STATE:
		case MessagePush.CATEGORY_USER:
		case MessagePush.CATEGORY_VERIFY:
			holder.infoIV.setImageResource(R.drawable.message_activity);
			break;
		case MessagePush.CATEGORY_CREDIBILITY:
			holder.infoIV.setImageResource(R.drawable.message_evaluate);
			break;
		case MessagePush.CATEGORY_INTEGRAL:
			holder.infoIV.setImageResource(R.drawable.message_coins);
			break;
		default:
			break;
		}
		if (messagePush.getIsPushed() == 1) {
			holder.newInfoIV.setVisibility(View.INVISIBLE);
		} else {
			holder.newInfoIV.setVisibility(View.VISIBLE);
		}
		holder.timeTV.setText(messagePush.getCreateTime().substring(0, messagePush.getCreateTime().length() - 2));
		holder.voiceTV.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				uiHandler = new Handler(context.getMainLooper()) {// 界面控制器

					@Override
					public void handleMessage(Message msg) {
						switch (msg.what) {
						case SHOW_DIALOG:
							loadingDialog.setLoadText(((String) msg.obj));
							loadingDialog.show();
							break;
						case HIDE_DIALOG:
							loadingDialog.dismiss();
							break;
						case EXIST_ERROR:
							Toast.makeText(context, "发生错误", Toast.LENGTH_SHORT).show();
							break;
						default:
							break;
						}
					}
				};

				newspeechSynthesizer = BaiduSpeech.getSpeechSynthesizer(context, ListViewMessageAdapter.this);
				new Thread(new Runnable() {

					@Override
					public void run() {
						System.out.println("Speak :" + messagePush.getPushContent());
						newspeechSynthesizer.speak( messagePush.getPushContent());
					}

				}).start();
			}
		});

		return convertView;
	}

	class ViewHolder {
		ImageView infoIV;
		TextView contentTV;
		String extraInfo;
		ImageView newInfoIV;
		TextView timeTV;
		ImageView voiceTV;
	}

	@Override
	public void onBufferProgressChanged(SpeechSynthesizer arg0, int arg1) {
		
	}

	@Override
	public void onCancel(SpeechSynthesizer arg0) {
		
	}

	@Override
	public void onError(SpeechSynthesizer arg0, SpeechError arg1) {
		uiHandler.sendMessage(uiHandler.obtainMessage(EXIST_ERROR));
	}

	@Override
	public void onNewDataArrive(SpeechSynthesizer arg0, byte[] arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechFinish(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechPause(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechResume(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSpeechStart(SpeechSynthesizer arg0) {
		uiHandler.sendMessage(uiHandler.obtainMessage(HIDE_DIALOG));
	}

	@Override
	public void onStartWorking(SpeechSynthesizer arg0) {
		uiHandler.sendMessage(uiHandler.obtainMessage(SHOW_DIALOG, "请等待..."));
	}
}
