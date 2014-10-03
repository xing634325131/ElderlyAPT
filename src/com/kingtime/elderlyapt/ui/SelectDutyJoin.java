package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewDutyJoinAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.Duty;
import com.kingtime.elderlyapt.entity.User;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SelectDutyJoin extends Activity{
	
	private static final String TAG = "SelectDutyJoin";

	private Button backBtn;
	private TextView titleTV;
	private ListView dutyJoinListView;
	private ListViewDutyJoinAdapter dutyJoinAdapter;
	private Button applyBtn;

	private int activityId;
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

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("加入活动");
		backBtn.setOnClickListener(listener);

		loadingDialog = new LoadingDialog(this);
		dutyList = new ArrayList<Duty>();
		dutyJoinAdapter = new ListViewDutyJoinAdapter(SelectDutyJoin.this, dutyList);
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
				} else {// 未知错误
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
					List<Duty> duties = ApiClient
							.getActivityDutyList(activityId);
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
		setContentView(R.layout.duty_join);

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("加入活动");
		backBtn.setOnClickListener(listener);
		applyBtn = (Button)findViewById(R.id.duty_join_apply);
		applyBtn.setOnClickListener(listener);

		dutyJoinListView = (ListView) findViewById(R.id.duty_join_list);
		dutyJoinListView.setAdapter(dutyJoinAdapter);
		dutyJoinAdapter.notifyDataSetChanged();
		dutyJoinListView.setCacheColorHint(0);
		dutyJoinListView.setOnItemClickListener(dutyListener);
	}
	
	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.duty_join_apply:
				toApply();
			default:
				break;
			}
		}
	};

	private OnItemClickListener dutyListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent dutyIntent = new Intent(SelectDutyJoin.this, DutyDetails.class);
			Bundle userBundle = new Bundle();
			userBundle.putString("dutyContent", dutyList.get(position)
					.getDutyContent());
			dutyIntent.putExtras(userBundle);
			startActivity(dutyIntent);
		}
	};

	private void toApply() {
		final Map<String, String> selectedDutyId = dutyJoinAdapter.getSelectedDutyId();
		if(selectedDutyId.size() < 1){
			DialogTool.createMessageDialog(this, "提示", "请选择要申请的活动职责！", "确定", null, DialogTool.NO_ICON).show();
		} else if(selectedDutyId.size() > 1){
			DialogTool.createMessageDialog(this, "提示", "不要太贪心哦，只能选择一项申请加入！", "确定", null, DialogTool.NO_ICON).show();
		} else{
			final AppContext appContext = (AppContext) getApplication();
			if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
				Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK,
						Toast.LENGTH_LONG).show();
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
							Toast.makeText(getApplicationContext(), "申请已提交", Toast.LENGTH_SHORT).show();
						} else{
							Toast.makeText(getApplicationContext(), "申请失败，请稍后再试", Toast.LENGTH_SHORT).show();
						}
						finish();
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

			if (loadingDialog != null) {
				loadingDialog.setLoadText("申请中...");
				loadingDialog.show();
			}

			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						User user = appContext.getLoginInfo();
						Set<String> keySet = selectedDutyId.keySet();
						Iterator<String> iterator = keySet.iterator();
						String value = null;
						while(iterator.hasNext()){//获取selectedDutyId
							String key = iterator.next();
							value = selectedDutyId.get(key);
						}
						boolean result = ApiClient.selectDutyJoin(user.getUid(),Integer.valueOf(value));
						msg.what = 1;
						msg.obj = result;
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
	}
}
