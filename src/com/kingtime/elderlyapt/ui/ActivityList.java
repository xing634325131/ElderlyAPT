package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewGeneralActivityAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.User;
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

public class ActivityList extends BaseActivity {

	private TextView titleTV;
	private Button backBtn;

	private int categoryId;
	private String requestName;
	private String titleName;
	private LoadingDialog loadingDialog;
	private ListView activityLV;
	private ListViewGeneralActivityAdapter activityAdapter;
	private List<MyActivity> myActivities;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle getBundle = getIntent().getExtras();
		requestName = getBundle.getString("requestName");
		titleName = getBundle.getString("titleName");
		if (requestName.equals("requestById")) {
			categoryId = getBundle.getInt("categoryId");
		}
		Log.i("ActivityList requestName:", requestName);

		setContentView(R.layout.header);
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV.setText(titleName);
		backBtn.setOnClickListener(listener);
		myActivities = new ArrayList<MyActivity>();

		loadingDialog = new LoadingDialog(this);
		initData();
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

	private void initData() {
		AppContext appContext = (AppContext) getApplication();
		final User user = appContext.getLoginInfo();
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
				} else if (msg.what == 0) {// 服务器无返回数据
					Toast.makeText(getApplicationContext(), "暂无此类活动！", Toast.LENGTH_SHORT).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("获取活动列表...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<MyActivity> activities = null;
					if (requestName.equals("requestById")) {
						activities = ApiClient.getActivities(categoryId);
					} else if (requestName.equals("requestVerify")) {
						activities = ApiClient.getActivitiesByRequest("getActivitiesByVerify", user.getUid());
					} else if (requestName.equals("requestPublished")) {
						activities = ApiClient.getActivitiesByRequest("getPublished", user.getUid());
					} else if (requestName.equals("requestJoined")) {
						activities = ApiClient.getActivitiesByRequest("getNowJoined", user.getUid());
					} else if (requestName.equals("requestEnded")) {
						activities = ApiClient.getActivitiesByRequest("getEnded", user.getUid());
					}
					myActivities.clear();
					int size = activities.size();
					for (int i = size - 1; i > 0; i--) {
						myActivities.add(activities.get(i));
					}
					Log.i("GetRecommend-length", String.valueOf(myActivities.size()));
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
		setContentView(R.layout.activity_list);
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV.setText(titleName);
		backBtn.setOnClickListener(listener);

		activityLV = (ListView) findViewById(R.id.activity_list);
		activityAdapter = new ListViewGeneralActivityAdapter(this, myActivities);
		activityLV.setAdapter(activityAdapter);
		activityLV.setOnItemClickListener(activityListener);
	}

	private OnItemClickListener activityListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Log.i("reuqestName", requestName);
			Intent intent = new Intent(ActivityList.this, ActivityDetails.class);
			if (requestName.equals("requestVerify")) {
				intent = new Intent(ActivityList.this, VerifyActivityDetails.class);
			}
			Bundle bundle = new Bundle();
			bundle.putInt("activityId", myActivities.get(position).getActivityId());
			Log.i("Main-ActivityDetail activityID", String.valueOf(myActivities.get(position).getActivityId()));
			intent.putExtras(bundle);
			startActivityForResult(intent, 0x01);
			// startActivity(intent);
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case 0x01:
			onCreate(null);// 刷新界面
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

}
