package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewDutyAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.Duty;
import com.kingtime.elderlyapt.widget.LoadingDialog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author xp
 * @created 2014年8月8日
 */
public class DutyList extends BaseActivity {

	private static final String TAG = "DutyList";

	private Button backBtn;
	private TextView titleTV;
	private ListView dutyListView;
	private ListViewDutyAdapter dutyAdapter;

	private int activityId;
	// private String activityName;
	private List<Duty> dutyList;

	private LoadingDialog loadingDialog;

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
		// activityName = bundle.getString("activityName");

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("活动职责列表");
		backBtn.setOnClickListener(listener);

		loadingDialog = new LoadingDialog(this);
		dutyList = new ArrayList<Duty>();
		dutyAdapter = new ListViewDutyAdapter(DutyList.this, dutyList);
		// dutyAdapter.setActivityName(activityName);
	}

	private void initData() {
		AppContext appContext = (AppContext) getApplication();
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK,
					Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					initLayout();
					if (loadingDialog != null) {
						loadingDialog.dismiss();
					}
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG)
							.show();
				} else if (msg.what == 0) {// 服务器返回数据为空，即暂无人员参加
					if (loadingDialog != null) {
						loadingDialog.dismiss();
					}
					Toast.makeText(getApplicationContext(),
							"暂无具体活动职责！", Toast.LENGTH_LONG)
							.show();
				}else {// 未知错误
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("获取活动职责...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<Duty> duties = ApiClient.getActivityDutyList(activityId);
					dutyList.clear();
					dutyList.addAll(duties);
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
		setContentView(R.layout.duty);

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("活动职责列表");
		backBtn.setOnClickListener(listener);

		dutyListView = (ListView) findViewById(R.id.duty_list);
		dutyListView.setAdapter(dutyAdapter);
		dutyAdapter.notifyDataSetChanged();
		dutyListView.setCacheColorHint(0);
		dutyListView.setOnItemClickListener(dutyListener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;

			default:
				break;
			}
		}
	};

	private OnItemClickListener dutyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent dutyIntent = new Intent(DutyList.this, DutyDetails.class);
			Bundle userBundle = new Bundle();
			userBundle.putString("dutyContent", dutyList.get(position)
					.getDutyContent());
			dutyIntent.putExtras(userBundle);
			startActivity(dutyIntent);
		}
	};
}
