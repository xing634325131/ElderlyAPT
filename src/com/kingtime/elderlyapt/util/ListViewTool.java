package com.kingtime.elderlyapt.util;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * @author xp
 * @created 2014年8月12日
 */
public class ListViewTool {

	/**
	 * 在Listview数据更新后，更新在Scroll里的Listview高度
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
				+ (listView.getDividerHeight() * (adapter.getCount() - 1));// 总行高+每行的间距
		return layoutParams;
	}
}
