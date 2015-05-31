package com.kingtime.elderlyapt.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.adapter.ListViewShowAddDutyAdapter;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.Duty;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.ImageUtils;
import com.kingtime.elderlyapt.util.ListViewTool;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.elderlyapt.widget.LoadingDialog;
import com.widget.time.ScreenInfo;
import com.widget.time.WheelMain;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PushActivity extends BaseActivity {

	private TextView titleTV;
	private Button backBtn;
	private LinearLayout setActivityImageLayout;
	private ImageView setImageView;
	private LinearLayout setStartTimeLayout;
	private TextView setStartTimeTV;
	private LinearLayout setEndTimeLayout;
	private TextView setEndTimeTV;
	private LinearLayout setCloseTimeLayout;
	private TextView setCloseTimeTV;
	private LinearLayout setCategoryLayout;
	private TextView setCategoryTV;
	private LinearLayout addDutyLayout;
	private ListView dutyListView;
	private Button createBtn;
	private EditText createNameET;
	private TextView createSumNumTV;
	private TextView createSumIntegralTV;
	private EditText createAddressET;
	private EditText createContentET;
	private ImageView voiceInputBtn;

	private WheelMain wheelMain;
	private BaiduASRDigitalDialog mDialog;// 语音识别对话框
	/**
	 * 开始时间
	 */
	private static final int SELECT_START_TIME = 0x01;
	/**
	 * 结束时间
	 */
	private static final int SELECT_END_TIME = 0x02;
	/**
	 * 截止时间
	 */
	private static final int SELECT_CLOSE_TIME = 0x03;

	/**
	 * 选择类别
	 */
	private static final int SELECT_CATEGORY = 0x04;
	/**
	 * 添加职责
	 */
	private static final int ADD_DUTY = 0x05;
	/**
	 * 默认位置ID，32代表衡阳
	 */
	private static final int DEFAULT_LOCATIONID = 32;
	private int timeState;

	// 图像信息处理
	private final static int CROP = 200;
	private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/ElderlyAPT/Activity/";
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;

	private LoadingDialog loading;
	private User nowUser;
	private int categoryId;
	AppContext appContext;

	// 添加职责
	private Duty addDuty;
	private List<Duty> addDutyList;
	private ListViewShowAddDutyAdapter addDutyAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.post_activity);

		initLayout();
	}

	private void initLayout() {
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		setActivityImageLayout = (LinearLayout) findViewById(R.id.push_image);
		setImageView = (ImageView) findViewById(R.id.push_set_image);
		setStartTimeLayout = (LinearLayout) findViewById(R.id.push_starttime);
		setStartTimeTV = (TextView) findViewById(R.id.push_set_starttime);
		setEndTimeLayout = (LinearLayout) findViewById(R.id.push_endtime);
		setEndTimeTV = (TextView) findViewById(R.id.push_set_endtime);
		setCloseTimeLayout = (LinearLayout) findViewById(R.id.push_enroll_endtime);
		setCloseTimeTV = (TextView) findViewById(R.id.push_set_closetime);
		setCategoryLayout = (LinearLayout) findViewById(R.id.push_category);
		setCategoryTV = (TextView) findViewById(R.id.push_set_category);
		addDutyLayout = (LinearLayout) findViewById(R.id.push_add_duty);
		dutyListView = (ListView) findViewById(R.id.push_duty_list);
		createBtn = (Button) findViewById(R.id.push_create);
		createNameET = (EditText) findViewById(R.id.push_name);
		createSumNumTV = (TextView) findViewById(R.id.push_sum_num);
		createSumIntegralTV = (TextView) findViewById(R.id.push_sum_integral);
		createAddressET = (EditText) findViewById(R.id.push_address);
		createContentET = (EditText) findViewById(R.id.push_content);
		voiceInputBtn = (ImageView) findViewById(R.id.push_voice_input);

		titleTV.setText("创建活动");
		backBtn.setOnClickListener(listener);
		setActivityImageLayout.setOnClickListener(listener);
		setStartTimeLayout.setOnClickListener(listener);
		setCloseTimeLayout.setOnClickListener(listener);
		setEndTimeLayout.setOnClickListener(listener);
		setCategoryLayout.setOnClickListener(listener);
		addDutyLayout.setOnClickListener(listener);
		createBtn.setOnClickListener(listener);
		voiceInputBtn.setOnClickListener(listener);

		loading = new LoadingDialog(this);
		nowUser = new User();
		addDutyList = new ArrayList<Duty>();
		addDutyAdapter = new ListViewShowAddDutyAdapter(this, addDutyList);
		dutyListView.setAdapter(addDutyAdapter);
		appContext = (AppContext) getApplication();
		nowUser = appContext.getLoginInfo();
		protraitFile = null;
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				confirmBack();
				break;
			case R.id.push_image:
				selectImage();
				break;
			case R.id.push_starttime:
				timeState = SELECT_START_TIME;
				selectDate();
				break;
			case R.id.push_enroll_endtime:
				timeState = SELECT_CLOSE_TIME;
				selectDate();
				break;
			case R.id.push_endtime:
				timeState = SELECT_END_TIME;
				selectDate();
				break;
			case R.id.push_category:
				Intent selectCategoryIntent = new Intent(PushActivity.this, SelectCategory.class);
				startActivityForResult(selectCategoryIntent, SELECT_CATEGORY);
				break;
			case R.id.push_add_duty:
				Intent addDutyIntent = new Intent(PushActivity.this, AddDuty.class);
				startActivityForResult(addDutyIntent, ADD_DUTY);
				break;
			case R.id.push_create:
				createActivity();
				break;
			case R.id.push_voice_input:
				startVoiceInput();
				break;
			default:
				break;
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			confirmBack();
		}
		return super.onKeyDown(keyCode, event);
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
					createContentET.setText(rs.get(0));
				}
			}
		};
		mDialog = DialogTool.createBaiduASRDigitalDialog(this, mRecognitionListener);
		mDialog.show();
	}

	/**
	 * 图片选择
	 */
	protected void selectImage() {
		CharSequence[] items = { getString(R.string.img_from_album), getString(R.string.img_from_camera) };
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("添加图片")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// 判断是否挂载了SD卡
						String storageState = Environment.getExternalStorageState();
						if (storageState.equals(Environment.MEDIA_MOUNTED)) {
							File savedir = new File(FILE_SAVEPATH);
							if (!savedir.exists()) {
								savedir.mkdirs();
							}
						} else {
							Toast.makeText(getApplicationContext(), "无法保存上传的头像，请检查SD卡是否挂载", Toast.LENGTH_SHORT).show();
							return;
						}

						// 输出裁剪的临时文件
						String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						// 照片命名
						String origFileName = "eapt_ac_" + timeStamp + ".jpg";
						String cropFileName = "eapt_ac_crop_" + timeStamp + ".jpg";

						// 裁剪头像的绝对路径
						protraitPath = FILE_SAVEPATH + cropFileName;
						protraitFile = new File(protraitPath);

						origUri = Uri.fromFile(new File(FILE_SAVEPATH, origFileName));
						cropUri = Uri.fromFile(protraitFile);

						// 相册选图
						if (item == 0) {
							startActionPickCrop(cropUri);
						}
						// 手机拍照
						else if (item == 1) {
							startActionCamera(origUri);
						}
					}
				}).create();

		imageDialog.show();
	}

	/**
	 * 选择图片裁剪
	 * 
	 * @param output
	 */
	private void startActionPickCrop(Uri output) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		startActivityForResult(Intent.createChooser(intent, "选择图片"), ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}

	/**
	 * 相机拍照
	 * 
	 * @param output
	 */
	private void startActionCamera(Uri output) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	/**
	 * 拍照后裁剪
	 * 
	 * @param data
	 *            原始图片
	 * @param output
	 *            裁剪后图片
	 */
	private void startActionCrop(Uri data, Uri output) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// 裁剪框比例
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// 输出图片大小
		intent.putExtra("outputY", CROP);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}

	/**
	 * 上传新照片
	 */
	private void uploadNewPhoto() {
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loading != null)
					loading.dismiss();
				if (msg.what == 1) {
					// 显示图片
					setImageView.setImageBitmap(protraitBitmap);
					boolean uploadResult = (Boolean) msg.obj;
					String toaString = uploadResult ? "活动图片上传成功" : "活动图片上传失败，请重新选择图片上传";
					Toast.makeText(getApplication(), toaString, Toast.LENGTH_LONG).show();
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("正在上传活动图片···");
			loading.show();
		}

		new Thread() {
			public void run() {
				// 获取头像缩略图
				if (!StringUtils.isEmpty(protraitPath) && protraitFile.exists()) {
					protraitBitmap = ImageUtils.loadImgThumbnail(protraitPath, 200, 200);
				}

				if (protraitBitmap != null) {
					Message msg = new Message();
					try {
						boolean result = ApiClient.uploadActivityImage(nowUser.getUid(), protraitFile);
						msg.what = 1;
						msg.obj = result;
					} catch (IOException e) {
						msg.what = -1;
						e.printStackTrace();
					}
					// if(res!=null && res.OK()){
					// //保存新头像到缓存
					// String filename = FileUtils.getFileName(user.getFace());
					// ImageUtils.saveImage(UserInfo.this, filename,
					// protraitBitmap);
					// }
					// msg.what = 1;
					// msg.obj = res;
					handler.sendMessage(msg);
				}
			};
		}.start();
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA:
			startActionCrop(origUri, cropUri);// 拍照后裁剪
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			uploadNewPhoto();// 上传新照片
			break;
		case SELECT_CATEGORY:
			Bundle categorybBundle = data.getExtras();
			setCategoryTV.setText(categorybBundle.getString("category"));
			categoryId = categorybBundle.getInt("categoryId");
			setCategoryTV.setTextColor(getResources().getColor(R.color.trd_color));
			break;
		case ADD_DUTY:
			addDuty = new Duty();
			Bundle addDutyBundle = data.getExtras();
			addDuty.setDutyName(addDutyBundle.getString("dutyName"));
			addDuty.setDutyContent(addDutyBundle.getString("dutyContent"));
			addDuty.setNeedNum(addDutyBundle.getInt("dutyNeedNum"));
			addDuty.setDutyIntegral(addDutyBundle.getInt("dutyIntegral"));
			addDutyList.add(addDuty);
			addDutyAdapter.notifyDataSetChanged();// 更新职责界面
			dutyListView.setCacheColorHint(0);
			dutyListView.setLayoutParams(ListViewTool.setLvHeight(dutyListView));
			;
			updateSumDuty();
			break;
		default:
			break;
		}
	}

	/**
	 * 更新界面上的总人数和总参与时间币
	 */
	private void updateSumDuty() {
		int sumNum = 0;
		int sumIntegral = 0;
		for (Duty updateDuty : addDutyList) {
			sumNum += updateDuty.getNeedNum();
			sumIntegral += updateDuty.getDutyIntegral() * updateDuty.getNeedNum();
		}
		if (sumNum > 0) {
			createSumNumTV.setText(sumNum + "人");
			createSumNumTV.setTextColor(getResources().getColor(R.color.trd_color));
			String integralString = sumIntegral > 0 ? "你将消耗" + sumIntegral : "你将获得" + Math.abs(sumIntegral);
			createSumIntegralTV.setText(integralString + "时间币");
		} else {
			createSumNumTV.setText("请添加活动职责");
			createSumNumTV.setTextColor(getResources().getColor(R.color.red));
		}
	}

	/**
	 * 日期选择
	 */
	protected void selectDate() {
		View timepickerview = View.inflate(this, R.layout.timepicker, null);
		ScreenInfo screenInfo = new ScreenInfo(PushActivity.this);
		wheelMain = new WheelMain(timepickerview, true);
		wheelMain.screenheight = screenInfo.getHeight();
		Calendar calendar = Calendar.getInstance();
		if (timeState == SELECT_START_TIME && !StringUtils.isEmpty(setStartTimeTV.getText().toString())) {
			calendar.setTime(StringUtils.toDate(setStartTimeTV.getText().toString() + ":00.0"));
		} else if (timeState == SELECT_END_TIME && !StringUtils.isEmpty(setEndTimeTV.getText().toString())) {
			calendar.setTime(StringUtils.toDate(setEndTimeTV.getText().toString() + ":00.0"));
		} else if (timeState == SELECT_CLOSE_TIME && !StringUtils.isEmpty(setCloseTimeTV.getText().toString())) {
			calendar.setTime(StringUtils.toDate(setCloseTimeTV.getText().toString() + ":00.0"));
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int min = calendar.get(Calendar.MINUTE);
		wheelMain.initDateTimePicker(year, month, day, hour, min);
		new AlertDialog.Builder(PushActivity.this).setTitle("选择时间").setView(timepickerview)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (timeState == SELECT_START_TIME) {
							setStartTimeTV.setText(wheelMain.getTime());
							setStartTimeTV.setVisibility(View.VISIBLE);
							setStartTimeTV.setTextColor(getResources().getColor(R.color.trd_color));
						} else if (timeState == SELECT_END_TIME) {
							setEndTimeTV.setText(wheelMain.getTime());
							setEndTimeTV.setVisibility(View.VISIBLE);
							setEndTimeTV.setTextColor(getResources().getColor(R.color.trd_color));
						} else if (timeState == SELECT_CLOSE_TIME) {
							setCloseTimeTV.setText(wheelMain.getTime());
							setCloseTimeTV.setVisibility(View.VISIBLE);
							setCloseTimeTV.setTextColor(getResources().getColor(R.color.trd_color));
						}
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// if (timeState == SELECT_START_TIME) {
						// setStartTimeTV.setVisibility(View.INVISIBLE);
						// } else if (timeState == SELECT_END_TIME) {
						// setEndTimeTV.setVisibility(View.INVISIBLE);
						// } else if (timeState == SELECT_CLOSE_TIME) {
						// setCloseTimeTV.setVisibility(View.INVISIBLE);
						// }
					}
				}).show();
	}

	/**
	 * 返回确认
	 */
	protected void confirmBack() {
		DialogTool.createConfirmDialog(PushActivity.this, "提示", "确定要放弃创建活动吗？", "确定", "取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				finish();
			}
		}, null, DialogTool.NO_ICON).show();

	}

	protected void createActivity() {
		MyActivity pushActivity = new MyActivity();
		pushActivity.setPostName(createNameET.getText().toString());
		pushActivity.setAddress(createAddressET.getText().toString());
		pushActivity.setBeginTime(setStartTimeTV.getText().toString());
		pushActivity.setCategoryId(categoryId);
		pushActivity.setCloseTime(setCloseTimeTV.getText().toString());
		pushActivity.setContent(createContentET.getText().toString());
		pushActivity.setEndTime(setEndTimeTV.getText().toString());
		pushActivity.setLocationId(DEFAULT_LOCATIONID);
		if (protraitFile != null) {
			pushActivity.setMainPic(protraitFile.getName());
		} else {
			pushActivity.setMainPic(null);
		}
		String sumNum = createSumNumTV.getText().toString();
		String sumIntegral = createSumIntegralTV.getText().toString();
		pushActivity.setNeedNum(Integer.valueOf(sumNum.substring(0, sumNum.length() - 1)));
		pushActivity.setPostUserId(nowUser.getUid());
		pushActivity.setSumIntegral(Integer.valueOf(sumIntegral.substring(4, sumIntegral.length() - 3)));
		if (sumIntegral.substring(2, 3).equals("获得")) {
			pushActivity.setSumIntegral(-pushActivity.getSumIntegral());
		}
		final String createActivityJSON = MyActivity.getJSON(pushActivity, addDutyList);
		System.out.println("Post JOSN:" + createActivityJSON);

		if (nowUser.getIntegral() < pushActivity.getSumIntegral()) {
			DialogTool.createMessageDialog(this, "提示", "您的时间币不够创建此活动，请联系社区管理员获取更多时间币！", "确定", null, DialogTool.NO_ICON).show();
			return;
		}

		// 连接网络。传递数据
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (loading != null) {
					loading.dismiss();
				}

				if (msg.what == 1) {
					MyActivity newActivity = (MyActivity) msg.obj;
					Toast.makeText(getApplicationContext(), "活动创建成功", Toast.LENGTH_LONG).show();
					Intent newActivityIntent = new Intent(PushActivity.this, ActivityDetails.class);
					Bundle newActivityBundle = new Bundle();
					newActivityBundle.putInt("activityId", newActivity.getActivityId());
					newActivityIntent.putExtras(newActivityBundle);
					startActivity(newActivityIntent);
					finish();
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loading != null) {
			loading.setLoadText("创建活动中...");
			loading.show();
		}

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					MyActivity newActivity = ApiClient.createNewActivity(createActivityJSON);
					msg.obj = newActivity;
					msg.what = 1;
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
				}
				handler.sendMessage(msg);
			}
		}.start();
	}

	@Override
	protected void onDestroy() {
		if (loading != null) {
			loading.dismiss();
		}
		if (mDialog != null) {
			mDialog.dismiss();
		}
		super.onDestroy();
	}

}
