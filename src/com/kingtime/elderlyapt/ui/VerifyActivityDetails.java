package com.kingtime.elderlyapt.ui;

import java.io.IOException;

import org.json.JSONException;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.api.URLs;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.widget.LoadingDialog;
import com.kingtime.elderlyapt.widget.LruImageCache;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class VerifyActivityDetails extends Activity {

	private TextView titleTV;
	private Button backBtn;
	private NetworkImageView detailImageIV;
	private TextView detailNameTV;
	private TextView detailStartTimeTV;
	private TextView detailEndTimeTV;
	private TextView detailCloseTimeTV;
	private TextView detailAddressTV;
	private TextView detailPostUserTV;
	private TextView detailContentTV;
	private TextView detailContactTV;
	private TextView detailCategoryTV;
	private TextView detailNeedNumTV;
	private LinearLayout phoneLinearLayout;
	private LinearLayout postUserLinearLayout;
	private LinearLayout dutyLinearLayout;
	private Button sureVerifyBtn;
	private Button cancleVerifyBtn;

	// ���ضԻ���
	private LoadingDialog loadingDialog;

	// ����ͼƬ��ȡ
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	// ����
	private int activityId;
	private MyActivity nowActivity;
	private User postUser;
	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.header);
		Log.i("VerifyActivityDetails", "Start");

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
		titleTV.setText("����˻����");
		backBtn.setOnClickListener(listener);
		appContext = (AppContext) getApplication();
		nowActivity = new MyActivity();
		postUser = new User();
		loadingDialog = new LoadingDialog(this);
	}

	private void initData() {
		if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
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
					initLayout();
					detailAddressTV.setText("���ϵص㣺" + nowActivity.getAddress());
					detailCategoryTV.setText(nowActivity.getCategory());
					detailCloseTimeTV.setText("��ֹ������"
							+ nowActivity.getCloseTime());
					detailContactTV.setText(postUser.getPhone());
					detailContentTV.setText(nowActivity.getContent());
					detailEndTimeTV.setText("����ʱ�䣺" + nowActivity.getEndTime());
					detailNameTV.setText(nowActivity.getPostName());
					detailNeedNumTV.setText(String.valueOf(nowActivity
							.getNeedNum()) + "��");
					detailPostUserTV.setText(postUser.getName());
					detailStartTimeTV.setText("��ʼʱ�䣺"
							+ nowActivity.getBeginTime());

					String imageURL = ApiClient
							.formImageURL(URLs.REQUEST_COMMON_IMAGE,
									nowActivity.getMainPic());
					detailImageIV.setImageUrl(imageURL, imageLoader);
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG)
							.show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("��ȡ�����...");
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
		setContentView(R.layout.verify_details);

		appContext.getLoginInfo();
		rQueue = Volley.newRequestQueue(this);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
		
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		detailAddressTV = (TextView) findViewById(R.id.verify_detail_address);
		detailCategoryTV = (TextView) findViewById(R.id.verify_detail_category);
		detailCloseTimeTV = (TextView) findViewById(R.id.verify_detail_close_time);
		detailContactTV = (TextView) findViewById(R.id.verify_detail_contact);
		detailContentTV = (TextView) findViewById(R.id.verify_detail_content);
		detailEndTimeTV = (TextView) findViewById(R.id.verify_detail_end_time);
		detailImageIV = (NetworkImageView) findViewById(R.id.verify_detail_image);
		detailNameTV = (TextView) findViewById(R.id.verify_detail_name);
		detailNeedNumTV = (TextView) findViewById(R.id.verify_detail_need_num);
		detailPostUserTV = (TextView) findViewById(R.id.verify_detail_post_user);
		detailStartTimeTV = (TextView) findViewById(R.id.verify_detail_start_time);
		phoneLinearLayout = (LinearLayout) findViewById(R.id.verify_detail_click_phone);
		postUserLinearLayout = (LinearLayout) findViewById(R.id.verify_detail_click_post_user);
		dutyLinearLayout = (LinearLayout) findViewById(R.id.verify_detail_click_duty);
		sureVerifyBtn = (Button)findViewById(R.id.verify_detail_pass);
		cancleVerifyBtn = (Button)findViewById(R.id.verify_detail_not_pass);

		titleTV.setText("����˻����");
		backBtn.setOnClickListener(listener);
		postUserLinearLayout.setOnClickListener(listener);
		phoneLinearLayout.setOnClickListener(listener);
		dutyLinearLayout.setOnClickListener(listener);
		sureVerifyBtn.setOnClickListener(listener);
		cancleVerifyBtn.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.verify_detail_click_phone:
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ postUser.getPhone())));
				Log.i("MakePhone", String.valueOf(postUser.getPhone()));
				break;
			case R.id.verify_detail_click_post_user:
				Intent userIntent = new Intent(VerifyActivityDetails.this,
						JoinUserInfoDetails.class);
				Bundle userBundle = new Bundle();
				userBundle.putInt("uid", postUser.getUid());
				userIntent.putExtras(userBundle);
				startActivity(userIntent);
				break;
			case R.id.verify_detail_click_duty:
				Intent dutyIntent = new Intent(VerifyActivityDetails.this,
						DutyList.class);
				Bundle dutyBundle = new Bundle();
				dutyBundle.putInt("activityId", nowActivity.getActivityId());
				dutyIntent.putExtras(dutyBundle);
				startActivity(dutyIntent);
				break;
			case R.id.verify_detail_pass:
				verifyActivity(true);
				break;
			case R.id.verify_detail_not_pass:
				verifyActivity(false);
				break;
			default:
				break;
			}

		}
	};

	protected void verifyActivity(final boolean temp) {
		System.out.println("temp----->>>" + temp);
		
		if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
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
					boolean result = (Boolean)msg.obj;
					String tipsString = null;
					if(result == true){
						tipsString = "�����ɹ���";
					}else{
						tipsString = "����ʧ�ܣ����Ժ����ԣ�";
					}
					Toast.makeText(VerifyActivityDetails.this, tipsString, Toast.LENGTH_SHORT).show();
					setResult(RESULT_OK);
					finish();
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG)
							.show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(),
							R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("������...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					boolean result = ApiClient.updateActivityState(activityId, temp ? MyActivity.ACTIVITY_APPLY : MyActivity.ACTIVITY_CANCEL);
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
	


	

	
}
