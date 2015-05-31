package com.kingtime.elderlyapt.ui;

import com.kingtime.elderlyapt.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月14日
 */
public class Setting extends BaseActivity {

	private TextView titleTV;
	private Button backBtn;
	private LinearLayout logoutLayout;
	private TextView logoutTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.setting);
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		logoutLayout = (LinearLayout)findViewById(R.id.setting_logout);
		logoutTV = (TextView)findViewById(R.id.setting_text_logout);
		
		titleTV.setText("设置");
		backBtn.setOnClickListener(listener);
		logoutLayout.setOnClickListener(listener);
		logoutLayout.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(event.getAction() == MotionEvent.ACTION_DOWN)
				{
					logoutTV.setTextColor(getResources().getColor(R.color.white));
				}else{
					logoutTV.setTextColor(getResources().getColor(R.color.sec_color));
				}
				return false;
			}
		});
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.setting_logout:
				logoutTV.setTextColor(getResources().getColor(R.color.white));
				startActivity(new Intent(Setting.this, Login.class));
				finish();
				break;
			default:
				break;
			}
		}
	};
}