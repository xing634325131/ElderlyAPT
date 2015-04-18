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

	// ����ͼƬ��ȡ
	private RequestQueue rQueue;
	private ImageLoader imageLoader;

	// ͼ����Ϣ����
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

		titleTV.setText("��������");
		backBtn.setOnClickListener(listener);
		saveBtn.setText("����");
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
		// ��ȡ��¼�û���Ϣ
		appContext = (AppContext) getApplication();
		nowUser = appContext.getLoginInfo();
		// ����ͼƬ��ȡ��ʼ��
		rQueue = Volley.newRequestQueue(this);
		LruImageCache imageCache = LruImageCache.instance();
		imageLoader = new ImageLoader(rQueue, imageCache);
		loading = new LoadingDialog(this);

		setData();// ��ʼ����Ϊ��������

		// �ڻ�ȡ�������ݺ󣬸��±��ظ�����Ϣ
		if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 1) {
					setData();
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
				super.handleMessage(msg);
			}
		};

		new Thread() {
			public void run() {
				Message msg = new Message();
				try {
					nowUser = ApiClient.getUserByUserId(nowUser.getUid());// �����û�
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
	 * ������ʾ����
	 */
	private void setData() {
		if (nowUser.getPhotoName() != null) {
			String actionURL = ApiClient.formImageURL(URLs.REQUEST_PHOTO_IMAGE, nowUser.getPhotoName());
			photoIV.setImageUrl(actionURL, imageLoader);
		}
		nameTV.setText(nowUser.getName());
		if (nowUser.getPhone().equals("null") || StringUtils.isEmpty(nowUser.getPhone())) {
			phoneTV.setText("����д");
			phoneTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			phoneTV.setText(nowUser.getPhone());
		}
		if (nowUser.getResPhone().equals("null") || StringUtils.isEmpty(nowUser.getResPhone())) {
			resphoneTV.setText("����д");
			resphoneTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			resphoneTV.setText(nowUser.getResPhone());
		}
		if (nowUser.getEmail().equals("null") || StringUtils.isEmpty(nowUser.getEmail())) {
			emailTV.setText("����д");
			emailTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			emailTV.setText(nowUser.getEmail());
		}
		if (nowUser.getInterest().equals("null") || StringUtils.isEmpty(nowUser.getInterest())) {
			interestTV.setText("����д");
			interestTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			interestTV.setText(nowUser.getInterest());
		}
		if (nowUser.getAddress().equals("null") || StringUtils.isEmpty(nowUser.getAddress())) {
			addressTV.setText("����д");
			addressTV.setTextColor(getResources().getColor(R.color.red));
		} else {
			addressTV.setText(nowUser.getAddress());
		}
		if (nowUser.getGender().equals("null") || StringUtils.isEmpty(nowUser.getGender())) {
			genderTV.setText("��ѡ��");
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
				editDialog(R.id.myinfo_my_name, InputType.TYPE_CLASS_TEXT, "�û���");
				break;
			case R.id.myinfo_gender:
				genderDialog();
				break;
			case R.id.myinfo_phone:
				editDialog(R.id.myinfo_my_phone, InputType.TYPE_CLASS_PHONE, "��ϵ��ʽ");
				break;
			case R.id.myinfo_resphone:
				editDialog(R.id.myinfo_my_resphone, InputType.TYPE_CLASS_PHONE, "������ϵ��ʽ");
				break;
			case R.id.myinfo_email:
				editDialog(R.id.myinfo_my_email, InputType.TYPE_CLASS_TEXT, "�����ַ");
				break;
			case R.id.myinfo_interest:
				editDialog(R.id.myinfo_my_interest, InputType.TYPE_CLASS_TEXT, "�س��ͶԹ�Ԣ�������ԷֺŸ���");
				break;
			case R.id.myinfo_address:
				editDialog(R.id.myinfo_my_address, InputType.TYPE_CLASS_TEXT, "��ϸ��ַ");
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
		if (tempTextView.getText().equals("����д")) {
			inputServer.setText("");
		} else {
			inputServer.setText(tempTextView.getText());
		}
		inputServer.setInputType(inputType);
		inputServer.setSelectAllOnFocus(true);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setView(inputServer).setNegativeButton("ȡ��", null);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {
				tempTextView.setText(inputServer.getText().toString());

			}
		});
		builder.show();
	}

	protected void genderDialog() {
		final String[] items = { "��", "Ů" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("�Ա�").setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				genderTV.setText(items[which]);
				dialog.dismiss();
			}
		}).setNegativeButton("ȡ��", null);
		builder.show();
	}

	/**
	 * ͼƬѡ��
	 */
	protected void selectPhoto() {
		CharSequence[] items = { getString(R.string.img_from_album), getString(R.string.img_from_camera) };
		AlertDialog imageDialog = new AlertDialog.Builder(this).setTitle("�޸�ͷ��")
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
						String origFileName = "eapt_photo_" + timeStamp + ".jpg";
						String cropFileName = "eapt_photo_crop_" + timeStamp + ".jpg";

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
					photoIV.setImageBitmap(protraitBitmap);
					boolean uploadResult = (Boolean) msg.obj;
					String toaString = uploadResult ? "ͷ���ϴ��ɹ�" : "ͷ���ϴ�ʧ�ܣ�������ѡ��ͼƬ�ϴ�";
					Toast.makeText(getApplication(), toaString, Toast.LENGTH_LONG).show();
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("�����ϴ�ͷ�񡤡���");
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
						boolean result = ApiClient.uploadUserPhoto(nowUser.getUid(), protraitFile);

						// ����ͷ�񵽻���
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
			startActionCrop(origUri, cropUri);// ���պ�ü�
			break;
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYSDCARD:
		case ImageUtils.REQUEST_CODE_GETIMAGE_BYCROP:
			uploadNewPhoto();// �ϴ�����Ƭ
			break;
		default:
			break;
		}
	}

	/**
	 * ���¸�����Ϣ
	 */
	protected void saveInfo() {
		nowUser.setName(nameTV.getText().toString());
		nowUser.setGender(genderTV.getText().toString());
		nowUser.setPhone(phoneTV.getText().toString());
		nowUser.setResPhone(resphoneTV.getText().toString());
		nowUser.setEmail(emailTV.getText().toString());
		nowUser.setInterest(interestTV.getText().toString());
		nowUser.setAddress(addressTV.getText().toString());

		if (appContext.getNetworkType() == 0) {// ��������޷����ӵ�����
			Toast.makeText(getApplicationContext(), R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
			return;
		}

		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				if (loading != null)
					loading.dismiss();
				if (msg.what == 1) {
					User newUser = (User) msg.obj;
					appContext.saveLoginInfo(newUser);// �����������Ϣ�󱣴浽����
					String toaString = newUser != null ? "���³ɹ�" : "����ʧ��";
					Toast.makeText(getApplication(), toaString, Toast.LENGTH_LONG).show();
				} else if (msg.what == -1) {// ����������Ӧ
					Toast.makeText(getApplicationContext(), R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
				} else if (msg.what == -2) {// �Ѵ��ڸ��û���
					DialogTool.createMessageDialog(MyInfo.this, "����ʧ��", "�Ѵ��ڸ��û���", "ȷ��", null, DialogTool.NO_ICON).show();
				} else {// δ֪����
					Toast.makeText(getApplicationContext(), R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
				}
			}
		};

		if (loading != null) {
			loading.setLoadText("���ڸ��¸�����Ϣ������");
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
