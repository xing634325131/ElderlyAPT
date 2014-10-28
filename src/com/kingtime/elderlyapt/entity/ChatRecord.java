package com.kingtime.elderlyapt.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kingtime.elderlyapt.util.StreamTool;

/**
 * 讨论实体
 * 
 * @author xp
 * @created 2014年5月10日
 */
public class ChatRecord {

	private int chatId;
	private int activityId;
	private int uid;
	private String chatTime;
	private String content;
	private String remark;

	public int getChatId() {
		return chatId;
	}

	public void setChatId(int chatId) {
		this.chatId = chatId;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getChatTime() {
		// 处理数据库日期格式显示形式
		return chatTime.substring(0, chatTime.length() - 2);
	}

	public void setChatTime(String chatTime) {
		this.chatTime = chatTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 解析JSON数据
	 * 
	 * @param inputStream
	 * @return 聊天列表
	 * @throws JSONException
	 *             如果为空则抛出异常
	 */
	public static List<ChatRecord> parse(InputStream inputStream) throws JSONException {
		List<ChatRecord> recordList = new ArrayList<ChatRecord>();
		byte[] data = null;
		try {
			data = StreamTool.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = new String(data);
		System.out.println("ChatRecord_JSON-->>" + json);
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			JSONObject object = array.getJSONObject(i);
			ChatRecord record = new ChatRecord();
			record.setChatId(object.getInt("chatId"));
			record.setActivityId(object.getInt("activityId"));
			record.setUid(object.getInt("uid"));
			record.setChatTime(object.getString("chatTime"));
			record.setContent(object.getString("content"));
			record.setRemark(object.getString("remark"));
			recordList.add(record);
		}
		return recordList;
	}
}
