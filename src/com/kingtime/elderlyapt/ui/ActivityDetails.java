package com.kingtime.elderlyapt.ui;

import java.io.IOException;

import org.json.JSONException;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;
import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.api.URLs;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.Record;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.BaiduSpeech;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.UIHelper;
import com.kingtime.elderlyapt.widget.LoadingDialog;
import com.kingtime.elderlyapt.widget.LruImageCache;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityDetails extends BaseActivity implements SpeechSynthesizerListener {

	private TextView titleTV;
	private Button backBtn;
	private Button rightBtn;
	private NetworkImageView detailImageIV;
	private TextView detailStateTV;
	private TextView detailNameTV;
	private TextView detailStartTimeTV;
	private TextView detailEndTimeTV;
	private TextView detailCloseTimeTV;
	private TextView detailAddressTV;
	private TextView detailChatTV;
	private TextView detailPostUserTV;
	private TextView detailContentTV;
	private TextView detailContactTV;
	private TextView detailCategoryTV;
	private TextView detailNeedNumTV;
	private TextView detailNowJoinTV;
	private LinearLayout phoneLinearLayout;
	private LinearLayout postUserLinearLayout;
	private LinearLayout dutyLinearLayout;
	private LinearLayout joinLinearLayout;
	private FrameLayout mylayoutLayout;
	private ImageView playOutIV;

	// 加载对话框
	private LoadingDialog loadingDialog;

	// 网络图片获取
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	// 数据
	private int activityId;
	private MyActivity nowActivity;
	private User postUser;
	private User nowUser;
	private AppContext appContext;

	// 自定义的弹出框类
	MenuWindow menuWindow; // 弹出框

	// 语音合成
	private SpeechSynthesizer newspeechSynthesizer;
	private Handler uiHandler;
	private static final int SHOW_DIALOG = 0;
	private static final int HIDE_DIALOG = 1;
	private static final int EXIST_ERROR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.header);
		Log.i("ActivityDetailes", "Start");

		preInit();
		initData();
	}

	private void preInit() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		activityId = bundle.getInt("activityId");
		System.out.println(activityId);

		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV.setText(R.string.activity_details);
		backBtn.setOnClickListener(listener);
		appContext = (AppContext) getApplication();
		nowActivity = new MyActivity();
		postUser = new User();
		loadingDialog = new LoadingDialog(this);
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
				if (msg.what == 1) {
					initLayout();
					detailAddressTV.setText(getResources().getString(R.string.set_address) + nowActivity.getAddress());
					// System.err.println(nowActivity.getAddress());
					// System.out.println("nowActivity.getCategoryId():" +
					// nowActivity.getCategoryId());
					detailCategoryTV.setText(nowActivity.getCategory());
					detailCloseTimeTV.setText(getResources().getString(R.string.close_time) + nowActivity.getCloseTime());
					detailContactTV.setText(postUser.getPhone());
					detailContentTV.setText(nowActivity.getContent());
					detailEndTimeTV.setText(getResources().getString(R.string.end_time) + nowActivity.getEndTime());
					detailNameTV.setText(nowActivity.getPostName());
					detailNeedNumTV.setText(String.valueOf(nowActivity.getNeedNum()) + "人");
					detailPostUserTV.setText(postUser.getName());
					detailStartTimeTV.setText(getResources().getString(R.string.begin_time) + nowActivity.getBeginTime());
					detailStateTV.setText(nowActivity.getState());

					String imageURL = ApiClient.formImageURL(URLs.REQUEST_COMMON_IMAGE, nowActivity.getMainPic());
					detailImageIV.setImageUrl(imageURL, imageLoader);
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("获取活动详情...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					nowActivity = ApiClient.getActivity(activityId);
					postUser = ApiClient.getUserByActivityId(activityId);
					msg.what = 1;
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
		}.start();
	}

	private void initLayout() {
		setContentView(R.layout.activity_details);

		nowUser = appContext.getLoginInfo();
		rQueue = Volley.newRequestQueue(this);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
		menuWindow = new MenuWindow(ActivityDetails.this, itemsOnClick, nowActivity);

		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		rightBtn = (Button) findViewById(R.id.head_right);
		detailAddressTV = (TextView) findViewById(R.id.detail_address);
		detailCategoryTV = (TextView) findViewById(R.id.detail_category);
		detailChatTV = (TextView) findViewById(R.id.detail_chat);
		detailCloseTimeTV = (TextView) findViewById(R.id.detail_close_time);
		detailContactTV = (TextView) findViewById(R.id.detail_contact);
		detailContentTV = (TextView) findViewById(R.id.detail_content);
		detailEndTimeTV = (TextView) findViewById(R.id.detail_end_time);
		detailImageIV = (NetworkImageView) findViewById(R.id.detail_image);
		detailNameTV = (TextView) findViewById(R.id.detail_name);
		detailNeedNumTV = (TextView) findViewById(R.id.detail_need_num);
		detailNowJoinTV = (TextView) findViewById(R.id.detail_now_join);
		detailPostUserTV = (TextView) findViewById(R.id.detail_post_user);
		detailStartTimeTV = (TextView) findViewById(R.id.detail_start_time);
		detailStateTV = (TextView) findViewById(R.id.detail_state);
		phoneLinearLayout = (LinearLayout) findViewById(R.id.detail_click_phone);
		postUserLinearLayout = (LinearLayout) findViewById(R.id.detail_click_post_user);
		dutyLinearLayout = (LinearLayout) findViewById(R.id.detail_click_duty);
		joinLinearLayout = (LinearLayout) findViewById(R.id.detail_click_join);
		mylayoutLayout = (FrameLayout) findViewById(R.id.it_top);
		playOutIV = (ImageView) findViewById(R.id.push_play_out);

		titleTV.setText("活动详情");
		if (nowUser.getUid() == postUser.getUid()) {// 创建者显示菜单
			rightBtn.setText("");
			rightBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_menu));
			LayoutParams params = rightBtn.getLayoutParams();
			params.height = 50;
			params.width = 70;
			rightBtn.setOnClickListener(listener);
			rightBtn.setLayoutParams(params);
			rightBtn.setVisibility(View.VISIBLE);
		}
		if (nowActivity.getStateId() == MyActivity.ACTIVITY_END) {
			detailNowJoinTV.setText(R.string.evaluate_now);
			Drawable drawable = getResources().getDrawable(R.drawable.ic_comment);
			drawable.setBounds(1, 1, 24, 24);
			detailNowJoinTV.setCompoundDrawables(drawable, null, null, null);
		}
		backBtn.setOnClickListener(listener);
		detailChatTV.setOnClickListener(listener);
		detailNowJoinTV.setOnClickListener(listener);
		joinLinearLayout.setOnClickListener(listener);
		postUserLinearLayout.setOnClickListener(listener);
		phoneLinearLayout.setOnClickListener(listener);
		dutyLinearLayout.setOnClickListener(listener);
		playOutIV.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				setResult(RESULT_OK);
				finish();
				break;
			case R.id.detail_click_phone:
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + postUser.getPhone())));
				Log.i("MakePhone", String.valueOf(postUser.getPhone()));
				break;
			case R.id.detail_chat:
				Intent chatIntent = new Intent(ActivityDetails.this, Chat.class);
				Bundle chatBundle = new Bundle();
				chatBundle.putInt("activityId", nowActivity.getActivityId());
				chatIntent.putExtras(chatBundle);
				startActivity(chatIntent);
