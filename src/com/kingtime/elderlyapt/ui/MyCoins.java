package com.kingtime.elderlyapt.ui;

import java.io.IOException;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014年8月14日
 */
public class MyCoins extends Activity {

	private TextView titleTV;
	private Button backBtn;
	private LinearLayout acquireLayout;
	private TextView mycoinsTV;
	private TextView aboutCoinsTV;

	private AppContext appContext;
	private User user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.mycoins);
		
		appContext = (AppContext) getApplication();
		user = appContext.getLoginInfo();
		initData();
		
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		acquireLayout = (LinearLayout) findViewById(R.id.mycoins_acquire);
		mycoinsTV = (TextView) findViewById(R.id.mycoins_my);
		aboutCoinsTV = (TextView) findViewById(R.id.about_coins);

		titleTV.setText("我的时间币");
		backBtn.setOnClickListener(listener);
		acquireLayout.setOnClickListener(listener);
		aboutCoinsTV.setOnClickListener(listener);
		mycoinsTV.setText(user.getIntegral() + " 时间币");
	}

	private void initData() {
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {

				if (msg.what == 1) {
					appContext.saveLoginInfo(user);
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					user = ApiClient.getUserByUserId(user.getUid());
					msg.what = 1;
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				} catch (JSONException e) {
					// TODO Auto-generated catch block
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
			case R.id.head_back:
				finish();
				break;
			case R.id.mycoins_acquire:
				startActivity(new Intent(MyCoins.this, AcquireCoins.class));
				break;
			case R.id.about_coins:
				startActivity(new Intent(MyCoins.this, AboutCoins.class));
				break;
			default:
				break;
			}
		}
	};
}