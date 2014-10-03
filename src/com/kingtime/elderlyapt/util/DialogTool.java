package com.kingtime.elderlyapt.util;

import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

/**
 * 对话框封装类
 * 
 * @author xp
 * @created 2014年6月13日
 */
public class DialogTool {

	public static final int NO_ICON = -1; // 无图标

	/**
	 * 创建消息对话框
	 * 
	 * @param context
	 *            上下文 必填
	 * @param iconId
	 *            图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
	 * @param title
	 *            标题 必填
	 * @param message
	 *            显示内容 必填
	 * @param btnName
	 *            按钮名称 必填
	 * @param listener
	 *            监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
	 * @return
	 */
	public static Dialog createMessageDialog(Context context, String title,
			String message, String btnName, OnClickListener listener, int iconId) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		if (iconId != NO_ICON) {
			builder.setIcon(iconId);
		}
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(btnName, listener);
		dialog = builder.create();

		return dialog;
	}

	/**
	 * 创建警示（确认、取消）对话框
	 * 
	 * @param context
	 *            上下文 必填
	 * @param iconId
	 *            图标，如：R.drawable.icon 或 DialogTool.NO_ICON 必填
	 * @param title
	 *            标题 必填
	 * @param message
	 *            显示内容 必填
	 * @param positiveBtnName
	 *            确定按钮名称 必填
	 * @param negativeBtnName
	 *            取消按钮名称 必填
	 * @param positiveBtnListener
	 *            监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
	 * @param negativeBtnListener
	 *            监听器，需实现android.content.DialogInterface.OnClickListener接口 必填
	 * @return
	 */
	public static Dialog createConfirmDialog(Context context, String title,
			String message, String positiveBtnName, String negativeBtnName,
			OnClickListener positiveBtnListener,
			OnClickListener negativeBtnListener, int iconId) {
		Dialog dialog = null;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);

		if (iconId != NO_ICON) {
			builder.setIcon(iconId);
		}
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setPositiveButton(positiveBtnName, positiveBtnListener);
		builder.setNegativeButton(negativeBtnName, negativeBtnListener);
		dialog = builder.create();

		return dialog;
	}

	/**
	 * 创建百度语音识别对话框
	 * 
	 * @param context
	 *            上下文
	 * @param mRecognitionListener
	 *            回调监听器
	 * @return
	 */
	public static BaiduASRDigitalDialog createBaiduASRDigitalDialog(
			Context context, DialogRecognitionListener mRecognitionListener) {
		Bundle params = new Bundle();
		BaiduASRDigitalDialog mDialog;
		// 设置开放API Key
		params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
				"kslfNHwxFVQG1GWrIOriNWvq");
		// 设置开放平台Secret Key
		params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
				"P8sh8YnPqG0DSqWCpU0ck9sSqgBedzj1");
		// 设置识别领域：搜索、输入、地图、音乐……，可选。默认为输入。
		params.putInt(BaiduASRDigitalDialog.PARAM_PROP,
				VoiceRecognitionConfig.PROP_INPUT);
		// 设置语种类型：中文普通话，中文粤语，英文，可选。默认为中文普通话
		params.putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
				VoiceRecognitionConfig.LANGUAGE_CHINESE);

		// 如果需要语义解析，设置下方参数。领域为输入不支持
		// params.putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE,true);
		// 设置对话框主题，可选。BaiduASRDigitalDialog提供了蓝、暗、红、绿、橙四中颜色，每种颜
		// 色又分亮、暗两种色调。共8种主题，开发者可以按需选择，取值参考BaiduASRDigitalDialog中
		// 前缀为THEME_的常量。默认为亮蓝色
		// params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
		// BaiduASRDigitalDialog.THEME_RED_DEEPBG);
		mDialog = new BaiduASRDigitalDialog(context, params);
		mDialog.setDialogRecognitionListener(mRecognitionListener);
		return mDialog;
	}
}