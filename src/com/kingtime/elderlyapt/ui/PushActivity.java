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
	private BaiduASRDigitalDialog mDialog;// ����ʶ��Ի���
	/**
	 * ��ʼʱ��
	 */
	private static final int SELECT_START_TIME = 0x01;
	/**
	 * ����ʱ��
	 */
	private static final int SELECT_END_TIME = 0x02;
	/**
	 * ��ֹʱ��
	 */
	private static final int SELECT_CLOSE_TIME = 0x03;

	/**
	 * ѡ�����
	 */
	private static final int SELECT_CATEGORY = 0x04;
	/**
	 * ���ְ��
	 */
	private static final int ADD_DUTY = 0x05;
	/**
	 * Ĭ��λ��ID��32�������
	 */
	private static final int DEFAULT_LOCATIONID = 32;
	private int timeState;

	// ͼ����Ϣ����
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

	// ���ְ��
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

		titleTV.setText("�����");
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
	 * ��������ʶ��
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
	 * ͼƬѡ��
	 */
	protected void selectImage() {
		CharSequence[] items = { getString(R.string.img_from_album), getString(R.string.img_from_camera) };
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("���ͼƬ")
				.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						// �ж��Ƿ������SD��
						String storageState = Environment.getExternalStorageState();
						if (storageState.equals(Environment.MEDIA_MOUNTED)) {
							File savedir = new File(FILE_SAVEPATH);
							if (!savedir.exists()) {
								savedir.mkdirs();
							}
						} else {
							Toast.makeText(getApplicationContext(), "�޷������ϴ���ͷ������SD���Ƿ����", Toast.LENGTH_SHORT).show();
							return;
						}

						// ����ü�����ʱ�ļ�
						String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
						// ��Ƭ����
						String origFileName = "eapt_ac_" + timeStamp + ".jpg";
						String cropFileName = "eapt_ac_crop_" + timeStamp + ".jpg";

						// �ü�ͷ��ľ���·��
						protraitPath = FILE_SAVEPATH + cropFileName;
						protraitFile = new File(protraitPath);

						origUri = Uri.fromFile(new File(FILE_SAVEPATH, origFileName));
						cropUri = Uri.fromFile(protraitFile);

						// ���ѡͼ
						if (item == 0) {
							startActionPickCrop(cropUri);
						}
						// �ֻ�����
						else if (item == 1) {
							startActionCamera(origUri);
						}
					}
				}).create();

		imageDialog.show();
	}

	/**
	 * ѡ��ͼƬ�ü�
	 * 
	 * @param output
	 */
	private void startActionPickCrop(Uri output) {
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setType("image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// �ü������
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// ���ͼƬ��С
		intent.putExtra("outputY", CROP);
		startActivityForResult(Intent.createChooser(intent, "ѡ��ͼƬ"), ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD);
	}

	/**
	 * �������
	 * 
	 * @param output
	 */
	private void startActionCamera(Uri output) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCAMERA);
	}

	/**
	 * ���պ�ü�
	 * 
	 * @param data
	 *            ԭʼͼƬ
	 * @param output
	 *            �ü���ͼƬ
	 */
	private void startActionCrop(Uri data, Uri output) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(data, "image/*");
		intent.putExtra("output", output);
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);// �ü������
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", CROP);// ���ͼƬ��С
		intent.putExtra("outputY", CROP);
		startActivityForResult(intent, ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP);
	}

	/**
	 * �ϴ�����Ƭ
	 */
	private void uploadNewPhoto() {
		if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loading != null)
					loading.dismiss();
				if (msg.what == 1) {
					// ��ʾͼƬ
					setImageView.setImageBitmap(protraitBitmap);
					boolean uploadResult = (Boolean) msg.obj;
					String toaString = uploadResult ? "�ͼƬ�ϴ��ɹ�" : "�ͼƬ�ϴ�ʧ�ܣ�������ѡ��ͼƬ�ϴ�";
					Toast.makeText(getApplication(), toaString, Toast.LENGTH_LONG).show();
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("�����ϴ��ͼƬ������");
			loading.show();
		}

		new Thread() {
			public void run() {
				// ��ȡͷ������ͼ
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
					// //������ͷ�񵽻���
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
			startActionCrop(origUri, cropUri);// ���պ�ü�
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			uploadNewPhoto();// �ϴ�����Ƭ
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
			addDutyAdapter.notifyDataSetChanged();// ����ְ�����
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
	 * ���½����ϵ����������ܲ���ʱ���
	 */
	private void updateSumDuty() {
		int sumNum = 0;
		int sumIntegral = 0;
		for (Duty updateDuty : addDutyList) {
			sumNum += updateDuty.getNeedNum();
			sumIntegral += updateDuty.getDutyIntegral() * updateDuty.getNeedNum();
		}
		if (sumNum > 0) {
			createSumNumTV.setText(sumNum + "��");
			createSumNumTV.setTextColor(getResources().getColor(R.color.trd_color));
			String integralString = sumIntegral > 0 ? "�㽫����" + sumIntegral : "�㽫���" + Math.abs(sumIntegral);
			createSumIntegralTV.setText(integralString + "ʱ���");
		} else {
			createSumNumTV.setText("����ӻְ��");
			createSumNumTV.setTextColor(getResources().getColor(R.color.red));
		}
	}

	/**
	 * ����ѡ��
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
		new AlertDialog.Builder(PushActivity.this).setTitle("ѡ��ʱ��").setView(timepickerview)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
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
				}).setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
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
	 * ����ȷ��
	 */
	protected void confirmBack() {
		DialogTool.createConfirmDialog(PushActivity.this, "��ʾ", "ȷ��Ҫ�����������", "ȷ��", "ȡ��", new DialogInterface.OnClickListener() {

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
		if (sumIntegral.substring(2, 3).equals("���")) {
			pushActivity.setSumIntegral(-pushActivity.getSumIntegral());
		}
		final String createActivityJSON = MyActivity.getJSON(pushActivity, addDutyList);
		System.out.println("Post JOSN:" + createActivityJSON);

		if (nowUser.getIntegral() < pushActivity.getSumIntegral()) {
			DialogTool.createMessageDialog(this, "��ʾ", "����ʱ��Ҳ��������˻������ϵ��������Ա��ȡ����ʱ��ң�", "ȷ��", null, DialogTool.NO_ICON).show();
			return;
		}

		// �������硣��������
		if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
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
					Toast.makeText(getApplicationContext(), "������ɹ�", Toast.LENGTH_LONG).show();
					Intent newActivityIntent = new Intent(PushActivity.this, ActivityDetails.class);
					Bundle newActivityBundle = new Bundle();
					newActivityBundle.putInt("activityId", newActivity.getActivityId());
					newActivityIntent.putExtras(newActivityBundle);
					startActivity(newActivityIntent);
					finish();
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		if (loading != null) {
			loading.setLoadText("�������...");
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
