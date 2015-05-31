package com.kingtime.elderlyapt.ui;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.kingtime.elderlyapt.R;

/**
 * @author xp
 * @created 2014Äê8ÔÂ14ÈÕ
 */
public class AboutCoins extends BaseActivity {

	private TextView titleTV;
	private Button backBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about_timecoins);
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV.setText(R.string.about_time_coins);
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