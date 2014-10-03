package com.kingtime.elderlyapt.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kingtime.elderlyapt.util.StreamTool;

/**
 * @author xp
 * @created 2014年8月17日
 */
public class Record {

	/**
	 * 未加入过
	 */
	public static final int RECORD_NOT_JOIN = 0x00;
	/**
	 * 正常参与
	 */
	public static final int RECORD_COMMON = 0x01;
	/**
	 * 被取消参与
	 */
	public static final int RECORD_CANCLED = 0x02;
	/**
	 * 正在被管理
	 */
	public static final int RECORD_MANAGEING = 0x03;

	/**
	 * 未评价
	 */
	public static final int RECORD_NOT_EVALUATED = 0x04;
	/**
	 * 已评价
	 */
	public static final int RECORD_EVALUATED = 0x05;

	private int recordId;
	private int uid;
	private int dutyId;
	private int stateId;
	private String inTime;
	private String remark;
	private User user;
	private String dutyName;

	public int getRecordId() {
		return recordId;
	}

	public void setRecordId(int recordId) {
		this.recordId = recordId;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getDutyId() {
		return dutyId;
	}

	public void setDutyId(int dutyId) {
		this.dutyId = dutyId;
	}

	public int getStateId() {
		return stateId;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public String getInTime() {
		return inTime.substring(0, inTime.length() - 3);
	}

	public void setInTime(String inTime) {
		this.inTime = inTime;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getUser() {
		if (user == null) {
			return new User();
		}
		return user;
	}

	public void setUser(User user) {
		if (user == null) {
			user = new User();
		}
		this.user = user;
	}

	public String getDutyName() {
		return dutyName;
	}

	public void setDutyName(String dutyName) {
		this.dutyName = dutyName;
	}

	/**
	 * 解析JSON数据
	 * 
	 * @param stream
	 *            输入流
	 * @return 参与记录信息
	 * @throws JSONException
	 */
	public static List<Record> parse(InputStream stream) throws JSONException {
		List<Record> recordList = new ArrayList<Record>();
		byte[] data = null;
		try {
			data = StreamTool.read(stream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String json = new String(data);
		System.out.println(json + "\n");
		JSONArray array = new JSONArray(json);
		for (int i = 0; i < array.length(); i++) {
			Record record = new Record();
			record.user = new User();
			JSONObject jsonObject = array.getJSONObject(i);
			record.dutyId = jsonObject.getInt("dutyId");
			record.dutyName = jsonObject.getString("dutyName");
			record.recordId = jsonObject.getInt("recordId");
			record.uid = jsonObject.getInt("uid");
			record.stateId = jsonObject.getInt("stateId");
			record.remark = jsonObject.getString("remark");
			record.inTime = jsonObject.getString("inTime");
			record.user.setUid(record.uid);
			record.user.setRoleId(jsonObject.getInt("roleId"));
			record.user.setPhotoName(jsonObject.getString("photoName"));
			record.user.setCredibility(jsonObject.getLong("credibility"));
			record.user.setName(jsonObject.getString("userName"));
			recordList.add(record);
		}
		return recordList;
	}

}
