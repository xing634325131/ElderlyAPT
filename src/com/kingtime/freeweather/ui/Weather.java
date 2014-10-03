package com.kingtime.freeweather.ui;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.widget.OnViewChangeListener;
import com.kingtime.elderlyapt.widget.ScrollLayout;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Weather extends Activity implements OnClickListener, OnViewChangeListener{
	
	private Button backBtn;
	private TextView titleTV;
	private TextView setTV;
	
	// 滑动界面和顶部模块
	private ScrollLayout mScrollLayout;
	private LinearLayout[] mImageViews;
	private int mViewCount;
	private int mCurSel;
	private TextView infoTV;
	private TextView suggestTV;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.weather);
		initMainLayout();
		preInit();
	}

	private void preInit(){
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		setTV = (TextView)findViewById(R.id.head_right);
		titleTV.setText("天气");
		setTV.setText("设置");
		setTV.setVisibility(View.VISIBLE);
		backBtn.setOnClickListener(this);
		setTV.setOnClickListener(this);
		
	}
	
	private void initMainLayout() {
		mScrollLayout = (ScrollLayout) findViewById(R.id.weather_scrolllayout);
		LinearLayout linearLayout = (LinearLayout) findViewById(R.id.weather_layout);
		infoTV = (TextView) findViewById(R.id.weather_since);
		suggestTV = (TextView) findViewById(R.id.weather_tips);

		mViewCount = mScrollLayout.getChildCount();
		//System.out.println("mviewCount-->"+mViewCount);
		mImageViews = new LinearLayout[mViewCount];
		for (int i = 0; i < mViewCount; i++) {
			mImageViews[i] = (LinearLayout) linearLayout.getChildAt(i);
			mImageViews[i].setEnabled(true);
			mImageViews[i].setOnClickListener(this);
			mImageViews[i].setTag(i);
		}
		mCurSel = 0;
		mImageViews[mCurSel].setEnabled(false);
		mScrollLayout.SetOnViewChangeListener(this);
		
	}
	
	private void setCurPoint(int index) {
		if (index < 0 || index > mViewCount - 1 || mCurSel == index) {
			return;
		}
		mImageViews[mCurSel].setEnabled(true);
		mImageViews[index].setEnabled(false);
		mCurSel = index;

		if (index == 0) {
			infoTV.setTextColor(0xff228B22);
			suggestTV.setTextColor(Color.WHITE);
		} else if (index == 1) {
			suggestTV.setTextColor(0xff228B22);
			infoTV.setTextColor(Color.WHITE);
		}
	}
	
	@Override
	public void OnViewChange(int view) {
		setCurPoint(view);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.head_back){
			finish();
		} else if(v.getId() == R.id.head_right){
			//
		} else{
			int pos = (Integer) (v.getTag());
			setCurPoint(pos);
			mScrollLayout.snapToScreen(pos);
		}
	}
}
