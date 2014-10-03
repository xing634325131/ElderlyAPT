package com.kingtime.elderlyapt.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kingtime.elderlyapt.util.StreamTool;

/**
 * 职责实体
 * 
 * @author xp
 * @created 2014年8月8日
 */
public class Duty {

	private int dutyId;
	private int activityId;
	private int needNum;
	private int nowNum;
	private String dutyContent;
	private int dutyIntegral;
	private String remark;
	private String dutyName;

	public int getDutyId() {
		return dutyId;
	}

	public void setDutyId(int dutyId) {
		this.dutyId = dutyId;
	}

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getNeedNum() {
		return needNum;
	}

	public void setNeedNum(int needNum) {
		this.needNum = needNum;
	}

	public int getNowNum() {
		return nowNum;
	}

	public void setNowNum(int nowNum) {
		this.nowNum = nowNum;
	}

	public String getDutyContent() {
		return dutyContent;
	}

	public void setDutyContent(String dutyContent) {
		this.dutyContent = dutyContent;
	}

	public int getDutyIntegral() {
		return dutyIntegral;
	}

	public void setDutyIntegral(int dutyIntegral) {
		this.dutyIntegral = dutyIntegral;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
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
	 * @return 职责信息
	 * @throws JSONException
	 */
	public static List<Duty> parse(InputStream stream) throws JSONException {
		List<Duty> dutyList = new ArrayList<Duty>();
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
			Duty duty = new Duty();
			JSONObject jsonObject = array.getJSONObject(i);
			duty.dutyId = jsonObject.getInt("dutyId");
			duty.activityId = jsonObject.getInt("activityId");
			duty.dutyContent = jsonObject.getString("dutyContent");
			duty.dutyIntegral = jsonObject.getInt("dutyIntegral");
			duty.dutyName = jsonObject.getString("dutyName");
			duty.needNum = jsonObject.getInt("needNum");
			duty.nowNum = jsonObject.getInt("nowNum");
			duty.remark = jsonObject.getString("remark");
			dutyList.add(duty);
		}
		return dutyList;
	}
}
