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
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.StringUtils;
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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014年8月21日
 */
public class Search extends Activity {

	private EditText searchET;
	private Button searchBtn;
	private Button searchBack;
	private ListView searchListView;

	private String searchString;

	private List<MyActivity> myActivities;
	private ListViewGeneralActivityAdapter activityAdapter;

	private LoadingDialog loadingDialog;
	private AppContext appContext;

	// private boolean isFirstSearch;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search);

		preInit();
		initData();
	}

	private void preInit() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		searchString = bundle.getString("searchString");

		searchBtn = (Button) findViewById(R.id.search_btn);
		searchET = (EditText) findViewById(R.id.search_editer);
		searchListView = (ListView) findViewById(R.id.search_list);
		searchBack = (Button) findViewById(R.id.search_back);

		searchBack.setVisibility(View.VISIBLE);
		searchBack.setOnClickListener(listener);
		searchBtn.setOnClickListener(listener);
		if (!StringUtils.isEmpty(searchString)) {
			searchET.setText(searchString);
		}

		myActivities = new ArrayList<MyActivity>();
		loadingDialog = new LoadingDialog(this);
		appContext = (AppContext) getApplication();
		// isFirstSearch = true;

		activityAdapter = new ListViewGeneralActivityAdapter(Search.this, myActivities);
		searchListView.setAdapter(activityAdapter);
		searchListView.setOnItemClickListener(searchListListener);
	}

	private void initData() {
		if (StringUtils.isEmpty(searchET.getText().toString())) {
			return;
		}

		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(Search.this, R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}

				if (msg.what == 1) {
					activityAdapter.notifyDataSetChanged();
					searchListView.setCacheColorHint(0);
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(Search.this, R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else if (msg.what == -2) {// 搜索无结果
					Toast.makeText(Search.this, "没有搜索到相关内容", Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(Search.this, R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("搜索中...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<MyActivity> activities = ApiClient.searchForActivity(searchString);
					myActivities.clear();
					myActivities.addAll(activities);
					msg.what = 1;
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				} catch (JSONException e) {
					msg.what = -2;
					msg.obj = e;
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.search_btn:
				searchString = searchET.getText().toString();
				searchET.clearFocus();
				checkData();
				initData();
				break;
			case R.id.search_back:
				finish();
				break;
			default:
				break;
			}
		}
	};

	private OnItemClickListener searchListListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent intent = new Intent(Search.this, ActivityDetails.class);
			Bundle bundle = new Bundle();
			bundle.putInt("activityId", myActivities.get(position).getActivityId());
			Log.i("Main-ActivityDetail activityID", String.valueOf(myActivities.get(position).getActivityId()));
			intent.putExtras(bundle);
			startActivityForResult(intent, 0x01);

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		case 0x01:
			// 有一点小问题，暂停使用刷新
			// onCreate(null);//刷新界面
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	protected void checkData() {
		if (StringUtils.isEmpty(searchString)) {
			DialogTool.createMessageDialog(this, "提示", "请输入搜索内容", "确定", null, DialogTool.NO_ICON).show();
			searchET.setText("");
			return;
		}
		myActivities.clear();
		activityAdapter.notifyDataSetChanged();
		searchListView.setCacheColorHint(0);
	}
}
