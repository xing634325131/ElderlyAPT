package com.kingtime.elderlyapt.ui;

import java.io.IOException;

import com.kingtime.elderlyapt.AppContext;
import com.kingtime.elderlyapt.R;
import com.kingtime.elderlyapt.api.ApiClient;
import com.kingtime.elderlyapt.entity.MyActivity;
import com.kingtime.elderlyapt.util.DialogTool;
import com.kingtime.elderlyapt.widget.LoadingDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.Toast;

/**
 * @author xp
 * @created 2014年8月17日
 */
public class MenuWindow extends PopupWindow {

	private Button menuModifyStateBtn;
	private Button menuManageJoinedBtn;
	private View mMenuView;
	private MyActivity myActivity;
	private Activity c;
	private String[] nowState;
	private int selectId;
	// 加载对话框
	private LoadingDialog loadingDialog;

	public MenuWindow(final Activity context, OnClickListener itemsOnClick, MyActivity activity) {
		super(context);
		this.myActivity = activity;
		this.c = context;
		loadingDialog = new LoadingDialog(c);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mMenuView = inflater.inflate(R.layout.bottomdialog, null);

		int w = context.getWindowManager().getDefaultDisplay().getWidth();
		menuModifyStateBtn = (Button) mMenuView.findViewById(R.id.menu_modify_state);
		menuManageJoinedBtn = (Button) mMenuView.findViewById(R.id.menu_manage_joined);
		menuManageJoinedBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent joinIntent = new Intent(context, JoinUserList.class);
				Bundle joinBundle = new Bundle();
				joinBundle.putInt("activityId", myActivity.getActivityId());
				joinBundle.putInt("postUserId", myActivity.getPostUserId());
				joinIntent.putExtras(joinBundle);
				dismiss();
				context.startActivity(joinIntent);
			}
		});
		menuModifyStateBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dismiss();
				showDialog();
			}
		});
		// 设置SelectPicPopupWindow的View
		this.setContentView(mMenuView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(w / 2 - 40);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(LayoutParams.WRAP_CONTENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 设置SelectPicPopupWindow弹出窗体动画效果
		this.setAnimationStyle(R.style.menustyle);
		// 实例化一个ColorDrawable颜色为半透明
		ColorDrawable dw = new ColorDrawable(0000000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(dw);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mMenuView.setOnTouchListener(new OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {

				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
	}

	protected void showDialog() {
		if (myActivity.getStateId() == MyActivity.ACTIVITY_CANCEL) {
			DialogTool.createMessageDialog(c, "提醒", "活动未通过审核，请联系社区管理员！", "确定", null, DialogTool.NO_ICON).show();
			return;
		}
		if (myActivity.getStateId() == MyActivity.ACTIVITY_REVIEW) {
			DialogTool.createMessageDialog(c, "提醒", "请等待活动通过审核！", "确定", null, DialogTool.NO_ICON).show();
			return;
		}if (myActivity.getStateId() == MyActivity.ACTIVITY_END) {
			DialogTool.createMessageDialog(c, "提醒", "活动已结束！", "确定", null, DialogTool.NO_ICON).show();
			return;
		}
		int stateNum = 6 - myActivity.getStateId();
		nowState = new String[stateNum];
		for (int i = 0; i < stateNum; i++) {
			nowState[i] = MyActivity.STATE[myActivity.getStateId() + i + 1];
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(c);
		builder.setTitle("更改活动状态").setSingleChoiceItems(nowState, -1, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				selectId = which;
				DialogTool.createConfirmDialog(c, "警告", "确定要将活动状态置为：" + nowState[which], "确定", "取消", changeListener, null,
						DialogTool.NO_ICON).show();
				dialog.dismiss();
			}
		}).setNegativeButton("取消", null);
		builder.show();
	}

	private DialogInterface.OnClickListener changeListener = new DialogInterface.OnClickListener() {

		@Override
		public void onClick(DialogInterface dialog, int which) {
			if (selectId == 0) {// 当前状态，无需更新
				return;
			}
			final int stateId = myActivity.getStateId(nowState[selectId]);

			final AppContext appContext = (AppContext) c.getApplication();
			if (appContext.getNetworkType() == 0) {// 网络错误，无法连接到网络
				Toast.makeText(c, R.string.ERROR_NO_NETWORK, Toast.LENGTH_LONG).show();
				return;
			}

			final Handler handler = new Handler() {

				@Override
				public void handleMessage(Message msg) {
					if (loadingDialog != null) {
						loadingDialog.dismiss();
					}

					if (msg.what == 1) {
						Toast.makeText(c, "更新成功！", Toast.LENGTH_LONG).show();
						Intent intent = new Intent(c, ActivityDetails.class);
						Bundle bundle = new Bundle();
						bundle.putInt("activityId", myActivity.getActivityId());
						intent.putExtras(bundle);
						c.startActivity(intent);
						c.finish();
					} else if (msg.what == -1) {// 服务器无响应
						Toast.makeText(c, R.string.ERROR_CONNECT_NETWORK, Toast.LENGTH_LONG).show();
					} else {// 未知错误
						Toast.makeText(c, R.string.ERROR_OTHERS, Toast.LENGTH_LONG).show();
					}
					super.handleMessage(msg);
				}
			};

			if (loadingDialog != null) {
				loadingDialog.setLoadText("更新中...");
				loadingDialog.show();
			}

			new Thread() {
				public void run() {
					Message msg = new Message();
					try {
						boolean result = ApiClient.updateActivityState(myActivity.getActivityId(), stateId);
						msg.obj = result;
						msg.what = 1;
					} catch (IOException e) {
						System.out.println(e.toString());
						e.printStackTrace();
						msg.what = -1;
						msg.obj = e;
					}
					handler.sendMessage(msg);
				}
			}.start();
		}
	};
}
