//package com.kingtime.elderlyapt.ui;
//
//import java.util.ArrayList;
//
//import com.baidu.speechsynthesizer.SpeechSynthesizer;
//import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
//import com.baidu.speechsynthesizer.player.SpeechPlayerListener;
//import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
//import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
//import com.kingtime.elderlyapt.R;
//import com.kingtime.elderlyapt.util.DialogTool;
//
//import android.app.Activity;
//import android.os.Bundle;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.Button;
//import android.widget.TextView;
//
//public class MessageCenter extends Activity {
//
//	private TextView testVoiceTV;
//	private Button testVoiceBtn;
//	private BaiduASRDigitalDialog mDialog;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		// TODO Auto-generated method stub
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.message_center);
//
//		initLayout();
//	}
//
//	private void initLayout() {
//		testVoiceTV = (TextView) findViewById(R.id.test_voice);
//		testVoiceBtn = (Button) findViewById(R.id.test_voice_start);
//
//		testVoiceBtn.setOnClickListener(listener);
//	}
//
//	private void testVoiceInput() {
//
//		DialogRecognitionListener mRecognitionListener = new DialogRecognitionListener() {
//			@Override
//			public void onResults(Bundle results) {
//				// 在Results中获取Key 为DialogRecognitionListener
//				// .RESULTS_RECOGNITION的
//				// String ArrayList，可能为空。获取到识别结果后执行相应的业务逻辑即可，此回调会在主线程调用。
//				ArrayList<String> rs = results != null ? results
//						.getStringArrayList(RESULTS_RECOGNITION) : null;
//				if (rs != null) {
//					// 此处处理识别结果，识别结果可能有多个，按置信度从高到低排列，第一个元素是置信度最高的结果。
//					testVoiceTV.setText(rs.get(0));
//				}
//			}
//		};
//
//
//		mDialog = DialogTool.createBaiduASRDigitalDialog(this, mRecognitionListener);
//		mDialog.show();
//	}
//
//	private OnClickListener listener = new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			case R.id.test_voice_start:
//				//testVoiceInput();
//				testVoiceOut();
//				break;
//			default:
//				break;
//			}
//
//		}
//	};
//
//	@Override
//	protected void onDestroy() {
//		mDialog.dismiss();
//		super.onDestroy();
//	}
//
//	protected void testVoiceOut() {
//		// 注：第二个参数当前请传入任意非空字符串即可
//		SpeechSynthesizer speechSynthesizer =  new SpeechSynthesizer(getApplicationContext(), "holder", null);
//		// 注：your-apiKey和your-secretKey需要换成在百度开发者中心注册应用得到的对应值
//		speechSynthesizer.setApiKey("kslfNHwxFVQG1GWrIOriNWvq", "P8sh8YnPqG0DSqWCpU0ck9sSqgBedzj1");
//		speechSynthesizer.speak("百度一下");
//		
//	}
//	
//	private SpeechSynthesizerListener = 
//
//}
package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewMessageAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.MessagePush;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.FileUtils;
import com.kingtime.elderlyapt.widget.LoadingDialog;
import com.kingtime.elderlyapt.widget.OnViewChangeListener;
import com.kingtime.elderlyapt.widget.ScrollLayout;

/**
 * @author xp
 * @created 2014年8月24日
 */
public class MessageCenter extends BaseActivity implements OnClickListener, OnViewChangeListener {

	// 滑动界面和顶部模块
	private ScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private TextView coinsTV;
	private TextView evaluateTV;
	private TextView activityTV;

	private ListView activityListView;
	private ListView evaluateListView;
	private ListView coinsListView;
	private LinearLayout activityLayout;
	private LinearLayout evaluateLayout;
	private LinearLayout coinsLayout;

	private Button backBtn;
	private TextView titleTV;

	private List<MessagePush> activityPushs;
	private List<MessagePush> evaluatePushs;
	private List<MessagePush> coinsPushs;
	private ListViewMessageAdapter activityMessageAdapter;
	private ListViewMessageAdapter evaluateMessageAdapter;
	private ListViewMessageAdapter coinsMessageAdapter;

	private MyActivity myActivity;
	private LoadingDialog loadingDialog;
	private AppContext appContext;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		setContentView(R.layout.message_center);

