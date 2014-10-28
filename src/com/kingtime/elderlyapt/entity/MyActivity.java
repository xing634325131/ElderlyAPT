package com.kingtime.elderlyapt.entity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.kingtime.elderlyapt.util.StreamTool;
import com.kingtime.elderlyapt.util.StringUtils;

/**
 * 活动实体
 * 
 * @author xp
 * @created 2014年8月6日
 */
public class MyActivity {

	private int activityId;
	private int postUserId;
	private int categoryId;
	private int locationId;
	private String postName;
	private String content;
	private String postTime;
	private String beginTime;
	private String endTime;
	private String closeTime;
	private int needNum;
	private int nowNum;
	private int stateId;
	private String address;
	private int sumIntegral;
	private String remark;
	private String mainPic;

	public static final String[] CATEGORY = new String[] { "互助", "娱乐", "家务",
			"陪护", "水电维修", "医疗", "志愿活动", "代办", "短途旅游", "推荐" };
	public static final String[] STATE = new String[] { "未通过审核", "待审核", "报名中", "报名截止",
			"即将开始", "进行中", "已结束" };
	/**
	 * 未通过审核
	 */
	public static final int ACTIVITY_CANCEL = 0x00;
	/**
	 * 待审核
	 */
	public static final int ACTIVITY_REVIEW = 0x01;
	/**
	 * 报名中
	 */
	public static final int ACTIVITY_APPLY = 0x02;
	/**
	 * 报名截止
	 */
	public static final int ACTIVITY_CLOSED = 0x03;
	/**
	 * 即将开始
	 */
	public static final int ACTIVITY_WILL_START = 0x04;
	/**
	 * 进行中
	 */
	public static final int ACTIVITY_RUNNING = 0x05;
	/**
	 * 已结束
	 */
	public static final int ACTIVITY_END = 0x06;

	public int getActivityId() {
		return activityId;
	}

	public void setActivityId(int activityId) {
		this.activityId = activityId;
	}

	public int getPostUserId() {
		return postUserId;
	}

	public void setPostUserId(int postUserId) {
		this.postUserId = postUserId;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public int getLocationId() {
		return locationId;
	}

	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}

	public String getPostName() {
		return postName;
	}

