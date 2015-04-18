package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.Evaluate;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.widget.LoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月19日
 */
public class EvaluateActivity extends Activity implements OnRatingBarChangeListener{

	private TextView titleTV;
	private Button backBtn;
	private TextView rateDescTV;
	private EditText contentET;
	private ImageView voiceIV;
	private RatingBar rateBar;
	private Button sureBtn;
	
	private int activityId;
	private BaiduASRDigitalDialog mDialog;// 语音识别对话框
	private Evaluate evaluate;
	private AppContext appContext;
	private LoadingDialog loading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluate);
		
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		activityId = bundle.getInt("activityId");
		Log.i("Evaluate", "activityId:"+activityId);
		
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		contentET = (EditText)findViewById(R.id.evaluate_content);
		rateBar = (RatingBar)findViewById(R.id.evaluate_credibility);
		rateDescTV = (TextView)findViewById(R.id.evaluate_credibility_desc);
		voiceIV = (ImageView)findViewById(R.id.evaluate_voice_input);
		sureBtn = (Button)findViewById(R.id.evaluate_sure);
		
		titleTV.setText("评价");
		backBtn.setOnClickListener(listener);
		sureBtn.setOnClickListener(listener);
		voiceIV.setOnClickListener(listener);
		rateBar.setOnRatingBarChangeListener(this);
		
		evaluate = new Evaluate();
		appContext = (AppContext)getApplication();
		loading = new LoadingDialog(this);
	}

	private OnClickListener listener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.evaluate_voice_input:
				startVoiceInput();
				break;
			case R.id.evaluate_sure:
				handleData();
				break;
			default:
				break;
			}	
		}
	};

	@Override
	public void onRatingChanged(RatingBar ratingBar, float rating,
			boolean fromUser) {
		rateDescTV.setText("评分：" + rating + "分");
		
	}

	/**
	 * 启动语音识别
	 */
	protected void startVoiceInput() {
		DialogRecognitionListener mRecognitionListener = new DialogRecognitionListener() {
			@Override
			public void onResults(Bundle results) {
				ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION) : null;
				if (rs != null) {
					contentET.setText(rs.get(0));
				}
			}
		};
		mDialog = DialogTool.createBaiduASRDigitalDialog(this,
				mRecognitionListener);
		mDialog.show();
	}
	
	private void handleData() {
		evaluate.setContent(contentET.getText().toString());
		evaluate.setCredibility(rateBar.getRating());
		evaluate.setActivityId(activityId);
		evaluate.setUid(appContext.getLoginInfo().getUid());
		
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK,
					Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (loading != null) {
					loading.dismiss();
				}
				
				if (msg.what == 1) {
					boolean result = (Boolean)msg.obj;
					if(result == true){
						Toast.makeText(EvaluateActivity.this, "评价成功！", Toast.LENGTH_SHORT).show();
						setResult(RESULT_OK, null);
						finish();
					} else{
						Toast.makeText(EvaluateActivity.this, "评价失败。请稍后再试！", Toast.LENGTH_SHORT).show();
					}
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG)
							.show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loading != null) {
			loading.setLoadText("评价中...");
			loading.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					boolean result = ApiClient.evaluateActivity(evaluate);
					msg.obj = result;
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
	
	@Override
	protected void onDestroy() {
		if(mDialog != null){
			mDialog.dismiss();
		}
		super.onDestroy();
	}
}
