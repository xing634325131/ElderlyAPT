package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import java.io.InputStream;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.util.FileUtils;
import com.kingtime.elderlyapt.util.StreamTool;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Toast;

public class AppStart extends Activity {

	private String recommendContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		final View view = View.inflate(this, R.layout.start, null);
		setContentView(view);
		recommendContent = null;

		// 渐变展示启动屏
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {

				final Handler handler = new Handler() {

					@Override
					public void handleMessage(Message msg) {
						if (msg.what == 1) {
							Log.i("AppStart", "Start");
							FileUtils.write(AppStart.this, "recommend", recommendContent);
						} else if (msg.what == -1) {// 服务器无响应
							Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
						} else {// 未知错误
							Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
						}
						Intent intent = new Intent(AppStart.this, Login.class);
						startActivity(intent);
						finish();
						super.handleMessage(msg);
					}
				};

				new Thread() {
					public void run() {
						Message msg = new Message();
						try {
							InputStream recommendStream = ApiClient.getActivityStream("recommend");
							byte[] data = null;
							try {
								data = StreamTool.read(recommendStream);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							recommendContent = new String(data);
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
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

		});
	}

}