	public void setPostName(String postName) {
		this.postName = postName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getPostTime() {
		return postTime.substring(0, postTime.length() - 3);
	}

	public void setPostTime(String postTime) {
		this.postTime = postTime;
	}

	public String getBeginTime() {
		return beginTime.substring(0, beginTime.length() - 2);
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
	}

	public String getEndTime() {
		return endTime.substring(0, endTime.length() - 2);
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getCloseTime() {
		return closeTime.substring(0, closeTime.length() - 2);
	}

	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
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

	public int getStateId() {
		return stateId;
	}

	public int getStateId(String state) {
		int id = 0;
		for (int i = 0; i < STATE.length; i++) {
			if (state.equals(STATE[i])) {
				id = i;
				break;
			}
		}
		return id;
	}

	public void setStateId(int stateId) {
		this.stateId = stateId;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getSumIntegral() {
		return sumIntegral;
	}

	public void setSumIntegral(int sumIntegral) {
		this.sumIntegral = sumIntegral;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCategory() {
		return CATEGORY[categoryId - 1];
	}

	public String getState() {
		return STATE[stateId];
	}

	public String getMainPic() {
		return mainPic;
	}

	public void setMainPic(String mainPic) {
		this.mainPic = mainPic;
	}

	/**
	 * 将日期调整为指定格式
	 * 
	 * @return
	 */
	public String getSustained() {
		StringBuilder dateString = new StringBuilder();
		Date startDate = StringUtils.toDate(beginTime);
		Date endDate = StringUtils.toDate(endTime);
		Calendar startCalendar = Calendar.getInstance();
		startCalendar.setTime(startDate);
		Calendar endCalendar = Calendar.getInstance();
		endCalendar.setTime(endDate);
		int startMonth = startCalendar.get(Calendar.MONTH) + 1;
		int endMonth = endCalendar.get(Calendar.MONTH) + 1;
		dateString.append(startMonth).append("月");
		dateString.append(startCalendar.get(Calendar.DAY_OF_MONTH)).append("日");
		dateString.append("至");
		dateString.append(endMonth).append("月");
		dateString.append(endCalendar.get(Calendar.DAY_OF_MONTH)).append("日");
		System.out.println("Date:" + dateString.toString());
		return dateString.toString();
	}

	/**
	 * 解析JSON数据
	 * 
	 * @param inputStream
	 *            输入流
	 * @return 活动信息列表
	 * @throws JSONException
	 * @throws Exception
	 */
	public static List<MyActivity> parse(InputStream inputStream)
			throws JSONException {
		byte[] data = null;
		try {
			data = StreamTool.read(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return parse(new String(data));
	}

	/**
	 * @param content
	 * @return
	 * @throws JSONException
	 */
	public static List<MyActivity> parse(String content) throws JSONException {
		List<MyActivity> myActivities = new ArrayList<MyActivity>();
		System.out.println(content + "\n");
		JSONArray array = new JSONArray(content);
		for (int i = 0; i < array.length(); i++) {
			MyActivity myActivity = new MyActivity();
			JSONObject jsonObject = array.getJSONObject(i);
			myActivity.activityId = jsonObject.getInt("activityId");
			myActivity.postUserId = jsonObject.getInt("postUserId");
			myActivity.address = jsonObject.getString("address");
			myActivity.beginTime = jsonObject.getString("beginTime");
			myActivity.categoryId = jsonObject.getInt("categoryId");
			myActivity.closeTime = jsonObject.getString("closeTime");
			myActivity.content = jsonObject.getString("content");
			myActivity.endTime = jsonObject.getString("endTime");
			myActivity.locationId = jsonObject.getInt("locationId");
			myActivity.needNum = jsonObject.getInt("needNum");
			myActivity.nowNum = jsonObject.getInt("nowNum");
			myActivity.postName = jsonObject.getString("postName");
			myActivity.postTime = jsonObject.getString("postTime");
			myActivity.stateId = jsonObject.getInt("stateId");
			myActivity.remark = jsonObject.getString("remark");
			myActivity.sumIntegral = jsonObject.getInt("sumIntegral");
			myActivity.mainPic = jsonObject.getString("mainPic");
			myActivities.add(myActivity);
		}
		return myActivities;
	}

	/**
	 * 创建活动JSON数据，便于服务器解析
	 * 
	 * @param pushActivity
	 * @param addDutyList
	 * @return
	 */
	public static String getJSON(MyActivity pushActivity, List<Duty> addDutyList) {
		StringBuilder builder = new StringBuilder();
		builder.append("[{");
		builder.append("postName:\"").append(pushActivity.postName)
				.append("\",");
		builder.append("address:\"").append(pushActivity.address).append("\",");
		builder.append("beginTime:\"").append(pushActivity.beginTime)
				.append("\",");
		builder.append("categoryId:").append(pushActivity.categoryId)
				.append(",");
		builder.append("closeTime:\"").append(pushActivity.closeTime)
				.append("\",");
		builder.append("content:\"").append(pushActivity.content).append("\",");
		builder.append("endTime:\"").append(pushActivity.endTime).append("\",");
		builder.append("locationId:").append(pushActivity.locationId)
				.append(",");
		builder.append("mainPic:\"").append(pushActivity.mainPic).append("\",");
		builder.append("needNum:").append(pushActivity.needNum).append(",");
		builder.append("postUserId:\"").append(pushActivity.postUserId)
				.append("\",");
		builder.append("sumIntegral:").append(pushActivity.sumIntegral)
				.append(",");

		builder.append("duty:").append("[");
		for (Duty duty : addDutyList) {
			builder.append("{");
			builder.append("dutyContent:\"").append(duty.getDutyContent())
					.append("\",");
			builder.append("needNum:").append(duty.getNeedNum()).append(",");
			builder.append("dutyName:\"").append(duty.getDutyName())
					.append("\",");
			builder.append("dutyIntegral:").append(duty.getDutyIntegral());
			builder.append("},");
		}
		builder.deleteCharAt(builder.length() - 1);
		builder.append("]");

		builder.append("}]");
		return builder.toString();
	}
}