//				DialogTool.createMessageDialog(ActivityDetails.this, getResources().getString(R.string.hint),
//						getResources().getString(R.string.function_not_open), getResources().getString(R.string.sure), null,
//						DialogTool.NO_ICON).show();
				break;
			case R.id.detail_click_post_user:
				Intent userIntent = new Intent(ActivityDetails.this, JoinUserInfoDetails.class);
				Bundle userBundle = new Bundle();
				userBundle.putInt("uid", postUser.getUid());
				userIntent.putExtras(userBundle);
				startActivity(userIntent);
				break;
			case R.id.detail_click_duty:
				Intent dutyIntent = new Intent(ActivityDetails.this, DutyList.class);
				Bundle dutyBundle = new Bundle();
				dutyBundle.putInt("activityId", nowActivity.getActivityId());
				dutyIntent.putExtras(dutyBundle);
				startActivity(dutyIntent);
				break;
			case R.id.detail_click_join:
				Intent joinIntent = new Intent(ActivityDetails.this, JoinUserList.class);
				Bundle joinBundle = new Bundle();
				joinBundle.putInt("activityId", nowActivity.getActivityId());
				joinBundle.putInt("postUserId", nowActivity.getPostUserId());
				joinIntent.putExtras(joinBundle);
				startActivity(joinIntent);
				break;
			case R.id.detail_now_join:
				if (nowActivity.getStateId() == MyActivity.ACTIVITY_END) {
					if (nowUser.getUid() == postUser.getUid()) {
						checkAppraise();
					} else {
						checkEvaluated();
					}
				} else if (checkState() && checkNowNum()) {
					checkedRecord();
				}
				break;
			case R.id.head_right:
				showMenu(ActivityDetails.this);
				break;
			case R.id.push_play_out:// 文字转语音
				voicePlayOut();
				break;
			default:
				break;
			}

		}
	};

	public void showMenu(final Activity context) {

		System.out.println("height-->" + mylayoutLayout.getHeight() + "ststus-->" + UIHelper.getStatusHeight(this));
		// 显示窗口
		menuWindow.showAtLocation(ActivityDetails.this.findViewById(R.id.head_right), Gravity.TOP | Gravity.RIGHT, 0,
				mylayoutLayout.getHeight() + UIHelper.getStatusHeight(this) + 1); // 设置layout在PopupWindow中显示的位置
	}

	/**
	 * 文字转语音
	 */
	protected void voicePlayOut() {
		uiHandler = new Handler(getMainLooper()) {// 界面控制器

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
					Toast.makeText(ActivityDetails.this, "发生错误", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}
		};

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		newspeechSynthesizer = BaiduSpeech.getSpeechSynthesizer(getApplicationContext(), this);
		new Thread(new Runnable() {

			@Override
			public void run() {
				System.out.println("Speak :" + detailContentTV.getText().toString());
				newspeechSynthesizer.speak(detailContentTV.getText().toString());
			}

		}).start();
	}

	// 为弹出窗口实现监听类
	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			menuWindow.dismiss();
		}
	};

	/**
	 * 侦测活动当前状态是否可以参与
	 * 
	 * @return
	 */
	protected boolean checkState() {
		if (nowActivity.getStateId() == MyActivity.ACTIVITY_APPLY) {
			return true;
		} else {
			DialogTool.createConfirmDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.cant_join_to_create), getResources().getString(R.string.sure),
					getResources().getString(R.string.create_activity), null, createBtnListener, DialogTool.NO_ICON).show();
			return false;
		}
	}

	/**
	 * 侦测活动当前人数是否可以参与
	 * 
	 * @return
	 */
	protected boolean checkNowNum() {
		System.out.println("Num:" + nowActivity.getNeedNum() + "---" + nowActivity.getNowNum());
		if (nowActivity.getNeedNum() > nowActivity.getNowNum()) {
			return true;
		} else {
			DialogTool.createConfirmDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.fill_up_to_create), getResources().getString(R.string.sure),
					getResources().getString(R.string.create_activity), null, createBtnListener, DialogTool.NO_ICON).show();
			return false;
		}
	}

	private DialogInterface.OnClickListener createBtnListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			startActivity(new Intent(ActivityDetails.this, PushActivity.class));
			finish();
		}
	};

	/**
	 * 检测参与用户是否评论过该活动
	 */
	protected void checkEvaluated() {
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
				if (msg.what == 1) {
					toEvaluate((Integer) msg.obj);
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("请稍候...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					User user = appContext.getLoginInfo();
					int stateId = ApiClient.getUserJoinStateId(user.getUid(), activityId);
					msg.obj = stateId;
					msg.what = 1;
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	/**
	 * 检测创建者是否评论过该活动参与者
	 */
	private void checkAppraise() {
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
				if (msg.what == 1) {
					boolean state = (Boolean) msg.obj;
					if (state == true) {
						DialogTool.createMessageDialog(ActivityDetails.this, getResources().getString(R.string.hint),
								getResources().getString(R.string.have_evaluated), getResources().getString(R.string.sure), null,
								DialogTool.NO_ICON).show();
					} else {
						Intent appraiseIntent = new Intent(ActivityDetails.this, Appraise.class);
						Bundle appraiseBundle = new Bundle();
						appraiseBundle.putInt("activityId", nowActivity.getActivityId());
						appraiseIntent.putExtras(appraiseBundle);
						startActivity(appraiseIntent);
					}
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("请稍候...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					boolean state = ApiClient.getAppraiseState(activityId);
					msg.obj = state;
					msg.what = 1;
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private void toEvaluate(int stateId) {
		if (stateId == Record.RECORD_NOT_EVALUATED)// 为评论过，去评论
		{
			Intent evaluateIntent = new Intent(ActivityDetails.this, EvaluateActivity.class);
			Bundle evaluateBundle = new Bundle();
			evaluateBundle.putInt("activityId", activityId);
			evaluateIntent.putExtras(evaluateBundle);
			startActivityForResult(evaluateIntent, 0x01);
		} else if (stateId == Record.RECORD_EVALUATED) {
			DialogTool.createMessageDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.have_joined_evaluate), getResources().getString(R.string.sure), null,
					DialogTool.NO_ICON).show();
		} else {
			DialogTool.createMessageDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.no_premission_to_evaluate), getResources().getString(R.string.sure), null,
					DialogTool.NO_ICON).show();
		}
	}

	/**
	 * 检测是否申请加入过该活动
	 */
	protected void checkedRecord() {
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
				if (msg.what == 1) {
					joinHandle((Integer) msg.obj);
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("申请加入中...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					User user = appContext.getLoginInfo();
					int stateId = ApiClient.getUserJoinStateId(user.getUid(), activityId);
					msg.obj = stateId;
					msg.what = 1;
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private void joinHandle(int stateId) {
		AppContext appContext = (AppContext) getApplication();
		User user = appContext.getLoginInfo();
		if (user.getUid() == postUser.getUid()) {// 判断加入者是否为活动发起人
			DialogTool.createMessageDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.postuser_have_joined), getResources().getString(R.string.sure), null,
					DialogTool.NO_ICON).show();
			return;
		}

		switch (stateId) {
		case Record.RECORD_CANCLED:
			DialogTool.createMessageDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.cancel_joined), getResources().getString(R.string.sure), null,
					DialogTool.NO_ICON).show();
			break;
		case Record.RECORD_COMMON:
			DialogTool.createMessageDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.have_joined), getResources().getString(R.string.sure), null,
					DialogTool.NO_ICON).show();
			break;
		case Record.RECORD_NOT_JOIN:
			Toast.makeText(getApplication(), R.string.select_duty, Toast.LENGTH_SHORT).show();
			Intent joinIntent = new Intent(ActivityDetails.this, SelectDutyJoin.class);
			Bundle bundle = new Bundle();
			bundle.putInt("activityId", activityId);
			joinIntent.putExtras(bundle);
			startActivity(joinIntent);
			break;
		default:
			DialogTool.createMessageDialog(this, getResources().getString(R.string.hint),
					getResources().getString(R.string.cant_join), getResources().getString(R.string.sure), null,
					DialogTool.NO_ICON).show();
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case 0x01:
			onCreate(null);// 刷新
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			setResult(RESULT_OK);
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onBufferProgressChanged(SpeechSynthesizer arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCancel(SpeechSynthesizer arg0) {
		// TODO Auto-generated method stub

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
