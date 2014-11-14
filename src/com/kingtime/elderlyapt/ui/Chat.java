package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewChatMsgAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.ChatRecord;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.elderlyapt.widget.LoadingDialog;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014年8月26日
 */
public class Chat extends Activity {
	private TextView titleTV;
	private Button backBtn;
	private Button sendBtn;
	private EditText contentET;
	private LinearLayout notiveLayout;
	private ListView chatListView;

	private int activityId;

	private ListViewChatMsgAdapter msgAdapter;
	private List<User> users;
	private List<ChatRecord> records;

	private LoadingDialog loadingDialog;
	private AppContext appContext;
	private User user;
	private ChatRecord chatRecord;
	private String content;
	private Timer refreshTimer;
	private TimerTask recordTask;
	private static final int REFRESH_DELAY = 5 * 1000;
	private static int remember_size;// 记录消息条数，是否需要更新
	private static int times;// 刷新次数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.header);

		Bundle getBundle = getIntent().getExtras();
		activityId = getBundle.getInt("activityId");
		initPreLayout();
		initData();
	}

	private void initPreLayout() {
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV.setText("活动讨论");
		backBtn.setOnClickListener(listener);

		users = new ArrayList<User>();
		records = new ArrayList<ChatRecord>();
		loadingDialog = new LoadingDialog(this);
		appContext = (AppContext) getApplication();
		user = appContext.getLoginInfo();
		refreshTimer = new Timer();
		remember_size = 0;
		times = 0;

		if (loadingDialog != null) {
			loadingDialog.setLoadText("加载数据...");
			loadingDialog.show();
		}
	}

	private void initData() {
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}
				initLayout(msg.what);
				super.handleMessage(msg);
			}
		};

		recordTask = new TimerTask() {
			@Override
			public void run() {
				Message msg = new Message();
				try {
					List<ChatRecord> chatRecords = ApiClient.getChatRecordsByActivity(activityId);
					records.clear();
					records.addAll(chatRecords);
					if (remember_size != records.size()) {
						users.clear();
						for (ChatRecord record : records) {
							users.add(ApiClient.getUserByUserId(record.getUid()));
						}
						Log.i("Chat", String.valueOf(users.size()));
						msg.what = 1;
					} else {// 没有消息更新
						msg.what = 2;
					}
					remember_size = records.size();
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				} catch (JSONException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = 0;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		};

		refreshTimer.scheduleAtFixedRate(recordTask, 0, REFRESH_DELAY);
	}

	protected void initLayout(int what) {
		if (times++ == 0) {
			setContentView(R.layout.chat);
			titleTV = (TextView) findViewById(R.id.head_title);
			backBtn = (Button) findViewById(R.id.head_back);
			chatListView = (ListView) findViewById(R.id.chat_listview);
			contentET = (EditText) findViewById(R.id.chat_editmessage);
			sendBtn = (Button) findViewById(R.id.chat_send);
			notiveLayout = (LinearLayout) findViewById(R.id.chat_notice);

			titleTV.setText("活动讨论");
			backBtn.setOnClickListener(listener);
			sendBtn.setOnClickListener(listener);
			msgAdapter = new ListViewChatMsgAdapter(Chat.this, getApplicationContext(), records, users);
			chatListView.setAdapter(msgAdapter);
		}

		switch (what) {
		case 1:// 正常情况
			if (records.size() == 0) {
				notiveLayout.setVisibility(View.VISIBLE);
			} else {
				notiveLayout.setVisibility(View.INVISIBLE);
				msgAdapter.notifyDataSetChanged();
				chatListView.setCacheColorHint(0);
			}
			break;
		case 2:// 无需更新界面
			break;
		case -1:// 服务器无响应
			Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
			break;
		case 0:// 服务器无返回数据
			notiveLayout.setVisibility(View.VISIBLE);
			break;
		default:// 未知错误
			Toast.makeText(getApplication(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
			break;
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.chat_send:
				sendChat();
				contentET.setText("");
				break;
			default:
				break;
			}
		}
	};

	protected void sendChat() {
		content = contentET.getText().toString();
		if (StringUtils.isEmpty(content)) {
			return;
		}
		chatRecord = new ChatRecord();
		chatRecord.setActivityId(activityId);
		chatRecord.setUid(user.getUid());
		chatRecord.setContent(content);

		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}
		final Handler myhandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					boolean result = (Boolean) msg.obj;
					if (result == false) {
						Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_LONG).show();
					} else {

					}
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					boolean result = ApiClient.sendChatMessage(chatRecord);
					if (result == true) {
						recordTask.run();
					}
					msg.what = 1;
					msg.obj = result;
				} catch (IOException e) {
					System.out.println(e.toString());
					msg.what = -1;
					msg.obj = e;
				}
				myhandler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		refreshTimer.cancel();
		super.onDestroy();
	}

}
