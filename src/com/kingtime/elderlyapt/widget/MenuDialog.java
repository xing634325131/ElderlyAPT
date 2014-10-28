package com.kingtime.elderlyapt.widget;

import com.kingtime.elderlyapt.R;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

/**
 * @author xp
 * @created 2014年8月26日
 */
public class MenuDialog extends Dialog {

	private Context mContext;
	private LayoutInflater inflater;
	private LayoutParams lp;
	private TextView chatPlayTV;
	private TextView chatCopyTV;

	public MenuDialog(Context context) {
		super(context, R.style.MenuDialog);

		this.mContext = context;

		inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.chat_menu, null);
		chatCopyTV = (TextView) layout.findViewById(R.id.chat_menu_copy);
		chatPlayTV = (TextView) layout.findViewById(R.id.chat_menu_play);
		setContentView(layout);

		// 设置window属性
		lp = getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;
		lp.dimAmount = (float) 0.5; // 去背景遮盖
		lp.alpha = 1.0f;
		getWindow().setAttributes(lp);
	}
	
	public void setPlayListener(View.OnClickListener listener){
		chatPlayTV.setOnClickListener(listener);
		this.dismiss();
	}
	
	public void setCopyListener(View.OnClickListener listener){
		chatCopyTV.setOnClickListener(listener);
		this.dismiss();
	}

}