		initMainLayout();
		initData();
		initListLayout();
	}

	private void initMainLayout() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.main_scrolllayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.center_layout);
		activityTV = (TextView) findViewById(R.id.center_activity);
		evaluateTV = (TextView) findViewById(R.id.center_evaluate);
		coinsTV = (TextView) findViewById(R.id.center_coins);

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

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("消息");
		backBtn.setOnClickListener(this);
	}

	private void initData() {
		myActivity = new MyActivity();
		loadingDialog = new LoadingDialog(this);
		appContext = (AppContext) getApplication();
		user = appContext.getLoginInfo();

		activityPushs = new ArrayList<MessagePush>();
		evaluatePushs = new ArrayList<MessagePush>();
		coinsPushs = new ArrayList<MessagePush>();
		try {
			activityPushs = MessagePush.parse(FileUtils.read(this, "activityMessage"));
			evaluatePushs = MessagePush.parse(FileUtils.read(this, "evaluateMessage"));
			coinsPushs = MessagePush.parse(FileUtils.read(this, "coinsMessage"));
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void initListLayout() {
		activityListView = (ListView) findViewById(R.id.activity_message_list);
		evaluateListView = (ListView) findViewById(R.id.evaluate_message_list);
		coinsListView = (ListView) findViewById(R.id.coins_message_list);
		activityLayout = (LinearLayout) findViewById(R.id.push_activity_notice);
		evaluateLayout = (LinearLayout) findViewById(R.id.push_evaluate_notice);
		coinsLayout = (LinearLayout) findViewById(R.id.push_coins_notice);

		activityMessageAdapter = new ListViewMessageAdapter(this, activityPushs);
		evaluateMessageAdapter = new ListViewMessageAdapter(this, evaluatePushs);
		coinsMessageAdapter = new ListViewMessageAdapter(this, coinsPushs);
		if (activityPushs.size() > 0) {
			activityListView.setAdapter(activityMessageAdapter);
			activityListView.setCacheColorHint(0);
		} else {
			activityLayout.setVisibility(View.VISIBLE);
		}
		if (evaluatePushs.size() > 0) {
			evaluateListView.setAdapter(evaluateMessageAdapter);
			evaluateListView.setCacheColorHint(0);
		} else {
			evaluateLayout.setVisibility(View.VISIBLE);
		}
		if (coinsPushs.size() > 0) {
			coinsListView.setAdapter(coinsMessageAdapter);
			coinsListView.setCacheColorHint(0);
		} else {
			coinsLayout.setVisibility(View.VISIBLE);
		}

		activityListView.setOnItemClickListener(activityMessageListener);
		coinsListView.setOnItemClickListener(coinsMessageListener);
		evaluateListView.setOnItemClickListener(evaluateMessageListener);
	}

	private void getActivity(final int activityId) {
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
					myActivity = ApiClient.getActivity(activityId);
					msg.what = 1;
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				} catch (JSONException e) {
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private OnItemClickListener activityMessageListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			MessagePush messagePush = activityPushs.get(position);
			int activityId = 0;
			try {
				activityId = Integer.valueOf(messagePush.getRemark());
			} catch (NumberFormatException e) {
				System.out.println("Empty remark!");
				return;
			}
			getActivity(activityId);
			Intent intent = null;
			if (myActivity.getStateId() == MyActivity.ACTIVITY_REVIEW && user.getRoleId() == User.MANAGE_USER) {
				intent = new Intent(MessageCenter.this, VerifyActivityDetails.class);
			} else if (myActivity.getStateId() == MyActivity.ACTIVITY_REVIEW) {
				DialogTool.createMessageDialog(MessageCenter.this, "提醒", "请等待活动通过审核！", "确定", null, DialogTool.NO_ICON).show();
			} else {
				intent = new Intent(MessageCenter.this, ActivityDetails.class);
			}
			Bundle bundle = new Bundle();
			bundle.putInt("activityId", activityId);
			intent.putExtras(bundle);
			startActivity(intent);
		}
	};

	private OnItemClickListener coinsMessageListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			startActivity(new Intent(MessageCenter.this, MyCoins.class));
		}

	};

	private OnItemClickListener evaluateMessageListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			MessagePush messagePush = evaluatePushs.get(position);
			Intent intent = new Intent(MessageCenter.this, EvaluateDetails.class);
			Bundle bundle = new Bundle();
			bundle.putString("integralString", messagePush.getPushContent());
			bundle.putString("appraiseString", messagePush.getRemark());
			intent.putExtras(bundle);
			startActivity(intent);
		}

	};

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.head_back) {
			finish();
		} else {
			int pos = (Integer) (v.getTag());
			setCurPoint(pos);
			mScrollLayout.snapToScreen(pos);
		}
	}

	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			activityTV.setTextColor(0xff228B22);
			coinsTV.setTextColor(Color.WHITE);
			evaluateTV.setTextColor(Color.WHITE);
		} else if (index == 1) {
			evaluateTV.setTextColor(0xff228B22);
			coinsTV.setTextColor(Color.WHITE);
			activityTV.setTextColor(Color.WHITE);
		} else {
			coinsTV.setTextColor(0xff228B22);
			evaluateTV.setTextColor(Color.WHITE);
			activityTV.setTextColor(Color.WHITE);
		}
	}

	@Override
	public void OnViewChange(int view) {
		setCurPoint(view);
	}
}
