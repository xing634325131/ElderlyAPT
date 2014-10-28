package com.kingtime.elderlyapt.ui;

import com.kingtime.elderlyapt.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 找回密码界面
 * 
 * @author xp
 * @created 2014年7月27日
 */
public class ForgetPassword extends Activity {

	private TextView titleTV;
	private Button backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.forget_password);
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV.setText(R.string.find_password);
		backBtn.setOnClickListener(listener);
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
}
