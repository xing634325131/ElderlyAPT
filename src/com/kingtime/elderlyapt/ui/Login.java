package com.kingtime.elderlyapt.ui;

import java.io.IOException;
import org.json.JSONException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.elderlyapt.widget.LoadingDialog;
import com.kingtime.elderlyapt.api.ApiClient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014年7月26日 登录
 */
public class Login extends Activity {

	private TextView titleTV;
	private Button backBtn;
	private Button loginBtn;
	private Button registerBtn;
	private Button forgetPasswordBtn;
	private CheckBox rememberAccountCB;
	private EditText accountET;
	private EditText passwordET;

	private String account;
	private String password;
	private Boolean rememberAccount;
	private AppContext appContext;
	private User user;
	private LoadingDialog loadingDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.login);
		initLayout();
		initData();
	}

	/**
	 * 初始化布局
	 */
	private void initLayout() {
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		loginBtn = (Button) findViewById(R.id.login);
		registerBtn = (Button) findViewById(R.id.register);
		forgetPasswordBtn = (Button) findViewById(R.id.forget_password);
		rememberAccountCB = (CheckBox) findViewById(R.id.remember_account);
		accountET = (EditText) findViewById(R.id.account);
		passwordET = (EditText) findViewById(R.id.password);

		titleTV.setText("登录");
		backBtn.setOnClickListener(listener);
		loginBtn.setOnClickListener(listener);
		registerBtn.setOnClickListener(listener);
		forgetPasswordBtn.setOnClickListener(listener);

		loadingDialog = new LoadingDialog(this);
	}

	/**
	 * 初始化数据 主要为是否设置保留了账号信息
	 */
	private void initData() {
		appContext = (AppContext) getApplication();
		User userRecord = appContext.getLoginInfo();
		accountET.setText(userRecord.getName());
		if (appContext.getRemberMe()) {
			passwordET.setText(userRecord.getPwd());
		}
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.head_back:
				finish();
				System.exit(0);
				break;
			case R.id.login:
				if (checkInput()) {
					doLogin();
				}
				break;
			case R.id.register:
				Intent registerIntent = new Intent(Login.this, Register.class);
				startActivity(registerIntent);
				finish();
				break;
			case R.id.forget_password:
				Intent forgetPasswordIntent = new Intent(Login.this, ForgetPassword.class);
				startActivity(forgetPasswordIntent);
				finish();
				break;
			default:
				break;
			}
		}

		/**
		 * 检测表单数据是否合法
		 */
		private boolean checkInput() {
			account = accountET.getText().toString();
			password = passwordET.getText().toString();
			rememberAccount = rememberAccountCB.isChecked();
			System.out.println("login-->isrememberme-->" + rememberAccount);
			appContext.setRememberMe(rememberAccount);
			if (StringUtils.isEmpty(account) || StringUtils.isEmpty(password)) {
				DialogTool.createMessageDialog(Login.this, "提示", "用户名或密码不能为空", "确定", null, DialogTool.NO_ICON).show();
				return false;
			} else if (password.length() < 8) {
				DialogTool.createMessageDialog(Login.this, "提示", "密码长度不能少于8位", "确定", null, DialogTool.NO_ICON).show();
				return false;
			}
			return true;
		}
	};

	private void doLogin() {
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loadingDialog != null) {
					loadingDialog.dismiss();
				}

				if (msg.what == 1) {
					user = (User) msg.obj;
					if (user != null) {// 登录成功跳转到主界面
						AppContext ac = (AppContext) getApplication();
						ac.saveLoginInfo(user);
						
						startActivity(new Intent(Login.this, Main.class));
						finish();
					}
				} else if (msg.what == 0) {// 用户名或密码错误
					DialogTool.createMessageDialog(Login.this, "提示", getResources().getString(R.string.ERROR_RESPONSE_LOGIN), "确定", null, DialogTool.NO_ICON).show();
					passwordET.setText("");
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
			}
		};

		if (loadingDialog != null) {
			loadingDialog.setLoadText("正在登录...");
			loadingDialog.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					User user = ApiClient.login((AppContext) getApplication(), account, password);
					msg.what = 1;
					msg.obj = user;
				} catch (IOException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -1;
					msg.obj = e;
				} catch (JSONException e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = 0;
					msg.obj = e;
				} catch (Exception e) {
					System.out.println(e.toString());
					e.printStackTrace();
					msg.what = -2;
					msg.obj = e;
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			System.exit(0);
		}
		return super.onKeyDown(keyCode, event);
	}
}
