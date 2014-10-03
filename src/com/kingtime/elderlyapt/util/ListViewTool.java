package com.kingtime.elderlyapt.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author xp
 * @created 2014��8��12��
 */
public class ListViewTool {

	/**
	 * ��Listview���ݸ��º󣬸�����Scroll���Listview�߶�
	 * 
	 * @param listView
	 * @return
	 */
	public static LayoutParams setLvHeight(ListView listView) {
		ListAdapter adapter = listView.getAdapter();
		if (adapter == null) {
			Log.i("ListViewTool", "no data in duty ListView");
			return null;
		}
		int totalHeight = 0;
		for (int i = 0; i < adapter.getCount(); i++) {
			View itemView = adapter.getView(i, null, listView);
			itemView.measure(0, 0);
			totalHeight += itemView.getMeasuredHeight();
		}
		LayoutParams layoutParams = listView.getLayoutParams();
		layoutParams.height = totalHeight
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));// ���и�+ÿ�еļ��
		return layoutParams;
	}
}
