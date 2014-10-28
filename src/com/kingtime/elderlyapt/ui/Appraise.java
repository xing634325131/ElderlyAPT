package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewAppraiseAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.Evaluate;
import com.kingtime.elderlyapt.entity.Record;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Appraise extends Activity {

	private static final String TAG = "Appraise";

	private Button backBtn;
	private TextView titleTV;
	private ListView appraiseListView;
	private Button sureBtn;

	private ListViewAppraiseAdapter appraiseAdapter;
	private int activityId;
	private List<Record> recordList;

	private LoadingDialog loadingDialog;
	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.header);

		Log.i(TAG, "Start");
		preInit();
		initData();
	}

	private void preInit() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		activityId = bundle.getInt("activityId");

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("评论");
		backBtn.setOnClickListener(listener);

		loadingDialog = new LoadingDialog(this);
		recordList = new ArrayList<Record>();
		appContext = (AppContext) getApplication();
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
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else if (msg.what == 0) {// 服务器返回数据为空，即暂无人员参加
					if (loadingDialog != null) {
						loadingDialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), "暂无人员参加！", Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("获取用户列表...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<Record> records = ApiClient.getJoinUserList(activityId);
					System.out.println("return ::" + records.size());
					recordList.clear();
					recordList.addAll(records);
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

	protected void initLayout() {
		setContentView(R.layout.appraise);

		appraiseListView = (ListView) findViewById(R.id.appraise_list);
		sureBtn = (Button) findViewById(R.id.appraise_apply);
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("评论");
		backBtn.setOnClickListener(listener);
		sureBtn.setOnClickListener(listener);
		appraiseAdapter = new ListViewAppraiseAdapter(this, recordList);
		appraiseListView.setAdapter(appraiseAdapter);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.appraise_apply:
				handleAppraise();
				break;
			default:
				break;
			}
		}

	};

	protected void handleAppraise() {
		List<Evaluate> evaluates = appraiseAdapter.getEvaluates();

		final StringBuilder builder = new StringBuilder();
		builder.append("[");
		for (Evaluate evaluate : evaluates) {
			builder.append("{");
			builder.append("uid:").append(evaluate.getUid()).append(",");
			builder.append("credibility:").append(evaluate.getCredibility());
			builder.append("},");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("]");
		System.out.println("Evaluate json:" + builder.toString());

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
					boolean result = (boolean)msg.obj;
					if(result == true){
						Toast.makeText(Appraise.this, "评价成功！", Toast.LENGTH_SHORT).show();
						finish();
					} else{
						Toast.makeText(Appraise.this, "评价失败。请稍后再试！", Toast.LENGTH_SHORT).show();
						return ;
					}
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else if (msg.what == 0) {// 服务器返回数据为空，即暂无人员参加
					if (loadingDialog != null) {
						loadingDialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), "暂无人员参加！", Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("评价中...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					boolean result = ApiClient.appriaseActivity(builder.toString(), activityId);
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
		if(loadingDialog != null){
			loadingDialog.dismiss();
		}
		super.onDestroy();
	}
	
	
}
