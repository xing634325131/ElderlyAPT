package com.kingtime.elderlyapt.ui;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.util.StringUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月14日
 */
public class EvaluateDetails extends Activity {

	private TextView titleTV;
	private Button backBtn;
	private TextView integralTV;
	private TextView appraiseTV;

	private String integralString;
	private String appraiseString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.evaluate_details);

		Bundle bundle = getIntent().getExtras();
		integralString = bundle.getString("integralString");
		appraiseString = bundle.getString("appraiseString");

		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		integralTV = (TextView) findViewById(R.id.evaluate_integral);
		appraiseTV = (TextView) findViewById(R.id.evaluate_appraise);

		titleTV.setText("评论详情");
		backBtn.setOnClickListener(listener);
		integralTV.setText(integralString);
		if (!StringUtils.isEmpty(appraiseString) && !appraiseString.equals("null")) {
			appraiseTV.setText("他给你活动的评价为：" + appraiseString);
			appraiseTV.setVisibility(View.VISIBLE);
		}
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
