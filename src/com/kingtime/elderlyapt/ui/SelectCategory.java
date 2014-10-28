package com.kingtime.elderlyapt.ui;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * @author xp
 * @created 2014年8月11日
 */
public class SelectCategory extends ListActivity {

	private String[] categoryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		categoryList = new String[] { "互助", "娱乐", "家务", "陪护", "水电维修", "医疗",
				"志愿活动", "代办", "短途旅游" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, categoryList);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		System.out.println(position);
		Intent resultIntent = new Intent(SelectCategory.this,
				PushActivity.class);
		Bundle resultBundle = new Bundle();
		resultBundle.putString("category", categoryList[position]);
		resultBundle.putInt("categoryId", position + 1);
		resultIntent.putExtras(resultBundle);
		setResult(RESULT_OK, resultIntent);
		finish();
		super.onListItemClick(l, v, position, id);
	}

}
