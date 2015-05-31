package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewJoinUserAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.Record;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.widget.LoadingDialog;

import android.content.DialogInterface;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014��8��8��
 */
public class JoinUserList extends BaseActivity {

	private static final String TAG = "JoinUserList";

	private Button backBtn;
	private TextView titleTV;
	private Button rightBtn;
	private ListView joinUserListView;
	private ListViewJoinUserAdapter joinUserAdapter;
	private RelativeLayout joinInfoDeleteLayout;
	private Button deleteUserBtn;

	private int activityId;
	private int postUserId;
	private List<Record> recordList;
	private MyActivity myActivity;
	StringBuilder deleteRecordIdBuilder;

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
		postUserId = bundle.getInt("postUserId");

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		titleTV.setText("�Ѳμӻ��Ա");
		backBtn.setOnClickListener(listener);

		loadingDialog = new LoadingDialog(this);
		recordList = new ArrayList<Record>();
		joinUserAdapter = new ListViewJoinUserAdapter(JoinUserList.this, recordList);
		joinUserAdapter.setPostUserId(postUserId);
		myActivity = new MyActivity();
	}

	private void initData() {
		AppContext appContext = (AppContext) getApplication();
		if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
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
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else if (msg.what == 0) {// ��������������Ϊ�գ���������Ա�μ�
					if (loadingDialog != null) {
						loadingDialog.dismiss();
					}
					Toast.makeText(getApplicationContext(), "������Ա�μӣ�", Toast.LENGTH_LONG).show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("��ȡ�û��б�...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					List<Record> records = ApiClient.getJoinUserList(activityId);
					myActivity = ApiClient.getActivity(activityId);
					System.out.println("return ::");
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

	private void initLayout() {
		setContentView(R.layout.join_info);

		joinInfoDeleteLayout = (RelativeLayout) findViewById(R.id.join_info_delete_layout);
		deleteUserBtn = (Button) findViewById(R.id.join_info_delete);

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		rightBtn = (Button) findViewById(R.id.head_right);
		titleTV.setText("�Ѳμӻ��Ա");
		backBtn.setOnClickListener(listener);
		deleteUserBtn.setOnClickListener(listener);

		AppContext appContext = (AppContext) getApplication();
		User userRecord = appContext.getLoginInfo();
		if (userRecord.getUid() == postUserId) {
			rightBtn.setText("����");
			rightBtn.setVisibility(View.VISIBLE);
			rightBtn.setOnClickListener(listener);
		}

		joinUserListView = (ListView) findViewById(R.id.join_info_list);
		joinUserListView.setAdapter(joinUserAdapter);
		joinUserAdapter.notifyDataSetChanged();
		joinUserListView.setCacheColorHint(0);
		joinUserListView.setOnItemClickListener(joinUserListener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.head_right:// ��������û�
				if(checkState()){
					refreshLayout();
				}
				break;
			case R.id.join_info_delete:
				deleteJoinUser();
				break;
			default:
				break;
			}
		}

	};

	private OnItemClickListener joinUserListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			Intent userIntent = new Intent(JoinUserList.this, JoinUserInfoDetails.class);
			Bundle userBundle = new Bundle();
			userBundle.putInt("uid", recordList.get(position).getUid());
			userIntent.putExtras(userBundle);
			startActivity(userIntent);
		}
	};
	
	/**
	 * ���״̬�Ƿ��ʺϽ�����Ա����
	 * @return
	 */
	private boolean checkState(){
		if(myActivity.getStateId() == MyActivity.ACTIVITY_END){
			DialogTool.createMessageDialog(this, "��ʾ", "��ѽ������޷����й���", "ȷ��", null, DialogTool.NO_ICON).show();
			return false;
		}
		return true;
	}

	private void refreshLayout() {
		boolean flag = recordList.get(0).getStateId() == Record.RECORD_COMMON ? true : false;
		joinInfoDeleteLayout.setVisibility(flag ? View.VISIBLE : View.INVISIBLE);
		rightBtn.setText(flag ? "ȡ��" : "����");
		// ��recordId����ΪRECORD_MANAGEING�����ΪRECORD_COMMON
		for (int i = 0; i < recordList.size(); i++) {
			recordList.get(i).setStateId(flag ? Record.RECORD_MANAGEING : Record.RECORD_COMMON);
		}
		joinUserAdapter.notifyDataSetChanged();
	}

	/**
	 * ȡ���û�����
	 */
	private void deleteJoinUser() {
		List<Record> deleteRecords = joinUserAdapter.getDeleteRecords();
		deleteRecordIdBuilder = new StringBuilder();
		deleteRecordIdBuilder.append("[");
		for (Record record : deleteRecords) {
			if (record.getStateId() == Record.RECORD_CANCLED) {
				deleteRecordIdBuilder.append("{recordId:");
				deleteRecordIdBuilder.append(record.getRecordId()).append("},");
			}
		}
		deleteRecordIdBuilder.deleteCharAt(deleteRecordIdBuilder.length() - 1);
		deleteRecordIdBuilder.append("]");
		DialogTool.createConfirmDialog(this, "����", "ȷ��Ҫȡ����Щ�û��Ļ�������룿", "ȷ��", "ȡ��", deleteUserListener, null,
				android.R.drawable.ic_delete).show();
	}

	private DialogInterface.OnClickListener deleteUserListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.i(TAG, "deleteUserJoin,recordId:" + deleteRecordIdBuilder);
			AppContext appContext = (AppContext) getApplication();
			if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
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
						Toast.makeText(getApplicationContext(), "ɾ���ɹ���", Toast.LENGTH_LONG).show();
						initData();// ɾ���ɹ���ˢ�½���
					} else if (msg.what == 0) {
						Toast.makeText(getApplicationContext(), "ɾ��ʧ�ܣ����Ժ����ԣ�", Toast.LENGTH_LONG).show();
					} else if (msg.what == -1) {// ����������Ӧ
						Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
					} else {// δ֪����
						Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
					}
					super.handleMessage(msg);
				}
			};

			if (loadingDialog != null) {
				loadingDialog.setLoadText("ɾ����...");
				loadingDialog.show();
			}

			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						boolean result = ApiClient.cancelUserJoin(deleteRecordIdBuilder.toString());
						msg.what = result ? 1 : 0;
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

	};
}
