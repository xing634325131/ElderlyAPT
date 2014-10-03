package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.GridViewServiceAdapter;
import com.kingtime.elderlyapt.adapter.ListViewGeneralActivityAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.ServiceCell;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.service.MessagePushService;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.FileUtils;
import com.kingtime.elderlyapt.util.StreamTool;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.elderlyapt.widget.OnViewChangeListener;
import com.kingtime.elderlyapt.widget.ScrollLayout;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends Activity implements OnViewChangeListener, OnClickListener {

	// ��������͵ײ�ģ��
	private ScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private TextView serviceTV;
	private TextView recommendTV;
	private TextView searchTV;
	private TextView myTV;

	// �˳�ģ��
	private static boolean isExit = false;
	private static Timer tExit = null;

	// 1.Service
	private List<ServiceCell> serviceCells = new ArrayList<ServiceCell>();
	private GridView serviceGV;
	private Button pushActivityBtn;
	private Button messageCenterBtn;

	// 2.Recommend
	private ListView recommendLV;
	private ListViewGeneralActivityAdapter activityAdapter;
	private List<MyActivity> myActivities;

	// 3.Search
	private EditText searchET;
	private Button searchBtn;

	// 4.My
	private LinearLayout myinfoLayout;
	private LinearLayout myfriendLayout;
	private LinearLayout myfavoriesLayout;
	private LinearLayout publishedLayout;
	private LinearLayout joinedLayout;
	private LinearLayout endedLayout;
	private LinearLayout mycoinesLayout;
	private Button settingBtn;
	private LinearLayout verifyLayout;

	// LoadingDialog
	// private LoadingDialog loadingDialog;
	private User nowUser;

	// ������Ϣ��¼����̨�ռ�����
	private String coinsString;
	private String activityString;
	private String evaluateString;
	private static int times;// ����������һ���������Ϣ���ڶ������û�и������ü�����Ϣ
	private int activityNum, evaluateNum, coinsNum;// ����Ϣ��������
	private MessagePushService pushService;

	private static final int REFRESH_TIME = 10 * 1000;
	private Timer mainTimer;
	private TimerTask messageTask;

	// ������Ϣ����
	private NotificationManager nManager;
	private Notification notification;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initMainLayout();
		initServiceLayout();
		initRecommendLayout();
		initSearchLayout();
		initMyLayout();
		initData();
		initMessagePush();
	}

	private void initMainLayout() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.lllayout);
		serviceTV = (TextView) findViewById(R.id.main_service);
		recommendTV = (TextView) findViewById(R.id.main_recommend);
		searchTV = (TextView) findViewById(R.id.main_search);
		myTV = (TextView) findViewById(R.id.main_my);

		mViewCount = mScrollLayout.getChildCount();
		mImageViews = new LinearLayout[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);

		// loadingDialog = new LoadingDialog(this);
		AppContext appContext = (AppContext) getApplication();
		nowUser = appContext.getLoginInfo();
	}

	private void initServiceLayout() {
		serviceGV = (GridView) findViewById(R.id.service_all);
		for (int i = 0; i < GridViewServiceAdapter.getCellLength(); i++) {
			serviceCells.add(new ServiceCell(BitmapFactory.decodeResource(getResources(),
					GridViewServiceAdapter.getServiceImages(i)), GridViewServiceAdapter.getServiceDesc(i)));
			Log.i("main-serviceDesc", GridViewServiceAdapter.getServiceDesc(i));
		}
		GridViewServiceAdapter serviceAdapter = new GridViewServiceAdapter(Main.this, serviceCells);
		serviceGV.setAdapter(serviceAdapter);
		serviceGV.setOnItemClickListener(serviceListener);
		pushActivityBtn = (Button) findViewById(R.id.service_push_activity);
		messageCenterBtn = (Button) findViewById(R.id.service_message_center);
		pushActivityBtn.setOnClickListener(listener);
		messageCenterBtn.setOnClickListener(listener);
	}

	private void initRecommendLayout() {
		recommendLV = (ListView) findViewById(R.id.recommend_list);
	}

	private void initSearchLayout() {
		searchET = (EditText) findViewById(R.id.search_editer);
		searchBtn = (Button) findViewById(R.id.search_btn);

		searchBtn.setOnClickListener(listener);
	}

	private void initMyLayout() {
		myinfoLayout = (LinearLayout) findViewById(R.id.my_info);
		mycoinesLayout = (LinearLayout) findViewById(R.id.my_coins);
		myfavoriesLayout = (LinearLayout) findViewById(R.id.my_favorites);
		myfriendLayout = (LinearLayout) findViewById(R.id.my_friend);
		publishedLayout = (LinearLayout) findViewById(R.id.my_published);
		joinedLayout = (LinearLayout) findViewById(R.id.my_joined);
		endedLayout = (LinearLayout) findViewById(R.id.my_ended);
		settingBtn = (Button) findViewById(R.id.my_setting);
		verifyLayout = (LinearLayout) findViewById(R.id.my_verify);

		mycoinesLayout.setOnClickListener(listener);
		myfavoriesLayout.setOnClickListener(listener);
		myfriendLayout.setOnClickListener(listener);
		myinfoLayout.setOnClickListener(listener);
		publishedLayout.setOnClickListener(listener);
		joinedLayout.setOnClickListener(listener);
		endedLayout.setOnClickListener(listener);
		settingBtn.setOnClickListener(listener);
		if (nowUser.getRoleId() == User.MANAGE_USER) {// ����Ա������˻
			verifyLayout.setVisibility(View.VISIBLE);
		}
		verifyLayout.setOnClickListener(listener);
	}

	private void initData() {
		try {
			myActivities = MyActivity.parse(FileUtils.read(this, "recommend"));
			activityAdapter = new ListViewGeneralActivityAdapter(getApplication(), myActivities);
			recommendLV.setAdapter(activityAdapter);
			recommendLV.setOnItemClickListener(recommendListener);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initMessagePush() {
		times = 0;
		mainTimer = new Timer();

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					sendToUI();
				}
			}
		};
		messageTask = new TimerTask() {

			@Override
			public void run() {
				Message msg = new Message();
				try {
					if (nowUser != null) {
						InputStream activityInputStream = ApiClient.getMessageStreamByCategory(nowUser.getUid(),
								"requestByActivity");
						InputStream evaluateInputStream = ApiClient.getMessageStreamByCategory(nowUser.getUid(),
								"requestByEvaluate");
						InputStream coinsInputStream = ApiClient.getMessageStreamByCategory(nowUser.getUid(), "requestByCoins");
						byte[] data1 = null;
						byte[] data2 = null;
						byte[] data3 = null;
						try {
							data1 = StreamTool.read(activityInputStream);
							data2 = StreamTool.read(evaluateInputStream);
							data3 = StreamTool.read(coinsInputStream);
						} catch (Exception e) {
							e.printStackTrace();
						}
						activityString = new String(data1);
						evaluateString = new String(data2);
						coinsString = new String(data3);
						pushService = new MessagePushService(activityString, evaluateString, coinsString);
						activityNum = pushService.checkActivityMessage();
						evaluateNum = pushService.checkEvaluateMessage();
						coinsNum = pushService.checkCoinsMessage();

						if (times == 0 || activityNum > 0 || evaluateNum > 0 || coinsNum > 0) {// ��Ϣ�и���ʱ�����±����ļ�
							times++;// �������ۼ�

							// ����������Ϣ
							FileUtils.write(Main.this, "activityMessage", activityString);
							FileUtils.write(Main.this, "evaluateMessage", evaluateString);
							FileUtils.write(Main.this, "coinsMessage", coinsString);
						}
					}
					msg.what = 1;
				} catch (IOException e) {
					System.out.println(e.toString());
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		};

		mainTimer.scheduleAtFixedRate(messageTask, 0, REFRESH_TIME);
	}

	/**
	 * ��ʾ������Ϣ������
	 */
	private void sendToUI() {
		nManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		if (activityNum > 0) {
			notification = new Notification();
			notification.when = System.currentTimeMillis();
			notification.icon = R.drawable.logo;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.tickerText = "�����µĻ֪ͨ��";
			notification.defaults = Notification.DEFAULT_SOUND;
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(Main.this, MessageCenter.class), 0);
			if (activityNum == 1) {// ���͵�����Ϣ
				notification.setLatestEventInfo(this, "���Ϣ", pushService.getSingleMessage(MessagePushService.ACTIVITY_CATEGORY)
						.getPushContent(), pendingIntent);
			} else {// ������Ϣ�ϲ�����
				notification.setLatestEventInfo(this, "�֪ͨ", "���ж������Ϣ����ȥ�����ɣ�", pendingIntent);
			}
			nManager.notify(times, notification);
		}

		if (evaluateNum > 0) {
			notification = new Notification();
			notification.when = System.currentTimeMillis();
			notification.icon = R.drawable.logo;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.tickerText = "�����µ�������Ϣ��";
			notification.defaults = Notification.DEFAULT_SOUND;
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(Main.this, MessageCenter.class), 0);
			if (evaluateNum == 1) {
				notification.setLatestEventInfo(this, "����֪ͨ", pushService.getSingleMessage(MessagePushService.ACTIVITY_CATEGORY)
						.getPushContent(), pendingIntent);
			} else {
				notification.setLatestEventInfo(this, "����֪ͨ", "������������Ļ����ȥ�����ɣ�", pendingIntent);
			}
			nManager.notify(times * 2, notification);
		}

		if (coinsNum > 0) {
			notification = new Notification();
			notification.when = System.currentTimeMillis();
			notification.icon = R.drawable.logo;
			notification.flags = Notification.FLAG_AUTO_CANCEL;
			notification.tickerText = "���ʱ��ҷ����仯����";
			notification.defaults = Notification.DEFAULT_SOUND;
			PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(Main.this, MessageCenter.class), 0);
			if (coinsNum == 1) {
				notification.setLatestEventInfo(this, "ʱ���֪ͨ", pushService.getSingleMessage(MessagePushService.ACTIVITY_CATEGORY)
						.getPushContent(), pendingIntent);
			} else {
				notification.setLatestEventInfo(this, "ʱ���֪ͨ", "���ʱ��ұ仯�ˣ���ȥ�����ɣ�", pendingIntent);
			}
			nManager.notify(times * 3, notification);
		}
	}

	private OnItemClickListener serviceListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(Main.this, ActivityList.class);
			Bundle bundle = new Bundle();
			bundle.putString("requestName", "requestById");
			bundle.putString("titleName", MyActivity.CATEGORY[position]);
			bundle.putInt("categoryId", position);
			Log.i("Main-ActivityList categoryID", String.valueOf(position));
			intent.putExtras(bundle);
			startActivity(intent);
		}

	};

	private OnItemClickListener recommendListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(Main.this, ActivityDetails.class);
			Bundle bundle = new Bundle();
			bundle.putInt("activityId", myActivities.get(position).getActivityId());
			Log.i("Main-ActivityDetail activityID", String.valueOf(myActivities.get(position).getActivityId()));
			intent.putExtras(bundle);
			startActivity(intent);
		}

	};

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// ��ȡ��ǰ�û�ID��Ϣ
			Bundle userBundle = new Bundle();
			Intent activityListIntent = new Intent(Main.this, ActivityList.class);
			userBundle.putInt("uid", nowUser.getUid());

			// ��ť����¼�����
			switch (v.getId()) {
			case R.id.service_push_activity:
				startActivity(new Intent(Main.this, PushActivity.class));
				break;
			case R.id.service_message_center:
				startActivity(new Intent(Main.this, MessageCenter.class));
				break;
			case R.id.my_coins:
				startActivity(new Intent(Main.this, MyCoins.class));
				break;
			case R.id.my_info:
				startActivity(new Intent(Main.this, MyInfo.class));
				break;
			case R.id.my_friend:
				DialogTool.createMessageDialog(Main.this, "����", "�������ڿ����У���ȴ���", "ȷ��", null, DialogTool.NO_ICON).show();
				break;
			case R.id.my_favorites:
				DialogTool.createMessageDialog(Main.this, "����", "�������ڿ����У���ȴ���", "ȷ��", null, DialogTool.NO_ICON).show();
				break;
			case R.id.my_ended:
				Bundle endedBundle = new Bundle();
				endedBundle.putString("titleName", "�ѽ����");
				endedBundle.putString("requestName", "requestEnded");
				activityListIntent.putExtras(endedBundle);
				startActivity(activityListIntent);
				break;
			case R.id.my_joined:
				Bundle joinBundle = new Bundle();
				joinBundle.putString("titleName", "���μӵĻ");
				joinBundle.putString("requestName", "requestJoined");
				activityListIntent.putExtras(joinBundle);
				startActivity(activityListIntent);
				break;
			case R.id.my_published:
				Bundle publishBundle = new Bundle();
				publishBundle.putString("titleName", "�ѷ����");
				publishBundle.putString("requestName", "requestPublished");
				activityListIntent.putExtras(publishBundle);
				startActivity(activityListIntent);
				break;
			case R.id.my_setting:
				startActivity(new Intent(Main.this, Setting.class));
				break;
			case R.id.search_btn:
				startSearchForActivity();
				break;
			case R.id.my_verify:
				// System.out.println("to---->VerifyActivityList");
				Bundle verifyBundle = new Bundle();
				verifyBundle.putString("requestName", "requestVerify");
				verifyBundle.putString("titleName", "����˻");
				activityListIntent.putExtras(verifyBundle);
				startActivity(activityListIntent);
				break;
			default:
				break;
			}

		}
	};

	private void startSearchForActivity() {
		String searchString = searchET.getText().toString();
		if (StringUtils.isEmpty(searchString)) {
			DialogTool.createMessageDialog(this, "��ʾ", "��������������", "ȷ��", null, DialogTool.NO_ICON).show();
			searchET.setText("");
			return;
		}

		// InputMethodManager imm =
		// (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		// imm.hideSoftInputFromInputMethod(searchET.getWindowToken(), 0);
		searchET.clearFocus();
		// searchBtn.setVisibility(View.GONE);
		// searchBar.setVisibility(View.VISIBLE);
		Intent searchIntent = new Intent(Main.this, Search.class);
		Bundle searchBundle = new Bundle();
		searchBundle.putString("searchString", searchString);
		searchIntent.putExtras(searchBundle);
		startActivityForResult(searchIntent, 0x01);
	}

	@Override
	public void onClick(View v) {
		int pos = (Integer) (v.getTag());
		setCurPoint(pos);
		mScrollLayout.snapToScreen(pos);
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			serviceTV.setTextColor(0xff228B22);
			searchTV.setTextColor(Color.BLACK);
			myTV.setTextColor(Color.BLACK);
			recommendTV.setTextColor(Color.BLACK);
		} else if (index == 1) {
			recommendTV.setTextColor(0xff228B22);
			searchTV.setTextColor(Color.BLACK);
			myTV.setTextColor(Color.BLACK);
			serviceTV.setTextColor(Color.BLACK);
		} else if (index == 2) {
			searchTV.setTextColor(0xff228B22);
			serviceTV.setTextColor(Color.BLACK);
			myTV.setTextColor(Color.BLACK);
			recommendTV.setTextColor(Color.BLACK);
		} else {
			myTV.setTextColor(0xff228B22);
			searchTV.setTextColor(Color.BLACK);
			serviceTV.setTextColor(Color.BLACK);
			recommendTV.setTextColor(Color.BLACK);
		}
	}

	@Override
	public void OnViewChange(int view) {
		setCurPoint(view);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_MENU)) {
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK) {// �����η��ؼ��˳�����
			if (isExit == false) {
				isExit = true;
				if (tExit != null) {
					tExit.cancel();
				}
				tExit = new Timer();
				TimerTask task = new TimerTask() {

					@Override
					public void run() {
						isExit = false;
					}
				};
				Toast.makeText(getApplicationContext(), R.string.exit_twice, Toast.LENGTH_SHORT).show();
				tExit.schedule(task, 2000);
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 0x01:
			searchET.setText("");
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		mainTimer.cancel();
		super.onDestroy();
	}

}
