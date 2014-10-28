package com.kingtime.freeweather.widget;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.util.StreamTool;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.freeweather.entity.BaseLocation;
import com.kingtime.freeweather.utils.XMLReader;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;

public class WeatherSelectDialog extends Dialog {

	private Spinner provinceSP;
	private Spinner citySP;
	private Button sureButton;
	private Context wContext;
	private LayoutInflater inflater;
	private LayoutParams lp;

	private List<BaseLocation> provinceList;
	private List<BaseLocation> cityList;
	
	public int defaultCityCode = 1722;

	public WeatherSelectDialog(Context context) {
		super(context,R.style.SelectDialog);
		this.wContext = context;
		inflater = (LayoutInflater) wContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.weather_city_select, null);
		provinceSP = (Spinner) layout.findViewById(R.id.province_sp);
		citySP = (Spinner) layout.findViewById(R.id.city_sp);
		sureButton = (Button)layout.findViewById(R.id.select_weather_sure);
		provinceSP.setOnItemSelectedListener(provinceListener);
		citySP.setOnItemSelectedListener(cityListener);
		provinceList = new ArrayList<BaseLocation>();
		cityList = new ArrayList<BaseLocation>();
		initData();
		setContentView(layout);

		// 设置window属性
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = (float) 0.5; // 去背景遮盖
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}

	public void setSureOnClickListener(android.view.View.OnClickListener sureListener){
		sureButton.setOnClickListener(sureListener);
	}
	
	private void initData() {
		InputStream provinceInputStream = wContext.getResources().openRawResource(R.raw.province);
		String provinceString = null;
		try {
			provinceString = new String(StreamTool.read(provinceInputStream));
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// System.out.println(provinceString);
		if (!StringUtils.isEmpty(provinceString)) {
			try {
				System.out.println("Start parsing province...");
				provinceList = BaseLocation.toBaseLocations(XMLReader.readToStringList(provinceString));
			} catch (Exception e) {
				e.printStackTrace();
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(wContext, android.R.layout.simple_spinner_item,
					BaseLocation.baseNames(provinceList));
			provinceSP.setAdapter(adapter);
		}
	}

	private OnItemSelectedListener provinceListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

			int provinceCode = provinceList.get(position).getBaseCode();
			int provinceId = wContext.getResources().getIdentifier("p"+provinceCode, "raw", wContext.getPackageName());
			InputStream cityInputStream = wContext.getResources().openRawResource(provinceId);
			String cityString = null;
			try {
				cityString = new String(StreamTool.read(cityInputStream));
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (!StringUtils.isEmpty(cityString)) {
				try {
					System.out.println("Start parsing city...");
					cityList = BaseLocation.toBaseLocations(XMLReader.readToStringList(cityString));
				} catch (Exception e) {
					e.printStackTrace();
				}
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(wContext, android.R.layout.simple_spinner_dropdown_item,
						BaseLocation.baseNames(cityList));
				citySP.setAdapter(adapter);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};

	private OnItemSelectedListener cityListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
			defaultCityCode = cityList.get(arg2).getBaseCode();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub

		}
	};
}
