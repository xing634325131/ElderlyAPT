package com.kingtime.elderlyapt.ui;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.api.URLs;
import com.kingtime.elderlyapt.entity.User;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.util.FileUtils;
import com.kingtime.elderlyapt.util.ImageUtils;
import com.kingtime.elderlyapt.util.StringUtils;
import com.kingtime.elderlyapt.widget.LoadingDialog;
import com.kingtime.elderlyapt.widget.LruImageCache;

import android.app.Activity;
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
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class MyInfo extends Activity {

	private TextView titleTV;
	private Button backBtn;
	private Button saveBtn;
	private LinearLayout photoLayout;
	private LinearLayout nameLayout;
	private LinearLayout genderLayout;
	private LinearLayout phoneLayout;
	private LinearLayout resphoneLayout;
	private LinearLayout emailLayout;
	private LinearLayout interestLayout;
	private LinearLayout addressLayout;
	private NetworkImageView photoIV;
	private TextView nameTV;
	private TextView genderTV;
	private TextView phoneTV;
	private TextView resphoneTV;
	private TextView emailTV;
	private TextView interestTV;
	private TextView addressTV;
	private RatingBar credibilityRating;
	private TextView createTimeTV;

	private User nowUser;
	private LoadingDialog loading;
	private AppContext appContext;

	// 网络图片获取
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	// 图像信息处理
	private final static int CROP = 200;
	private final static String FILE_SAVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath()
			+ "/ElderlyAPT/Portrait/";
	private Uri origUri;
	private Uri cropUri;
	private File protraitFile;
	private Bitmap protraitBitmap;
	private String protraitPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.myinfo);
		initLayout();
		initData();
	}

	private void initLayout() {
		titleTV = (TextView) findViewById(R.id.head_title);
		backBtn = (Button) findViewById(R.id.head_back);
		saveBtn = (Button) findViewById(R.id.head_right);
		phoneLayout = (LinearLayout) findViewById(R.id.myinfo_photo);
		nameLayout = (LinearLayout) findViewById(R.id.myinfo_name);
		genderLayout = (LinearLayout) findViewById(R.id.myinfo_gender);
		photoLayout = (LinearLayout) findViewById(R.id.myinfo_phone);
		resphoneLayout = (LinearLayout) findViewById(R.id.myinfo_resphone);
		interestLayout = (LinearLayout) findViewById(R.id.myinfo_interest);
		addressLayout = (LinearLayout) findViewById(R.id.myinfo_address);
		emailLayout = (LinearLayout) findViewById(R.id.myinfo_email);
		photoIV = (NetworkImageView) findViewById(R.id.myinfo_my_photo);
		nameTV = (TextView) findViewById(R.id.myinfo_my_name);
		genderTV = (TextView) findViewById(R.id.myinfo_my_gender);
		phoneTV = (TextView) findViewById(R.id.myinfo_my_phone);
		resphoneTV = (TextView) findViewById(R.id.myinfo_my_resphone);
		emailTV = (TextView) findViewById(R.id.myinfo_my_email);
		interestTV = (TextView) findViewById(R.id.myinfo_my_interest);
		addressTV = (TextView) findViewById(R.id.myinfo_my_address);
		createTimeTV = (TextView) findViewById(R.id.myinfo_my_createtime);
		credibilityRating = (RatingBar) findViewById(R.id.myinfo_my_credibility);

		titleTV.setText("个人资料");
		backBtn.setOnClickListener(listener);
		saveBtn.setText("保存");
		saveBtn.setVisibility(View.VISIBLE);
		saveBtn.setOnClickListener(listener);
		photoLayout.setOnClickListener(listener);
		nameLayout.setOnClickListener(listener);
		genderLayout.setOnClickListener(listener);
		phoneLayout.setOnClickListener(listener);
		resphoneLayout.setOnClickListener(listener);
		emailLayout.setOnClickListener(listener);
		interestLayout.setOnClickListener(listener);
		addressLayout.setOnClickListener(listener);
	}

	private void initData() {
		// 获取登录用户信息
		appContext = (AppContext) getApplication();
		nowUser = appContext.getLoginInfo();
		// 网络图片获取初始化
		rQueue = Volley.newRequestQueue(this);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
		loading = new LoadingDialog(this);

		setData();// 初始设置为本地数据

		// 在获取网络数据后，更新本地个人信息
		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					setData();
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					nowUser = ApiClient.getUserByUserId(nowUser.getUid());// 更新用户
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

	/**
	 * 设置显示数据
	 */
	private void setData() {
		if (nowUser.getPhotoName() != null) {
			String actionURL = ApiClient.formImageURL(URLs.REQUEST_PHOTO_IMAGE, nowUser.getPhotoName());
			photoIV.setImageUrl(actionURL, imageLoader);
		}
		nameTV.setText(nowUser.getName());
		if (nowUser.getPhone().equals("null") || StringUtils.isEmpty(nowUser.getPhone())) {
			phoneTV.setText("请填写");
			phoneTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			phoneTV.setText(nowUser.getPhone());
		}
		if (nowUser.getResPhone().equals("null") || StringUtils.isEmpty(nowUser.getResPhone())) {
			resphoneTV.setText("请填写");
			resphoneTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			resphoneTV.setText(nowUser.getResPhone());
		}
		if (nowUser.getEmail().equals("null") || StringUtils.isEmpty(nowUser.getEmail())) {
			emailTV.setText("请填写");
			emailTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			emailTV.setText(nowUser.getEmail());
		}
		if (nowUser.getInterest().equals("null") || StringUtils.isEmpty(nowUser.getInterest())) {
			interestTV.setText("请填写");
			interestTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			interestTV.setText(nowUser.getInterest());
		}
		if (nowUser.getAddress().equals("null") || StringUtils.isEmpty(nowUser.getAddress())) {
			addressTV.setText("请填写");
			addressTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			addressTV.setText(nowUser.getAddress());
		}
		if (nowUser.getGender().equals("null") || StringUtils.isEmpty(nowUser.getGender())) {
			genderTV.setText("请选择");
			genderTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			genderTV.setText(nowUser.getGender());
		}
		credibilityRating.setRating(nowUser.getCredibility());
		createTimeTV.setText(nowUser.getCreateTime());
	}

	private OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.head_back:
				finish();
				break;
			case R.id.myinfo_photo:
				selectPhoto();
				break;
			case R.id.myinfo_name:
				editDialog(R.id.myinfo_my_name, InputType.TYPE_CLASS_TEXT, "用户名");
				break;
			case R.id.myinfo_gender:
				genderDialog();
				break;
			case R.id.myinfo_phone:
				editDialog(R.id.myinfo_my_phone, InputType.TYPE_CLASS_PHONE, "联系方式");
				break;
			case R.id.myinfo_resphone:
				editDialog(R.id.myinfo_my_resphone, InputType.TYPE_CLASS_PHONE, "备用联系方式");
				break;
			case R.id.myinfo_email:
				editDialog(R.id.myinfo_my_email, InputType.TYPE_CLASS_TEXT, "邮箱地址");
				break;
			case R.id.myinfo_interest:
				editDialog(R.id.myinfo_my_interest, InputType.TYPE_CLASS_TEXT, "特长和对公寓的需求，以分号隔开");
				break;
			case R.id.myinfo_address:
				editDialog(R.id.myinfo_my_address, InputType.TYPE_CLASS_TEXT, "详细地址");
				break;
			case R.id.head_right:
				saveInfo();
				break;
			default:
				break;
			}
		}
	};

	protected void editDialog(final int id, int inputType, String title) {
		final EditText inputServer = new EditText(this);
		final TextView tempTextView = (TextView) findViewById(id);
		if (tempTextView.getText().equals("请填写")) {
			inputServer.setText("");
		} else {
			inputServer.setText(tempTextView.getText());
		}
		inputServer.setInputType(inputType);
		inputServer.setSelectAllOnFocus(true);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setView(inputServer).setNegativeButton("取消", null);
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				tempTextView.setText(inputServer.getText().toString());

			}
		});
		builder.show();
	}

	protected void genderDialog() {
		final String[] items = { "男", "女" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("性别").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				genderTV.setText(items[which]);
				dialog.dismiss();
			}
		}).setNegativeButton("取消", null);
		builder.show();
	}

	/**
	 * 图片选择
	 */
	protected void selectPhoto() {
		CharSequence[] items = { getString(R.string.img_from_album), getString(R.string.img_from_camera) };
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("修改头像")
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
						String origFileName = "eapt_photo_" + timeStamp + ".jpg";
						String cropFileName = "eapt_photo_crop_" + timeStamp + ".jpg";

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
					photoIV.setImageBitmap(protraitBitmap);
					boolean uploadResult = (Boolean) msg.obj;
					String toaString = uploadResult ? "头像上传成功" : "头像上传失败，请重新选择图片上传";
					Toast.makeText(getApplication(), toaString, Toast.LENGTH_LONG).show();
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("正在上传头像···");
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
						boolean result = ApiClient.uploadUserPhoto(nowUser.getUid(), protraitFile);

						// 保存头像到缓存
						String filename = FileUtils.getFileName(protraitFile.getPath());
						ImageUtils.saveImage(MyInfo.this, filename, protraitBitmap);
						msg.what = 1;
						msg.obj = result;
					} catch (IOException e) {
						msg.what = -1;
						e.printStackTrace();
					}
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
		default:
			break;
		}
	}

	/**
	 * 更新个人信息
	 */
	protected void saveInfo() {
		nowUser.setName(nameTV.getText().toString());
		nowUser.setGender(genderTV.getText().toString());
		nowUser.setPhone(phoneTV.getText().toString());
		nowUser.setResPhone(resphoneTV.getText().toString());
		nowUser.setEmail(emailTV.getText().toString());
		nowUser.setInterest(interestTV.getText().toString());
		nowUser.setAddress(addressTV.getText().toString());

		if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loading != null)
					loading.dismiss();
				if (msg.what == 1) {
					User newUser = (User) msg.obj;
					appContext.saveLoginInfo(newUser);// 更新完个人信息后保存到本地
					String toaString = newUser != null ? "更新成功" : "更新失败";
					Toast.makeText(getApplication(), toaString, Toast.LENGTH_LONG).show();
				} else if (msg.what == -1) {// 服务器无响应
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else if (msg.what == -2) {// 已存在该用户名
					DialogTool.createMessageDialog(MyInfo.this, "更新失败", "已存在该用户名", "确定", null, DialogTool.NO_ICON).show();
				} else {// 未知错误
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("正在更新个人信息···");
			loading.show();
		}

		new Thread() {
			public void run() {

				Message msg = new Message();
				try {
					User newUser = ApiClient.updateUserInfo(nowUser);
					msg.what = 1;
					msg.obj = newUser;
				} catch (IOException e) {
					msg.what = -1;
					e.printStackTrace();
				} catch (JSONException e) {
					msg.what = -2;
					e.printStackTrace();
				}
				handler.sendMessage(msg);
			};
		}.start();
	}

}
