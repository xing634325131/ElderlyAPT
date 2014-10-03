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
 * �Ի����װ��
 * 
 * @author xp
 * @created 2014��6��13��
 */
public class DialogTool {

	public static final int NO_ICON = -1; // ��ͼ��

	/**
	 * ������Ϣ�Ի���
	 * 
	 * @param context
	 *            ������ ����
	 * @param iconId
	 *            ͼ�꣬�磺R.drawable.icon �� DialogTool.NO_ICON ����
	 * @param title
	 *            ���� ����
	 * @param message
	 *            ��ʾ���� ����
	 * @param btnName
	 *            ��ť���� ����
	 * @param listener
	 *            ����������ʵ��android.content.DialogInterface.OnClickListener�ӿ� ����
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
	 * ������ʾ��ȷ�ϡ�ȡ�����Ի���
	 * 
	 * @param context
	 *            ������ ����
	 * @param iconId
	 *            ͼ�꣬�磺R.drawable.icon �� DialogTool.NO_ICON ����
	 * @param title
	 *            ���� ����
	 * @param message
	 *            ��ʾ���� ����
	 * @param positiveBtnName
	 *            ȷ����ť���� ����
	 * @param negativeBtnName
	 *            ȡ����ť���� ����
	 * @param positiveBtnListener
	 *            ����������ʵ��android.content.DialogInterface.OnClickListener�ӿ� ����
	 * @param negativeBtnListener
	 *            ����������ʵ��android.content.DialogInterface.OnClickListener�ӿ� ����
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
	 * �����ٶ�����ʶ��Ի���
	 * 
	 * @param context
	 *            ������
	 * @param mRecognitionListener
	 *            �ص�������
	 * @return
	 */
	public static BaiduASRDigitalDialog createBaiduASRDigitalDialog(
			Context context, DialogRecognitionListener mRecognitionListener) {
		Bundle params = new Bundle();
		BaiduASRDigitalDialog mDialog;
		// ���ÿ���API Key
		params.putString(BaiduASRDigitalDialog.PARAM_API_KEY,
				"kslfNHwxFVQG1GWrIOriNWvq");
		// ���ÿ���ƽ̨Secret Key
		params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY,
				"P8sh8YnPqG0DSqWCpU0ck9sSqgBedzj1");
		// ����ʶ���������������롢��ͼ�����֡�������ѡ��Ĭ��Ϊ���롣
		params.putInt(BaiduASRDigitalDialog.PARAM_PROP,
				VoiceRecognitionConfig.PROP_INPUT);
		// �����������ͣ�������ͨ�����������Ӣ�ģ���ѡ��Ĭ��Ϊ������ͨ��
		params.putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
				VoiceRecognitionConfig.LANGUAGE_CHINESE);

		// �����Ҫ��������������·�����������Ϊ���벻֧��
		// params.putBoolean(BaiduASRDigitalDialog.PARAM_NLU_ENABLE,true);
		// ���öԻ������⣬��ѡ��BaiduASRDigitalDialog�ṩ�����������졢�̡���������ɫ��ÿ����
		// ɫ�ַ�����������ɫ������8�����⣬�����߿��԰���ѡ��ȡֵ�ο�BaiduASRDigitalDialog��
		// ǰ׺ΪTHEME_�ĳ�����Ĭ��Ϊ����ɫ
		// params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
		// BaiduASRDigitalDialog.THEME_RED_DEEPBG);
		mDialog = new BaiduASRDigitalDialog(context, params);
		mDialog.setDialogRecognitionListener(mRecognitionListener);
		return mDialog;
	}
}