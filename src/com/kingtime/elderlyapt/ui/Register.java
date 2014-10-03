package com.kingtime.elderlyapt.ui;

import java.io.IOException;

import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.elderlyapt.widget.LoadingDialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014��7��27��
 */
public class Register extends Activity {

	private Button backBtn;
	private TextView titleTV;
	private EditText accoutET;
	private EditText passwordET;
	private EditText passwordConfirmET;
	private Button registerBtn;
	private TextView goLoginTV;

	private String account;
	private String password;
	private String passwordConfirm;
	private User user;
	private LoadingDialog loadingDialog;
	private AppContext appContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);
		intLayout();
	}

	private void intLayout() {
		backBtn = (Button) findViewById(R.id.head_back);
		titleTV = (TextView) findViewById(R.id.head_title);
		accoutET = (EditText) findViewById(R.id.register_account);
		passwordET = (EditText) findViewById(R.id.register_password);
		passwordConfirmET = (EditText) findViewById(R.id.register_password_confirm);
		registerBtn = (Button) findViewById(R.id.register_register);
		goLoginTV = (TextView) findViewById(R.id.register_go_login);

		backBtn.setOnClickListener(listener);
		goLoginTV.setOnClickListener(listener);
		registerBtn.setOnClickListener(listener);
		titleTV.setText(R.string.register);
		user = new User();
		
		loadingDialog = new LoadingDialog(this);
		appContext = (AppContext)getApplication();
		user = new User();
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.register_go_login:
				//Intent goLoginIntent = new Intent(Register.this, Login.class);
				//startActivity(goLoginIntent);
				goLoginTV.setTextColor(Color.parseColor("#4484B9"));
				finish();
				break;
			case R.id.register_register:
				if (checkInput()) {
					goRegister();
				}
				break;
			default:
				break;
			}
		}

		private void goRegister() {
			if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
				Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK,
						Toast.LENGTH_LONG).show();
				return;
			}

			final Handler handler = new Handler(){
				public void handleMessage(Message msg) {
					if(loadingDialog != null){
						loadingDialog.dismiss();
					}
					
					switch (msg.what) {
					case 1:
						Toast.makeText(getApplicationContext(), R.string.REGISTER_SUCCESS, Toast.LENGTH_LONG).show();
						finish();
						break;
					case -2://ע���û��Ѵ���
						Toast.makeText(getApplicationContext(), R.string.ERROR_REGISTER_INFO, Toast.LENGTH_LONG).show();
						accoutET.setText("");
						passwordET.setText("");
						passwordConfirmET.setText("");
						break;
					case -1://����������Ӧ
						Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
						break;
					default://δ֪����
						Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
						break;
					}
				}
			};

			
			if(loadingDialog != null){
				loadingDialog.setLoadText("����ע��...");
				loadingDialog.show();
			}
			
			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						User newUser= ApiClient.register(user);
						msg.what = 1;
						msg.obj = newUser;
					} catch (IOException e) {
						System.out.println(e.toString());
						e.printStackTrace();
						msg.what = -1;
					}catch (JSONException e) {
						System.out.println(e.toString());
						e.printStackTrace();
						msg.what=-2;
					}catch (Exception e) {
						System.out.println(e.toString());
						e.printStackTrace();
						msg.what = -3;}
					handler.sendMessage(msg);
				}
			}.start();
		}

		private boolean checkInput() {
			account = accoutET.getText().toString();
			password = passwordET.getText().toString();
			passwordConfirm = passwordConfirmET.getText().toString();
			if (StringUtils.isEmpty(account)) {
				DialogTool.createMessageDialog(Register.this, "��ʾ", "�ʺ����Ʋ���Ϊ��",
						"ȷ��", null, DialogTool.NO_ICON).show();
				return false;
			} else if (StringUtils.isEmpty(password)
					|| StringUtils.isEmpty(passwordConfirm)) {
				DialogTool.createMessageDialog(Register.this, "��ʾ", "���벻��Ϊ��",
						"ȷ��", null, DialogTool.NO_ICON).show();
				return false;
			} else if (!password.equals(passwordConfirm)) {
				DialogTool.createMessageDialog(Register.this, "��ʾ",
						"������������벻��ͬ������������", "ȷ��", null, DialogTool.NO_ICON)
						.show();
				passwordET.setText("");
				passwordConfirmET.setText("");
				return false;
			} else if (password.length() < 8) {
				DialogTool.createMessageDialog(Register.this, "��ʾ",
						"���볤�Ȳ�������8λ", "ȷ��", null, DialogTool.NO_ICON).show();
				passwordET.setText("");
				passwordConfirmET.setText("");
				return false;
			}
			user.setName(account);
			user.setPwd(password);
			return true;
		}
	};

	@Override
	public void finish() {
		startActivity(new Intent(Register.this, Login.class));
		super.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
