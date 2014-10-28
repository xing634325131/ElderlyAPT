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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014年8月8日
 */
public class JoinUserInfoDetails extends Activity {

	private static final String TAG = "JoinUserInfoDetails";

	private TextView titleTV;
	private Button backBtn;
	private NetworkImageView photoImageView;
	private TextView userNameTV;
	private TextView addressTV;
	private TextView roleTV;
	private ImageView genderImageView;
	private TextView phoneTV;
	private TextView resPhoneTV;
	private TextView evaluateTimesTV;
	private RatingBar credibilityTV;
	private TextView interestTV;
	private TextView createTimeTV;
	private TextView emailTV;
	private LinearLayout phoneLinearLayout;
	private LinearLayout emailLinearLayout;

	private int uid;
	private LoadingDialog loadingDialog;
	private User postUser;

	// 网络图片获取
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "Start");
		preInit();
		initData();
	}

	private void preInit() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		uid = bundle.getInt("uid");
		loadingDialog = new LoadingDialog(this);
		postUser = new User();

		rQueue = Volley.newRequestQueue(this);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
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
			loadingDialog.setLoadText("获取用户信息...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					postUser = ApiClient.getUserByUserId(uid);
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
		if (postUser.getRoleId() == User.COMMON_USER) {//普通用户布局
			setContentView(R.layout.join_common_userinfo);
			photoImageView = (NetworkImageView) findViewById(R.id.userinfo_photo);
			userNameTV = (TextView) findViewById(R.id.userinfo_name);
			addressTV = (TextView) findViewById(R.id.userinfo_address);
			roleTV = (TextView) findViewById(R.id.userinfo_role);
			genderImageView = (ImageView) findViewById(R.id.userinfo_gender);
			phoneTV = (TextView) findViewById(R.id.userinfo_phone);
			resPhoneTV = (TextView) findViewById(R.id.userinfo_res_phone);
			evaluateTimesTV = (TextView) findViewById(R.id.userinfo_evaluate_times);
			credibilityTV = (RatingBar) findViewById(R.id.userinfo_credibility);
			interestTV = (TextView) findViewById(R.id.userinfo_interest);
			createTimeTV = (TextView) findViewById(R.id.userinfo_create_time);
			phoneLinearLayout = (LinearLayout) findViewById(R.id.userinfo_click_phone);

			resPhoneTV.setText(postUser.getResPhone());
			credibilityTV.setRating(postUser.getCredibility());
			interestTV.setText(postUser.getInterest());
		} else {//非普通用户布局
			setContentView(R.layout.join_outside_userinfo);
			photoImageView = (NetworkImageView) findViewById(R.id.outside_userinfo_photo);
			userNameTV = (TextView) findViewById(R.id.outside_userinfo_name);
			addressTV = (TextView) findViewById(R.id.outside_userinfo_address);
			roleTV = (TextView) findViewById(R.id.outside_userinfo_role);
			genderImageView = (ImageView) findViewById(R.id.outside_userinfo_gender);
			phoneTV = (TextView) findViewById(R.id.outside_userinfo_phone);
			evaluateTimesTV = (TextView) findViewById(R.id.outside_userinfo_evaluate_times);
			createTimeTV = (TextView) findViewById(R.id.outside_userinfo_create_time);
			emailTV = (TextView) findViewById(R.id.outside_userinfo_email);
			phoneLinearLayout = (LinearLayout) findViewById(R.id.outside_click_phone);
			emailLinearLayout = (LinearLayout) findViewById(R.id.outside_click_email);

			emailTV.setText(postUser.getEmail());
			emailLinearLayout.setOnClickListener(listener);
		}

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn.setOnClickListener(listener);
		titleTV.setText("用户信息");

		String imageURL = ApiClient.formImageURL(URLs.REQUEST_PHOTO_IMAGE,
				postUser.getPhotoName());
		photoImageView.setImageUrl(imageURL, imageLoader);
		userNameTV.setText(postUser.getName());
		addressTV.setText(postUser.getAddress());
		roleTV.setText(postUser.getRole());
		if (postUser.getGender().equals(User.GENDER_MALE)) {
			genderImageView.setBackgroundResource(R.drawable.icon_boy);
		} else {
			genderImageView.setBackgroundResource(R.drawable.icon_girl);
		}
		phoneTV.setText(postUser.getPhone());
		evaluateTimesTV.setText(String.valueOf(postUser.getEvaluateTimes()));
		createTimeTV.setText(postUser.getCreateTime());
		phoneLinearLayout.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.outside_click_email:
				Intent data = new Intent(Intent.ACTION_SENDTO);
				data.setData(Uri.parse("mailto:" + postUser.getEmail()));
				Log.i("SendMail", String.valueOf(postUser.getEmail()));
				data.putExtra(Intent.EXTRA_SUBJECT, "发送给" + postUser.getName());
				data.putExtra(Intent.EXTRA_TEXT, postUser.getName() + ",你好,我是在社区老年互助公寓上看见您的。");
				startActivity(data);
				break;
			case R.id.outside_click_phone:
			case R.id.userinfo_click_phone:
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
						+ postUser.getPhone())));
				Log.i("MakePhone", String.valueOf(postUser.getPhone()));
				break;
			default:
				break;
			}
		}
	};

}
