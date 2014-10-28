package com.kingtime.elderlyapt.ui;

import com.kingtime.elderlyapt.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * 职责详情描述
 * 
 * @author xp
 * @created 2014年8月8日
 */
public class DutyDetails extends Activity {

	private Button backBtn;
	private TextView titleTV;
	private TextView contentTV;

	private String dutyContent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.duty_details);

		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		dutyContent = bundle.getString("dutyContent");

		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		contentTV = (TextView) findViewById(R.id.duty_details_content);
		titleTV.setText("职责详情描述");
		backBtn.setOnClickListener(listener);
		contentTV.setText(dutyContent);
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
