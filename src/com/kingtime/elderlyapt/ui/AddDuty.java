package com.kingtime.elderlyapt.ui;

import java.util.ArrayList;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.entity.Duty;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.StringUtils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月11日
 */
public class AddDuty extends BaseActivity {

	private TextView titleTV;
	private Button backBtn;
	private EditText addNameET;
	private EditText addContentET;
	private EditText addNeedNumET;
	private EditText addIntegralET;
	private Button addSureBtn;
	private ImageView voiceImageView;
	private RadioGroup radioGroup;
	private RadioButton radioButton;
	private EditText selectNumET;

	private Duty nowDuty;
	private BaiduASRDigitalDialog mDialog;// 语音识别对话框

	private AlertDialog.Builder selectDialogBuilder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.add_duty);
		initLayout();

		super.onCreate(savedInstanceState);
	}

	private void initLayout() {
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		addNameET = (EditText) findViewById(R.id.add_name);
		addContentET = (EditText) findViewById(R.id.add_content);
		addNeedNumET = (EditText) findViewById(R.id.add_need_num);
		addIntegralET = (EditText) findViewById(R.id.add_integral);
		addSureBtn = (Button) findViewById(R.id.add_duty_sure);
		voiceImageView = (ImageView) findViewById(R.id.add_voice_input);

		titleTV.setText("添加职责");
		backBtn.setOnClickListener(listener);
		nowDuty = new Duty();
		addSureBtn.setOnClickListener(listener);
		voiceImageView.setOnClickListener(listener);
		addIntegralET.setOnClickListener(listener);
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.add_duty_sure:
				if (getData()) {
					Intent addIntent = new Intent(AddDuty.this, PushActivity.class);
					Bundle dutyBundle = new Bundle();
					dutyBundle.putString("dutyName", nowDuty.getDutyName());
					dutyBundle.putString("dutyContent", nowDuty.getDutyContent());
					dutyBundle.putInt("dutyIntegral", nowDuty.getDutyIntegral());
					dutyBundle.putInt("dutyNeedNum", nowDuty.getNeedNum());
					addIntent.putExtras(dutyBundle);
					setResult(RESULT_OK, addIntent);
					finish();
				}
				break;
			case R.id.add_voice_input:
				startVoiceInput();
				break;
			case R.id.add_integral:
				selectNeedNum();
				break;
			default:
				break;
			}
		}
	};

	protected boolean getData() {
		nowDuty.setDutyName(addNameET.getText().toString());
		nowDuty.setDutyContent(addContentET.getText().toString());
		String integralString = addIntegralET.getText().toString();
		if (StringUtils.isEmpty(integralString)) {
			DialogTool.createMessageDialog(this, "提示", "请输入正确的时间币！", "确定", null, DialogTool.NO_ICON).show();
			addIntegralET.setText("");
			return false;
		}
		int integral = Integer.valueOf(integralString.substring(4, integralString.length()));
		nowDuty.setDutyIntegral(integral);
		nowDuty.setNeedNum(Integer.valueOf(addNeedNumET.getText().toString()));
		return true;
	}

	protected void selectNeedNum() {
		selectDialogBuilder = new AlertDialog.Builder(this);
		final View view = getLayoutInflater().inflate(R.layout.select_dialog, null);
		selectDialogBuilder.setView(view);
		selectDialogBuilder.setTitle("参与时间币");
		radioGroup = (RadioGroup) view.findViewById(R.id.select_radio);
		radioButton = (RadioButton)view.findViewById(R.id.select_out);
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int buttonId = group.getCheckedRadioButtonId();
				radioButton = (RadioButton) view.findViewById(buttonId);
			}
		});
		selectNumET = (EditText) view.findViewById(R.id.select_num);
		selectDialogBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String textString = selectNumET.getText().toString();
				if (!StringUtils.isEmpty(textString)) {
					if (radioButton.getText().toString().equals("获得时间币")) {
						addIntegralET.setText("获得  " + textString);
					} else {
						addIntegralET.setText("支出 " + textString);
					}
				}
			}
		});
		selectDialogBuilder.create().show();
	}

	/**
	 * 启动语音识别
	 */
	protected void startVoiceInput() {
		DialogRecognitionListener mRecognitionListener = new DialogRecognitionListener() {
			@Override
			public void onResults(Bundle results) {
				ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION) : null;
				if (rs != null) {
					addContentET.setText(rs.get(0));
				}
			}
		};
		mDialog = DialogTool.createBaiduASRDigitalDialog(this, mRecognitionListener);
		mDialog.show();
	}

	@Override
	protected void onDestroy() {
		if (mDialog != null) {
			mDialog.dismiss();
		}
		super.onDestroy();
	}

}
